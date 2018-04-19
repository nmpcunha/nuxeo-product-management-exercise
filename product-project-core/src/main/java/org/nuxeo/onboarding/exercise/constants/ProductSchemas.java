package org.nuxeo.onboarding.exercise.constants;

public enum ProductSchemas {

    PRODUCT("product");

    private String name;

    ProductSchemas(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
