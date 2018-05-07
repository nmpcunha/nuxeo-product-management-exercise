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

package org.nuxeo.onboarding.exercise.events.listeners;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

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
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;
import org.nuxeo.onboarding.exercise.events.ProductEvents;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.onboarding.exercise.utils.features.OnboardingFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

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
    public void shouldBeRegistered() {
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
    public void shouldCreateFolderAndMoveVisualsWhenDocumentIsProduct() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(),
                product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel parent = session.getParentDocument(visual.getRef());

        assertNotNull(parent);
        assertEquals(ProductDocumentTypes.FOLDER.getName(), parent.getType());
        assertTrue(parent.isFolder());
        assertEquals("hiddenFolder", parent.getName());
    }

    @Test
    public void shouldReuseFolderAndMoveVisualsWhenDocumentIsProduct() {
        DocumentModel folder = session.createDocumentModel(SampleGenerator.WORKSPACE_PATH, "hiddenFolder",
                ProductDocumentTypes.FOLDER.getName());
        folder = session.createDocument(folder);

        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(),
                product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel parent = session.getParentDocument(visual.getRef());

        assertNotNull(parent);
        assertEquals(ProductDocumentTypes.FOLDER.getName(), parent.getType());
        assertTrue(parent.isFolder());

        assertEquals(folder, parent);
    }

    @Test
    public void shouldAccessFolderWhenAuthorized() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(),
                product.getDocumentModel());
        eventService.fireEvent(ctx.newEvent(ProductEvents.DEPRECATED.getIdentifier()));

        DocumentModel folder = session.getParentDocument(visual.getRef());

        ACE[] acEs = folder.getACP().getACL("onboarding").getACEs();

        assertEquals(1, acEs.length);
        assertEquals("Group1", acEs[0].getUsername());
        assertEquals(SecurityConstants.READ, acEs[0].getPermission());
        assertFalse(acEs[0].isGranted());

        DocumentModel newUser = userManager.getBareUserModel();
        newUser.setProperty("user", "username", "authorizedUser");
        newUser.setProperty("user", "groups", Collections.singletonList("administrators"));
        userManager.createUser(newUser);

        NuxeoPrincipal authorizedUser = userManager.getPrincipal("authorizedUser");
        userManager.updateUser(authorizedUser.getModel());

        CoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), authorizedUser);

        DocumentModel document = userSession.getDocument(folder.getRef());
        assertNotNull(document);
        assertEquals(ProductDocumentTypes.FOLDER.getName(), document.getType());
        assertTrue(document.isFolder());
        assertEquals("hiddenFolder", document.getName());

        ((CloseableCoreSession) userSession).close();
    }

    @Test(expected = DocumentSecurityException.class)
    public void shouldThrowExceptionWhenTryingToAccessFolderWithUnauthorizedUsers() {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.create();

        NxProductAdapter product = SampleGenerator.getAsianProduct(session);
        product.create();
        product.addVisual(visual);

        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(),
                product.getDocumentModel());
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
