// src/components/VoirMonCV.js
import React, { useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/esm/Page/AnnotationLayer.css"; // Optional: For annotations
import 'react-pdf/dist/Page/TextLayer.css'; // Optional: For text layer
import "./VoirMonCV.css"; // Optional: For custom styling
import pdfFile from '../assets/pdf.pdf';

// Configure PDF.js worker
pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;


const VoirMonCV = () => {
    const [numPages, setNumPages] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
        setPageNumber(1); // Reset to first page on load
    };

    const goToPrevPage = () =>
        setPageNumber((prevPage) => (prevPage <= 1 ? 1 : prevPage - 1));

    const goToNextPage = () =>
        setPageNumber((prevPage) => (prevPage >= numPages ? numPages : prevPage + 1));

    return (
        <div className="pdf-container">
            <Document
                file={pdfFile}
                onLoadSuccess={onDocumentLoadSuccess}
                onLoadError={(error) => console.error("Error loading PDF:", error)}
                loading="Loading PDF..."
                error="Failed to load PDF."
            >
                <Page pageNumber={pageNumber} />
            </Document>
            {numPages && (
                <div className="pagination">
                    <button onClick={goToPrevPage} disabled={pageNumber <= 1}>
                        Previous
                    </button>
                    <span>
            Page {pageNumber} of {numPages}
          </span>
                    <button onClick={goToNextPage} disabled={pageNumber >= numPages}>
                        Next
                    </button>
                </div>
            )}
        </div>
    );
};

export default VoirMonCV;
