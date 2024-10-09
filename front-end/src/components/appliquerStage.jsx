import React, { useState, useEffect } from "react";
import { Worker, Viewer } from '@react-pdf-viewer/core';
//import { Zoom } from '@react-pdf-viewer/zoom';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/zoom/lib/styles/index.css';

const AppliquerStage = ({ vraiProps }) => {
    const { title, entrepriseName, location, description, createdAt, pdfUrl } = vraiProps;
    const [pdfFileUrl, setPdfFileUrl] = useState(null);
    const [zoomLevel, setZoomLevel] = useState(1);

    useEffect(() => {
        setPdfFileUrl(pdfUrl);
    }, [pdfUrl]);

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold">{title}</h1>
            <h2 className="text-xl">{entrepriseName}</h2>
            <h3 className="text-lg">{location}</h3>
            <p>{description}</p>
            <p className="text-gray-500">{createdAt}</p>

            {/*<div className="mb-4">*/}
            {/*    <Zoom*/}
            {/*        onZoomChange={(zoom) => setZoomLevel(zoom)}*/}
            {/*    />*/}
            {/*</div>*/}

            {pdfFileUrl ? (
                <div className="h-[600px] w-full overflow-auto">
                    <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                        <Viewer
                            fileUrl={pdfFileUrl}
                            defaultScale={zoomLevel}
                        />
                    </Worker>
                </div>
            ) : (
                <p>Aucun PDF disponible</p>
            )}
        </div>
    );
}

export default AppliquerStage;