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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestNxVisualAdapter {

    @Inject
    private CoreSession session;

    @Test
    public void shouldReturnNullWhenVisualIsNotAssociatedToProductCollection() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        List<NxProductAdapter> correspondingProducts = visual.getCorrespondingProducts();

        assertNotNull(correspondingProducts);
        assertEquals(0, correspondingProducts.size());
    }

    @Test
    public void shouldStoreWhenTryingToAddVisualDocumentToCollection() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(visual);

        List<NxProductAdapter> correspondingProducts = visual.getCorrespondingProducts();

        assertNotNull(correspondingProducts);
        assertEquals(1, correspondingProducts.size());
        assertEquals(product.getId(), correspondingProducts.get(0).getId());
    }

}