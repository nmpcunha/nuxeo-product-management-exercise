package org.nuxeo.onboarding.exercise.adapters.model;

import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.runtime.api.Framework;

import java.util.List;
import java.util.stream.Collectors;

public class NxProductAdapter extends AbstractProductAdapter {

    private static final String UNEXPECTED_DOCUMENT_TYPE = "An unexpected Document Type was received when expecting '%s'";

    private final CollectionManager collectionManager;

    public NxProductAdapter(DocumentModel doc) {
        super(doc);
        this.collectionManager = Framework.getService(CollectionManager.class);
    }

    //region Metadata
    public List<DocumentRef> getVisuals() {
        return getDocumentModel().getAdapter(Collection.class)
                .getCollectedDocumentIds()
                .stream()
                .map(IdRef::new)
                .collect(Collectors.toList());
    }

    public void addVisual(NxVisualAdapter visual) {
        if (visual == null) {
            throw new NuxeoException(String.format(UNEXPECTED_DOCUMENT_TYPE, ProductDocumentTypes.VISUAL.getName()));
        }
        collectionManager.addToCollection(getDocumentModel(), visual.getDocumentModel(), getDocumentModel().getCoreSession());
    }
    //endregion

}
