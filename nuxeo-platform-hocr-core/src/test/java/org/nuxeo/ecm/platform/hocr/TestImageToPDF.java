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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.BlobWrapper;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.ConditionalIgnoreRule;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @since 10.10
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("org.nuxeo.ecm.core.convert.api")
@Deploy("org.nuxeo.ecm.platform.convert")
@Deploy("org.nuxeo.ecm.platform.hocr")
@ConditionalIgnoreRule.Ignore(condition = IgnoreNoOCR.class, cause = "Needs an OCR engine")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestImageToPDF extends BaseConverterTest {

    private static Logger log = Logger.getLogger(TestImageToPDF.class);

    static Map<String, Blob> blobs = new HashMap<>();

    @AfterClass
    public static void tearDownClass() {
        blobs.clear();
    }

    public static void put(String name, File f, String mimeType) throws IOException {
        put(name, Blobs.createBlob(f, mimeType));
    }

    public static void put(String name, Blob b) {
        blobs.put(name, b);
        log.info("Put blob: " + name + " => " + b.getFile() + " type: " + b.getMimeType());
    }

    public static Blob get(String name) throws IOException {
        Blob b = blobs.get(name);
        assertNotNull("No such blob: " + name, b);
        log.info("Get blob: " + name + " => " + b.getFile() + " type: " + b.getMimeType());
        return b;
    }

    @Test
    public void a_testBinarize() throws Exception {
        String converterName = "hocr_binarize";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = getBlobFromPath("data/source.png");
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("scale", "5.0");

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals("image/jpeg", mainBlob.getMimeType());
        File f = new File(mainBlob.getFile().getParentFile(), "scaled.jpg");
        mainBlob.getFile().renameTo(f);
        put("scaled.jpg", f, mainBlob.getMimeType());
    }

    @Test
    public void b_testTesseract() throws Exception {
        String converterName = "hocr_tesseract";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = new SimpleBlobHolder(get("scaled.jpg"));
        Map<String, Serializable> parameters = new HashMap<>();

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals(HOCR_MIME_TYPE, mainBlob.getMimeType());
        File f = new File(mainBlob.getFile().getParentFile(), "scaled.hocr");
        mainBlob.getFile().renameTo(f);
        put("scaled.hocr", f, mainBlob.getMimeType());
    }

    @Test
    public void c_testScaleHocr() throws Exception {
        String converterName = "hocr_scale";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder pdfBH = new SimpleBlobHolder(get("scaled.hocr"));
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("percent", "20");

        BlobHolder result = cs.convert(converterName, pdfBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals(HOCR_MIME_TYPE, mainBlob.getMimeType());
        File f = new File(mainBlob.getFile().getParentFile(), "source.hocr");
        mainBlob.getFile().renameTo(f);
        put("source.hocr", f, mainBlob.getMimeType());
    }

    @Test
    public void d_testHocrMergeScaledPdf() throws Exception {
        String converterName = "hocr_pdf_box";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder ocrText = new SimpleBlobHolder(get("scaled.hocr"));
        BlobHolder sourceImage = new SimpleBlobHolder(get("scaled.jpg"));
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
        File f = new File(mainBlob.getFile().getParentFile(), "scaledpdf.pdf");
        mainBlob.getFile().renameTo(f);
        put("scaledpdf.pdf", f, mainBlob.getMimeType());
    }

    @Test
    public void e_testHocrMergePdf() throws Exception {
        String converterName = "hocr_pdf";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        BlobHolder ocrText = new SimpleBlobHolder(get("source.hocr"));
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
        File f = new File(mainBlob.getFile().getParentFile(), "output.pdf");
        mainBlob.getFile().renameTo(f);
        put("output.pdf", f, mainBlob.getMimeType());
    }

    @Test
    public void f_testHocrMergePdfBox() throws Exception {
        String converterName = "hocr_pdf_box";

        checkConverterAvailability(converterName);
        checkCommandAvailability(converterName);

        Blob ocrText = get("source.hocr");
        BlobHolder sourceImage = getBlobFromPath("data/source.jpg");
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("targetFilePath", "ocrBox.pdf");
        parameters.put("hocrFilePath", new BlobWrapper(ocrText));

        BlobHolder result = cs.convert(converterName, sourceImage, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(1, blobs.size());

        Blob mainBlob = result.getBlob();
        assertNotNull(mainBlob.getFilename());
        assertEquals("application/pdf", mainBlob.getMimeType());
        File f = new File(mainBlob.getFile().getParentFile(), "pdfbox.pdf");
        mainBlob.getFile().renameTo(f);
        put("pdfbox.pdf", f, mainBlob.getMimeType());
    }
}