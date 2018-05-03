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

package org.nuxeo.onboarding.exercise.adapters;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

public abstract class AbstractDocumentAdapter {

    private final CoreSession session;

    private DocumentModel doc;

    public AbstractDocumentAdapter(DocumentModel doc) {
        this.doc = doc;
        session = doc.getCoreSession();
    }

    public void create() {
        doc = session.createDocument(doc);
    }

    public void save() {
        doc = session.saveDocument(doc);
    }

    public DocumentModel getDocumentModel() {
        return doc;
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    public DocumentModel getParent() {
        return session.getParentDocument(doc.getRef());
    }

    //region Technical Properties

    public String getId() {
        return doc.getId();
    }

    public DocumentRef getRef() {
        return doc.getRef();
    }

    public String getName() {
        return doc.getName();
    }

    public String getPath() {
        return doc.getPathAsString();
    }

    public String getState() {
        return doc.getCurrentLifeCycleState();
    }

    //endregion

    //region Metadata

    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue("dc:title", value);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue("dc:description");
    }

    public void setDescription(String value) {
        doc.setPropertyValue("dc:description", value);
    }

    //endregion
}
