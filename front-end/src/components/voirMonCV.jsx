import React, { useState } from "react";
import pdfFile from "../assets/pdf.pdf";


const VoirMonCV = () => {

    return (
        <div className="flex flex-col items-center w-full">
            <div
                className="w-full max-w-4xl h-[80vh] sm:h-[90vh] md:h-[100vh] overflow-hidden rounded shadow-lg transform"
            >
                <iframe
                    src={pdfFile}
                    title="Mon CV"
                    className="w-full h-full border-0"
                    allowFullScreen
                ></iframe>
            </div>
        </div>
    );
};

export default VoirMonCV;
