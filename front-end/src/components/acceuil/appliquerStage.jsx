import React, { useEffect, useState } from "react";
import { Worker, Viewer } from '@react-pdf-viewer/core';
import { defaultLayoutPlugin } from '@react-pdf-viewer/default-layout';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import {useTranslation} from "react-i18next";

const AppliquerStage = ({ idStage }) => {
    const [showPDF, setShowPDF] = useState(false);
    const [unStage, setUnStage] = useState(null);
    const [pdfUrl, setPdfUrl] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);
    const defaultLayoutPluginInstance = defaultLayoutPlugin();
    const navigate = useNavigate();
    const {t} = useTranslation();

    const localhost = "http://localhost:8080/";
    const urlApplicationStageAPI = "api/application-stage";
    const urlOffreStageAPI = "api/offres-stages";

    const togglePDF = () => {
        setShowPDF(!showPDF);
    };

    useEffect(() => {
        if (idStage) {
            fetchStage();
        }
    }, [idStage]);

    const fetchStage = async () => {
        try {
            const response = await axios.get(localhost + urlOffreStageAPI + "/id/" + idStage);
            const stageData = response.data;
            setUnStage(stageData);

            if (stageData.fileData) {
                const base64String = stageData.fileData;
                const dataUrl = `data:application/pdf;base64,${base64String}`;
                setPdfUrl(dataUrl);
            }

            setLoading(false);
        } catch (error) {
            console.error("Erreur lors de la récupération du stage:", error);
            setError(t("ErreurRecuperationStage"));
            setLoading(false);
        }
    };

    const applyForStage = async () => {
        const token = localStorage.getItem("token");
        setSuccessMessage(null);
        setErrorMessage(null);

        try {
            const response = await axios.post(localhost + urlApplicationStageAPI + "/apply", null, {
                params: { id: idStage },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setSuccessMessage(t("ApplicationEffectueeSuccess"));
        } catch (error) {
            console.error("Erreur lors de l'application au stage:", error);
            setErrorMessage(t("ApplicationEffectueeErreur"));
            if (error.response) {
                if (error.response.status === 409) {
                    setErrorMessage(t("VousAvezDejaPostule"));
                } else if (error.response.status === 403) {
                    setErrorMessage(t("CetteOffreNestPlusDisponible"));
                }
            }
        }
    };

    const formaterDate = (dateAFormater) => {
        return new Date(dateAFormater).toISOString().split('T')[0];
    };

    const handleReturnHome = () => {
        navigate('/accueil');
    };

    if (loading) {
        return <div>{t("chargementInfosStages")}</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    return (
        unStage ? (
            unStage.filename ? (
                <div className="w-full 2xl:w-3/4 flex justify-center h-1/2 p-8">
                    <div className="w-full 2xl:w-5/6 h-auto lg:bg-gray-50 p-4 flex lg:flex-row flex-col rounded-2xl">
                        <div
                            className={`lg:w-[90vh] w-full h-[70vh] border-2 border-gray-300 shadow-lg ${showPDF ? 'block' : 'hidden lg:block'}`}>
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
                                {t("fermerPDF")}
                            </button>
                        </div>

                        <div
                            className={`lg:w-96 w-full bg-white shadow-md p-6 lg:ml-8 break-words overflow-y-auto rounded-2xl lg:rounded-none ${showPDF ? 'hidden' : 'block'}`}>
                            <h2 className="text-2xl font-bold mb-4">{unStage.title || t("offreDeStage")}</h2>
                            <p className="text-gray-700 text-xl">{unStage.entrepriseName || t("EntrepriseInconnue")}</p>
                            <p className="text-gray-500 mb-6">{unStage.location || t("AdresseNonValide")}</p>
                            <p className="text-gray-600">{unStage.description || t("DescriptionNonValide")}</p>
                            <p className="text-gray-500 mt-4">{formaterDate(unStage.createdAt) || t("DateNonDisponible")}</p>

                            <button
                                onClick={applyForStage}
                                className="mt-6 bg-orange w-48 text-white px-12 py-3 rounded-lg hover:bg-orange-dark"
                            >
                                {t("boutonAppliquerAUnStage")}
                            </button>

                            <button
                                onClick={togglePDF}
                                className={`mt-6 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 lg:hidden ${showPDF ? 'hidden' : 'block'}`}
                            >
                                {showPDF ? t("fermerPDF") : t("AfficherPDF")}
                            </button>

                            {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
                            {errorMessage && <p className="text-red-500 mt-4">{errorMessage}</p>}
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
                                        {t("fermerPDF")}
                                    </button>
                                </Worker>
                            </div>
                        </div>
                    )}

                    <div className="w-full h-20 flex justify-center mt-4">
                        <button
                            onClick={handleReturnHome}
                            className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                        >
                            {t("RetourAccueil")}
                        </button>
                    </div>
                </div>
            ) : (
                <div className="w-3/4 lg:w-1/2 mt-8 h-[30vh] bg-white shadow-md p-6 overflow-y-auto break-words rounded-2xl">
                    <h2 className="text-2xl font-bold mb-4">{unStage.title || t("offreDeStage")}</h2>
                    <p className="text-gray-700 text-xl">{unStage.entrepriseName || t("EntrepriseInconnue")}</p>
                    <p className="text-gray-500 mb-6">{unStage.location || t("AdresseNonValide")}</p>
                    <p className="text-gray-600">{unStage.description || t("DescriptionNonValide")}</p>
                    <p className="text-gray-500 mt-4">{formaterDate(unStage.createdAt) || t("DateNonDisponible")}</p>

                    <button
                        onClick={applyForStage}
                        className="mt-14 bg-orange w-48 text-white px-12 py-3 rounded-lg hover:bg-orange-dark"
                    >
                        {t("boutonAppliquerAUnStage")}
                    </button>

                    {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
                    {errorMessage && <p className="text-red-500 mt-4">{errorMessage}</p>}

                    <div className="w-full mt-4">
                        <button
                            onClick={handleReturnHome}
                            className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                        >
                            {t("RetourAccueil")}
                        </button>
                    </div>
                </div>
            )
        ) : (
            <div>{t("chargementInfosStages")}</div>
        )
    );
};

export default AppliquerStage;

