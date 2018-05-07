package org.nuxeo.onboarding.exercise.utils.repositories;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;
import org.nuxeo.onboarding.exercise.constants.ProductDocumentTypes;

public class OnboardingRepositoryInit implements RepositoryInit {

    public void populate(CoreSession session) {
        createSampleWorkspace(session);
    }

    private void createSampleWorkspace(CoreSession session) {
        DocumentModel workspace = session.createDocumentModel("/default-domain/workspaces/", "sampleWorkspace",
                ProductDocumentTypes.WORKSPACE.getName());
        session.createDocument(workspace);
    }

}
