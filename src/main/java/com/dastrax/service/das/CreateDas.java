/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.das;

import com.dastrax.per.entity.DAS;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Create DAS CDI bean. Provides methods for creating DAS
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class CreateDas implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(CreateDas.class.getName());
    private static final long serialVersionUID = 1L;
    
    private DAS das;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateDas() {        
        this.das = new DAS();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of das
     *
     * @return the value of das
     */
    public DAS getDas() {
        return das;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of das.
     *
     * @param das new value of das
     */
    public void setDas(DAS das) {
        this.das = das;
    }

//</editor-fold>

}
