package org.nuxeo.ecm.platform.hocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.DocumentBlobHolder;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.ConditionalIgnoreRule;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.core.convert.api")
@Deploy("org.nuxeo.ecm.platform.convert")
@Deploy("org.nuxeo.ecm.platform.hocr")
@ConditionalIgnoreRule.Ignore(condition = IgnoreNoOCR.class, cause = "Needs an OCR engine")
public class TestImageOCR extends BaseConverterTest {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void testOCRAutomationChain() throws IOException, OperationException {
        BlobHolder pdfBH = getBlobFromPath("data/source.png");
        pdfBH.getBlob().setMimeType("image/png");
        DocumentBlobHolder bh = createDocumentBlob(pdfBH.getBlob());

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(bh.getDocument());
        DocumentModel doc = (DocumentModel) automationService.run(ctx, "javascript.AttachOCRPDF",
                Collections.emptyMap());
        assertNotNull(doc);
        Serializable p = doc.getPropertyValue("files:files/0/file");
        Blob blob = (Blob) p;
        assertNotNull("Blob is empty", blob);
        assertEquals("application/pdf", blob.getMimeType());
        assertEquals("source.pdf", blob.getFilename());
    }
}
