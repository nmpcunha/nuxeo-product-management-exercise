package org.nuxeo.onboarding.exercise.rest;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.onboarding.exercise.adapters.NxProductAdapter;
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

    static final String DOCUMENT_NOT_FOUND = "The document received as an argument does not exist in Nuxeo Platform.";

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
        if (!getContext().getCoreSession().exists(new IdRef(productId))) {
            return Response.status(Response.Status.NOT_FOUND).entity(DOCUMENT_NOT_FOUND).build();
        }

        DocumentModel product = ctx.getCoreSession().getDocument(new IdRef(productId));
        Double price;
        try {
            price = productService.computePrice(product.getAdapter(NxProductAdapter.class));
        } catch (NuxeoException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        return Response.ok(price).build();
    }

}

