package org.nuxeo.onboarding.exercise.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.extension.ProductPricingDescriptor;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.util.HashMap;
import java.util.Map;

public class ProductServiceImpl extends DefaultComponent implements ProductService {

    private static final Log log = LogFactory.getLog(ProductServiceImpl.class);

    private static final String NO_IDENTIFIER = "A Product Pricing Contribution arrived without identifier: %s";
    private static final String NULL_DOCUMENT_ADAPTER = "A NULL Document Adapter was received. This means that the original Document Model's type was not '%s'";

    private static final String CONTRIBUTION_LOADED = "The following contribution was loaded by Product Service: %s";
    private static final String CONTRIBUTION_UNLOADED = "The following contribution was unloaded by Product Service: %s";

    private Map<String, ProductPricingDescriptor> productPricing;

    /**
     * Component activated notification.
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        productPricing = new HashMap<>();
        super.activate(context);
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        productPricing = null;
        super.deactivate(context);
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        ProductPricingDescriptor pricing = convertContribution(contribution);
        productPricing.put(pricing.getProductPricingIdentifier(), pricing);
        log.debug(String.format(CONTRIBUTION_LOADED, pricing.toString()));
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        ProductPricingDescriptor pricing = convertContribution(contribution);
        productPricing.remove(pricing.getProductPricingIdentifier());
        log.debug(String.format(CONTRIBUTION_UNLOADED, pricing.toString()));
    }

    @NotNull
    private ProductPricingDescriptor convertContribution(Object contribution) {
        ProductPricingDescriptor pricing = (ProductPricingDescriptor) contribution;
        if (pricing.getProductPricingIdentifier() == null) {
            throw new NuxeoException(String.format(NO_IDENTIFIER, pricing.toString()));
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

        ProductPricingDescriptor countryPricing = findPricingDescriptorByCountry(country);

        if (countryPricing != null) {
            return getPriceWithTaxes(price, countryPricing.getTaxes());
        }
        return price;
    }

    @Override
    public ProductPricingDescriptor getPricingDescriptor(String pricingDescriptorIdentifier) {
        return productPricing.get(pricingDescriptorIdentifier);
    }

    private ProductPricingDescriptor findPricingDescriptorByCountry(String country) {
        for (Map.Entry<String, ProductPricingDescriptor> entry : productPricing.entrySet()) {
            if (entry.getValue().getCountries().contains(country)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Double getPriceWithTaxes(Double price, Map<String, Double> taxes) {
        Double taxAmount = 0.0;
        for (String tax : taxes.keySet()) {
            taxAmount += (price * taxes.get(tax));
        }
        return price + taxAmount;
    }
}
