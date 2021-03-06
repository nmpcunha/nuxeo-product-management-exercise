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

package org.nuxeo.onboarding.exercise.operations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.services.ProductService;

@Operation(id = PriceInflation.ID, category = Constants.CAT_DOCUMENT, label = "Inflate", description = "Inflates the product's price according a specified rate.")
public class PriceInflation {

    private static final Log log = LogFactory.getLog(PriceInflation.class);

    private static final String DOC_NOT_PROCESSED = "The Document with the following ID was not processed the the PriceInflation operation: '%s'";

    public static final String ID = "Document.PriceInflation";

    @Context
    protected ProductService productService;

    @Param(name = "rate")
    protected Double inflationRate;

    @OperationMethod
    public DocumentModel run(DocumentModel product) {
        inflatePrice(product);
        return product;
    }

    @OperationMethod
    public DocumentModelList run(DocumentModelList products) {
        for (DocumentModel product : products) {
            try {
                inflatePrice(product);
            } catch (NuxeoException exception) {
                log.warn(String.format(DOC_NOT_PROCESSED, product.getId()));
            }
        }
        return products;
    }

    private void inflatePrice(DocumentModel product) {
        NxProductAdapter nxProduct = product.getAdapter(NxProductAdapter.class);
        Double currentPrice = productService.computePrice(nxProduct);

        nxProduct.setPrice(currentPrice * (1 + inflationRate));
        nxProduct.save();
    }
}
