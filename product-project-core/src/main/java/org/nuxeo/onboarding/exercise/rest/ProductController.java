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

package org.nuxeo.onboarding.exercise.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.services.ProductService;
import org.nuxeo.runtime.api.Framework;

@WebObject(type = "product")
@Produces("application/json+nxentity")
@Path("product")
public class ProductController extends ModuleRoot {

    private static final Log log = LogFactory.getLog(ProductController.class);

    static final String DOCUMENT_NOT_FOUND = "The document received as an argument does not exist in Nuxeo Platform.";

    private static final String WORKSPACES_PATH = "default-domain/workspaces/%s/%s";

    private final ProductService productService;

    public ProductController() {
        productService = Framework.getService(ProductService.class);
    }

    @GET
    public Response checkHealth() {
        return Response.ok().build();
    }

    @GET
    @Path("price/{productId}")
    public Response getProductPrice(@PathParam("productId") String productId) {
        return getProductPriceByDocumentRef(new IdRef(productId));
    }

    @GET
    @Path("price/{workspace}/{productRef}")
    public Response getProductPrice(@PathParam("workspace") String workspace,
            @PathParam("productRef") String productRef) {
        return getProductPriceByDocumentRef(new PathRef(String.format(WORKSPACES_PATH, workspace, productRef)));
    }

    private Response getProductPriceByDocumentRef(DocumentRef ref) {
        if (!getContext().getCoreSession().exists(ref)) {
            return Response.status(Response.Status.NOT_FOUND).entity(DOCUMENT_NOT_FOUND).build();
        }

        DocumentModel product = getContext().getCoreSession().getDocument(ref);
        try {
            Double price = productService.computePrice(product.getAdapter(NxProductAdapter.class));
            return Response.ok(Double.toString(price)).build();
        } catch (NuxeoException ex) {
            log.warn(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
}
