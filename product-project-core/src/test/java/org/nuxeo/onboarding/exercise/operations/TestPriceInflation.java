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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.InvalidChainException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.JSONBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.services.ProductService;
import org.nuxeo.onboarding.exercise.utils.features.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestPriceInflation {

    private static final Double INFLATION_RATE = 0.10;

    @Inject
    private CoreSession session;

    @Inject
    private AutomationService automationService;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Mock
    @RuntimeService
    private ProductService productService;

    @Before
    public void setup() {
        when(productService.computePrice(null)).thenThrow(new NuxeoException());
    }

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
        ctx.setInput(new JSONBlob(""));
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Ignore("It seems that a BlobList is converted to DocumentModelList, which seems to be incorrect.")
    @Test(expected = InvalidChainException.class)
    public void shouldThrowExceptionWhenBlobListInput() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(new BlobList(new JSONBlob(""))); // Why BlobList extends ArrayList?
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenInputIsNotProduct() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(SampleGenerator.getFile(session));
        automationService.run(ctx, PriceInflation.ID, getParams());
    }

    @Test
    public void shouldInflatePriceAndSaveDocumentWhenInputIsDocument() throws OperationException {
        NxProductAdapter productAdapter = SampleGenerator.getAsianProduct(session);
        productAdapter.create();

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(productAdapter.getDocumentModel());

        automationService.run(ctx, PriceInflation.ID, getParams());

        verify(productService).computePrice(productAdapter.getDocumentModel().getAdapter(NxProductAdapter.class));
    }

    @Test
    public void shouldInflatePriceAndSaveDocumentWhenInputIsDocumentList() throws OperationException {
        OperationContext ctx = new OperationContext(session);

        NxProductAdapter asianProduct = SampleGenerator.getAsianProduct(session);
        asianProduct.create();

        NxProductAdapter americanProduct = SampleGenerator.getAmericanProduct(session);
        americanProduct.create();

        ctx.setInput(Arrays.asList(asianProduct.getDocumentModel(), americanProduct.getDocumentModel()));
        DocumentModelList docs = (DocumentModelList) automationService.run(ctx, PriceInflation.ID, getParams());

        for (DocumentModel doc : docs) {
            assertEquals(ProductDocumentTypes.PRODUCT.getName(), doc.getDocumentType().getName());
            verify(productService).computePrice(doc.getAdapter(NxProductAdapter.class));
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.operations.PriceInflation")
    public void shouldLogWhenListContainsInvalidDocumentTypes() throws OperationException, LogCaptureFeature.NoLogCaptureFilterException {
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(Collections.singletonList(SampleGenerator.getVisual(session).getDocumentModel()));
        automationService.run(ctx, PriceInflation.ID, getParams());

        logCaptureResult.assertHasEvent();
    }

    private Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("rate", INFLATION_RATE);
        return params;
    }
}
