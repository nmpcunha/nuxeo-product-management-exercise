package org.nuxeo.onboarding.exercise.constants;

public enum ProductDocumentTypes {

    PRODUCT("nxProduct"),
    VISUAL("nxVisual");

    private String name;

    ProductDocumentTypes(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
