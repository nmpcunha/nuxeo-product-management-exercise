/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      nuno
 */

package org.nuxeo.onboarding.exercise.events.listeners;

import java.util.List;

import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;

public class DeprecatedProductListener implements EventListener {

    private static final String UNEXPECTED_DOCUMENT_TYPE = "An unexpected Document Type '%s' was received when expecting '%s'";

    private static final String HIDDEN_FOLDER_PATH_REF = "/hiddenFolder";

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
            throw new NuxeoException(String.format(UNEXPECTED_DOCUMENT_TYPE, doc.getType(), ProductDocumentTypes.PRODUCT.getName()));
        }

        List<DocumentRef> documentRefs = doc.getAdapter(NxProductAdapter.class).getVisuals();
        DocumentRef hiddenFolderRef = createOrGetHiddenFolder(ctx.getCoreSession());
        ctx.getCoreSession().move(documentRefs, hiddenFolderRef);
    }

    private DocumentRef createOrGetHiddenFolder(CoreSession session) {
        DocumentRef ref = new PathRef(HIDDEN_FOLDER_PATH_REF);
        if (!session.exists(ref)) {
            return createFolder(session);
        }
        return ref;
    }

    private DocumentRef createFolder(CoreSession session) {
        DocumentModel folder = session.createDocumentModel("/", "hiddenFolder", "Folder");
        folder = session.createDocument(folder);
        folder.setACP(getPermissions(), true);
        return folder.getRef();
    }

    private ACP getPermissions() {
        ACP acp = new ACPImpl();
        acp.addACE("onboarding", new ACE("Group1", SecurityConstants.READ, false));
        return acp;
    }
}
