import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

const VoirMonCV = () => {
    const [isFullscreen, setIsFullscreen] = useState(false);
    const pdfContainerRef = useRef(null);
    const [pdfUrl, setPdfUrl] = useState(null);
    const [cvStatus, setCvStatus] = useState(null);
    const [statusDescription, setStatusDescription] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const { t } = useTranslation();
    const navigate = useNavigate();

    useEffect(() => {
        fetchCV();
    }, []);

    const fetchCV = async () => {
        let token = localStorage.getItem("token");
        try {
            const response = await axios.post("http://localhost:8080/api/cv/current", {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });
            const base64String = response.data.fileData;
            const status = response.data.status;
            const description = response.data.statusDescription;

            if (!base64String) {
                throw new Error(t("errorNoPDF"));
            }

            const dataUrl = `data:application/pdf;base64,${base64String}`;
            setPdfUrl(dataUrl);
            setCvStatus(status);
            setStatusDescription(description);
            setLoading(false);
        } catch (error) {
            console.error("Erreur lors de la récupération du CV:", error);
            setError(t("impossibleLoadCV"));
            setLoading(false);
        }
    };

    const enterFullscreen = () => {
        const PDFContainer = pdfContainerRef.current;

        if (!document.fullscreenElement) {
            PDFContainer.requestFullscreen().then(() => {
                setIsFullscreen(true);
            }).catch(err => {
                console.error(`Erreur lors de l'activation du mode plein écran: ${err.message}`);
            });
        }
    };

    const exitFullscreen = () => {
        if (document.fullscreenElement) {
            document.exitFullscreen().then(() => {
                setIsFullscreen(false);
            }).catch(err => {
                console.error(`Erreur lors de la sortie du mode plein écran: ${err.message}`);
            });
        }
    };

    useEffect(() => {
        const handleFullscreenChange = () => {
            if (!document.fullscreenElement) {
                setIsFullscreen(false);
            }
        };

        document.addEventListener("fullscreenchange", handleFullscreenChange);

        return () => {
            document.removeEventListener("fullscreenchange", handleFullscreenChange);
        };
    }, []);

    return (
        <div className="min-h-full flex items-start justify-center p-8">
            <div className="bg-[#FFF8F2] shadow-lg rounded-lg flex flex-col md:flex-row w-full max-w-6xl">
                {/* Section PDF */}
                <div className="w-full md:w-[70%] p-8 border-b md:border-b-0 md:border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-8 mt-4 text-center">{t("myCV")}</h1>
                    <div className="flex justify-center" id="pdfContainer" ref={pdfContainerRef}>
                        <iframe
                            src={pdfUrl}
                            title="Mon CV"
                            className={`w-full border ${isFullscreen ? "h-[100vh]" : "h-[70vh]"}`}
                            allowFullScreen
                        ></iframe>
                        <div className="fixed bottom-4 left-0 right-0 flex justify-center">
                            {isFullscreen && (
                                <button
                                    onClick={exitFullscreen}
                                    className="mt-4 px-4 py-2 bg-[#afafea] font-bold text-black p-2 rounded-lg hover:bg-[#7d7ded] cursor-pointer disabled:hover:bg-[#afafea] disabled:cursor-auto"
                                >
                                    {t("exitFullScreen")}
                                </button>
                            )}
                        </div>
                    </div>
                </div>

                {/* Section des infos de statut et commentaire */}
                <div className="w-full md:w-[30%] p-8 flex flex-col items-center justify-center md:items-start">
                    {cvStatus && (
                        <div className="mb-4 w-full">
                            <p className="text-lg font-semibold mb-4">{t("status")}: {t(cvStatus)}</p>

                            {cvStatus === "ACCEPTED" && (
                                <p className="text-green-600 mb-4">
                                    {t("acceptedMessage")}
                                </p>
                            )}
                            {cvStatus === "WAITING" && (
                                <p className="text-yellow-600 mb-4">
                                    {t("waitingMessage")}
                                </p>
                            )}
                            {cvStatus === "REFUSED" && (
                                <p className="text-red-600 mb-4">
                                    {t("refusedMessage")}
                                </p>
                            )}

                            {(cvStatus === "ACCEPTED" || cvStatus === "REFUSED") && statusDescription && (
                                <p className="text-md text-gray-700 mb-4">
                                    {t("internshipManagerComment")}: {statusDescription}
                                </p>
                            )}
                        </div>
                    )}

                    <button
                        onClick={() => navigate('/televerserCV')}
                        className="mb-8 px-4 py-2 bg-gray-300 font-bold text-black p-2 rounded-lg hover:bg-gray-400 cursor-pointer"
                    >
                        {t("uploadNewCV")}
                    </button>

                    <button
                        onClick={enterFullscreen}
                        className="mt-4 px-4 py-2 bg-[#afafea] font-bold text-black p-2 rounded-lg hover:bg-[#7d7ded] cursor-pointer"
                    >
                        {t("fullScreen")}
                    </button>


                </div>
            </div>
        </div>
    );
};

export default VoirMonCV;
