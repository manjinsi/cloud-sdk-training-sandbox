package com.capgemini.sandbox.controllers;

import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private BusinessPartnerService service = new DefaultBusinessPartnerService();

    @PostMapping
    public ResponseEntity<Object> createAddress(@RequestBody BusinessPartnerAddress address) throws Exception {
        if (!validateInputForCreate(address)) {
            logger.warn("Invalid request to create an address: {}.", address);
            return ResponseEntity.badRequest().body("Bad request: business partner of address needs to be specified.");
        }

        logger.info("Received post request to create address {}", address);

        Destination destination = DestinationAccessor.getDestination("ErpQueryEndpoint");
        BusinessPartnerAddress addressCreated = service.createBusinessPartnerAddress(address)
                .execute(destination.asHttp());
        return ResponseEntity.status(HttpStatus.CREATED).body(addressCreated);
    }

    @PatchMapping
    public ResponseEntity<Object> updateAddress(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final String businessPartnerId = request.getParameter("businessPartnerId");
        final String addressId = request.getParameter("addressId");
        if (!validateIds(businessPartnerId, addressId)) {
            logger.warn("Invalid request to update: at least one mandatory parameter was invalid, query was: {}",
                    request.getQueryString());
            return ResponseEntity.badRequest()
                    .body("Please provide valid values for query parameters businessPartnerId and addressId");
        }

        BusinessPartnerAddress addressFromBody = null;

        final String body = CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8));
        logger.info("Converting body {} to address", body);
//            addressFromBody = new Gson().fromJson(body, BusinessPartnerAddress.class);

        if (!validateInputForUpdate(addressFromBody, businessPartnerId, addressId)) {
            logger.warn("Invalid request to update: at least one mismatch between body and query, query was: {}"
                    + "; and parsed body was: {}.", request.getQueryString(), addressFromBody);
            return ResponseEntity.badRequest().body("Address in body must have none or matching identifiers.");
        }

        final BusinessPartnerAddress addressToUpdate = createAddressToUpdate(businessPartnerId, addressId,
                addressFromBody);

        logger.info("Received patch request to update address {}", addressToUpdate);
        try {

//          //TODO: Task 3 - Implement business partner update query
            Destination destination = DestinationAccessor.getDestination("ErpQueryEndpoint");
            service.updateBusinessPartnerAddress(addressToUpdate).execute(destination.asHttp());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error while updating address {} in SAP S/4HANA.", addressToUpdate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format(
                    "Could not update address %s of business partner %s" + " due to error while calling SAP S/4HANA.",
                    addressId, businessPartnerId));
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAddress(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        final String businessPartnerId = request.getParameter("businessPartnerId");
        final String addressId = request.getParameter("addressId");

        if (!validateIds(businessPartnerId, addressId)) {
            logger.warn("Invalid request to delete: at least one mandatory parameter was invalid, query was: {}",
                    request.getQueryString());
            return ResponseEntity.badRequest()
                    .body("Please provide valid values for query parameters businessPartnerId and addressId");
        }

        logger.info("Received delete request to delete address {},{}", businessPartnerId, addressId);
        try {
            // TODO: Task 3 - Implement business partner address delete query
            Destination destination = DestinationAccessor.getDestination("ErpQueryEndpoint");

            BusinessPartnerAddress businessPartnerAddress = new BusinessPartnerAddress();
            businessPartnerAddress.setBusinessPartner(businessPartnerId);
            businessPartnerAddress.setAddressID(addressId);

            service.deleteBusinessPartnerAddress(businessPartnerAddress).execute(destination.asHttp());

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error while deleting address {} of business partner {} in SAP S/4HANA.", addressId,
                    businessPartnerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format(
                    "Could not delete address %s of business partner %s" + " due to error while calling SAP S/4HANA.",
                    addressId, businessPartnerId));
        }
    }

    private boolean validateInputForCreate(BusinessPartnerAddress addressToCreate) {
        return !Strings.isNullOrEmpty(addressToCreate.getBusinessPartner());
    }

    private BusinessPartnerAddress createAddressToUpdate(String businessPartnerId, String addressId,
            BusinessPartnerAddress addressFromBody) {
        final BusinessPartnerAddress addressToUpdate = BusinessPartnerAddress.builder()
                .businessPartner(businessPartnerId).addressID(addressId).build();
        // Only change properties for which non-null values have been provided
        if (addressFromBody.getStreetName() != null)
            addressToUpdate.setStreetName(addressFromBody.getStreetName());
        if (addressFromBody.getHouseNumber() != null)
            addressToUpdate.setHouseNumber(addressFromBody.getHouseNumber());
        if (addressFromBody.getCityName() != null)
            addressToUpdate.setCityName(addressFromBody.getCityName());
        if (addressFromBody.getPostalCode() != null)
            addressToUpdate.setPostalCode(addressFromBody.getPostalCode());
        if (addressFromBody.getCountry() != null)
            addressToUpdate.setCountry(addressFromBody.getCountry());
        return addressToUpdate;
    }

    private boolean validateInputForUpdate(BusinessPartnerAddress addressFromBody, String businessPartnerId,
            String addressId) {
        return (addressFromBody.getBusinessPartner() == null
                || addressFromBody.getBusinessPartner().equals(businessPartnerId))
                && (addressFromBody.getAddressID() == null || addressFromBody.getAddressID().equals(addressId));
    }

    private boolean validateIds(String businessPartnerId, String addressId) {
        return (!Strings.isNullOrEmpty(businessPartnerId) && businessPartnerId.length() <= 10)
                && (!Strings.isNullOrEmpty(addressId) && addressId.length() <= 10);
    }
}
