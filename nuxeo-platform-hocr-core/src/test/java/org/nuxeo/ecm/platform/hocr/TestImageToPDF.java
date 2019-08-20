/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Miguel Nixo
 */

package org.nuxeo.ecm.platform.hocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.BlobWrapper;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @since 5.2
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("org.nuxeo.ecm.core.convert.api")
@Deploy("org.nuxeo.ecm.platform.convert")
@Deploy("org.nuxeo.ecm.platform.hocr")
public class TestImageToPDF extends BaseConverterTest {

    @Test
    public void testBinarize() throws Exception {
        String converterName = "hocr_binarize";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = getBlobFromPath("data/source.png");
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("scale", "500");

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals("image/jpeg", mainBlob.getMimeType());
    }

    @Test
    public void testTesseract() throws Exception {
        String converterName = "hocr_tesseract";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = getBlobFromPath("data/scaled.jpg");
        Map<String, Serializable> parameters = new HashMap<>();

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals(HOCR_MIME_TYPE, mainBlob.getMimeType());
    }

    @Test
    public void testScaleHocr() throws Exception {
        String converterName = "hocr_scale";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = getBlobFromPath("data/large.hocr");
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("scale", "20");

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals(HOCR_MIME_TYPE, mainBlob.getMimeType());
    }

    @Test
    public void testHocrMergePdf() throws Exception {
        String converterName = "hocr_pdf";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder ocrText = getBlobFromPath("data/scaled.hocr");
        BlobHolder sourceImage = getBlobFromPath("data/source.jpg");
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("targetFilePath", "ocr.pdf");
        parameters.put("hocrFilePath", new BlobWrapper(ocrText.getBlob()));

        BlobHolder result = cs.convert(converterName, sourceImage, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals("application/pdf", mainBlob.getMimeType());
    }
}