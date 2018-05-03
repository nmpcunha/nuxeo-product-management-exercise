package org.nuxeo.onboarding.exercise.constants;

public enum ProductSchemas {

    DUBLIN_CORE("dublincore"),
    FILE("file"),
    PRODUCT("product");

    private String name;

    ProductSchemas(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
