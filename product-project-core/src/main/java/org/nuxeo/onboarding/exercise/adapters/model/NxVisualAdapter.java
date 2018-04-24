package org.nuxeo.onboarding.exercise.adapters.model;

import org.nuxeo.ecm.core.api.DocumentModel;

public class NxVisualAdapter extends AbstractProductAdapter {

    public NxVisualAdapter(DocumentModel doc) {
        super(doc);
    }

    //region Metadata
    public NxProductAdapter getCorrespondingProduct() {
        return getParent().getAdapter(NxProductAdapter.class);
    }
    //endregion

}
