package org.nuxeo.onboarding.exercise.adapters.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxVisualAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldReturnNullWhenVisualIsNotAssociatedToProductCollection() {
        NxVisualAdapter nxVisual = SampleGenerator.getVisual(session);
        nxVisual.create();
        nxVisual.save();

        assertNull(nxVisual.getCorrespondingProduct());
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        NxVisualAdapter nxVisual = SampleGenerator.getVisual(session);
        nxVisual.create();
        nxVisual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.addVisual(nxVisual);

        NxProductAdapter correspondingProduct = nxVisual.getCorrespondingProduct();

        assertNotNull(correspondingProduct);
        assertEquals(product, correspondingProduct);
    }

}