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
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.onboarding.exercise.constants.ProductSchemas;
import org.nuxeo.runtime.api.Framework;

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
