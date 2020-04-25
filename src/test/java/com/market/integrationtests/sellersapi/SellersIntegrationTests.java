package com.market.integrationtests.sellersapi;

import io.restassured.response.Response;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.market.integrationtests.helpers.TestData.invalidSellerJson;
import static com.market.integrationtests.helpers.TestData.validSellerJson;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.*;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class SellersIntegrationTests {
    private static String host;
    private static String url;
    private static String sellerId;
    private static boolean setUpIsDone = false;

    @Value("${host}")
    public void setHost(String pHost) {
        host = pHost;
    }

    @Before
    public void setUp() {
        if(setUpIsDone) return;
        url = host + "/market-place-sellers/marketplace/api/v1/sellers";
        setUpIsDone = true;
    }

    @Test
    public void a_WhenGetAllSellersWithNothingInDb_ThenNotFound() {
        given().contentType("application/json")
                .when()
                .request("GET", url)
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void b_WhenGetNonExistentSeller_ThenNotFound() {
        given().contentType("application/json")
                .when()
                .request("GET", url + "/test_seller_id")
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void c_WhenPostInvalidSeller_ThenBadRequest() {
        with().body(invalidSellerJson())
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url)
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void d_WhenEditInvalidNonExistentSeller_ThenNotFound() {
        with().body(validSellerJson(null))
                .given()
                .contentType("application/json")
                .when()
                .request("PUT", url)
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void e_WhenPostValidSeller_ThenOk() {
        Response response = with().body(validSellerJson(null))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url);

        sellerId = response.getBody().jsonPath().get("id");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    public void f_WhenEditValidSeller_ThenNoContent() {
        with().body(validSellerJson(sellerId))
                .given()
                .contentType("application/json")
                .when()
                .request("PUT", url)
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void g_WhenDeleteNonExistentSeller_ThenNoContent() {
        given().contentType("application/json")
                .when()
                .request("DELETE", url + "/test_seller_id")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void h_WhenDeleteValidSeller_ThenNoContent() {
        given().contentType("application/json")
                .when()
                .request("DELETE", url + "/" + sellerId)
                .then()
                .statusCode(NO_CONTENT.value());
    }
}
