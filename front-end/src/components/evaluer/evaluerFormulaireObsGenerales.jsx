import React from 'react';
import { useTranslation } from 'react-i18next';

const EvaluerFormulaireObsGenerales = ({ formData, handleChange }) => {
    const { t } = useTranslation();

    // Fonction pour vérifier les erreurs
    function hasError(critere) {
        return critere && critere.hasError;
    }

    return (
        <div className="w-full max-w-4xl bg-white rounded-lg shadow-md p-10 mb-8">
            <h2 className="text-xl font-bold">{t("generalObservations")}</h2>
            <hr className="mt-3 mb-4" />

            {/* Stage à privilégier */}
            <div className="mb-4">
                <label className="font-medium">{t("environmentPreferredFor")} :</label>
                <div className="flex gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.milieuStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="milieuStage"
                            value="premierStage"
                            checked={formData.milieuStage === "premierStage"}
                            onChange={(e) => handleChange("milieuStage", e.target.value)}
                        />
                        {t("firstInternship")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.milieuStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="milieuStage"
                            value="deuxiemeStage"
                            checked={formData.milieuStage === "deuxiemeStage"}
                            onChange={(e) => handleChange("milieuStage", e.target.value)}
                        />
                        {t("secondInternship")}
                    </label>
                </div>
                {hasError(formData.milieuStage) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300" />

            {/* Nombre de stagiaires */}
            <div className="mb-4">
                <label className="font-medium">{t("openToWelcoming")} :</label>
                <div className="flex flex-wrap gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="nombreStagiaires"
                            value="un"
                            checked={formData.nombreStagiaires === "un"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("oneIntern")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="nombreStagiaires"
                            value="deux"
                            checked={formData.nombreStagiaires === "deux"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("twoInterns")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="nombreStagiaires"
                            value="trois"
                            checked={formData.nombreStagiaires === "trois"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("threeInterns")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.nombreStagiaires) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="nombreStagiaires"
                            value="plusDeTrois"
                            checked={formData.nombreStagiaires === "plusDeTrois"}
                            onChange={(e) => handleChange("nombreStagiaires", e.target.value)}
                        />
                        {t("moreThanThreeInterns")}
                    </label>
                </div>
                {hasError(formData.nombreStagiaires) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300" />

            {/* Prochain stage */}
            <div className="mb-4">
                <label className="font-medium">{t("moreInternsInTheFuture")} :</label>
                <div className="flex gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.prochainStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="prochainStage"
                            value="oui"
                            checked={formData.prochainStage === "oui"}
                            onChange={(e) => handleChange("prochainStage", e.target.value)}
                        />
                        {t("OUI")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.prochainStage) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="prochainStage"
                            value="non"
                            checked={formData.prochainStage === "non"}
                            onChange={(e) => handleChange("prochainStage", e.target.value)}
                        />
                        {t("NON")}
                    </label>
                </div>
                {hasError(formData.prochainStage) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}
            </div>
            <hr className="my-6 border-gray-300" />

            {/* Quarts de travail variables */}
            <div className="mb-4">
                <label className="font-medium">{t("variableWorkShifts")} :</label>
                <div className="flex gap-4 mt-2">
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.quartsVariables) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="quartsVariables"
                            value="oui"
                            checked={formData.quartsVariables === "oui"}
                            onChange={(e) => handleChange("quartsVariables", e.target.value)}
                        />
                        {t("OUI")}
                    </label>
                    <label>
                        <input
                            className={`mr-2 ${hasError(formData.quartsVariables) ? 'border-red-500' : ''}`}
                            type="radio"
                            name="quartsVariables"
                            value="non"
                            checked={formData.quartsVariables === "non"}
                            onChange={(e) => handleChange("quartsVariables", e.target.value)}
                        />
                        {t("NON")}
                    </label>
                </div>
                {hasError(formData.quartsVariables) && (
                    <div className="text-red-500 text-sm mt-1">{t("fieldRequired")}</div>
                )}

                {/* Affichage des plages horaires si "Oui" */}
                {formData.quartsVariables === "oui" && (
                    <div className="mt-4 space-y-2">
                        {["quart1", "quart2", "quart3"].map((quart, index) => (
                            <div key={index} className="flex items-center gap-4">
                                <span>De</span>
                                <input
                                    type="time"
                                    value={formData[quart]?.de || ""}
                                    onChange={(e) =>
                                        handleChange(quart, {...formData[quart], de: e.target.value})
                                    }
                                    className={`border rounded px-2 py-1 ${hasError(formData[quart]) ? 'border-red-500' : ''}`}
                                />
                                <span>à</span>
                                <input
                                    type="time"
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
        </div>
    );
};

export default EvaluerFormulaireObsGenerales;
