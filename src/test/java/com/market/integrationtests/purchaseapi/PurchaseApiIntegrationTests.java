package com.market.integrationtests.purchaseapi;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.market.integrationtests.helpers.TestDataInitializer.*;
import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@RunWith(SpringRunner.class)
public class PurchaseApiIntegrationTests {

    private static String host;
    private static String url;
    private static boolean setUpIsDone = false;
    private static String productId;

    @Value("${host}")
    public void setHost(String pHost) {
        host = pHost;
    }

    @Before
    public void setUp() {
        if(setUpIsDone) return;
        url = host + "/market-place-purchase/marketplace/api/v1/purchase";
        String sellerId = postTestSeller(host);
        productId = postTestProduct(host, sellerId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void finish() {
        deleteTestSeller(host);
    }

    @Test
    public void whenPurchaseStartWithValidPayload_ThenAccepted() {
        with().body("{\"idProduct\": \"" + productId + "\", \"quantity\": 1}")
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/start")
                .then()
                .statusCode(ACCEPTED.value());
    }

    @Test
    public void whenPurchaseStartWithValidPayload_ThenBodyIsEmpty() {
        String response = with().body("{\"idProduct\": \"" + productId + "\", \"quantity\": 1}")
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/start")
                .andReturn()
                .body()
                .asString();

        assertTrue(response.isEmpty());
    }

    @Test
    public void whenPurchaseStartWithInvalidPayload_ThenBadRequest() {
        with().body("{\"invalidName\": \"5e54179b8afd307ee0aac998\", \"invalidName\": 1}")
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/start")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void whenPurchaseStartWithZeroQuantityPayload_ThenBadRequest() {
        with().body("{\"idProduct\": \"" + productId + "\", \"quantity\": 0}")
            .given()
            .contentType("application/json")
            .when()
            .request("POST", url + "/start")
            .then()
            .statusCode(BAD_REQUEST.value());
    }
}