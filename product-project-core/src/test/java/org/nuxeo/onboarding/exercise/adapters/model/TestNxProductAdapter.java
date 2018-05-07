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

package org.nuxeo.onboarding.exercise.adapters.model;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.onboarding.exercise.constants.model.NxProduct;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.onboarding.exercise.utils.features.OnboardingFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxProductAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldCreateDocumentWhenRefIsNull() {
        NxProductAdapter adapter = SampleGenerator.getUnreferencedProduct(session);
        adapter.create();

        assertTrue(session.exists(adapter.getDocumentModel().getRef()));
    }

    @Test
    public void shouldCreateDocumentWhenRefIsProvided() {
        NxProductAdapter adapter = SampleGenerator.getAsianProduct(session);
        adapter.create();

        assertTrue(session.exists(adapter.getDocumentModel().getRef()));
    }

    @Test(expected = DocumentNotFoundException.class)
    public void shouldThrowExceptionWhenDocumentIsNotCreated() {
        NxProductAdapter asianProduct = SampleGenerator.getAsianProduct(session);
        asianProduct.save();
    }

    @Test
    public void shouldSaveChangesWhenDocumentIsAlreadyCreated() {
        NxProductAdapter adapter = SampleGenerator.getAsianProduct(session);
        adapter.create();

        String newTitle = "New Title";
        adapter.setTitle(newTitle);
        adapter.save();

        assertEquals(newTitle, adapter.getTitle());
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenTryingToAddNonVisualDocumentToCollection() {
        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();

        product.addVisual(null);
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        NxVisualAdapter nxVisual = SampleGenerator.getVisual(session);
        nxVisual.create();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.addVisual(nxVisual);

        List<DocumentRef> visuals = product.getVisuals();
        assertNotNull(visuals);
        assertEquals(1, visuals.size());
        assertEquals(nxVisual.getRef(), visuals.get(0));
    }

    @Test
    public void shouldBeAbleToSetAndGetComplexProperty() {
        NxProductAdapter europeanProduct = SampleGenerator.getEuropeanProduct(session);

        Map<String, Serializable> distributor = new HashMap<>();
        distributor.put(NxProduct.DISTRIBUTOR_NAME.getPropertyKey(), "DHL");
        distributor.put(NxProduct.DISTRIBUTOR_SELL_LOCATION.getPropertyKey(), "Portugal");

        europeanProduct.setDistributor(distributor);

        Map<String, Serializable> productDistributor = europeanProduct.getDistributor();

        assertNotNull(productDistributor);
        assertEquals(distributor, productDistributor);
    }
}
