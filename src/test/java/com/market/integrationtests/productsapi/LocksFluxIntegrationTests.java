package com.market.integrationtests.productsapi;

import io.restassured.response.Response;
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
public class LocksFluxIntegrationTests {
    @Value("${host}")
    private String host;
    private String url;
    private String productId;
    private String lockId;

    @Before
    public void setUp() {
        this.url = this.host + "/market-place-products/marketplace/api/v1/products";
        this.productId = "5e540ff31963c50510c20d7f";
        this.lockId = "5e540ff31963c50510c20d8l";

        this.postTestProduct();
    }

    @Test
    public void whenPostWithValidLock_ThenOk() {
        with().body(this.validLockJson())
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url + "/lock")
                .then()
                .statusCode(OK.value());
    }

    private void postTestProduct() {
        Response test = with().body(this.validProductJson())
                .given()
                .contentType("application/json")
                .when()
                .request("POST", this.url);

        System.out.println(test);
    }

    private String validLockJson() {
        return "{\n" +
                "  \"idProduct\": \"" + this.productId + "\",\n" +
                "  \"quantity\": 1\n" +
                "}";
    }

    private String validProductJson() {
        return "{" +
                "\"id\": \"" + this.productId + "\",\n" +
                "\"idSeller\": \"5e540ff31963c50510c20c6f\",\n" +
                "\"model\": \"Test Model\",\n" +
                "\"name\": \"Test Water Cooler Corsair Hydro Series6\",\n" +
                "\"description\": \"Test Play your favorite games for long period of time without having to stop!\",\n" +
                "\"price\": 110.12,\n" +
                "\"quantity\": 99\n" +
                "}";
    }
}
