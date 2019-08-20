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
> Package built here: `nuxeo-platform-hocr-package/target`

> Install with `nuxeoctl mp-install <package>`

## Use

This package provides the `Document.OCR.ToPDF` automation chain.  The chain works with JPEG, PNG, and GIF blobs.  Extraction and combination of PDF documents can be done with a variety of platform operations.

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

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).

