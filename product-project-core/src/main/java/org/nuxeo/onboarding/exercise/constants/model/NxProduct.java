package org.nuxeo.onboarding.exercise.constants.model;

import org.nuxeo.onboarding.exercise.constants.ProductSchemas;

import java.util.Arrays;

public enum NxProduct {

    AVAILABLE_IMMEDIATELY("available_immediately"),
    CATEGORY("category"),
    DELIVERY_TIME("delivery_time"),
    DESCRIPTION("description"),
    DISTRIBUTOR("distributor"),
    DISTRIBUTOR_NAME("name"),
    DISTRIBUTOR_SELL_LOCATION("sell_location"),
    NAME("name"),
    ORIGIN_OF_FABRICATION("origin_of_fabrication"),
    PRICE("price"),
    PRODUCT_ID("product_id"),
    SIZE("size");

    private String propertyKey;

    NxProduct(String propertyKey){
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey(){
        return propertyKey;
    }

    public String getPropertyXPath() {
        return String.join(":", Arrays.asList(ProductSchemas.PRODUCT.getName(), propertyKey));
    }

}
