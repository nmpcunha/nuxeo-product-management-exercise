package org.nuxeo.onboarding.exercise.extensions.filemanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.onboarding.exercise.adapters.model.NxProductAdapter;
import org.nuxeo.onboarding.exercise.adapters.model.NxVisualAdapter;
import org.nuxeo.onboarding.exercise.utils.OnboardingFeature;
import org.nuxeo.onboarding.exercise.utils.SampleGenerator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(FeaturesRunner.class)
@Features(OnboardingFeature.class)
@Deploy("org.nuxeo.ecm.platform.filemanager.api")
@Deploy("org.nuxeo.ecm.platform.filemanager.core")
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestNxVisualImporter {

    @Inject
    private CoreSession session;

    @Inject
    private FileManager fileManager;

    @Inject
    private LogCaptureFeature.Result logCaptureResult;

    @Test
    public void shouldCreateNewVisualWhenImportingNonExistingPng() throws IOException {
        Blob blob = SampleGenerator.getPngBlob();
        DocumentModel documentModel = fileManager.createDocumentFromBlob(session, blob, session.getRootDocument().getPathAsString(), true, blob.getFile().getCanonicalPath());

        assertNotNull(documentModel);
    }

    @Test
    @LogCaptureFeature.FilterOn(logLevel = "DEBUG", loggerName = "org.nuxeo.onboarding.exercise.extensions.filemanager.NxVisualImporter")
    public void shouldReplaceVisualWhenImportingExistingPng() throws IOException, LogCaptureFeature.NoLogCaptureFilterException {
        NxVisualAdapter visual = SampleGenerator.getVisual(session);
        visual.setTitle("sample.png");
        visual.create();
        visual.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        Blob blob = SampleGenerator.getPngBlob();

        DocumentModel documentModel = fileManager.createDocumentFromBlob(session, blob, "/", true, blob.getFile().getCanonicalPath());
        assertNotNull(documentModel);

        assertEquals(visual.getId(), documentModel.getId());
        logCaptureResult.assertHasEvent();
    }

    @Test
    public void shouldCreateNewVisualWhenImportingAnExistingTitle() throws IOException {
        NxProductAdapter product = SampleGenerator.getAmericanProduct(session);
        product.setTitle("sample.png");
        product.create();
        product.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        Blob blob = SampleGenerator.getPngBlob();

        DocumentModel documentModel = fileManager.createDocumentFromBlob(session, blob, "/", true, blob.getFile().getCanonicalPath());
        assertNotNull(documentModel);

        assertNotEquals(product.getId(), documentModel.getId());
    }
}