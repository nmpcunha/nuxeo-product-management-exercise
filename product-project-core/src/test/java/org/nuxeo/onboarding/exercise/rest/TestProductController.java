package org.nuxeo.onboarding.exercise.rest;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.jaxrs.test.HttpClientTestRule;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.onboarding.exercise.rest.ProductController.DOCUMENT_NOT_FOUND;


@RunWith(FeaturesRunner.class)
@Features({RestServerFeature.class, OnboardingFeature.class})
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestProductController {

    private final String ENDPOINT_BASE_URL = "http://localhost:18090/product";

    @Inject
    private CoreSession session;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Rule
    public HttpClientTestRule httpClientRule = new HttpClientTestRule.Builder()
            .adminCredentials()
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
            assertResponse(response, Response.Status.NOT_FOUND, DOCUMENT_NOT_FOUND);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentId() throws LogCaptureFeature.NoLogCaptureFilterException {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        try (CloseableClientResponse response = httpClientRule.get("price/" + visual.getId())) {
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

        try (CloseableClientResponse response = httpClientRule.get("price/" + product.getId())) {
            assertResponse(response, Response.Status.OK);
        }
    }

    @Test
    public void shouldReturnNotFoundWhenPassingNonexistentWorkspace() {
        try (CloseableClientResponse response = httpClientRule.get("price/nonexistentWorkspace/anyId")) {
            assertResponse(response, Response.Status.NOT_FOUND, DOCUMENT_NOT_FOUND);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "WARN", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentRef() throws LogCaptureFeature.NoLogCaptureFilterException {
        DocumentModel workspace = SampleGenerator.getWorkspace(session);
        workspace = session.createDocument(workspace);
        workspace = session.saveDocument(workspace);

        DocumentModel file = SampleGenerator.getFile(session);
        file = session.createDocument(file);
        file = session.saveDocument(file);

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        try (CloseableClientResponse response = httpClientRule.get("price/" + workspace.getName() + "/" + file.getName())) {
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

        try (CloseableClientResponse response = httpClientRule.get("price/" + workspace.getName() + "/" + product.getName())) {
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