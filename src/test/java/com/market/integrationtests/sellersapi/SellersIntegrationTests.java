package com.market.integrationtests.sellersapi;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.with;
import static org.springframework.http.HttpStatus.OK;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class SellersIntegrationTests {
    @Value("${host}")
    private String host;
    private String url;
    private String sellerId;

    @Before
    public void setUp() {
        this.url = this.host + "/market-place-products/marketplace/api/v1/sellers";
    }

    @Test
    public void whenGetProductsByExistentSeller_ThenOk() {
//        String sellerUrl = this.host + "/market-place-sellers/marketplace/api/v1/sellers";
//
//        with().body(this.validProductJson())
//                .given()
//                .contentType("application/json")
//                .when()
//                .request("GET", sellerUrl + "/" + this.sellerId)
//                .then()
//                .statusCode(OK.value());
    }
}
