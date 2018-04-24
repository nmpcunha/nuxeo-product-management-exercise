package org.nuxeo.onboarding.exercise.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.services.ProductService;
import org.nuxeo.runtime.api.Framework;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@WebObject(type = "product")
@Produces("application/json+nxentity")
@Path("product")
public class ProductController extends ModuleRoot {

    private static final Log log = LogFactory.getLog(ProductController.class);

    static final String DOCUMENT_NOT_FOUND = "The document received as an argument does not exist in Nuxeo Platform.";

    private final String WORKSPACES_PATH = "/default-domain/workspaces/%s/%s";

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
        DocumentRef ref = new IdRef(productId);
        return getProductDocumentByRef(ref);
    }

    @GET
    @Path("price/{workspace}/{productRef}")
    public Response getProductPrice(@PathParam("workspace") String workspace, @PathParam("productRef") String productRef) {
        DocumentRef ref = new PathRef(String.format(WORKSPACES_PATH, workspace, productRef));
        return getProductDocumentByRef(ref);
    }

    private Response getProductDocumentByRef(DocumentRef ref){
        if (!getContext().getCoreSession().exists(ref)) {
            return Response.status(Response.Status.NOT_FOUND).entity(DOCUMENT_NOT_FOUND).build();
        }

        DocumentModel product = getContext().getCoreSession().getDocument(ref);
        Double price;
        try {
            price = productService.computePrice(product.getAdapter(NxProductAdapter.class));
        } catch (NuxeoException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        return Response.ok(price.toString()).build();
    }
}

