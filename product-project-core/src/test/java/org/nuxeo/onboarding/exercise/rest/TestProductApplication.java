package org.nuxeo.onboarding.exercise.rest;

import com.google.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.jaxrs.test.HttpClientTestRule;
import org.nuxeo.onboarding.exercise.OnboardingFeature;
import org.nuxeo.onboarding.exercise.adapters.NxProductAdapter;
import org.nuxeo.onboarding.exercise.samples.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.onboarding.exercise.rest.ProductController.DOCUMENT_NOT_FOUND;

@RunWith(FeaturesRunner.class)
@Features({WebEngineFeature.class, OnboardingFeature.class})
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestProductApplication {

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
    public void shouldReturnNotFoundWhenDocumentDoesNotExist() {
        try (CloseableClientResponse response = httpClientRule.get("price/unexistingId")) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            String responseContent = response.getEntity(String.class);
            assertNotNull(responseContent);
            assertEquals(DOCUMENT_NOT_FOUND, responseContent);
        }
    }

    @Test
    public void shouldReturnBadRequestWhenDocumentTypeIsNotProduct() {

    }

    @Test
    public void shouldReturnOkWhenDocumentIsProductAndExists() {
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

}