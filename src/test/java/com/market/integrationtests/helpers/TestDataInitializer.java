package com.market.integrationtests.helpers;

import static com.market.integrationtests.helpers.TestData.validProductJson;
import static com.market.integrationtests.helpers.TestData.validSellerJson;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;

public abstract class TestDataInitializer {
    private static String sellerId;

    public static String postTestSeller(String host) {
        String sellerUrl = host + "/market-place-sellers/marketplace/api/v1/sellers";

        sellerId = with().body(validSellerJson())
                .given().contentType("application/json")
                .when()
                .request("POST", sellerUrl)
                .getBody()
                .jsonPath()
                .get("id");

        return sellerId;
    }

    public static void deleteTestSeller(String host) {
        String sellerUrl = host + "/market-place-sellers/marketplace/api/v1/sellers";

        with().body(validSellerJson())
                .given().contentType("application/json").when()
                .request("DELETE", sellerUrl + "/" + sellerId);
    }

    public static String postTestProduct(String host, String sellerId) {
        String productUrl = host + "/market-place-products/marketplace/api/v1/products";

        return with().body(validProductJson(sellerId, null))
            .given().contentType("application/json")
            .when()
            .request("POST", productUrl)
            .getBody()
            .jsonPath()
            .get("id");
    }

    public static void deleteTestProduct(String host, String productId) {
        String productUrl = host + "/market-place-products/marketplace/api/v1/products";

        given()
            .contentType("application/json")
            .when()
            .request("DELETE", productUrl + "/" + productId);
    }
}
