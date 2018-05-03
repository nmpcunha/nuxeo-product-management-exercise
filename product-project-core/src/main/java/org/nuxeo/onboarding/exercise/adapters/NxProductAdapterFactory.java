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

package org.nuxeo.onboarding.exercise.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.ProductSchemas;

public class NxProductAdapterFactory implements DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(NxProductAdapterFactory.class);

    private static final String MISMATCHING_TYPE = "The Document received is from '%s' type and not met the expected one: '%s'";

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if (ProductDocumentTypes.PRODUCT.getName().equals(doc.getType()) && NxProductAdapter.class.equals(itf)
                && doc.hasSchema(ProductSchemas.DUBLIN_CORE.getName())
                && doc.hasSchema(ProductSchemas.PRODUCT.getName())) {
            return new NxProductAdapter(doc);
        } else if (ProductDocumentTypes.VISUAL.getName().equals(doc.getType()) && NxVisualAdapter.class.equals(itf)
                && doc.hasSchema(ProductSchemas.DUBLIN_CORE.getName())
                && doc.hasSchema(ProductSchemas.PRODUCT.getName())) {
            return new NxVisualAdapter(doc);
        } else {
            log.warn(String.format(MISMATCHING_TYPE, doc.getDocumentType().getName(),
                    ProductDocumentTypes.PRODUCT.getName()));
            return null;
        }
    }
}
