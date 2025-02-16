import React from 'react';
import { useTranslation } from 'react-i18next';
import SignerContratCanvas from "../signerContrats/signerContratCanvas.jsx";
import InputErrorMessage from "../inputErrorMesssage.jsx";
import {IoCloseSharp} from "react-icons/io5";

const EvaluerFormulaireObsGenerales = ({ formData, handleChange, setErrorKeySignature, errorKeySignature, setDrewSomething, canvasRef }) => {
    const { t } = useTranslation();

    // Fonction pour vérifier les erreurs
    function hasError(critere) {
        return critere && critere.hasError;
    }

    return (
        <div className="w-full max-w-4xl bg-white rounded-lg shadow-md p-10 mb-8">
            <h2 className="text-xl font-bold">{t("generalObservations")}</h2>
            <hr className="mt-3 mb-4"/>

            {/* Stage à privilégier */}
            <div className="mb-4">
                <label htmlFor="milieuStage" className="font-medium">{t("environmentPreferredFor")} :</label>
                <div className="flex gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.milieuStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="milieuStage"
                            name="milieuStage"
                            value="PREMIER_STAGE"
                            checked={formData.milieuStage === "PREMIER_STAGE"}
                            onChange={(e) => handleChange("milieuStage", e.target.value)}
                        />
                        {t("firstInternship")}
                    </label>
                    <label htmlFor="milieuStage">
                        <input
                            className={`mr-2 ${hasError(formData.milieuStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="milieuStage"
                            name="milieuStage"
                            value="DEUXIEME_STAGE"
                            checked={formData.milieuStage === "DEUXIEME_STAGE"}
                            onChange={(e) => handleChange("milieuStage", e.target.value)}
                        />
                        {t("secondInternship")}
                    </label>
                </div>
                {hasError(formData.milieuStage) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300"/>

            {/* Nombre de stagiaires */}
            <div className="mb-4">
                <label htmlFor="nombreStagiaires" className="font-medium">{t("openToWelcoming")} :</label>
                <div className="flex flex-wrap gap-4 mt-2">
                    <label htmlFor="nombreStagiaires">
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="nombreStagiaires"
                            name="nombreStagiaires"
                            value="UN"
                            checked={formData.nombreStagiaires === "UN"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("oneIntern")}
                    </label>
                    <label htmlFor="nombreStagiaires">
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="nombreStagiaires"
                            name="nombreStagiaires"
                            value="DEUX"
                            checked={formData.nombreStagiaires === "DEUX"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("twoInterns")}
                    </label>
                    <label htmlFor="nombreStagiaires">
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="nombreStagiaires"
                            name="nombreStagiaires"
                            value="TROIS"
                            checked={formData.nombreStagiaires === "TROIS"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("threeInterns")}
                    </label>
                    <label htmlFor="nombreStagiaires">
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="nombreStagiaires"
                            name="nombreStagiaires"
                            value="PLUS_DE_TROIS"
                            checked={formData.nombreStagiaires === "PLUS_DE_TROIS"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("moreThanThreeInterns")}
                    </label>
                </div>
                {hasError(formData.nombreStagiaires) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300"/>

            {/* Prochain stage */}
            <div className="mb-4">
                <label className="font-medium">{t("moreInternsInTheFuture")} :</label>
                <div className="flex gap-4 mt-2">
                    <label htmlFor="prochainStage">
                        <input
                            className={`mr-2 ${hasError(formData.prochainStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="prochainStage"
                            name="prochainStage"
                            value="OUI"
                            checked={formData.prochainStage === "OUI"}
                            onChange={(e) => handleChange("prochainStage", e.target.value)}
                        />
                        {t("OUI")}
                    </label>
                    <label htmlFor="prochainStage">
                        <input
                            className={`mr-2 ${hasError(formData.prochainStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="prochainStage"
                            name="prochainStage"
                            value="NON"
                            checked={formData.prochainStage === "NON"}
                            onChange={(e) => handleChange("prochainStage", e.target.value)}
                        />
                        {t("NON")}
                    </label>
                </div>
                {hasError(formData.prochainStage) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300"/>

            {/* Quarts de travail variables */}
            <div className="mb-4">
                <label htmlFor="quartsVariables" className="font-medium">{t("variableWorkShifts")} :</label>
                <div className="flex gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.quartsVariables) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="quartsVariables"
                            name="quartsVariables"
                            value="OUI"
                            checked={formData.quartsVariables === "OUI"}
                            onChange={(e) => handleChange("quartsVariables", e.target.value)}
                        />
                        {t("OUI")}
                    </label>
                    <label htmlFor="quartsVariables">
                        <input
                            className={`mr-2 ${hasError(formData.quartsVariables) ? 'border-red-500' : ''}`}
                            type="radio"
                            id="quartsVariables"
                            name="quartsVariables"
                            value="NON"
                            checked={formData.quartsVariables === "NON"}
                            onChange={(e) => handleChange("quartsVariables", e.target.value)}
                        />
                        {t("NON")}
                    </label>
                </div>
                {hasError(formData.quartsVariables) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}

                {/* Affichage des plages horaires si "Oui" */}
                {formData.quartsVariables === "OUI" && (
                    <div className="mt-4 space-y-2">
                        {["quart1", "quart2", "quart3"].map((quart, index) => (
                            <div key={index} className="flex items-center gap-4">
                                <label htmlFor="startTime">De</label>
                                <input
                                    id="startTime"
                                    type="datetime-local"
                                    value={formData[quart]?.de || ""}
                                    onChange={(e) =>
                                        handleChange(quart, {...formData[quart], de: e.target.value})
                                    }
                                    className={`border rounded px-2 py-1 ${hasError(formData[quart]) ? 'border-red-500' : ''}`}
                                />
                                <label htmlFor="endTime">à</label>
                                <input
                                    id="endTime"
                                    type="datetime-local"
                                    value={formData[quart]?.a || ""}
                                    onChange={(e) =>
                                        handleChange(quart, {...formData[quart], a: e.target.value})
                                    }
                                    className={`border rounded px-2 py-1 ${hasError(formData[quart]) ? 'border-red-500' : ''}`}
                                />
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <div className='flex flex-col items-center mt-5'>
                <h1 className='mb-2'>{t("signature")} :</h1>
                <SignerContratCanvas
                    canvasRef={canvasRef}
                    setDrewSomething={setDrewSomething}
                    errorKeySignature={errorKeySignature}
                    setErrorKeySignature={setErrorKeySignature}/>
                <div className='w-full'>
                    <InputErrorMessage messageKey={errorKeySignature}/>
                </div>
                <button
                    id="clearCanvas"
                    className="p-2 bg-orange hover:bg-opacity-90 text-white rounded mt-3 mb-5"
                    onClick={(e) => {
                        e.preventDefault()
                        canvasRef.current.clearCanvas();
                        setDrewSomething(false);
                        setErrorKeySignature("");
                    }}
                >
                    <IoCloseSharp/>
                </button>
            </div>
        </div>
    );
};

export default EvaluerFormulaireObsGenerales;
