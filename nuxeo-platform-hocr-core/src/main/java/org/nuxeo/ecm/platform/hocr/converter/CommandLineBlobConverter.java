/*
 * (C) Copyright 2014-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger
 */

package org.nuxeo.ecm.platform.hocr.converter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.platform.convert.plugins.CommandLineConverter;
import org.nuxeo.runtime.api.Framework;

/**
 * Generic converter executing a contributed command line.
 * <p>
 * The command line to call is stored in the {@code CommandLineName} parameter.
 * <p>
 * The target file name is in the {@code targetFileName} parameter. If it's null, a temporary one will be created.
 * <p>
 * All the converter parameters are passed to the command line.
 * <p>
 * A sample contribution using this converter:
 *
 * <pre>
 *     <converter name="pdf2image" class="org.nuxeo.ecm.platform.convert.plugins.CommandLineConverter">
 *       <sourceMimeType>application/pdf</sourceMimeType>
 *       <destinationMimeType>image/jpeg</destinationMimeType>
 *       <destinationMimeType>image/png</destinationMimeType>
 *       <destinationMimeType>image/gif</destinationMimeType>
 *       <parameters>
 *         <parameter name="CommandLineName">pdftoimage</parameter>
 *       </parameters>
 *     </converter>
 * </pre>
 *
 * @since 10.10
 */
public class CommandLineBlobConverter extends CommandLineConverter {

    @Override
    protected Map<String, Blob> getCmdBlobParameters(BlobHolder blobHolder, Map<String, Serializable> parameters)
            throws ConversionException {
        Map<String, Blob> cmdBlobParams = new HashMap<>();
        cmdBlobParams.put(SOURCE_FILE_PATH_KEY, blobHolder.getBlob());
        if (parameters != null && !parameters.isEmpty()) {
            Iterator<Map.Entry<String, Serializable>> iter = parameters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Serializable> entry = iter.next();
                if (entry.getValue() instanceof Blob) {
                    cmdBlobParams.put(entry.getKey(), (Blob) entry.getValue());
                    iter.remove();
                }
            }
        }
        return cmdBlobParams;
    }

    @Override
    protected Map<String, String> getCmdStringParameters(BlobHolder blobHolder, Map<String, Serializable> parameters)
            throws ConversionException {
        String tmpDir = getTmpDirectory(parameters);
        Path tmpDirPath = tmpDir != null ? Paths.get(tmpDir) : null;
        try {
            Path outDirPath = tmpDirPath != null ? Files.createTempDirectory(tmpDirPath, null)
                    : Framework.createTempDirectory(null);

            Map<String, String> cmdStringParams = new HashMap<>();
            cmdStringParams.put(OUT_DIR_PATH_KEY, outDirPath.toString());

            String targetFileName = (String) parameters.get(TARGET_FILE_NAME_KEY);
            Path targetFilePath;
            if (targetFileName == null) {
                targetFilePath = Files.createTempFile(outDirPath, null, null);
            } else {
                targetFilePath = Paths.get(outDirPath.toString(), targetFileName);
            }
            cmdStringParams.put(TARGET_FILE_PATH_KEY, targetFilePath.toString());
            if (targetFileName == null) {
                // do not keep a temporary file, just created to get a path
                Files.delete(targetFilePath);
            }

            // XXX: Allow for override of parameters here!!

            // pass all the converter descriptor parameters to the commandline
            for (Map.Entry<String, String> entry : initParameters.entrySet()) {
                if (!RESERVED_PARAMETERS.contains(entry.getKey())) {
                    cmdStringParams.put(entry.getKey(), entry.getValue());
                }
            }
            // pass all converter parameters to the command line
            for (Map.Entry<String, Serializable> entry : parameters.entrySet()) {
                if (!RESERVED_PARAMETERS.contains(entry.getKey())) {
                    cmdStringParams.put(entry.getKey(), (String) entry.getValue());
                }
            }

            return cmdStringParams;
        } catch (IOException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }
}
