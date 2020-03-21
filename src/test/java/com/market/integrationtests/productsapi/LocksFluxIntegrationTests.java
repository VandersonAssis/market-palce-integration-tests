package com.market.integrationtests.productsapi;

import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.market.integrationtests.helpers.TestData.validLockJson;
import static com.market.integrationtests.helpers.TestDataInitializer.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.*;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class LocksFluxIntegrationTests {
    private static String host;
    private static String url;
    private static String lockId;
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
        String sellerId = postTestSeller(host);
        productId = postTestProduct(host, sellerId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void finish() {
        deleteTestProduct(host, productId);
        deleteTestSeller(host);
    }

    @Test
    public void a_WhenGetNonExistentLock_ThenNotFound() {
        given()
            .contentType("application/json")
            .when()
            .request("GET", url + "test_lock_id/lock")
            .then()
            .statusCode(NOT_FOUND.value());
    }

    @Test
    public void b_WhenPostWithValidLock_ThenOkWithValidBody() throws JSONException {
        Response response = with().body(validLockJson(productId, 1))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/lock");

        lockId = response.jsonPath().get("lockId");
        String json = response.getBody().asString();
        JSONAssert.assertEquals("{lockId: x, idProduct: x, quantity: x, orderStatus: x}",
                json,
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("lockId", new RegularExpressionValueMatcher<>("^[a-z0-9]*$")),
                        new Customization("idProduct", new RegularExpressionValueMatcher<>("^[a-z0-9]*$")),
                        new Customization("quantity", new RegularExpressionValueMatcher<>("^[0-9]*$")),
                        new Customization("orderStatus", new RegularExpressionValueMatcher<>("^[A-Z]*$"))));

        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    public void d_WhenPostLockWithLargerQuantityThanAvailable_ThenBadRequest() {
        with().body(validLockJson(productId, 1000))
            .given()
            .contentType("application/json")
            .when()
            .request("POST", url + "/lock")
            .then()
            .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void e_WhenPostLockWithZeroedQuantity_ThenBadRequest() {
        with().body(validLockJson(productId, 0))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/lock")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void f_WhenPostLockWithNegativeQuantity_ThenBadRequest() {
        with().body(validLockJson(productId, -10))
                .given()
                .contentType("application/json")
                .when()
                .request("POST", url + "/lock")
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void g_WhenGetExistentLock_ThenOk() {
        given().contentType("application/json")
                .when()
                .request("GET", url + "/" + lockId + "/lock")
                .then()
                .statusCode(OK.value());
    }

    @Test
    public void h_WhenGetValidLock_ThenValidBody() throws JSONException {
        Response response = given().contentType("application/json")
                .when()
                .request("GET", url + "/" + lockId + "/lock");

        String json = response.getBody().asString();

        JSONAssert.assertEquals("{lockId: x, idProduct: x, quantity: x, orderStatus: x}",
                json,
                new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization("lockId", new RegularExpressionValueMatcher<>("^[a-z0-9]*$")),
                        new Customization("idProduct", new RegularExpressionValueMatcher<>("^[a-z0-9]*$")),
                        new Customization("quantity", new RegularExpressionValueMatcher<>("^[0-9]*$")),
                        new Customization("orderStatus", new RegularExpressionValueMatcher<>("^[A-Z]*$"))));
    }

    @Test
    public void i_WhenUnlockValidLock_ThenOk() {
        with().body(lockId)
                .given()
                .contentType("text/plain")
                .when()
                .request("POST", url + "/unlock")
                .then()
                .statusCode(OK.value());
    }

    @Test
    public void j_WhenUnlockValidLock_ThenNotFound() {
        with().body("test_lock_id")
                .given()
                .contentType("text/plain")
                .when()
                .request("POST", url + "/unlock")
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void k_WhenDeleteExistentLock_ThenOk() {
        given().contentType("application/json")
                .when()
                .request("DELETE", url + "/" + lockId + "/lock")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void k_WhenDeleteNonExistentLock_ThenOk() {
        given().contentType("application/json")
                .when()
                .request("DELETE", url + "/test_lock_id/lock")
                .then()
                .statusCode(NO_CONTENT.value());
    }
}
