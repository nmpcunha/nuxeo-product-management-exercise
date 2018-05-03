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

package org.nuxeo.onboarding.exercise.utils;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.test.runner.SimpleFeature;

@Features({ AutomationFeature.class, LogCaptureFeature.class, MockitoFeature.class, TransactionalFeature.class })
@Deploy({ "org.nuxeo.ecm.platform.tag", "org.nuxeo.ecm.platform.collections.core" })
@Deploy({ "org.nuxeo.onboarding.exercise.product-project-core", "studio.extensions.ncunha-SANDBOX" })
@Deploy("org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/test/worldtaxes-contrib.xml")
public class OnboardingFeature extends SimpleFeature {
}