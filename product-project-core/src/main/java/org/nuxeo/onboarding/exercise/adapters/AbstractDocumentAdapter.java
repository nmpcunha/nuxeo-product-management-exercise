package org.nuxeo.onboarding.exercise.adapters;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

public abstract class AbstractDocumentAdapter {

    private final CoreSession session;

    private DocumentModel doc;

    public AbstractDocumentAdapter(DocumentModel doc) {
        this.doc = doc;
        session = doc.getCoreSession();
    }

    public void create() {
        doc = session.createDocument(doc);
    }

    public void save() {
        doc = session.saveDocument(doc);
    }

    public DocumentModel getDocumentModel() {
        return doc;
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    public DocumentModel getParent() {
        return session.getParentDocument(doc.getRef());
    }

    //region Technical Properties

    public String getId() {
        return doc.getId();
    }

    public DocumentRef getRef() {
        return doc.getRef();
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

    //endregion

    //region Metadata

    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue("dc:title", value);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue("dc:description");
    }

    public void setDescription(String value) {
        doc.setPropertyValue("dc:description", value);
    }

    //endregion
}
