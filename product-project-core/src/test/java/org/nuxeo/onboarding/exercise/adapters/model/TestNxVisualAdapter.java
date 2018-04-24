package org.nuxeo.onboarding.exercise.adapters.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxVisualAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldReturnNullWhenVisualIsNotAssociatedToProductCollection() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        List<NxProductAdapter> correspondingProducts = visual.getCorrespondingProducts();

        assertNotNull(correspondingProducts);
        assertEquals(0, correspondingProducts.size());
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(visual);

        List<NxProductAdapter> correspondingProducts = visual.getCorrespondingProducts();

        assertNotNull(correspondingProducts);
        assertEquals(1, correspondingProducts.size());
        assertEquals(product.getId(), correspondingProducts.get(0).getId());
    }

}