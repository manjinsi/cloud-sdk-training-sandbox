package com.capgemini.sandbox.controllers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

import io.vavr.control.Try;

@RestController
@RequestMapping("/api/business-partners")
public class BusinessPartnerController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerController.class);

    private static final String CATEGORY_PERSON = "1";

    private BusinessPartnerService service = new DefaultBusinessPartnerService();

    @GetMapping
    public List<BusinessPartner> getBusinessPartners() throws Exception {
        logger.info("Retrieving all business partners");
        List<BusinessPartner> partners = new ArrayList<>();
        
        // TODO BusinessPartner Task 1 - Retrieve a list of business partners

        // TODO BusinessPartner Task 2 - Add caching support to the call. Cache the data for 5 minutes.
        // Hint: com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator might help 
        
        // TODO BusinessPartner Task 3 - Add a circuit breaker with a 30 seconds duration

        // TODO BusinessPartner Task 4 - Select just the id, first and last name of the business partners, filter them by category = '1' and order them by last name ascending
       
        return partners;
    }

    @GetMapping(params ="id")
    public BusinessPartner getBusinessPartners(@Valid  @Size(max = 10) @RequestParam("id") String id) throws IllegalArgumentException, ODataException {

        if (!validateInput(id)) {
            logger.warn("Invalid request to retrieve a business partner, id: {}.", id);
        }
        logger.info("Retrieving business partner with id {}", id);
        
        BusinessPartner partner = null; 
        
        // TODO BusinessPartner Task 5 - Read one business partner by key and corresponding addresses

        return partner;
    }

    private boolean validateInput(String id) {
        return !Strings.isNullOrEmpty(id) && id.length() <= 10;
    }

}
