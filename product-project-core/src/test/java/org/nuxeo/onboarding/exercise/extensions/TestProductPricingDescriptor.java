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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestProductPricingDescriptor {

    @Test
    public void shouldReturnCloneWhenCloningEmptyDescriptor() {
        ProductPricingDescriptor descriptor = new ProductPricingDescriptor();
        ProductPricingDescriptor clonedDescriptor = descriptor.clone();
        assertNotNull(clonedDescriptor);
        assertEquals(descriptor, clonedDescriptor);
    }

    @Test
    public void shouldReturnCloneWhenCloningNotEmptyDescriptor() {
        ProductPricingDescriptor descriptor = getAsianDescriptor();

        ProductPricingDescriptor clonedDescriptor = descriptor.clone();
        assertNotNull(clonedDescriptor);
        assertEquals(descriptor, clonedDescriptor);
    }

    @Test
    public void shouldReturnSameObjectWhenMergingEmptyDescriptor() {
        ProductPricingDescriptor descriptor = getAsianDescriptor();
        ProductPricingDescriptor copy = descriptor.clone();

        descriptor.merge(new ProductPricingDescriptor());
        assertEquals(copy, descriptor);
    }

    @Test
    public void shouldChangeVatForThailandWhenMergingTaxChanges() {
        ProductPricingDescriptor descriptor = getAsianDescriptor();
        ProductPricingDescriptor copy = descriptor.clone();

        descriptor.merge(getAsianTaxesChangesDescriptor());
        assertNotEquals(copy, descriptor);

        assertEquals(2, descriptor.getTaxes().size());
        assertEquals(new Double(0.05), descriptor.getTaxes().get("Customs"));
        assertEquals(new Double(0.09), descriptor.getTaxes().get("VAT"));
    }

    @Test
    public void shouldAddExtraTaxForThailandWhenMergingNewTax() {
        ProductPricingDescriptor descriptor = getAsianDescriptor();
        ProductPricingDescriptor copy = descriptor.clone();

        descriptor.merge(getAsianAdditionalTaxDescriptor());
        assertNotEquals(copy, descriptor);

        assertEquals(3, descriptor.getTaxes().size());
        assertEquals(new Double(0.05), descriptor.getTaxes().get("Customs"));
        assertEquals(new Double(0.11), descriptor.getTaxes().get("VAT"));
        assertEquals(new Double(0.03), descriptor.getTaxes().get("Extra"));
    }

    private ProductPricingDescriptor getAsianDescriptor() {
        Map<String, Double> taxes = new HashMap<>(2);
        taxes.put("VAT", 0.11);
        taxes.put("Customs", 0.05);

        return SampleGenerator.getProductPricingDescriptor("Thailand", taxes);
    }

    private ProductPricingDescriptor getAsianTaxesChangesDescriptor() {
        Map<String, Double> taxes = new HashMap<>(1);
        taxes.put("VAT", 0.09);

        return SampleGenerator.getProductPricingDescriptor("Thailand", taxes);
    }

    private ProductPricingDescriptor getAsianAdditionalTaxDescriptor() {
        Map<String, Double> taxes = new HashMap<>(1);
        taxes.put("Extra", 0.03);

        return SampleGenerator.getProductPricingDescriptor("Thailand", taxes);
    }

}