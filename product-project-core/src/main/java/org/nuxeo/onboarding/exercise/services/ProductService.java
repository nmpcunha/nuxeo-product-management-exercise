package org.nuxeo.onboarding.exercise.services;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;

public interface ProductService {

    Double computePrice(NxProductAdapter product) throws NuxeoException;

    ProductPricingDescriptor getPricingDescriptor(String pricingDescriptorIdentifier);

}
