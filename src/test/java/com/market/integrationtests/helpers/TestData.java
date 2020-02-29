package com.market.integrationtests.helpers;

public abstract class TestData {
    public static String validLockJson(String productId, int quantity) {
        return "{\n" +
                "  \"idProduct\": \"" + productId + "\",\n" +
                "  \"quantity\": " + quantity + "\n" +
                "}";
    }

    public static String validProductJson(String sellerId, String productId) {
        return "{" +
                "\"id\": \"" + productId + "\",\n" +
                "\"idSeller\": \"" + sellerId + "\",\n" +
                "\"model\": \"Test Model\",\n" +
                "\"name\": \"Test Water Cooler Corsair Hydro Series6\",\n" +
                "\"description\": \"Test Play your favorite games for long period of time without having to stop!\",\n" +
                "\"price\": 110.12,\n" +
                "\"quantity\": 99\n" +
                "}";
    }

    public static String validSellerJson() {
        return "{\n" +
                "  \"name\": \"Test seller\",\n" +
                "  \"cnae\": 5\n" +
                "}";
    }

    public static String invalidProductJson(String sellerId) {
        return "{" +
                "\"idSeller\": \"" + sellerId + "\",\n" +
                "\"model\": \"Test Model\",\n" +
                "\"invalidProperty\": \"Test Water Cooler Corsair Hydro Series6\",\n" +
                "\"description\": \"Test Play your favorite games for long period of time without having to stop!\",\n" +
                "\"price\": 110.12,\n" +
                "\"quantity\": 99\n" +
                "}";
    }
}
