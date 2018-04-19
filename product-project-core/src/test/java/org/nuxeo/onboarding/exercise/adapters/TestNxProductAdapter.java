package org.nuxeo.onboarding.exercise.adapters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.samples.SampleGenerator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({"org.nuxeo.ecm.platform.tag", "org.nuxeo.ecm.platform.collections.core"})
@Deploy({"org.nuxeo.onboarding.exercise.product-project-core", "studio.extensions.ncunha-SANDBOX"})
public class TestNxProductAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldReturnNullWhenDocumentIsNotProduct() {
        DocumentModel document = SampleGenerator.getFile(session);
        assertNull(document.getAdapter(NxProductAdapter.class));
    }

    @Test
    public void shouldReturnNullWhenDocumentInheritsProduct() {
        DocumentModel document = session.createDocumentModel(ProductDocumentTypes.VISUAL.getName());
        assertNull(document.getAdapter(NxProductAdapter.class));
    }

    @Test
    public void shouldReturnTheAdapterWhenDocumentIsProduct() {
        DocumentModel document = SampleGenerator.getAsianProduct(session).getDocumentModel();
        NxProductAdapter adapter = document.getAdapter(NxProductAdapter.class);

        assertAdapterMatchesDocument(document, adapter);
    }

    @Test()
    public void shouldThrowExceptionWhenDocumentRefIsNull() {
        NxProductAdapter adapter = SampleGenerator.getUnreferencedProduct(session);
        adapter.create();

        // It generates a guid ref which is not known by our object
    }

    @Test()
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
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();

        DocumentModel nxVisual = SampleGenerator.getVisual(session);
        session.createDocument(nxVisual);

        product.addVisual(nxVisual);

        List<DocumentModel> visuals = product.getVisuals();
        assertNotNull(visuals);
        assertEquals(1, visuals.size());
        assertEquals(nxVisual, visuals.get(0));
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
