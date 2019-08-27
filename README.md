# Nuxeo Platform - OCR Tools

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-platform-hocr-master)](https://qa.nuxeo.org/jenkins/view/Sandbox/job/Sandbox/job/sandbox_nuxeo-platform-hocr-master/)

OCR tools that produce a searchable PDF with extracted results.

## Dependencies

[Tesseract](https://github.com/tesseract-ocr/tesseract/wiki) - OCR Engine

[hocr-tools](https://github.com/nuxeo-sandbox/hocr-tools/) - hOCR Processing Tools

### Installation

1. Use your favorite package manager to install the latest 4+ version of `tesseract`. 
2. Use pip and python 2.7 to install the set of hocr-tools for Nuxeo:

```
pip install git+https://github.com/nuxeo-sandbox/hocr-tools/
```

## Build

Build with maven (at least 3.3)

```
mvn clean install
```

> Test with tesseract, hocr-tools installation, add to maven build: `-Dnuxeo.test.hocr=true`

> Package built here: `nuxeo-platform-hocr-package/target`

> Install with `nuxeoctl mp-install <package>`


## Use

This package provides the `javascript.AttachOCRPDFF` automation script.  The script works with JPEG, PNG, and GIF blobs.  Extraction and combination of PDF documents can be done with a variety of platform operations.

Individual operations are also provided to generate new automation chains and processes within the platform.  In general, processing should follow the example pattern:

* `Document.OCRPrepare` - scale, binarize and prepare the image for processing.
* `Blob.OCRExtract` - perform OCR on the input blob, produce hOCR.
* `Blob.OCRPostProcess` - scale the hOCR back to the original image dimensions.
* `Document.OCRCombine` - combine the scaled hOCR output with the input image.  Produces a searchable PDF document.

## Support

**These features are sand-boxed and not yet part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## About Nuxeo

[Nuxeo](www.nuxeo.com), developer of the leading Content Services Platform, is reinventing enterprise content management (ECM) and digital asset management (DAM). Nuxeo is fundamentally changing how people work with data and content to realize new value from digital information. Its cloud-native platform has been deployed by large enterprises, mid-sized businesses and government agencies worldwide. Customers like Verizon, Electronic Arts, ABN Amro, and the Department of Defense have used Nuxeo's technology to transform the way they do business. Founded in 2008, the company is based in New York with offices across the United States, Europe, and Asia.

Learn more at www.nuxeo.com.