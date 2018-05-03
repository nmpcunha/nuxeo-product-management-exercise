package org.nuxeo.onboarding.exercise.adapters.model;

import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.onboarding.exercise.constants.ProductSchemas;
import org.nuxeo.runtime.api.Framework;

import java.util.List;
import java.util.stream.Collectors;

public class NxVisualAdapter extends AbstractProductAdapter {

    private final CollectionManager collectionManager;

    public NxVisualAdapter(DocumentModel doc) {
        super(doc);
        this.collectionManager = Framework.getService(CollectionManager.class);
    }

    //region Metadata
    public List<NxProductAdapter> getCorrespondingProducts() {
        DocumentModel visual = getDocumentModel();

        return collectionManager.getVisibleCollection(visual, visual.getCoreSession()).stream()
                .map(documentModel -> documentModel.getAdapter(NxProductAdapter.class))
                .collect(Collectors.toList());
    }

    public void setFileContent(Blob content) {
        getDocumentModel().setProperty(ProductSchemas.FILE.getName(), "content", content);
    }
    //endregion

}
