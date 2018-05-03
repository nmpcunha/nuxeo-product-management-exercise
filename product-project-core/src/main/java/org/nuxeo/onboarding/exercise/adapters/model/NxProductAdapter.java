
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

package org.nuxeo.onboarding.exercise.adapters.model;

import java.util.List;
import java.util.stream.Collectors;

import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.runtime.api.Framework;

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
