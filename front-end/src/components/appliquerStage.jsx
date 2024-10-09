import React, { useState, useEffect } from "react";
import { Worker, Viewer } from '@react-pdf-viewer/core';
import { defaultLayoutPlugin } from '@react-pdf-viewer/default-layout';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';

const AppliquerStage = ({ vraiProps }) => {
    const { title, entrepriseName, location, description, createdAt, pdfUrl } = vraiProps;
    const [pdfFileUrl, setPdfFileUrl] = useState(null);
    const [isPdfVisible, setIsPdfVisible] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Plugin pour le layout par défaut qui inclut les outils
    const defaultLayoutPluginInstance = defaultLayoutPlugin();

    useEffect(() => {
        // Assurez-vous que pdfUrl est valide
        if (pdfUrl) {
            setPdfFileUrl(pdfUrl);
        } else {
            console.error('L\'URL du PDF est invalide.');
        }
    }, [pdfUrl]);

    const showPdf = () => {
        if (pdfFileUrl) {
            setIsPdfVisible(true);
        } else {
            console.error('Le fichier PDF ne peut pas être affiché car l\'URL est manquante.');
        }
    };

    const hidePdf = () => {
        setIsPdfVisible(false);
    };

    const openModal = () => {
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };

    return (
        <div className="flex flex-col h-screen w-full bg-gray-100">
            <div className="flex flex-col md:flex-row flex-1">
                {/* Conteneur pour afficher le PDF (3/4 de la page) */}
                <div className={`flex-1 md:w-3/4 ${isPdfVisible ? 'block' : 'hidden'} md:block`}>
                    <div className="relative w-full h-full">
                        <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                            {pdfFileUrl ? (
                                <Viewer
                                    fileUrl={pdfFileUrl}
                                    plugins={[defaultLayoutPluginInstance]}
                                    className="h-full w-full"
                                />
                            ) : (
                                <p>Le PDF n'est pas disponible.</p>
                            )}
                        </Worker>
                        {/* Bouton pour fermer le PDF, uniquement sur écrans plus petits */}
                        {isPdfVisible && (
                            <button
                                className="absolute top-4 right-4 bg-red-600 text-white font-bold py-2 px-4 rounded hover:bg-red-500 transition duration-200"
                                onClick={hidePdf}
                            >
                                Fermer
                            </button>
                        )}
                    </div>
                </div>

                {/* Informations de stage (1/4 de la page) */}
                <div className="flex-1 md:w-1/4 p-4 bg-white shadow-lg flex flex-col justify-center">
                    <h1 className="text-2xl font-bold">{title}</h1>
                    <h2 className="text-xl mt-2">{entrepriseName}</h2>
                    <h3 className="text-lg mt-1">{location}</h3>
                    <p className="mt-2">{description}</p>
                    <p className="text-gray-500 mt-2">{createdAt}</p>

                    {/* Bouton Appliquer */}
                    <div className="flex justify-center mt-4">
                        <button
                            className="bg-green-600 text-white font-bold py-2 px-4 rounded hover:bg-green-500 transition duration-200"
                            onClick={openModal}
                        >
                            Appliquer
                        </button>
                    </div>

                    {/* Bouton pour afficher le PDF (affiche uniquement sur les écrans sm et plus petits) */}
                    <div className="flex justify-center mt-4 md:hidden">
                        <button
                            className="bg-blue-600 text-white font-bold py-2 px-4 rounded hover:bg-blue-500 transition duration-200"
                            onClick={showPdf}
                        >
                            Afficher le PDF
                        </button>
                    </div>
                </div>
            </div>

            {/* Modal pour le bouton Appliquer */}
            {isModalOpen && (
                <div className="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-70">
                    <div className="bg-white p-6 rounded shadow-lg">
                        <h2 className="text-lg font-bold mb-4">Confirmation de la candidature</h2>
                        <p>Êtes-vous sûr de vouloir appliquer pour le stage chez {entrepriseName} ?</p>
                        <div className="flex justify-end mt-4">
                            <button
                                className="bg-green-600 text-white font-bold py-2 px-4 rounded hover:bg-green-500 transition duration-200 mr-2"
                                onClick={closeModal}
                            >
                                Confirmer
                            </button>
                            <button
                                className="bg-red-600 text-white font-bold py-2 px-4 rounded hover:bg-red-500 transition duration-200"
                                onClick={closeModal}
                            >
                                Annuler
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AppliquerStage;
