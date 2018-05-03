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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.nuxeo.onboarding.exercise.rest.ProductController.DOCUMENT_NOT_FOUND;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.jaxrs.test.HttpClientTestRule;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.services.ProductService;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class, OnboardingFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestProductController {

    private final String ENDPOINT_BASE_URL = "http://localhost:18090/product";

    @Inject
    private CoreSession session;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Mock
    @RuntimeService
    private ProductService productService;

    @Rule
    public HttpClientTestRule httpClientRule = new HttpClientTestRule.Builder().adminCredentials()
                                                                               .url(ENDPOINT_BASE_URL)
                                                                               .build();

    @Test
    public void shouldReturnOkWhenServerIsUpAndHealthy() {
        try (CloseableClientResponse response = httpClientRule.get("")) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void shouldReturnNotFoundWhenPassingNonexistentDocumentId() {
        try (CloseableClientResponse response = httpClientRule.get("price/nonexistentId")) {
            verify(productService, never()).computePrice(any(NxProductAdapter.class));
            assertResponse(response, Response.Status.NOT_FOUND, DOCUMENT_NOT_FOUND);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentId()
            throws LogCaptureFeature.NoLogCaptureFilterException {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        when(productService.computePrice(null)).thenThrow(new NuxeoException());

        try (CloseableClientResponse response = httpClientRule.get("price/" + visual.getId())) {
            verify(productService).computePrice(null);
            assertResponse(response, Response.Status.BAD_REQUEST);
            logCaptureResult.assertHasEvent();
        }
    }

    @Test
    public void shouldReturnOkWhenPassingExistingProductDocumentId() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        when(productService.computePrice(any(NxProductAdapter.class))).thenReturn(1.0);

        try (CloseableClientResponse response = httpClientRule.get("price/" + product.getId())) {
            verify(productService).computePrice(any(NxProductAdapter.class));
            assertResponse(response, Response.Status.OK);
        }
    }

    @Test
    public void shouldReturnNotFoundWhenPassingNonexistentWorkspace() {
        try (CloseableClientResponse response = httpClientRule.get("price/nonexistentWorkspace/anyId")) {
            verify(productService, never()).computePrice(any(NxProductAdapter.class));
            assertResponse(response, Response.Status.NOT_FOUND, DOCUMENT_NOT_FOUND);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentRef()
            throws LogCaptureFeature.NoLogCaptureFilterException {
        DocumentModel workspace = SampleGenerator.getWorkspace(session);
        workspace = session.createDocument(workspace);
        workspace = session.saveDocument(workspace);

        DocumentModel file = SampleGenerator.getFile(session);
        file = session.createDocument(file);
        file = session.saveDocument(file);

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        when(productService.computePrice(null)).thenThrow(new NuxeoException());

        try (CloseableClientResponse response = httpClientRule.get(
                "price/" + workspace.getName() + "/" + file.getName())) {
            verify(productService).computePrice(null);
            assertResponse(response, Response.Status.BAD_REQUEST);
            logCaptureResult.assertHasEvent();
        }
    }

    @Test
    public void shouldReturnOkWhenPassingExistingProductWorkspaceAndDocumentRef() {
        DocumentModel workspace = SampleGenerator.getWorkspace(session);
        workspace = session.createDocument(workspace);
        workspace = session.saveDocument(workspace);

        NxProductAdapter product = SampleGenerator.getEuropeanProduct(session);
        product.create();
        product.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        when(productService.computePrice(any(NxProductAdapter.class))).thenReturn(2.0);

        try (CloseableClientResponse response = httpClientRule.get(
                "price/" + workspace.getName() + "/" + product.getName())) {
            verify(productService).computePrice(any(NxProductAdapter.class));
            assertResponse(response, Response.Status.OK);
        }
    }

    private void assertResponse(ClientResponse response, Response.Status expectedStatus) {
        assertEquals(expectedStatus.getStatusCode(), response.getStatus());
        String responseContent = response.getEntity(String.class);
        assertNotNull(responseContent);
    }

    private void assertResponse(ClientResponse response, Response.Status expectedStatus, String expectedContent) {
        assertEquals(expectedStatus.getStatusCode(), response.getStatus());
        String responseContent = response.getEntity(String.class);
        assertNotNull(responseContent);
        assertEquals(expectedContent, responseContent);
    }
}