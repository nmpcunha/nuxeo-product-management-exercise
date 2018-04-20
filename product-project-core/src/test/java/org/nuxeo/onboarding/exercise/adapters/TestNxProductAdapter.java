package org.nuxeo.onboarding.exercise.adapters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.onboarding.exercise.OnboardingFeature;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.samples.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxProductAdapter {

    @Inject
    private CoreSession session;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.adapters.NxProductAdapterFactory")
    public void shouldReturnNullWhenDocumentIsNotProduct() throws LogCaptureFeature.NoLogCaptureFilterException {
        DocumentModel document = SampleGenerator.getFile(session);
        assertNull(document.getAdapter(NxProductAdapter.class));
        logCaptureResult.assertHasEvent();
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.adapters.NxProductAdapterFactory")
    public void shouldReturnNullWhenDocumentInheritsProduct() throws LogCaptureFeature.NoLogCaptureFilterException {
        DocumentModel document = session.createDocumentModel(ProductDocumentTypes.VISUAL.getName());
        assertNull(document.getAdapter(NxProductAdapter.class));
        logCaptureResult.assertHasEvent();
    }

    @Test
    public void shouldReturnTheAdapterWhenDocumentIsProduct() {
        DocumentModel document = SampleGenerator.getAsianProduct(session).getDocumentModel();
        NxProductAdapter adapter = document.getAdapter(NxProductAdapter.class);

        assertAdapterMatchesDocument(document, adapter);
    }

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

        assertAdapterMatchesDocument(adapter.getDocumentModel(), adapter);
        assertEquals(newTitle, adapter.getTitle());
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenTryingToAddNonVisualDocumentToCollection() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();

        DocumentModel file = SampleGenerator.getFile(session);
        session.createDocument(file);

        product.addVisual(file);
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        DocumentModel nxVisual = SampleGenerator.getVisual(session);
        session.createDocument(nxVisual);
        nxVisual = session.saveDocument(nxVisual);

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.addVisual(nxVisual);

        List<DocumentRef> visuals = product.getVisuals();
        assertNotNull(visuals);
        assertEquals(1, visuals.size());
        assertEquals(nxVisual.getRef(), visuals.get(0));
    }

    private void assertAdapterMatchesDocument(DocumentModel document, NxProductAdapter adapter) {
        assertEquals(document.getPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath()), adapter.IsImmediatelyAvailable());
        assertEquals(document.getPropertyValue(NxProduct.CATEGORY.getPropertyXPath()), adapter.getCategory());
        assertEquals(document.getPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath()), adapter.getDeliveryTime());
        assertEquals(document.getPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath()), adapter.getProductDescription());
        assertEquals(document.getPropertyValue(NxProduct.NAME.getPropertyXPath()), adapter.getProductName());
        assertEquals(document.getPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath()), adapter.getOriginOfFabrication());
        assertEquals(document.getPropertyValue(NxProduct.PRICE.getPropertyXPath()), adapter.getPrice());
        assertEquals(document.getPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath()), adapter.getProductIdentifier());
        assertEquals(document.getPropertyValue(NxProduct.SIZE.getPropertyXPath()), adapter.getSize());
    }
}
