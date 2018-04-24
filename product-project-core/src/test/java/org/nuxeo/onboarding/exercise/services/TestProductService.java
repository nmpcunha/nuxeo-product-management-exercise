package org.nuxeo.onboarding.exercise.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.extension.ProductPricingDescriptor;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.HotDeployer;

import javax.inject.Inject;
import java.util.Arrays;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestProductService {

    @Inject
    private ProductService productservice;

    @Inject
    private CoreSession coreSession;

    @Inject
    private HotDeployer deployer;

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

    @Test
    public void shouldThrowExceptionWhenInvalidContributionIsLoaded() {
        try {
            deployer.deploy("org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/test/invalid-contrib.xml");
            assertNull(productservice.getPricingDescriptor(null));
            assertNull(productservice.getPricingDescriptor(""));
        } catch (Exception e) {
            // It is not throwing the exception in sync mode... How should I validate this?
            assertThat(e, instanceOf(NuxeoException.class));
        }
    }

    //TODO How to unregister contributions?

    @Test
    public void shouldContainLoadedContributions() {
        ProductPricingDescriptor asianContribution = productservice.getPricingDescriptor("southernAsia");
        assertAsianContribution(asianContribution);

        ProductPricingDescriptor americanContribution = productservice.getPricingDescriptor("northAmerica");
        assertAmericanContribution(americanContribution);
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
