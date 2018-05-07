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

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;
import org.nuxeo.onboarding.exercise.utils.features.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestProductPricingRegistry {

    private ProductPricingRegistry registry;

    @Before
    public void setUp() {
        registry = new ProductPricingRegistry();
    }

    @Test
    public void shouldBeUpAndRunning() {
        assertNotNull(registry);
    }

    @Test
    public void shouldCallDescriptorMethodWhenCloning() {
        ProductPricingDescriptor mockedDescriptor = Mockito.mock(ProductPricingDescriptor.class);
        registry.clone(mockedDescriptor);
        verify(mockedDescriptor).clone();
    }

    @Test
    public void shouldCallDescriptorMethodWhenMerging() {
        ProductPricingDescriptor mockedDescriptor = Mockito.mock(ProductPricingDescriptor.class);
        ProductPricingDescriptor descriptor = new ProductPricingDescriptor();
        registry.merge(descriptor, mockedDescriptor);
        verify(mockedDescriptor).merge(eq(descriptor));
    }

    @Test
    public void shouldAddDescriptorWhenNotExists() {
        String country = "Thailand";

        assertNull(registry.getDescriptorByCountry(country));

        ProductPricingDescriptor descriptor = SampleGenerator.getProductPricingDescriptor(country, new HashMap<>());
        registry.addContribution(descriptor);

        ProductPricingDescriptor countryDescriptor = registry.getDescriptorByCountry(country);
        assertNotNull(countryDescriptor);
        assertEquals(descriptor, countryDescriptor);

        ContributionFragmentRegistry.FragmentList<ProductPricingDescriptor>[] fragments = registry.getFragments();
        assertEquals(1, fragments.length);
        assertEquals(descriptor, fragments[0].object);
        assertEquals(fragments[0].next.object, fragments[0].object);
    }

    @Test
    public void shouldUpdateDescriptorWhenAlreadyExists() {
        String country = "Thailand";
        ProductPricingDescriptor descriptor = SampleGenerator.getProductPricingDescriptor(country, new HashMap<>());
        registry.addContribution(descriptor);

        Map<String, Double> taxes = new HashMap<>(1);
        taxes.put("VAT", 0.11);

        ProductPricingDescriptor updatedDescriptor = SampleGenerator.getProductPricingDescriptor(country, taxes);
        registry.addContribution(updatedDescriptor);

        ProductPricingDescriptor countryDescriptor = registry.getDescriptorByCountry(country);
        assertNotNull(countryDescriptor);
        assertNotEquals(descriptor, countryDescriptor);
        assertEquals(updatedDescriptor, countryDescriptor);

        ContributionFragmentRegistry.FragmentList<ProductPricingDescriptor>[] fragments = registry.getFragments();
        assertEquals(1, fragments.length);

        assertEquals(updatedDescriptor, fragments[0].object);
        assertEquals(descriptor, fragments[0].next.object);
    }

    @Test
    public void shouldAddDescriptorWhenDifferentOneExists() {
        String country = "Thailand";
        ProductPricingDescriptor descriptor = SampleGenerator.getProductPricingDescriptor(country, new HashMap<>());
        registry.addContribution(descriptor);

        String newCountry = "Portugal";
        ProductPricingDescriptor newDescriptor = SampleGenerator.getProductPricingDescriptor(newCountry,
                new HashMap<>());
        registry.addContribution(newDescriptor);

        ContributionFragmentRegistry.FragmentList<ProductPricingDescriptor>[] fragments = registry.getFragments();
        assertEquals(2, fragments.length);
    }

    @Test
    public void shouldRemoveDescriptorWhenExists() {
        String country = "Thailand";
        ProductPricingDescriptor descriptor = SampleGenerator.getProductPricingDescriptor(country, new HashMap<>());
        registry.addContribution(descriptor);

        registry.removeContribution(descriptor);

        assertNull(registry.getDescriptorByCountry(country));

        ContributionFragmentRegistry.FragmentList<ProductPricingDescriptor>[] fragments = registry.getFragments();
        assertEquals(1, fragments.length);
        assertNull(fragments[0].object);
    }

    @Test
    public void shouldRemoveDescriptorWhenNotExists() {
        String country = "Thailand";
        ProductPricingDescriptor descriptor = SampleGenerator.getProductPricingDescriptor(country, new HashMap<>());
        registry.contributionRemoved(country, descriptor);

        assertNull(registry.getDescriptorByCountry(country));
        assertEquals(0, registry.getFragments().length);
    }
}