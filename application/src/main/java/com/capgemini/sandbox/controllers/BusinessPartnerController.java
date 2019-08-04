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
        
        Destination destination = DestinationAccessor.getDestination("ErpQueryEndpoint");
        
        // TODO: Task 1 - Retrieve a list of business partners
        partners = service.getAllBusinessPartner().execute(destination.asHttp());

        // TODO Task 2 - Add caching support to the call. Cache the data for 5 minutes.
        // Hint: com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator might help 
//        ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of(BusinessPartnerService.class)
//                .cacheConfiguration(CacheConfiguration.of(Duration.ofMinutes(5)).withoutParameters());
//        
//        partners = ResilienceDecorator.executeCallable(
//                () -> service.getAllBusinessPartner().execute(destination.asHttp()), resilienceConfiguration);
        
        // TODO Task 3 - Add a circuit breaker with a 30 seconds duration
//        ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of(BusinessPartnerService.class)
//                .cacheConfiguration(CacheConfiguration.of(Duration.ofMinutes(5)).withoutParameters())
//                .circuitBreakerConfiguration(CircuitBreakerConfiguration.of().waitDuration(Duration.ofSeconds(30)));
//
//        partners = ResilienceDecorator.executeCallable(
//                () -> service.getAllBusinessPartner().execute(destination.asHttp()), resilienceConfiguration);

        
        // TODO Task 4 - Select just the id, first and last name of the business partners, filter them by category = '1' and order them by last name ascending
//        ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of(BusinessPartnerService.class)
//                .cacheConfiguration(CacheConfiguration.of(Duration.ofMinutes(5)).withoutParameters())
//                .circuitBreakerConfiguration(CircuitBreakerConfiguration.of().waitDuration(Duration.ofSeconds(30)));
//
//        partners = ResilienceDecorator.executeCallable(
//                 () -> service.getAllBusinessPartner()
//                     .select(BusinessPartner.BUSINESS_PARTNER,
//                             BusinessPartner.LAST_NAME,
//                             BusinessPartner.FIRST_NAME)
//                     .filter(BusinessPartner.BUSINESS_PARTNER_CATEGORY.eq(CATEGORY_PERSON))
//                     .orderBy(BusinessPartner.LAST_NAME, Order.ASC)
//                     .execute(destination.asHttp()),
//                 resilienceConfiguration);
        
        // TODO Task 5 Remove the throws definition from the method and handle the exceptions with io.vavr.control.Try
//        partners = Try.of(
//                () -> service.getAllBusinessPartner()
//                    .select(BusinessPartner.BUSINESS_PARTNER,
//                            BusinessPartner.LAST_NAME,
//                            BusinessPartner.FIRST_NAME)
//                    .filter(BusinessPartner.BUSINESS_PARTNER_CATEGORY.eq(CATEGORY_PERSON))
//                    .orderBy(BusinessPartner.LAST_NAME, Order.ASC)
//                    .execute(destination.asHttp()))
//                .getOrElse(Collections.emptyList());
       
        return partners;
    }

    @GetMapping(params ="id")
    public BusinessPartner getBusinessPartners(@Valid  @Size(max = 10) @RequestParam("id") String id) throws IllegalArgumentException, ODataException {

        if (!validateInput(id)) {
            logger.warn("Invalid request to retrieve a business partner, id: {}.", id);
        }
        logger.info("Retrieving business partner with id {}", id);
        
        Destination destination = DestinationAccessor.getDestination("ErpQueryEndpoint");
        
        BusinessPartner partner = service.getBusinessPartnerByKey(id)
				                .select(BusinessPartner.BUSINESS_PARTNER, 
				                        BusinessPartner.LAST_NAME, 
				                        BusinessPartner.FIRST_NAME,
				                        BusinessPartner.IS_MALE, 
				                        BusinessPartner.IS_FEMALE, 
				                        BusinessPartner.CREATION_DATE,
				                        BusinessPartner.MIDDLE_NAME, 
				                        BusinessPartner.SEARCH_TERM1,
				                        BusinessPartner.TO_BUSINESS_PARTNER_ADDRESS.select(BusinessPartnerAddress.BUSINESS_PARTNER,
				                                BusinessPartnerAddress.ADDRESS_ID, 
				                                BusinessPartnerAddress.COUNTRY,
				                                BusinessPartnerAddress.POSTAL_CODE, 
				                                BusinessPartnerAddress.CITY_NAME,
				                                BusinessPartnerAddress.STREET_NAME, 
				                                BusinessPartnerAddress.HOUSE_NUMBER))
				                .execute(destination.asHttp());

        return partner;
    }

    private boolean validateInput(String id) {
        return !Strings.isNullOrEmpty(id) && id.length() <= 10;
    }

}
