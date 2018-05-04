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

package org.nuxeo.onboarding.exercise.extensions.registries;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.onboarding.exercise.constants.registry.RegistryOperations;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;

public class ProductPricingRegistry extends ContributionFragmentRegistry<ProductPricingDescriptor> {

    private static final Log log = LogFactory.getLog(ProductPricingRegistry.class);

    private static final String CONTRIBUTION_UPDATED = "The following contribution was %s by Product Pricing Registry: %s";

    private Map<String, ProductPricingDescriptor> productPricing = new HashMap<>();

    @Override
    public String getContributionId(ProductPricingDescriptor contrib) {
        return contrib.getCountry();
    }

    @Override
    public void contributionUpdated(String id, ProductPricingDescriptor contrib,
            ProductPricingDescriptor newOrigContrib) {
        if (productPricing.containsKey(id)) {
            productPricing.get(id).merge(contrib);
            log.debug(String.format(CONTRIBUTION_UPDATED, RegistryOperations.MERGED.getDescription(),
                    contrib.toString()));
        } else {
            productPricing.put(id, contrib);
            log.debug(String.format(CONTRIBUTION_UPDATED, RegistryOperations.LOADED.getDescription(),
                    contrib.toString()));
        }

    }

    @Override
    public void contributionRemoved(String id, ProductPricingDescriptor origContrib) {
        ProductPricingDescriptor removedDescriptor = productPricing.remove(id);
        String logEntry = removedDescriptor != null
                ? String.format(CONTRIBUTION_UPDATED, RegistryOperations.UNLOADED.getDescription(),
                        removedDescriptor.toString())
                : String.format(CONTRIBUTION_UPDATED, RegistryOperations.UNKNOWN.getDescription(),
                        origContrib.toString());
        log.debug(logEntry);
    }

    @Override
    public ProductPricingDescriptor clone(ProductPricingDescriptor orig) {
        return orig.clone();
    }

    @Override
    public void merge(ProductPricingDescriptor src, ProductPricingDescriptor dst) {
        dst.merge(src);
    }

    public ProductPricingDescriptor getDescriptorByCountry(String id) {
        return productPricing.get(id);
    }
}