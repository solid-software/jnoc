/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.services;

import java.util.Map;

/**
 * This class contains executable filter methods. In contrast to using SHIRO or
 * Servlet filters that map to URLs, this class provides data filters that can
 * be explicitly called and injected.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Aug 5, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */

// TODO: See http://www.byteslounge.com/tutorials/java-ee-cdi-programmatic-dependency-disabiguation-example-injection-point-inspection

public interface AttributeFilter {
    
    /**
     * Data tables have the option of implemented optional filters to help
     * reduce the amount of data returned from the original search. The filters
     * are stored in the database in JSON format.
     *
     * Example JSON filter: {"status":["OPEN","SOLVED"],"assignee":["UID"]}
     *
     * Additionally the unique place holder 'UID' in the filters is replaced
     * with the current users UID. (Authenticated via SHIRO)
     *
     * @param filterId The database index of the filter
     * @return An OOR representation of the JSON filter.
     */
//    public Map optionalTableFilter(String filterId);
    
    /**
     * Determines all the companies that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @param includeOwnCompany If true the users own company is included in
     * the list of authorized companies.
     * @return a complete Map of companies that the current user is
     * authorized to access. The map key is "company" and the value is a list of
     * company ID's
     */
    public Map authorizedCompanies(
            boolean includeOwnCompany);
    
    /**
     * Determines all the DAS that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of sites that the current user is authorized to 
     * access. The map key is "das" and the value is a list of DAS ID's
     */
    public Map authorizedDAS();
    
    /**
     * Determines all the users that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized 
     * to access. The map key is "author" and the value is a list of user 
     * UID's
     */
    public Map authorizedAuthors();
    
}
