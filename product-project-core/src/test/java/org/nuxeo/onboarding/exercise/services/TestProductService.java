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

import static org.junit.Assert.*;

import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.extensions.ProductPricingDescriptor;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestProductService {

    @Inject
    private ProductService productservice;

    @Inject
    private CoreSession coreSession;

    @Test
    public void shouldBeUpAndRunning() {
        assertNotNull(productservice);
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

    private void assertAsianContribution(ProductPricingDescriptor asianContribution) {
        assertNotNull(asianContribution);
        assertEquals(2, asianContribution.getCountries().size());
        assertTrue(asianContribution.getCountries().containsAll(Arrays.asList("Thailand", "Vietnam")));
        assertEquals(2, asianContribution.getTaxes().size());
        assertTrue(asianContribution.getTaxes().keySet().containsAll(Arrays.asList("VAT", "Customs")));

        Double asianVat = asianContribution.getTaxes().get("VAT");
        assertNotNull(asianVat);
        assertEquals(new Double(0.11), asianVat);

        Double asianCustoms = asianContribution.getTaxes().get("Customs");
        assertNotNull(asianCustoms);
        assertEquals(new Double(0.05), asianCustoms);
    }

    private void assertAmericanContribution(ProductPricingDescriptor americanContribution) {
        assertNotNull(americanContribution);
        assertEquals(1, americanContribution.getCountries().size());
        assertTrue(americanContribution.getCountries().contains("USA"));
        assertEquals(1, americanContribution.getTaxes().size());
        assertTrue(americanContribution.getTaxes().keySet().contains("VAT"));

        Double americanVat = americanContribution.getTaxes().get("VAT");
        assertNotNull(americanVat);
        assertEquals(new Double(0.2), americanVat);
    }
}
