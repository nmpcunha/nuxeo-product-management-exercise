package org.nuxeo.onboarding.exercise.utils;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.test.runner.SimpleFeature;


@Features({AutomationFeature.class, LogCaptureFeature.class, MockitoFeature.class, TransactionalFeature.class})
@Deploy({"org.nuxeo.ecm.platform.tag", "org.nuxeo.ecm.platform.collections.core"})
@Deploy({"org.nuxeo.onboarding.exercise.product-project-core", "studio.extensions.ncunha-SANDBOX"})
@Deploy("org.nuxeo.onboarding.exercise.product-project-core:OSGI-INF/test/worldtaxes-contrib.xml")
public class OnboardingFeature extends SimpleFeature {
}