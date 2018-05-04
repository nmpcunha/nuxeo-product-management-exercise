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

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;
import org.nuxeo.onboarding.exercise.extensions.registries.ProductPricingRegistry;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.HotDeployer;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestProductService {

    @Inject
    private ProductService productservice;

    @Inject
    private CoreSession coreSession;

    @Inject
    private HotDeployer hotDeployer;

    @Test
    public void shouldBeUpAndRunning() {
        assertNotNull(productservice);
    }

    @Test
    public void shouldContainWorldTaxesContribution() {
        ProductPricingRegistry registry = getProductPricingRegistry();
        assertNotNull(registry);
        assertEquals(2, registry.getFragments().length);

        Map<String, Double> thaiTaxes = new HashMap<>();
        thaiTaxes.put("VAT", 0.11);
        thaiTaxes.put("Customs", 0.05);
        assertCountryContribution(registry, "Thailand", thaiTaxes);

        Map<String, Double> americanTaxes = new HashMap<>();
        americanTaxes.put("VAT", 0.2);
        assertCountryContribution(registry, "USA", americanTaxes);
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenNoDocument() {
        Double computedPrice = productservice.computePrice(null);
        assertNull(computedPrice);
    }

    @Test
    public void shouldReturnPriceWithoutTaxes() {
        NxProductAdapter product = SampleGenerator.getEuropeanProduct(coreSession);
        Double computedPrice = productservice.computePrice(product);

        assertNotNull(computedPrice);
        assertEquals(product.getPrice(), computedPrice);
    }

    @Test
    public void shouldReturnPriceWithAsianTaxes() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(coreSession);
        Double computedPrice = productservice.computePrice(product);

        assertNotNull(computedPrice);
        assertEquals(new Double(522.0), computedPrice);
    }

    @Test
    public void shouldReturnPriceWithAmericanTaxes() {
        NxProductAdapter product = SampleGenerator.getAmericanProduct(coreSession);
        Double computedPrice = productservice.computePrice(product);

        assertNotNull(computedPrice);
        assertEquals(new Double(186.0), computedPrice);
    }

    @Test
    public void shouldNotLoadContributionsWithoutCountry() {
        ProductPricingRegistry registry = getProductPricingRegistry();

        try {
            hotDeployer.deploy(
                    "org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/extensions/invalid/no-country-contrib.xml");

            ProductPricingRegistry updatedRegistry = getProductPricingRegistry();
            assertEquals(registry.getFragments().length, updatedRegistry.getFragments().length);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldNotLoadInvalidContributions() {
        ProductPricingRegistry registry = getProductPricingRegistry();

        try {
            hotDeployer.deploy(
                    "org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/extensions/invalid/no-taxes-contrib.xml");

            ProductPricingRegistry updatedRegistry = getProductPricingRegistry();
            assertEquals(registry.getFragments().length, updatedRegistry.getFragments().length);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldLoadValidContributions() {
        ProductPricingRegistry registry = getProductPricingRegistry();
        try {
            hotDeployer.deploy(
                    "org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/extensions/valid-taxes-contrib.xml");

            ProductPricingRegistry updatedRegistry = getProductPricingRegistry();

            assertThat(updatedRegistry.getFragments().length, greaterThan(registry.getFragments().length));
            assertEquals(4, updatedRegistry.getFragments().length);

            Map<String, Double> thaiTaxes = new HashMap<>();
            thaiTaxes.put("VAT", 0.09);
            thaiTaxes.put("Customs", 0.05);
            assertCountryContribution(updatedRegistry, "Thailand", thaiTaxes);

            Map<String, Double> americanTaxes = new HashMap<>();
            americanTaxes.put("VAT", 0.2);
            assertCountryContribution(updatedRegistry, "USA", americanTaxes);

            Map<String, Double> portugueseTaxes = new HashMap<>();
            portugueseTaxes.put("VAT", 0.23);
            assertCountryContribution(updatedRegistry, "Portugal", portugueseTaxes);

            Map<String, Double> canadianTaxes = new HashMap<>();
            canadianTaxes.put("VAT", 0.15);
            assertCountryContribution(updatedRegistry, "Canada", canadianTaxes);
        } catch (Exception e) {
            fail("It is not expected to not be able to deploy a valid contribution");
        }
    }

    private ProductPricingRegistry getProductPricingRegistry() {
        ProductPricingRegistry registry = null;
        try {
            Field registryField = productservice.getClass().getDeclaredField("registry");
            if (registryField != null) {
                registryField.setAccessible(true);
                registry = (ProductPricingRegistry) registryField.get(productservice);
            }
        } catch (Exception ignored) {
        }
        return registry;
    }

    private void assertCountryContribution(ProductPricingRegistry registry, String country,
            Map<String, Double> expectedTaxes) {
        ProductPricingDescriptor contribution = registry.getDescriptorByCountry(country);
        assertNotNull(contribution);
        assertEquals(expectedTaxes, contribution.getTaxes());
    }

}
