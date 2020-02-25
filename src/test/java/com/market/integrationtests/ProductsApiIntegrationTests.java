package com.market.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@TestPropertySource("classpath:market-place-integrationtests-dev.properties")
@RunWith(SpringRunner.class)
public class ProductsApiIntegrationTests {
    @Value("${host}")
    private String host;
    private String url;

    @Before
    public void setUp() {
        this.url = this.host + "/market-place-products/marketplace/api/v1/products";
    }

    @Test
    public void whenPostValidProduct_ThenCreated() {

    }
}
