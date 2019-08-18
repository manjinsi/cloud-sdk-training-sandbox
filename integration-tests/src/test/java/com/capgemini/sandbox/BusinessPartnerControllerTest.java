package com.capgemini.sandbox;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.testutil.MockUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
public class BusinessPartnerControllerTest {

    public static final String BUPA_ID = "1003764";

    private static final MockUtil mockUtil = new MockUtil();

    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void beforeClass() {
        mockUtil.mockDefaults();
        mockUtil.mockDestination("ErpQueryEndpoint", "S4HANA");
    }

    @Test
    public void testGetAll() {
        new ThreadContextExecutor().execute(() -> mvc.perform(
                get("/api/business-partners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").value(hasSize(greaterThan(1))))
                .andExpect(jsonPath("$[0].BusinessPartner").value(not(isEmptyOrNullString()))));
    }

    @Test
    public void testGetSingle() {
    	// TODO: BusinessPartner Test Task 1 - Test reading single business partner via REST
    }
}
