import React, { useState } from "react";
import { Worker, Viewer } from '@react-pdf-viewer/core';
import { defaultLayoutPlugin } from '@react-pdf-viewer/default-layout';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';

const AppliquerStage = ({ vraiProps }) => {
    const { pdfUrl } = vraiProps;
    const [showPDF, setShowPDF] = useState(false);

    const defaultLayoutPluginInstance = defaultLayoutPlugin();

    const togglePDF = () => {
        setShowPDF(!showPDF);
    };

    return (
        <div className="w-full 2xl:w-3/4 flex justify-center h-1/2 p-8">
            <div className="w-full 2xl:w-5/6 h-auto lg:bg-gray-50 p-4 flex lg:flex-row flex-col rounded-2xl">

                <div className={`lg:w-[90vh] w-full h-[70vh] border-2 border-gray-300 shadow-lg ${showPDF ? 'block' : 'hidden lg:block'}`}>
                    <div className="w-full h-full overflow-hidden">
                        <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                            <Viewer
                                fileUrl={pdfUrl}
                                plugins={[defaultLayoutPluginInstance]}
                                className="w-full h-full"
                                style={{ padding: 0, minHeight: '70vh' }}
                            />
                        </Worker>
                    </div>

                    <button
                        onClick={togglePDF}
                        className={`mt-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 lg:hidden ${showPDF ? 'block' : 'hidden'}`}
                    >
                        Fermer PDF
                    </button>
                </div>

                <div className={`lg:w-96 w-full bg-white shadow-md p-6 lg:ml-8 break-words rounded-2xl lg:rounded-none ${showPDF ? 'hidden' : 'block'}`}>
                    <h2 className="text-2xl font-bold mb-4">{vraiProps.title || 'Offre de stage'}</h2>
                    <p className="text-gray-700 text-xl">{vraiProps.entrepriseName || "Entreprise Inconnu"}</p>
                    <p className="text-gray-500 mb-6">{vraiProps.location || 'Lieu par défaut'}</p>
                    <p className="text-gray-600">{vraiProps.description || 'Pas de description'}</p>
                    <p className="text-gray-500 mt-4">{vraiProps.createdAt || 'Date de création par défaut'}</p>

                    <button
                        onClick={() => alert('Application confirmed!')}
                        className="mt-6 bg-orange text-white px-4 py-2 rounded hover:bg-orange-dark"
                    >
                        Appliquer
                    </button>

                    <button
                        onClick={togglePDF}
                        className={`mt-6 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 lg:hidden ${showPDF ? 'hidden' : 'block'}`}
                    >
                        {showPDF ? 'Fermer PDF' : 'Afficher PDF'}
                    </button>
                </div>

            </div>

            {showPDF && (
                <div className="fixed inset-0 bg-gray-100 z-50 flex justify-center items-center lg:hidden">
                    <div className="w-full h-full">
                        <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                            <Viewer
                                fileUrl={pdfUrl}
                                plugins={[defaultLayoutPluginInstance]}
                                className="w-full h-full"
                                style={{ padding: 0, minHeight: '100vh' }}
                            />
                            <button
                                onClick={togglePDF}
                                className="absolute top-11 left-12 bg-red-400 text-white px-4 py-2 rounded hover:bg-red-500"
                            >
                                Fermer PDF
                            </button>
                        </Worker>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AppliquerStage;

