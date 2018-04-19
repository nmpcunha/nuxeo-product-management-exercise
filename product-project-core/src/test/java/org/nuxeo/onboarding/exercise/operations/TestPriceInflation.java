package org.nuxeo.onboarding.exercise.operations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.InvalidChainException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.onboarding.exercise.adapters.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.samples.SampleGenerator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({"org.nuxeo.onboarding.exercise.product-project-core", "studio.extensions.ncunha-SANDBOX"})
public class TestPriceInflation {

    private static final Double INFLATION_RATE = 0.10;

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test(expected = OperationException.class)
    public void shouldThrowExceptionWhenNoParamsPassed() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        automationService.run(ctx, PriceInflation.ID);
    }

    @Test(expected = InvalidChainException.class)
    public void shouldThrowExceptionWhenVoidInput() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test(expected = InvalidChainException.class)
    public void shouldThrowExceptionWhenNullInput() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(null);
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test(expected = InvalidChainException.class)
    public void shouldThrowExceptionWhenBlobInput() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(Mockito.any(Blob.class));
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test(expected = InvalidChainException.class)
    public void shouldThrowExceptionWhenBlobListInput() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(Mockito.anyListOf(Blob.class));
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenInputIsNotProduct() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(SampleGenerator.getFile(session));
        automationService.run(ctx, PriceInflation.ID, getParams());

        // Since it is already test on ProductService, should we just verify that the service is called?
    }

    @Test
    public void shouldInflatePriceAndSaveDocumentWhenInputIsDocument() throws OperationException {
        NxProductAdapter productAdapter = SampleGenerator.getAsianProduct(session);
        productAdapter.create();

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(productAdapter.getDocumentModel());

        DocumentModel doc = (DocumentModel) automationService.run(ctx, PriceInflation.ID, getParams());

        // Could we use BigDecimal instead of Double?
        assertEquals(new DecimalFormat("####0.00").format(495.00), new DecimalFormat("####0.00").format(doc.getPropertyValue(NxProduct.PRICE.getPropertyXPath())));
    }

    @Test
    public void shouldInflatePriceAndSaveDocumentWhenInputIsDocumentList() throws OperationException {
        OperationContext ctx = new OperationContext(session);

        NxProductAdapter asianProduct = SampleGenerator.getAsianProduct(session);
        asianProduct.create();

        NxProductAdapter americanProduct = SampleGenerator.getAmericanProduct(session);
        americanProduct.create();

        DocumentModelList products = new DocumentModelListImpl(Arrays.asList(asianProduct.getDocumentModel(), americanProduct.getDocumentModel()));
        ctx.setInput(products);

        DocumentModelList docs = (DocumentModelList) automationService.run(ctx, PriceInflation.ID, getParams());

        for (DocumentModel doc: docs) {
            assertEquals(ProductDocumentTypes.PRODUCT.getName(), doc.getDocumentType().getName());

        }

        //TODO Assert Each Document for price inflation
        fail();

    }

    private Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("rate", INFLATION_RATE);
        return params;
    }
}
