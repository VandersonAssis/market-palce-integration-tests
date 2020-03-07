package com.market.integrationtests.productsapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.market.integrationtests.helpers.TestData.invalidProductJson;
import static com.market.integrationtests.helpers.TestData.validProductJson;
import static com.market.integrationtests.helpers.TestDataInitializer.deleteTestSeller;
import static com.market.integrationtests.helpers.TestDataInitializer.postTestSeller;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.*;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class ProductsFluxIntegrationTests {
    private static String host;

    private static String url;
    private static String sellerId;
    private static String productId;
    private static boolean setUpIsDone = false;

    @Value("${host}")
    public void setHost(String pHost) {
        host = pHost;
    }

    @Before
    public void setUp() {
        if(setUpIsDone) return;
        url = host + "/market-place-products/marketplace/api/v1/products";
        sellerId = postTestSeller(host);

        setUpIsDone = true;
    }

    @AfterClass
    public static void finish() {
        deleteTestSeller(host);
    }

    @Test
    public void a_WhenEditNonExistent_ThenNotFound() {
        with().body(validProductJson(sellerId, "product_test_id"))
                .given()
                .contentType("application/json")
                .when()
                .request("PUT", url)
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void b_WhenPostValidProduct_ThenCreated() {
        Response response = with().body(validProductJson(sellerId, null))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url);

        productId = response.getBody().jsonPath().get("id");
        assertEquals(CREATED.value(), response.getStatusCode());
    }

    @Test
    public void c_WhenPostInvalidProduct_ThenBadRequest() {
        with().body(invalidProductJson(sellerId))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url)
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void d_WhenPostSameProduct_ThenConflict() {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(validProductJson(sellerId, productId), JsonObject.class);
        jsonObject.remove("id");
        String jsonWithoutId = gson.toJson(jsonObject);

        Response response = with().body(jsonWithoutId)
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url);

        assertEquals(CONFLICT.value(), response.getStatusCode());
    }

    @Test
    public void e_WhenEdit_ThenNoContent() {
        with().body(validProductJson(sellerId, productId))
            .given()
            .contentType("application/json")
            .when()
            .request("PUT", url)
            .then()
            .statusCode(NO_CONTENT.value());
    }

    @Test
    public void f_WhenGetProductsByNonExistentSeller_ThenNotFound() {
        given().contentType("application/json")
            .when()
            .request("GET", url + "/invalidid/seller")
            .then()
            .statusCode(NOT_FOUND.value());
    }

    @Test
    public void g_WhenGetProductsByExistentSeller_ThenOk() {
        given()
            .contentType("application/json")
            .when()
            .request("GET", url + "/" + sellerId + "/seller")
            .then()
            .statusCode(OK.value());
    }

    @Test
    public void h_WhenDeleteNonExistentProduct_ThenNoContent() {
        given()
            .contentType("application/json")
            .when()
            .request("DELETE", url + "/" + productId + "test")
            .then()
            .statusCode(NO_CONTENT.value());
    }

    @Test
    public void i_WhenDeleteExistentProduct_ThenNoContent() {
        given()
            .contentType("text/plain")
            .when()
            .request("DELETE", url + "/" + productId)
            .then()
            .statusCode(NO_CONTENT.value());
    }
}
