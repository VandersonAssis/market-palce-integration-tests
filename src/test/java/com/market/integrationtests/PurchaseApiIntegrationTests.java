package com.market.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@RunWith(SpringRunner.class)
public class PurchaseApiIntegrationTests {

    @Value("${host}")
    private String host;
    private String url;

    @Before
    public void setUp() {
        this.url = this.host + "/market-place-purchase/marketplace/api/v1/purchase";
    }

    @Test
    public void whenPurchaseStartWithValidPayload_ThenAccepted() {
        with().body("{\"idProduct\": \"5e54179b8afd307ee0aac998\", \"quantity\": 1}")
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url + "/start")
                .then()
                .statusCode(ACCEPTED.value());
    }

    @Test
    public void whenPurchaseStartWithValidPayload_ThenBodyIsEmpty() {
        String response = with().body("{\"idProduct\": \"5e54179b8afd307ee0aac998\", \"quantity\": 1}")
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url + "/start")
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
                .request("POST", this.url + "/start")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void whenPurchaseStartWithZeroQuantityPayload_ThenBadRequest() {
        with().body("{\"idProduct\": \"5e54179b8afd307ee0aac998\", \"quantity\": 0}")
            .given()
            .contentType("application/json")
            .when()
            .request("POST", this.url + "/start")
            .then()
            .statusCode(BAD_REQUEST.value());
    }
}