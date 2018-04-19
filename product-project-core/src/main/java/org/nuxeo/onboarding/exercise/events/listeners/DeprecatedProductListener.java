package org.nuxeo.onboarding.exercise.events.listeners;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.onboarding.exercise.adapters.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;

public class DeprecatedProductListener implements EventListener {

    private static final String UNEXPECTED_DOCUMENT_TYPE = "An unexpected Document Type '%s' was received when expecting 'Product'";

    @Override
    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();

        if (!doc.getType().equals(ProductDocumentTypes.PRODUCT.getName())) {
            event.markBubbleException();
            throw new NuxeoException(String.format(UNEXPECTED_DOCUMENT_TYPE, doc.getType()));
        }

        NxProductAdapter adapter = doc.getAdapter(NxProductAdapter.class);
        for (DocumentModel visual : adapter.getVisuals()) {
            moveToFolder(visual, "");
        }
    }

    private void moveToFolder(DocumentModel document, String folder){

    }
}
