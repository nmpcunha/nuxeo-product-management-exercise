package org.nuxeo.onboarding.exercise.events;

public enum ProductEvents {

    DEPRECATED("product.deprecated");

    private String identifier;

    ProductEvents(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
