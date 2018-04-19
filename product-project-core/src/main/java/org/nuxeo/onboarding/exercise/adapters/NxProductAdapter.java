package org.nuxeo.onboarding.exercise.adapters;

import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.runtime.api.Framework;

import java.util.List;

public class NxProductAdapter {

    private DocumentModel doc;

    private String titleXpath = "dc:title";
    private String descriptionXpath = "dc:description";

    public NxProductAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    public void create() {
        CoreSession session = doc.getCoreSession();
        session.createDocument(doc);
    }

    public void save() {
        CoreSession session = doc.getCoreSession();
        doc = session.saveDocument(doc);
    }

    public DocumentModel getDocumentModel(){
        return doc;
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    /** Technical Properties Getters **/
    public String getId() {
        return doc.getId();
    }

    public String getName() {
        return doc.getName();
    }

    public String getPath() {
        return doc.getPathAsString();
    }

    public String getState() {
        return doc.getCurrentLifeCycleState();
    }

    /** Metadata Getters and Setters **/
    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue(titleXpath, value);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue(descriptionXpath);
    }

    public void setDescription(String value) {
        doc.setPropertyValue(descriptionXpath, value);
    }

    public Boolean IsImmediatelyAvailable(){
        return (Boolean) doc.getPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath());
    }

    public void setImmediateAvailability(boolean availability){
        doc.setPropertyValue(NxProduct.AVAILABLE_IMMEDIATELY.getPropertyXPath(), availability);
    }

    public String getCategory(){
        return (String) doc.getPropertyValue(NxProduct.CATEGORY.getPropertyXPath());
    }

    public void setCategory(String category){
        doc.setPropertyValue(NxProduct.CATEGORY.getPropertyXPath(), category);
    }

    public Long getDeliveryTime(){
        return (Long) doc.getPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath());
    }

    public void setDeliveryTime(Long deliveryTime){
        doc.setPropertyValue(NxProduct.DELIVERY_TIME.getPropertyXPath(), deliveryTime);
    }

    public String getProductDescription(){
        return (String) doc.getPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath());
    }

    public void setProductDescription(String description){
        doc.setPropertyValue(NxProduct.DESCRIPTION.getPropertyXPath(), description);
    }

    public String getProductName(){
        return (String) doc.getPropertyValue(NxProduct.NAME.getPropertyXPath());
    }

    public void setProductName(String name) {
        doc.setPropertyValue(NxProduct.NAME.getPropertyXPath(), name);
    }

    public String getOriginOfFabrication(){
        return (String) doc.getPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath());
    }

    public void setOriginOfFabrication(String originOfFabrication){
        doc.setPropertyValue(NxProduct.ORIGIN_OF_FABRICATION.getPropertyXPath(), originOfFabrication);
    }

    public Double getPrice(){
        return (Double) doc.getPropertyValue(NxProduct.PRICE.getPropertyXPath());
    }

    public void setPrice(Double price){
        doc.setPropertyValue(NxProduct.PRICE.getPropertyXPath(), price);
    }

    public Long getProductIdentifier(){
        return (Long) doc.getPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath());
    }

    public void setProductIdentifier(Long identifier){
        doc.setPropertyValue(NxProduct.PRODUCT_ID.getPropertyXPath(), identifier);
    }

    public Long getSize(){
        return (Long) doc.getPropertyValue(NxProduct.SIZE.getPropertyXPath());
    }

    public void setSize(Long size){
        doc.setPropertyValue(NxProduct.SIZE.getPropertyXPath(), size);
    }

    public List<DocumentModel> getVisuals(){
        return Framework.getService(CollectionManager.class).getVisibleCollection(doc, doc.getCoreSession());
    }

    public void addVisual(DocumentModel visual){
        if(!visual.getType().equals(ProductDocumentTypes.VISUAL.getName())){
            throw new NuxeoException("");
        }
        Framework.getService(CollectionManager.class).addToCollection(doc, visual, doc.getCoreSession());
    }

}
