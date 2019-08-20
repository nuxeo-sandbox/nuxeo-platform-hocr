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
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;

/**
 *
 */
@Operation(id = OCRPostProcess.ID, category = Constants.CAT_BLOB, label = "Scale the hOCR output", description = "Scale the hOCR output to match the original image dimensions.")
public class OCRPostProcess {

    public static final String ID = "Blob.OCRPostProcess";

    public static final String CONVERTER = "hocr_scale";

    @Context
    protected CoreSession session;

    @Context
    protected ConversionService service;

    @Param(name = "scale", description = "Scaling factor to use for converter (inverse of prep)", required = false)
    protected String scale = "20";

    protected Map<String, Serializable> params() {
        Map<String, Serializable> params = new HashMap<>();
        params.put("scale", scale);
        return params;
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
