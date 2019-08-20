package org.nuxeo.ecm.platform.hocr.operations;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
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
import org.nuxeo.ecm.core.api.impl.blob.BlobWrapper;
import org.nuxeo.ecm.core.convert.api.ConversionService;

/**
 *
 */
@Operation(id = OCRCombine.ID, category = Constants.CAT_BLOB, label = "Combine OCR output with source Document", description = "Takes a JPEG input combined with an hOCR file to produce a searchable PDF.")
public class OCRCombine {

    public static final String ID = "Document.OCRCombine";

    public static final String CONVERTER = "hocr_pdf";

    @Context
    protected OperationContext ctx;

    @Context
    protected CoreSession session;

    @Context
    protected ConversionService service;

    @Param(name = "hocrVar", description = "Name of the context variable containing the hOCR text data.", required = false)
    protected String name = "text";

    public Blob restoreBlob() throws OperationException {
        Object obj = ctx.get(name);
        if (obj instanceof Blob) {
            return (Blob) obj;
        }
        throw new OperationException(
                "Illegal state error for restore file operation. The context map doesn't contains a file variable with name "
                        + name);
    }

    protected Map<String, Serializable> params() throws OperationException {
        Map<String, Serializable> params = new HashMap<>();
        params.put("hocrFilePath", new BlobWrapper(restoreBlob()));
        return params;
    }

    protected BlobHolder checkBlob(BlobHolder bh) {
        Blob img = bh.getBlob();
        if ("image/jpeg".equals(img.getMimeType()) || "image/jpg".equals(img.getMimeType())) {
            return bh;
        }
        return service.convertToMimeType("image/jpeg", bh, Collections.emptyMap());
    }

    @OperationMethod
    public Blob run(DocumentModel doc) throws OperationException {
        BlobHolder bh = doc.getAdapter(BlobHolder.class);
        if (bh == null) {
            return null;
        }
        bh = checkBlob(bh);
        return service.convert(CONVERTER, bh, params()).getBlob();
    }

    @OperationMethod
    public Blob run(Blob blob) throws OperationException {
        BlobHolder bh = new SimpleBlobHolder(blob);
        bh = checkBlob(bh);
        return service.convert(CONVERTER, bh, params()).getBlob();
    }

    @OperationMethod
    public BlobList run(BlobList blobs) throws OperationException {
        BlobList bl = new BlobList();
        for (Blob blob : blobs) {
            bl.add(this.run(blob));
        }
        return bl;
    }
}