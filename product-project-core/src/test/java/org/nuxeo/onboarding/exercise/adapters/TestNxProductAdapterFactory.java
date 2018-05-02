package org.nuxeo.onboarding.exercise.adapters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.onboarding.exercise.adapters.model.AbstractProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxProductAdapterFactory {

    @Inject
    private CoreSession session;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.adapters.NxProductAdapterFactory")
    public void shouldReturnNullWhenDocumentIsNotUnknown() throws LogCaptureFeature.NoLogCaptureFilterException {
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
    public void shouldReturnTheAdapterWhenDocumentIsVisual() {
        DocumentModel document = SampleGenerator.getVisual(session).getDocumentModel();
        NxVisualAdapter adapter = document.getAdapter(NxVisualAdapter.class);

        assertAdapterMatchesDocument(document, adapter);
    }

    private void assertAdapterMatchesDocument(DocumentModel document, AbstractProductAdapter adapter) {
        assertEquals(document.getPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath()), adapter.IsImmediatelyAvailable());
        assertEquals(document.getPropertyValue(NxProduct.CATEGORY.getPropertyXPath()), adapter.getCategory());
        assertEquals(document.getPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath()), adapter.getDeliveryTime());
        assertEquals(document.getPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath()), adapter.getProductDescription());
        assertEquals(document.getPropertyValue(NxProduct.DISTRIBUTOR.getPropertyXPath()), adapter.getDistributor());
        assertEquals(document.getPropertyValue(NxProduct.NAME.getPropertyXPath()), adapter.getProductName());
        assertEquals(document.getPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath()), adapter.getOriginOfFabrication());
        assertEquals(document.getPropertyValue(NxProduct.PRICE.getPropertyXPath()), adapter.getPrice());
        assertEquals(document.getPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath()), adapter.getProductIdentifier());
        assertEquals(document.getPropertyValue(NxProduct.SIZE.getPropertyXPath()), adapter.getSize());
    }

}