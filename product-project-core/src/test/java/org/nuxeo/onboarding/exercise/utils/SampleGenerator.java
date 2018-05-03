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

package org.nuxeo.onboarding.exercise.utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;

public class SampleGenerator {

    public static NxProductAdapter getAsianProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "apn", ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        Map<String, Serializable> distributor = new HashMap<>();
        distributor.put(NxProduct.DISTRIBUTOR_NAME.getPropertyKey(), "Jackie Chan");
        distributor.put(NxProduct.DISTRIBUTOR_SELL_LOCATION.getPropertyKey(), "China");

        product.setTitle("Sample Asian Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(10L);
        product.setDistributor(distributor);
        product.setProductDescription("a very interesting product");
        product.setProductName("asian product name");
        product.setOriginOfFabrication("Thailand");
        product.setPrice(450.00);
        product.setProductIdentifier(1L);
        product.setSize(50L);

        return product;
    }

    public static NxProductAdapter getAmericanProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "upn", ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        Map<String, Serializable> distributor = new HashMap<>();
        distributor.put(NxProduct.DISTRIBUTOR_NAME.getPropertyKey(), "Donald Trump");
        distributor.put(NxProduct.DISTRIBUTOR_SELL_LOCATION.getPropertyKey(), "USA");

        product.setTitle("Sample American Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(15L);
        product.setDistributor(distributor);
        product.setProductDescription("a very interesting product");
        product.setProductName("american product name");
        product.setOriginOfFabrication("USA");
        product.setPrice(155.00);
        product.setProductIdentifier(2L);
        product.setSize(20L);

        return product;
    }

    public static NxProductAdapter getEuropeanProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/default-domain/workspaces/sampleWorkspace", "epn",
                ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("Sample European Product");
        product.setImmediateAvailability(true);
        product.setCategory("interior");
        product.setDeliveryTime(5L);
        product.setProductDescription("a very interesting product");
        product.setProductName("european product name");
        product.setOriginOfFabrication("Germany");
        product.setPrice(211.50);
        product.setProductIdentifier(3L);
        product.setSize(50L);

        return product;
    }

    public static NxProductAdapter getUnreferencedProduct(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel(ProductDocumentTypes.PRODUCT.getName());
        NxProductAdapter product = doc.getAdapter(NxProductAdapter.class);

        product.setTitle("A Title");
        product.setCategory("interior");
        product.setProductName("A Name");

        return product;
    }

    public static NxVisualAdapter getVisual(CoreSession coreSession) {
        DocumentModel doc = coreSession.createDocumentModel("/", "visual", ProductDocumentTypes.VISUAL.getName());
        NxVisualAdapter visual = doc.getAdapter(NxVisualAdapter.class);

        visual.setTitle("Visual Product");
        visual.setCategory("interior");
        visual.setProductName("Visual Product Name");

        return visual;
    }

    public static DocumentModel getFile(CoreSession coreSession) {
        return coreSession.createDocumentModel("/default-domain/workspaces/sampleWorkspace", "sampleFile", "File");
    }

    public static DocumentModel getWorkspace(CoreSession coreSession) {
        return coreSession.createDocumentModel("/default-domain/workspaces/", "sampleWorkspace", "Workspace");
    }

    public static Blob getPngBlob() throws IOException {
        File sample = new File(FileUtils.getResourcePathFromContext("test-data/sample.png"));
        return Blobs.createBlob(sample, "image/png");
    }
}
