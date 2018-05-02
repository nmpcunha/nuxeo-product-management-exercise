package org.nuxeo.onboarding.exercise.rest;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
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

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.nuxeo.onboarding.exercise.rest.ProductController.DOCUMENT_NOT_FOUND;
import static org.nuxeo.onboarding.exercise.rest.ProductController.WORKSPACES_PATH;


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

    @Mock
    @RuntimeService
    private ProductService productService;

    @Rule
    public HttpClientTestRule httpClientRule = new HttpClientTestRule.Builder()
            .adminCredentials()
            .url(ENDPOINT_BASE_URL)
            .build();

    @Before
    public void setup() {
        when(productService.computePrice(any(NxProductAdapter.class))).thenReturn(0.0);
    }

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
            assertNotFoundResponse(response);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "DEBUG", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentId() throws LogCaptureFeature.NoLogCaptureFilterException {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        try (CloseableClientResponse response = httpClientRule.get("price/" + visual.getId())) {
            verify(productService).computePrice(null);
            assertBadRequestResponse(response);
            logCaptureResult.assertHasEvent();
        }
    }

    @Test
    public void shouldReturnOkWhenPassingExistingProductDocumentId() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();

        try (CloseableClientResponse response = httpClientRule.get("price/" + product.getId())) {
            verify(productService).computePrice(product.getDocumentModel().getAdapter(NxProductAdapter.class));
            assertResponsePriceEntity(response);
        }
    }

    @Test
    public void shouldReturnNotFoundWhenPassingNonexistentWorkspace() {
        try (CloseableClientResponse response = httpClientRule.get("price/nonexistentWorkspace/anyId")) {
            verify(productService, never()).computePrice(any(NxProductAdapter.class));
            assertNotFoundResponse(response);
        }
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "DEBUG", loggerName = "org.nuxeo.onboarding.exercise.rest.ProductController")
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentRef() throws LogCaptureFeature.NoLogCaptureFilterException {
        DocumentModel workspace = SampleGenerator.getWorkspace(session);
        workspace = session.createDocument(workspace);
        workspace = session.saveDocument(workspace);

        DocumentModel file = SampleGenerator.getFile(session);
        file = session.createDocument(file);
        file = session.saveDocument(file);

        try (CloseableClientResponse response = httpClientRule.get("price/" + String.format(WORKSPACES_PATH, workspace.getName(), file.getName()))) {
            verify(productService).computePrice(null);
            assertBadRequestResponse(response);
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

        try (CloseableClientResponse response = httpClientRule.get("price/" + String.format(WORKSPACES_PATH, workspace.getName(), product.getProductName()))) {
            verify(productService).computePrice(product.getDocumentModel().getAdapter(NxProductAdapter.class));
            assertResponsePriceEntity(response);
        }
    }

    private void assertResponsePriceEntity(ClientResponse response) {
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Double responseContent = response.getEntity(Double.class);
        assertNotNull(responseContent);
    }

    private void assertBadRequestResponse(ClientResponse response) {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        String responseContent = response.getEntity(String.class);
        assertNotNull(responseContent);
    }

    private void assertNotFoundResponse(ClientResponse response){
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        String responseContent = response.getEntity(String.class);
        assertNotNull(responseContent);
        assertEquals(DOCUMENT_NOT_FOUND, responseContent);
    }

}