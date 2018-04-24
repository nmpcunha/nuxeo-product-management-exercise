package org.nuxeo.onboarding.exercise.rest;

import org.nuxeo.ecm.webengine.app.WebEngineModule;

import java.util.HashSet;
import java.util.Set;

public class ProductApplication extends WebEngineModule {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> result = new HashSet<>();
        result.add(ProductController.class);
        return result;
    }
}