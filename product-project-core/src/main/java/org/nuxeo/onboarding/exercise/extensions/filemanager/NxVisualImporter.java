package org.nuxeo.onboarding.exercise.extensions.filemanager;

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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NxVisualImporter extends AbstractFileImporter {

    private static final Log log = LogFactory.getLog(NxVisualImporter.class);

    @Override
    public DocumentModel create(CoreSession documentManager, Blob content, String path, boolean overwrite, String fullname, TypeManager typeService) throws IOException {
        String filename = FileManagerUtils.fetchFileName(fullname);
        String title = FileManagerUtils.fetchTitle(filename);

        content.setFilename(filename);

        NxVisualAdapter visual;

        // Check to see if an existing Document with the same title exists.
        DocumentModel docModel = FileManagerUtils.getExistingDocByTitle(documentManager, path, title);
        if (overwrite && (docModel != null) && docModel.getType().equals(ProductDocumentTypes.VISUAL.getName())) {
            log.debug("The file already exists and an overwrite will take place.");

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

        // Visual Custom Properties
        setDefaultVisualProperties(visual);

        visual.getDocumentModel().setPathInfo(path, Framework.getService(PathSegmentService.class).generatePathSegment(docModel));
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
