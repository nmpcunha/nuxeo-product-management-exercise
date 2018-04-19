package org.nuxeo.onboarding.exercise.events.listeners;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.onboarding.exercise.events.ProductEvents;
import org.nuxeo.onboarding.exercise.samples.SampleGenerator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({"org.nuxeo.onboarding.exercise.product-project-core", "studio.extensions.ncunha-SANDBOX"})
public class TestDeprecatedProductListener {

    protected final List<String> events = Collections.singletonList(ProductEvents.DEPRECATED.getIdentifier());

    @Inject
    protected EventService eventService;

    @Inject
    protected CoreSession session;

    @Test
    public void listenerRegistration() {
        EventListenerDescriptor listener = eventService.getEventListener("deprecatedproduct");
        assertNotNull(listener);
        assertTrue(events.stream().allMatch(listener::acceptEvent));
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenDocumentIsNotProduct(){
        DocumentModel doc = SampleGenerator.getFile(session);
        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc);
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));
    }


}
