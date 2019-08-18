package com.capgemini.sandbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;
import com.sap.cloud.sdk.testutil.MockUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AddressControllerTest {
    
    private static final MockUtil mockUtil = new MockUtil();
    
    public static final String BUPA_ID = "1003764";
    
    private static final String CREATE_BODY_TEMPLATE =
            "{\n" +
            "  \"BusinessPartner\": \"{bupaId}\",\n" +
            "  \"CityName\": \"Potsdam\",\n" +
            "  \"Country\": \"DE\",\n" +
            "  \"HouseNumber\": \"{houseNumber}\",\n" +
            "  \"PostalCode\": \"14469\",\n" +
            "  \"StreetName\": \"Konrad-Zuse-Ring\"\n" +
            "}";
    private static final String UPDATE_BODY_TEMPLATE =
            "{\n" +
            "  \"CityName\": \"Potsdam\",\n" +
            "  \"Country\": \"DE\",\n" +
            "  \"HouseNumber\": \"{houseNumber}\",\n" +
            "  \"PostalCode\": \"14469\",\n" +
            "  \"StreetName\": \"Konrad-Zuse-Ring\"\n" +
            "}";

    private static Destination mockDestination;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private MockMvc mvc;
    

    @BeforeClass
    public static void beforeClass() {
        mockUtil.mockDefaults();
        mockDestination = mockUtil.mockDestination("ErpQueryEndpoint", "S4HANA");
    }

    @Test
    public void testCreate() throws Exception {
        String houseNumber = String.valueOf(new Random().nextInt(100));

        String newAddressId = createAddress(houseNumber);

        // Verify newly created address can be retrieved
        BusinessPartnerAddress newBusinessPartnerAddress = getAddress(BUPA_ID, newAddressId);
        assertThat(newBusinessPartnerAddress.getHouseNumber()).isEqualTo(houseNumber);
    }

    /**
     * Creates a new address via the controller, validates response and
     * returns ID of new address.
     * @param houseNumber Value to set for property HouseNumber
     * @return Value of property AddressID of newly created instance
     */
    private String createAddress(String houseNumber) throws Exception{
    	
    	// TODO: Address Test Task 1 - Creates a new address via REST
        MvcResult result = new ThreadContextExecutor().execute(() -> mvc.perform(
                post("/api/addresses")
                    .content(CREATE_BODY_TEMPLATE.replace("{bupaId}", BUPA_ID).replace("{houseNumber}", houseNumber))
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.BusinessPartner").value(equalTo(BUPA_ID)))
                .andExpect(jsonPath("$.AddressID").value(not(isEmptyOrNullString())))
                .andReturn());
        
        String addressId = mapper.readValue(result.getResponse().getContentAsByteArray(), BusinessPartnerAddress.class).getAddressID();
        return addressId;
    }

    @Test
    public void testDelete() throws Exception {
        // Create address to delete afterwards
        String addressId = createAddress("10");
        
        // TODO: Address Test Task 2 - Delete a address via REST
        // Delete the address
        new ThreadContextExecutor().execute(() -> mvc.perform(
                delete("/api/addresses?businessPartnerId={bupaId}&addressId={addressId}", BUPA_ID, addressId))
                .andExpect(status().isNoContent()));
        
        // Verify just deleted address cannot be found anymore
        assertThat(getAddress(BUPA_ID, addressId)).isNull();
    }

    @Test
    public void testUpdate() throws Exception {
        // Create address to update
        String addressId = createAddress("10");
        
        // TODO: Address Test Task 3 - Update address via REST
        new ThreadContextExecutor().execute(() -> mvc.perform(
                patch("/api/addresses?businessPartnerId={bupaId}&addressId={addressId}", BUPA_ID, addressId)
                    .content(UPDATE_BODY_TEMPLATE.replace("{houseNumber}", "100"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent()));
        
        // Verify that address contains new value also when retrieved again
        BusinessPartnerAddress addressUpdated = getAddress(BUPA_ID, addressId);
        assertThat(addressUpdated.getHouseNumber()).isEqualTo("100");
    }

    private BusinessPartnerAddress getAddress(String bupaId, String addressId) {
        try {
            return new DefaultBusinessPartnerService().getBusinessPartnerAddressByKey(bupaId, addressId)
                    .execute(mockDestination.asHttp());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ODataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
