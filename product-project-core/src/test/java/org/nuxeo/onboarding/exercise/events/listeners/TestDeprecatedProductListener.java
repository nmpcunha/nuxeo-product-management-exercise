package org.nuxeo.onboarding.exercise.events.listeners;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLSession;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.events.ProductEvents;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
public class TestDeprecatedProductListener {

    private final List<String> events = Collections.singletonList(ProductEvents.DEPRECATED.getIdentifier());

    @Inject
    private EventService eventService;

    @Inject
    private CoreSession session;

    @Inject
    private UserManager userManager;

    @Before
    public void setUp() {
        Framework.getProperties().setProperty(SQLSession.ALLOW_NEGATIVE_ACL_PROPERTY, "true");
    }

    @Test
    public void listenerRegistration() {
        EventListenerDescriptor listener = eventService.getEventListener("deprecatedproduct");
        assertNotNull(listener);
        assertTrue(events.stream().allMatch(listener::acceptEvent));
    }

    @Test(expected = NuxeoException.class)
    public void shouldThrowExceptionWhenDocumentIsNotProduct() {
        DocumentModel doc = SampleGenerator.getFile(session);
        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc);
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));
    }

    @Test
    public void shouldMoveVisualsWhenDocumentIsProduct() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel parent = session.getParentDocument(visual.getRef());

        assertNotNull(parent);
        assertEquals("Folder", parent.getType());
        assertTrue(parent.isFolder());
        assertEquals("hiddenFolder", parent.getName());
    }

    @Test
    public void shouldAccessFolderWhenAuthorized() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel folder = session.getParentDocument(visual.getRef());

        ACE[] acEs = folder.getACP().getACL("onboarding").getACEs();

        assertEquals(1, acEs.length);
        assertEquals("Group1", acEs[0].getUsername());
        assertEquals(SecurityConstants.READ, acEs[0].getPermission());
        assertFalse(acEs[0].isGranted());


        DocumentModel newUser = userManager.getBareUserModel();
        newUser.setProperty("user", "username", "authorizedUser");
        userManager.createUser(newUser);

        NuxeoPrincipal authorizedUser = userManager.getPrincipal("authorizedUser");
        authorizedUser.setGroups(Collections.singletonList("administrators"));
        userManager.updateUser(authorizedUser.getModel());

        CoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), authorizedUser);

        DocumentModel document = userSession.getDocument(folder.getRef());
        assertNotNull(document);
        assertEquals("Folder", document.getType());
        assertTrue(document.isFolder());
        assertEquals("hiddenFolder", document.getName());

        ((CloseableCoreSession) userSession).close();
    }

    @Test(expected = DocumentSecurityException.class)
    public void shouldThrowExceptionWhenTryingToAccessFolderWithUnauthorizedUsers() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();
        visual.save();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.save();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel folder = session.getParentDocument(visual.getRef());

        ACE[] acEs = folder.getACP().getACL("onboarding").getACEs();

        assertEquals(1, acEs.length);
        assertEquals("Group1", acEs[0].getUsername());
        assertEquals(SecurityConstants.READ, acEs[0].getPermission());
        assertFalse(acEs[0].isGranted());


        DocumentModel newUser = userManager.getBareUserModel();
        newUser.setProperty("user", "username", "unauthorizedUser");
        userManager.createUser(newUser);

        DocumentModel newGroup = userManager.getBareGroupModel();
        newGroup.setProperty("group", "groupname", acEs[0].getUsername());
        userManager.createGroup(newGroup);


        NuxeoPrincipal unauthorizedUser = userManager.getPrincipal("unauthorizedUser");
        unauthorizedUser.setGroups(Collections.singletonList(acEs[0].getUsername()));
        userManager.updateUser(unauthorizedUser.getModel());

        CoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), unauthorizedUser);
        try {
            userSession.getDocument(folder.getRef());
        } finally {
            ((CloseableCoreSession) userSession).close();
        }
    }
}
