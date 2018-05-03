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

package org.nuxeo.onboarding.exercise.extensions.filemanager;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.platform.filemanager.service.extension.AbstractFileImporter;
import org.nuxeo.ecm.platform.filemanager.utils.FileManagerUtils;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.runtime.api.Framework;

public class NxVisualImporter extends AbstractFileImporter {

    private static final Log log = LogFactory.getLog(NxVisualImporter.class);

    private static final String FILE_ALREADY_EXISTS = "The file '%s' already exists as a %s and an overwrite will take place.";

    @Override
    public DocumentModel create(CoreSession documentManager, Blob content, String path, boolean overwrite,
            String fullname, TypeManager typeService) throws IOException {
        String filename = FileManagerUtils.fetchFileName(fullname);
        String title = FileManagerUtils.fetchTitle(filename);

        content.setFilename(filename);

        NxVisualAdapter visual;

        // Check to see if an existing Document with the same title exists.
        DocumentModel docModel = FileManagerUtils.getExistingDocByTitle(documentManager, path, title);
        if (overwrite && (docModel != null) && docModel.getType().equals(ProductDocumentTypes.VISUAL.getName())) {
            log.warn(String.format(FILE_ALREADY_EXISTS, title, ProductDocumentTypes.VISUAL.getName()));

            visual = docModel.getAdapter(NxVisualAdapter.class);
            visual.setTitle(title);
            visual.setFileContent(content);

            visual.save();

            return visual.getDocumentModel();
        }

        docModel = documentManager.createDocumentModel(ProductDocumentTypes.VISUAL.getName());

        visual = docModel.getAdapter(NxVisualAdapter.class);
        visual.setTitle(title);
        visual.setFileContent(content);

        setDefaultVisualProperties(visual);

        visual.getDocumentModel().setPathInfo(path,
                Framework.getService(PathSegmentService.class).generatePathSegment(docModel));
        visual.create();

        return visual.getDocumentModel();
    }

    private void setDefaultVisualProperties(NxVisualAdapter visual) {
        Map<String, Serializable> distributor = new HashMap<>();
        distributor.put(NxProduct.DISTRIBUTOR_NAME.getPropertyKey(), "DHL");
        distributor.put(NxProduct.DISTRIBUTOR_SELL_LOCATION.getPropertyKey(), "Germany");

        visual.setImmediateAvailability(true);
        visual.setCategory("interior");
        visual.setDeliveryTime(15L);
        visual.setDistributor(distributor);
        visual.setProductDescription("a very interesting product");
        visual.setProductName("a product");
        visual.setOriginOfFabrication("Germany");
        visual.setPrice(155.00);
        visual.setProductIdentifier(2L);
        visual.setSize(20L);
    }
}
