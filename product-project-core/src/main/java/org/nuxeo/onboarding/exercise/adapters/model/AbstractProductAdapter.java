package org.nuxeo.onboarding.exercise.adapters.model;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.onboarding.exercise.adapters.AbstractDocumentAdapter;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;

import java.io.Serializable;
import java.util.Map;

public abstract class AbstractProductAdapter extends AbstractDocumentAdapter {

    public AbstractProductAdapter(DocumentModel doc) {
        super(doc);
    }

    public Boolean IsImmediatelyAvailable() {
        return (Boolean) getDocumentModel().getPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath());
    }

    public void setImmediateAvailability(boolean availability) {
        getDocumentModel().setPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath(), availability);
    }

    public String getCategory() {
        return (String) getDocumentModel().getPropertyValue(NxProduct.CATEGORY.getPropertyXPath());
    }

    public void setCategory(String category) {
        getDocumentModel().setPropertyValue(NxProduct.CATEGORY.getPropertyXPath(), category);
    }

    public Long getDeliveryTime() {
        return (Long) getDocumentModel().getPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath());
    }

    public void setDeliveryTime(Long deliveryTime) {
        getDocumentModel().setPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath(), deliveryTime);
    }

    public String getProductDescription() {
        return (String) getDocumentModel().getPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath());
    }

    public void setProductDescription(String description) {
        getDocumentModel().setPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath(), description);
    }

    public String getProductName() {
        return (String) getDocumentModel().getPropertyValue(NxProduct.NAME.getPropertyXPath());
    }

    public void setProductName(String name) {
        getDocumentModel().setPropertyValue(NxProduct.NAME.getPropertyXPath(), name);
    }

    public String getOriginOfFabrication() {
        return (String) getDocumentModel().getPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath());
    }

    public void setOriginOfFabrication(String originOfFabrication) {
        getDocumentModel().setPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath(), originOfFabrication);
    }

    public Double getPrice() {
        return (Double) getDocumentModel().getPropertyValue(NxProduct.PRICE.getPropertyXPath());
    }

    public void setPrice(Double price) {
        getDocumentModel().setPropertyValue(NxProduct.PRICE.getPropertyXPath(), price);
    }

    public Long getProductIdentifier() {
        return (Long) getDocumentModel().getPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath());
    }

    public void setProductIdentifier(Long identifier) {
        getDocumentModel().setPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath(), identifier);
    }

    public Long getSize() {
        return (Long) getDocumentModel().getPropertyValue(NxProduct.SIZE.getPropertyXPath());
    }

    public void setSize(Long size) {
        getDocumentModel().setPropertyValue(NxProduct.SIZE.getPropertyXPath(), size);
    }

    public Map<String, Serializable> getDistributor(){
        return (Map<String, Serializable>) getDocumentModel().getPropertyValue(NxProduct.DISTRIBUTOR.getPropertyXPath());
    }

    public void setDistributor(Map<String, Serializable> distributor){
        getDocumentModel().setPropertyValue(NxProduct.DISTRIBUTOR.getPropertyXPath(), (Serializable) distributor);
    }
}
