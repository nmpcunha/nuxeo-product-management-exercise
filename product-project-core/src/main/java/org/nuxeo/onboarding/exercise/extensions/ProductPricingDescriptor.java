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

package org.nuxeo.onboarding.exercise.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * XMap descriptor for the contribution of a new {@link org.nuxeo.onboarding.exercise.services.ProductService}.
 */
@XObject("productPricing")
public class ProductPricingDescriptor {

    @XNode("country")
    private String country;

    @XNodeMap(value = "taxes/tax", key = "@name", type = HashMap.class, componentType = Double.class)
    private Map<String, Double> taxes;

    public ProductPricingDescriptor() {
        taxes = new HashMap<>();
    }

    public String getCountry() {
        return country;
    }

    public Map<String, Double> getTaxes() {
        return taxes;
    }

    @Override
    public String toString() {
        return "ProductPricingDescriptor{" + "country='" + country + '\'' + ", taxes=" + taxes.toString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductPricingDescriptor that = (ProductPricingDescriptor) o;
        return Objects.equals(country, that.country) && Objects.equals(taxes, that.taxes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, taxes);
    }

    public ProductPricingDescriptor clone() {
        ProductPricingDescriptor descriptor = new ProductPricingDescriptor();
        descriptor.country = country;
        descriptor.taxes = new HashMap<>(taxes);
        return descriptor;
    }

    public void merge(ProductPricingDescriptor other) {
        if (other.getTaxes() != null) {
            mergeTaxes(other.getTaxes());
        }
    }

    private void mergeTaxes(Map<String, Double> taxes) {
        taxes.keySet().forEach(tax -> this.taxes.put(tax, taxes.get(tax)));
    }
}
