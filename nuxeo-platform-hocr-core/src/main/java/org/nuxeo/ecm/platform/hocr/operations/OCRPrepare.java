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
@Operation(id = OCRPrepare.ID, category = Constants.CAT_DOCUMENT, label = "Prepare image for OCR", description = "Scale and binarize a source image to provide better input for the OCR engine.")
public class OCRPrepare {

    public static final String ID = "Document.OCRPrepare";

    public static final String CONVERTER = "hocr_binarize";

    @Context
    protected CoreSession session;

    @Context
    protected ConversionService service;

    @Param(name = "scale", description = "Scaling factor to use for converter", required = false)
    protected String scale = "400";

    protected Map<String, Serializable> params() {
        Map<String, Serializable> params = new HashMap<>();
        params.put("scale", scale);
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
