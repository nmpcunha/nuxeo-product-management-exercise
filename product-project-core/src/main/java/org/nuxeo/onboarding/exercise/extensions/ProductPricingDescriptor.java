package org.nuxeo.onboarding.exercise.extensions;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XMap descriptor for the contribution of a new {@link org.nuxeo.onboarding.exercise.services.ProductService}.
 */
@XObject("productPricing")
public class ProductPricingDescriptor {

    @XNode("@id")
    private String productPricingId;

    @XNodeMap(value = "taxes/tax", key = "@name", type = HashMap.class, componentType = Double.class)
    private Map<String, Double> taxes;

    @XNodeList(value = "country", type = ArrayList.class, componentType = String.class)
    private List<String> countries;

    public ProductPricingDescriptor() {
        taxes = new HashMap<>();
        countries = new ArrayList<>();
    }

    public String getProductPricingIdentifier() {
        return productPricingId;
    }

    public Map<String, Double> getTaxes() {
        return taxes;
    }

    public List<String> getCountries() {
        return countries;
    }

    @Override
    public String toString() {
        return "ProductPricingDescriptor{" +
                "productPricingId='" + productPricingId + '\'' +
                ", taxes=" + taxes +
                ", countries=" + countries +
                '}';
    }
}
