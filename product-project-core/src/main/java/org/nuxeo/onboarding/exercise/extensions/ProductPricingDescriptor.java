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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

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
        return "ProductPricingDescriptor{" + "productPricingId='" + productPricingId + '\'' + ", taxes=" + taxes
                + ", countries=" + countries + '}';
    }
}
