/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dastrax.cnx.snmp;

import com.dastrax.app.util.ExceptionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 * @version Build 1.0.0
 * @since Sep 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class SnmpUtil {

    // Logger--------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(SnmpUtil.class.getName());
    
    // EJB----------------------------------------------------------------------
    @EJB
    ExceptionUtil exu;
    
    // Methods------------------------------------------------------------------
    /**
     * A manager-to-agent request to retrieve the value of a variable or list 
     * of variables. Desired variables are specified in variable bindings 
     * (values are not used). Retrieval of the specified variable values is to 
     * be done as an atomic operation by the agent.
     * 
     * @param targetaddress
     * @param oid
     * @return A Response with current values is returned.
     */
    public String get(Address targetaddress, OID oid) {
        String ret = null;
        TransportMapping transport;
        try {
            // Create community transport
            transport = new DefaultUdpTransportMapping();
            UserTarget target = new UserTarget();
            target.setAddress(targetaddress);
            target.setRetries(3);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString("MD5DES"));

            // Create the PDU 
            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(PDU.GET);
            // Put the oid you want to get
            pdu.add(new VariableBinding(oid));
            pdu.setNonRepeaters(0);

            // PDU string
            Snmp snmp = new Snmp(transport);
            snmp.listen();

            // Send the PDU 
            ResponseEvent responseEvent = snmp.send(pdu, target);

            // Extract the response PDU (could be null if timed out) 
            PDU responsePDU = responseEvent.getResponse();
            LOG.log(Level.INFO, "SNMP Event: ", responsePDU.toString());
            
            if (responsePDU.getVariableBindings().size() > 0) {
                VariableBinding vb = (VariableBinding) responsePDU.getVariableBindings().get(0);
                ret = vb.getVariable().toString();
            }

        } catch (IOException e) {
            exu.report(e);
            LOG.log(Level.INFO, "SNMP Get IOException: ", e);
        }
        return ret;
    }

    /**
     * A manager-to-agent request to discover available variables and their 
     * values. Returns a Response with variable binding for the lexicographically 
     * next variable in the MIB. The entire MIB of an agent can be walked by 
     * iterative application of GetNextRequest starting at OID 0. Rows of a 
     * table can be read by specifying column OIDs in the variable bindings of 
     * the request.
     * 
     * @param targetaddress
     * @param comm
     * @param oid
     * @return 
     */
    protected VariableBinding getNext(Address targetaddress, String comm, OID oid) {
        VariableBinding ret = null;
        TransportMapping transport;
        try {
            // Create community transport
            transport = new DefaultUdpTransportMapping();
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(targetaddress);
            target.setRetries(3);
            target.setTimeout(2000);
            target.setVersion(SnmpConstants.version3);

            // Create the PDU 
            PDU pdu = new PDU();
            pdu.setType(PDU.GETNEXT);
            // Put the oid you want to get
            pdu.add(new VariableBinding(oid));
            pdu.setNonRepeaters(0);

            // PDU string
            System.out.println("pdu " + pdu.toString());
            Snmp snmp = new Snmp(transport);
            snmp.listen();

            // Send the PDU 
            ResponseEvent responseEvent = snmp.send(pdu, target);
            LOG.log(Level.INFO, "SNMP Event: ", responseEvent.toString());
            
            // Extract the response PDU (could be null if timed out) 
            PDU responsePDU = responseEvent.getResponse();
            LOG.log(Level.INFO, "SNMP Event: ", responsePDU.toString());
            
            if (responsePDU.getVariableBindings().size() > 0) {
                ret = (VariableBinding) responsePDU.getVariableBindings().get(0);
            }

        } catch (IOException e) {
            LOG.log(Level.INFO, "SNMP GetNext IOException: ", e);
        }
        return ret;
    }

    /**
     * The command snmpwalk uses the SNMP GETNEXT request to query a network for 
     * a tree of information. An object identifier (OID) may be given. This OID 
     * specifies which portion of the object identifier space will be searched 
     * using GETNEXT requests. All variables in the subtree below the given OID 
     * are queried and their values presented to the user. If no OID argument is 
     * present, snmpwalk will search the subtree rooted at SNMPv2-SMI::mib-2 
     * (including any MIB object values from other MIB modules, that are defined 
     * as lying within this subtree).
     * 
     * @param address
     * @param comm
     * @param rootOID
     * @return retrieve a subtree of management values using SNMP GETNEXT 
     * requests.
     */
    protected List<VariableBinding> walk(Address address, String comm, OID rootOID) {
        List<VariableBinding> ret = new ArrayList<>();

        PDU requestPDU = new PDU();
        requestPDU.add(new VariableBinding(rootOID));
        requestPDU.setType(PDU.GETBULK);
        // Maximum oid per pdu request
        requestPDU.setMaxRepetitions(5);

        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(comm));
        target.setAddress(address);
        target.setVersion(SnmpConstants.version2c);

        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            boolean finished = false;
            // int iter = 0;

            while (!finished) {
                VariableBinding vb = null;

                ResponseEvent respEvt = snmp.send(requestPDU, target);
                //Logger.getLogger(SnmpV2cUtils.class.getName()).log(Level.INFO, "GETBULK iteration number " + iter++);
                PDU responsePDU = respEvt.getResponse();

                if (responsePDU != null) {
                    if (responsePDU.getVariableBindings() != null && responsePDU.getVariableBindings().size() > 0) {
                        for (int i = 0; i < responsePDU.getVariableBindings().size(); i++) {
                            // vb sanity check
                            vb = (VariableBinding) responsePDU.getVariableBindings().get(i);
                            if (vb.getOid() == null) {
                                LOG.log(Level.INFO, "SNMP Event: vb.getOid() == null");
                                finished = true;
                                break;
                            } else if (vb.getOid().size() < rootOID.size()) {
                                LOG.log(Level.INFO, "SNMP Event: vb.getOid().size() < targetOID.size()");
                                finished = true;
                                break;
                            } else if (rootOID.leftMostCompare(rootOID.size(), vb.getOid()) != 0) {
                                LOG.log(Level.INFO, "SNMP Event: targetOID.leftMostCompare() != 0)");
                                finished = true;
                                break;
                            } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
                                LOG.log(Level.INFO, "SNMP Event: Null.isExceptionSyntax(vb.getVariable().getSyntax())");
                                finished = true;
                                break;
                            } else if (vb.getOid().compareTo(rootOID) <= 0) {
                                LOG.log(Level.INFO, "SNMP Event: Variable received is not "
                                        + "lexicographic successor of requested "
                                        + "one:");
                                LOG.log(Level.INFO, "SNMP Event: {0} <= {1}", new Object[]{vb.toString(), rootOID});
                                finished = true;
                                break;
                            }
                            ret.add(vb);
                        }
                    }
                }

                if (!finished) {
                    if (responsePDU == null) {
                        LOG.log(Level.INFO, "SNMP Event: responsePDU == null");
                        finished = true;
                    } else if (responsePDU.getErrorStatus() != 0) {
                        LOG.log(Level.INFO, "SNMP Event: responsePDU.getErrorStatus() != 0");
                        LOG.log(Level.INFO, "SNMP Event: ", responsePDU.getErrorStatusText());
                        finished = true;
                    } else {
                        // Set up the variable binding for the next entry.
                        requestPDU.setRequestID(new Integer32(0));
                        requestPDU.set(0, vb);
                    }
                }
            }
            snmp.close();
        } catch (IOException e) {
            LOG.log(Level.INFO, "SNMP Walk IOException: ", e);
        }
        return ret;
    }
}
