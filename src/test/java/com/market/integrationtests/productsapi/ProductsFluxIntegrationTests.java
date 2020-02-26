package com.market.integrationtests.productsapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.with;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.*;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class ProductsFluxIntegrationTests {
    @Value("${host}")
    private String host;
    private static String classHost;
    private static String url;
    private static String productId;
    private static String sellerId;

    private static boolean setUpIsDone = false;

    @Before
    public void setUp() {
        if(setUpIsDone) return;

        classHost = this.host;
        url = host + "/market-place-products/marketplace/api/v1/products";
        productId = "5e540ff31963c50510c20d7f";
        sellerId = this.postTestSeller();

        setUpIsDone = true;
    }

    @AfterClass
    public static void finish() {
        deleteTestSeller();
    }

    @Test
    public void a_WhenEditNonExistent_ThenNotFound() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("PUT", this.url)
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void b_WhenPostValidProduct_ThenCreated() {
        Response response = with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url);

        assertEquals(CREATED.value(), response.getStatusCode());
    }

    @Test
    public void c_WhenPostInvalidProduct_ThenBadRequest() {
        with().body(this.invalidProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url)
                .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    public void d_WhenPostSameProduct_ThenConflict() {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(this.validProductJson(), JsonObject.class);
        jsonObject.remove("id");
        String jsonWithoutId = gson.toJson(jsonObject);

        Response response = with().body(jsonWithoutId)
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url);

        assertEquals(CONFLICT.value(), response.getStatusCode());
    }

    @Test
    public void e_WhenEdit_ThenNoContent() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("PUT", this.url)
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void f_WhenGetProductsByNonExistentSeller_ThenNotFound() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("GET", this.url + "/invalidid/seller")
                .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    public void g_WhenGetProductsByExistentSeller_ThenOk() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("GET", this.url + "/" + this.sellerId + "/seller")
                .then()
                .statusCode(OK.value());
    }

    @Test
    public void h_WhenDeleteNonExistentProduct_ThenNoContent() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("DELETE", this.url + "/" + this.productId + "test")
                .then()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void i_WhenDeleteExistentProduct_ThenNoContent() {
        with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("DELETE", this.url + "/" + this.productId)
                .then()
                .statusCode(NO_CONTENT.value());
    }

    private String postTestSeller() {
        String sellerUrl = this.host + "/market-place-sellers/marketplace/api/v1/sellers";

        return with().body(validSellerJson())
                .given().contentType("application/json").when()
                .request("POST", sellerUrl)
                .getBody()
                .jsonPath()
                .get("id");
    }

    private static void deleteTestSeller() {
        String sellerUrl = classHost + "/market-place-sellers/marketplace/api/v1/sellers";

        with().body(validSellerJson())
            .given().contentType("application/json").when()
            .request("DELETE", sellerUrl + "/" + sellerId);
    }

    private static String validSellerJson() {
        return "{\n" +
                "  \"name\": \"Test seller\",\n" +
                "  \"cnae\": 5\n" +
                "}";
    }

    private String invalidProductJson() {
        return "{" +
                "\"id\": \" " + this.productId + "\",\n" +
                "\"idSeller\": \"" + this.sellerId + "\",\n" +
                "\"model\": \"Test Model\",\n" +
                "\"invalidProperty\": \"Test Water Cooler Corsair Hydro Series6\",\n" +
                "\"description\": \"Test Play your favorite games for long period of time without having to stop!\",\n" +
                "\"price\": 110.12,\n" +
                "\"quantity\": 99\n" +
                "}";
    }

    private String validProductJson() {
        return "{" +
                    "\"id\": \"" + this.productId + "\",\n" +
                    "\"idSeller\": \"" + this.sellerId + "\",\n" +
                    "\"model\": \"Test Model\",\n" +
                    "\"name\": \"Test Water Cooler Corsair Hydro Series6\",\n" +
                    "\"description\": \"Test Play your favorite games for long period of time without having to stop!\",\n" +
                    "\"price\": 110.12,\n" +
                    "\"quantity\": 99\n" +
                "}";
    }
}
