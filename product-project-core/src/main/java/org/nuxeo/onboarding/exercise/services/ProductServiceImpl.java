/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      nuno
 */

package org.nuxeo.onboarding.exercise.services;

import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;
import org.nuxeo.onboarding.exercise.extensions.registries.ProductPricingRegistry;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class ProductServiceImpl extends DefaultComponent implements ProductService {

    private static final String INVALID_CONTRIBUTION_MISSING_COUNTRY = "An Invalid Product Pricing Contribution arrived due to missing country: %s";

    private static final String INVALID_CONTRIBUTION_MISSING_TAXES = "An Invalid Product Pricing Contribution arrived due to missing taxes: %s";

    private static final String NULL_DOCUMENT_ADAPTER = "A NULL Document Adapter was received. This means that the original Document Model's type was not '%s'";

    private ProductPricingRegistry registry;

    /**
     * Component activated notification. Called when the component is activated. All component dependencies are resolved
     * at that moment. Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        registry = new ProductPricingRegistry();
        super.activate(context);
    }

    /**
     * Component deactivated notification. Called before a component is unregistered. Use this method to do cleanup if
     * any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        registry = null;
        super.deactivate(context);
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        ProductPricingDescriptor descriptor = convertContribution(contribution);
        registry.addContribution(descriptor);
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        ProductPricingDescriptor descriptor = convertContribution(contribution);
        registry.removeContribution(descriptor);
    }

    private ProductPricingDescriptor convertContribution(Object contribution) {
        ProductPricingDescriptor pricing = (ProductPricingDescriptor) contribution;
        if (pricing.getCountry() == null) {
            throw new NuxeoException(String.format(INVALID_CONTRIBUTION_MISSING_COUNTRY, pricing.toString()));
        } else if (pricing.getTaxes() == null || pricing.getTaxes().values().contains(null)) {
            throw new NuxeoException(String.format(INVALID_CONTRIBUTION_MISSING_TAXES, pricing.toString()));
        }
        return pricing;
    }

    @Override
    public Double computePrice(NxProductAdapter product) throws NuxeoException {
        if (product == null) {
            throw new NuxeoException(String.format(NULL_DOCUMENT_ADAPTER, ProductDocumentTypes.PRODUCT.getName()));
        }

        String country = product.getOriginOfFabrication();
        Double price = product.getPrice();

        ProductPricingDescriptor countryPricing = registry.getDescriptorByCountry(country);

        if (countryPricing != null) {
            return getPriceWithTaxes(price, countryPricing.getTaxes());
        }
        return price;
    }

    private Double getPriceWithTaxes(Double price, Map<String, Double> taxes) {
        Double taxAmount = 0.0;
        for (String tax : taxes.keySet()) {
            taxAmount += (price * taxes.get(tax));
        }
        return price + taxAmount;
    }
}
