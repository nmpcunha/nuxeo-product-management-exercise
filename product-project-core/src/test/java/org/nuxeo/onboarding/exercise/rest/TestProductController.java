package org.nuxeo.onboarding.exercise.rest;

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

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.onboarding.exercise.rest.ProductController.DOCUMENT_NOT_FOUND;
import static org.nuxeo.onboarding.exercise.rest.ProductController.WORKSPACES_PATH;


@RunWith(FeaturesRunner.class)
@Features({RestServerFeature.class, OnboardingFeature.class})
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestProductController {

    @Inject
    private CoreSession session;

    private final String ENDPOINT_BASE_URL = "http://localhost:18090/product";

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
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            String responseContent = response.getEntity(String.class);
            assertNotNull(responseContent);
            assertEquals(DOCUMENT_NOT_FOUND, responseContent);
        }
    }

    @Test
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentId() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        try (CloseableClientResponse response = httpClientRule.get("price/" + visual.getId())) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            String responseContent = response.getEntity(String.class);
            assertNotNull(responseContent);
        }
    }

    @Test
    public void shouldReturnOkWhenPassingExistingProductDocumentId() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();

        try (CloseableClientResponse response = httpClientRule.get("price/" + product.getId())) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Double responseContent = response.getEntity(Double.class);
            assertNotNull(responseContent);
            assertEquals(new Double(0.0), responseContent);
        }
    }

    @Test
    public void shouldReturnNotFoundWhenPassingNonexistentWorkspace() {
        try (CloseableClientResponse response = httpClientRule.get("price/nonexistentWorkspace/anyId")) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            String responseContent = response.getEntity(String.class);
            assertNotNull(responseContent);
            assertEquals(DOCUMENT_NOT_FOUND, responseContent);
        }
    }

    @Test
    public void shouldReturnBadRequestWhenPassingNotProductTypeDocumentRef() {
        DocumentModel workspace = SampleGenerator.getWorkspace(session);
        workspace = session.createDocument(workspace);
        workspace = session.saveDocument(workspace);

        DocumentModel file = SampleGenerator.getFile(session);
        file = session.createDocument(file);
        file = session.saveDocument(file);

        try (CloseableClientResponse response = httpClientRule.get("price/" + String.format(WORKSPACES_PATH, workspace.getName(), file.getName()))) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            String responseContent = response.getEntity(String.class);
            assertNotNull(responseContent);
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
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Double responseContent = response.getEntity(Double.class);
            assertNotNull(responseContent);
            assertEquals(new Double(0.0), responseContent);
        }
    }

}