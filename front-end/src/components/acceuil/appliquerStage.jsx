import React, {useEffect, useState} from "react";
import {Worker, Viewer} from '@react-pdf-viewer/core';
import {defaultLayoutPlugin} from '@react-pdf-viewer/default-layout';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';
import axios from "axios";
import {useNavigate} from 'react-router-dom';
import {useTranslation} from "react-i18next";

const AppliquerStage = ({idStage}) => {
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
                params: {id: idStage},
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
                <div className="w-full 2xl:w-3/4 flex justify-center p-8">
                    <div
                        className="w-full 2xl:w-5/6 h-auto lg:bg-gray-50 p-4 flex lg:flex-row flex-col rounded-2xl">
                        <div
                            className={`lg:w-[90vh] w-full h-[70vh] border-2 border-gray-300 shadow-lg ${showPDF ? 'block' : 'hidden lg:block'}`}>
                            <div className="w-full h-full overflow-hidden">
                                <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                                    <Viewer
                                        fileUrl={pdfUrl}
                                        plugins={[defaultLayoutPluginInstance]}
                                        className="w-full h-full"
                                        style={{padding: 0, minHeight: '70vh'}}
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
                            className={`lg:w-96 w-full bg-white shadow-md p-6 flex items-center flex-col lg:ml-8 break-words overflow-y-auto rounded-2xl lg:rounded-none ${showPDF ? 'hidden' : 'block'}`}>
                            <h2 className="text-3xl font-bold mb-4 break-words text-center">{unStage.title || t("offreDeStage")}</h2>
                            <p className="text-gray-700 text-2xl break-words">{unStage.entrepriseName || t("EntrepriseInconnue")}</p>
                            <p className="text-gray-500 mt-4 break-words">{t("publieLe") + formaterDate(unStage.createdAt) || t("DateNonDisponible")}</p>
                            <button
                                onClick={applyForStage}
                                className="mt-20 lg:mt-8 bg-deep-orange-300 w-52 lg:w-48 text-white px-12 py-3 center rounded-lg hover:bg-orange-dark disabled:opacity-65 disabled:hover:bg-orange"
                                disabled={successMessage}
                            >
                                {t("boutonAppliquerAUnStage")}
                            </button>

                            <button
                                onClick={togglePDF}
                                className={`mt-2 bg-blue-200 w-52 lg:w-48 text-white px-12 py-3 rounded-lg hover:bg-blue-600 lg:hidden ${showPDF ? 'hidden' : 'block'}`}
                            >
                                {showPDF ? t("fermerPDF") : t("AfficherPDF")}
                            </button>
                            <button
                                onClick={handleReturnHome}
                                className="bg-gray-500 w-52 lg:w-48 text-white px-12 py-3 rounded-lg hover:bg-gray-600 mt-2 lg:mt-4 mb-4"
                            >
                                {t("RetourAccueil")}
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
                                        style={{padding: 0, minHeight: '100vh'}}
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
                </div>
            ) : (
                // Cas sans PDF, afficher les champs à gauche et les boutons à droite comme pour le PDF
                <div className="w-full 2xl:w-3/4 flex justify-center p-8">
                    <div className="w-full 2xl:w-5/6 h-auto lg:bg-gray-50 p-4 flex lg:flex-row flex-col rounded-2xl">
                        <div className="lg:w-[90vh] w-full h-auto border-2 border-gray-300 shadow-lg">
                            <div className="p-6">
                                <h2 className="text-3xl font-bold mb-4 text-center">{unStage.title || t("offreDeStage")}</h2>
                                <p className="text-gray-700 text-2xl text-center">{unStage.entrepriseName || t("EntrepriseInconnue")}</p>
                                <p className="text-gray-500 mt-4 text-center">{t("publieLe") + formaterDate(unStage.createdAt)}</p>
                                <p className="text-gray-600 mt-6 text-center">{unStage.description || t("DescriptionNonValide")}</p>
                                <p className="text-gray-600 mt-6 text-center">{t("salaireDeOffre") + unStage.salary + "$" || t("DescriptionNonValide")}</p>
                                <p className="text-gray-600 mt-6 text-center">{t("websiteStage")} : <a
                                    href={unStage.website}
                                    className="underline"><strong>{unStage.website || t("DescriptionNonValide")}</strong></a>
                                </p>
                            </div>

                            <div className="bg-gray-200 mx-7 mb-7 rounded-xl shadow-md p-5">
                                <p className="text-gray-600 mb-4 text-left 2xl:text-2xl ml-8 break-words">{t("Employeur")}
                                    <strong> {unStage.employerName || t("DescriptionNonValide")}</strong></p>
                                <p className="text-gray-600 mb-4 text-left 2xl:text-2xl ml-8 break-words">{t("EmailEmployeur")}
                                    <strong>{unStage.email || t("DescriptionNonValide")}</strong></p>
                            </div>
                        </div>

                        <div
                            className="lg:w-96 w-full bg-white shadow-md p-6 flex items-center flex-col lg:ml-8 rounded-2xl lg:rounded-none">
                            <button
                                onClick={applyForStage}
                                className="mt-20 bg-deep-orange-300 w-52 text-white px-12 py-3 rounded-lg"
                                disabled={successMessage}
                            >
                                {t("boutonAppliquerAUnStage")}
                            </button>

                            <button
                                onClick={handleReturnHome}
                                className="bg-gray-500 w-52 lg:w-48 text-white px-12 py-3 rounded-lg hover:bg-gray-600 mt-2 lg:mt-4 mb-4"
                            >
                                {t("RetourAccueil")}
                            </button>

                            {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
                            {errorMessage && <p className="text-red-500 mt-4">{errorMessage}</p>}
                        </div>
                    </div>
                </div>
            )
        ) : (
            <div>{t("chargementInfosStages")}</div>
        )
    );
};

export default AppliquerStage;

