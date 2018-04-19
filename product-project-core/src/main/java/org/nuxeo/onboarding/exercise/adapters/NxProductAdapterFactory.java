package org.nuxeo.onboarding.exercise.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.ProductSchemas;
import org.nuxeo.onboarding.exercise.services.ProductServiceImpl;

public class NxProductAdapterFactory implements DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(ProductServiceImpl.class);

    private static final String MISMATCHING_TYPE = "The Document received is from '%s' type and not met the expected one: 'Product'";

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if (ProductDocumentTypes.PRODUCT.getName().equals(doc.getType()) && doc.hasSchema("dublincore")
                && doc.hasSchema(ProductSchemas.PRODUCT.getName())) {
            return new NxProductAdapter(doc);
        } else {
            log.warn(String.format(MISMATCHING_TYPE, doc.getDocumentType().getName()));
            return null;
        }
    }
}
