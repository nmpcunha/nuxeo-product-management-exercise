package org.nuxeo.onboarding.exercise.adapters.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxProductAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldCreateDocumentWhenRefIsNull() {
        NxProductAdapter adapter = SampleGenerator.getUnreferencedProduct(session);
        adapter.create();

        assertTrue(session.exists(adapter.getDocumentModel().getRef()));
    }

    @Test
    public void shouldCreateDocumentWhenRefIsProvided() {
        NxProductAdapter adapter = SampleGenerator.getAsianProduct(session);
        adapter.create();

        assertTrue(session.exists(adapter.getDocumentModel().getRef()));
    }

    @Test(expected = DocumentNotFoundException.class)
    public void shouldThrowExceptionWhenDocumentIsNotCreated() {
        SampleGenerator.getAsianProduct(session).save();
        // Should we just verify that the method is called?
    }

    @Test
    public void shouldSaveChangesWhenDocumentIsAlreadyCreated() {
        NxProductAdapter adapter = SampleGenerator.getAsianProduct(session);
        adapter.create();

        String newTitle = "New Title";
        adapter.setTitle(newTitle);
        adapter.save();

        assertEquals(newTitle, adapter.getTitle());
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenTryingToAddNonVisualDocumentToCollection() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();

        product.addVisual(null);
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        NxVisualAdapter nxVisual = SampleGenerator.getVisual(session);
        nxVisual.create();
        nxVisual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(nxVisual);

        List<DocumentRef> visuals = product.getVisuals();
        assertNotNull(visuals);
        assertEquals(1, visuals.size());
        assertEquals(nxVisual.getRef(), visuals.get(0));
    }

    @Test
    public void shouldBeAbleToSetAndGetComplexProperty() {
        NxProductAdapter europeanProduct = SampleGenerator.getEuropeanProduct(session);

        Map<String, Serializable> distributor = new HashMap<>();
        distributor.put(NxProduct.DISTRIBUTOR_NAME.getPropertyKey(), "DHL");
        distributor.put(NxProduct.DISTRIBUTOR_SELL_LOCATION.getPropertyKey(), "Portugal");

        europeanProduct.setDistributor(distributor);

        Map<String, Serializable> productDistributor = europeanProduct.getDistributor();

        assertNotNull(productDistributor);
        assertEquals(distributor, productDistributor);
    }
}
