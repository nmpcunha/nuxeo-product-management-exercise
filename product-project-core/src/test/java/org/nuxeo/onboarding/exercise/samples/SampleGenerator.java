package org.nuxeo.onboarding.exercise.samples;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.onboarding.exercise.adapters.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;

public class SampleGenerator {

    public static NxProductAdapter getAsianProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "apn", ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("Sample Asian Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(10L);
        product.setProductDescription("a very interesting product");
        product.setProductName("asian product name");
        product.setOriginOfFabrication("Thailand");
        product.setPrice(450.00);
        product.setProductIdentifier(1L);
        product.setSize(50L);

        return product;
    }

    public static NxProductAdapter getAmericanProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "upn", ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("Sample American Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(15L);
        product.setProductDescription("a very interesting product");
        product.setProductName("american product name");
        product.setOriginOfFabrication("USA");
        product.setPrice(155.00);
        product.setProductIdentifier(2L);
        product.setSize(20L);

        return product;
    }

    public static NxProductAdapter getEuropeanProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "epn", ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("Sample European Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(5L);
        product.setProductDescription("a very interesting product");
        product.setProductName("european product name");
        product.setOriginOfFabrication("Germany");
        product.setPrice(211.50);
        product.setProductIdentifier(3L);
        product.setSize(50L);

        return product;
    }

    public static NxProductAdapter getUnreferencedProduct(CoreSession coreSession){
        DocumentModel doc = coreSession.createDocumentModel(ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("A Title");
        product.setCategory("interior");
        product.setProductName("A Name");

        return product;
    }

    public static DocumentModel getVisual(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "visual", ProductDocumentTypes.VISUAL.getName());

        doc.setPropertyValue("dc:title", "A Visual");
        doc.setPropertyValue("product:name", "A Sample Visual");
        doc.setPropertyValue("product:category", "interior");

        return doc;
    }

    public static DocumentModel getFile(CoreSession coreSession) {
        return coreSession.createDocumentModel("File");
    }
}
