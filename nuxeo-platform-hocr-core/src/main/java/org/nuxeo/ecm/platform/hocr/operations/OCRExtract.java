package org.nuxeo.ecm.platform.hocr.operations;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;

/**
 *
 */
@Operation(id = OCRExtract.ID, category = Constants.CAT_BLOB, label = "Extract text from an image", description = "Use 'tesseract' to extract text (OCR) from an image.", aliases = "Blob.OCRExtract")
public class OCRExtract {

    public static final String ID = "Document.OCRExtract";

    public static final String CONVERTER = "hocr_tesseract";

    @Context
    protected CoreSession session;

    @Context
    protected ConversionService service;

    @Param(name = "language", description = "Language(s) to use for tesseract engine", required = false)
    protected String language = "eng";

    protected Map<String, Serializable> params() {
        Map<String, Serializable> params = new HashMap<>();
        params.put("language", language);
        return params;
    }

    @OperationMethod
    public Blob run(DocumentModel doc) throws IOException {
        BlobHolder bh = doc.getAdapter(BlobHolder.class);
        if (bh == null) {
            return null;
        }
        return service.convert(CONVERTER, bh, params()).getBlob();
    }

    @OperationMethod
    public Blob run(Blob blob) throws IOException {
        return service.convert(CONVERTER, new SimpleBlobHolder(blob), params()).getBlob();
    }

    @OperationMethod
    public BlobList run(BlobList blobs) throws IOException {
        BlobList bl = new BlobList();
        for (Blob blob : blobs) {
            bl.add(this.run(blob));
        }
        return bl;
    }
}
