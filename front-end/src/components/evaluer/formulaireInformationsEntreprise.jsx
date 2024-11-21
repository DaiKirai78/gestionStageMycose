import React from 'react';
import { useTranslation } from "react-i18next";

const FormulaireInformationsEntreprise = ({ formData, handleChange }) => {
    const { t } = useTranslation();

    return (
        <div className="w-full max-w-4xl bg-white rounded-lg shadow-md p-10 mb-8">
            <h2 className="text-xl font-bold">{t("informationsEntreprise")}</h2>
            <hr className="mt-3 mb-4" />

            {[
                { id: "nomEntreprise", label: "nomEntreprise" },
                { id: "nomPersonneContact", label: "nomPersonneContact" },
                { id: "adresseEntreprise", label: "adresseEntreprise" },
                { id: "villeEntreprise", label: "villeEntreprise" },
                { id: "codePostalEntreprise", label: "codePostalEntreprise" },
                { id: "telephoneEntreprise", label: "telephoneEntreprise" },
                { id: "telecopieurEntreprise", label: "telecopieurEntreprise" },
                { id: "dateDebutStage", label: "dateDebutStage", type: "date" },
            ].map((field) => (
                <div key={field.id} className="mb-4">
                    <label htmlFor={field.id} className="block font-medium mb-1">
                        {t(field.label)}
                    </label>
                    <input
                        type={field.type || "text"}
                        id={field.id}
                        value={formData[field.id]?.value || ""}
                        onChange={(e) => handleChange(field.id, e.target.value)}
                        className="w-full border rounded p-2"
                    />
                    {formData[field.id]?.hasError && (
                        <p className="text-red-700 text-sm">
                            {t(`${field.label} ne peut pas être vide.`)}
                        </p>
                    )}
                </div>
            ))}

            {/* Groupe pour le numéro du stage */}
            <div className="mb-4">
                <label className="block font-medium mb-1">{t("Numéro du stage")}</label>
                <div className="flex space-x-4">
                    {["1", "2"].map((stageNumber) => (
                        <label key={stageNumber} className="flex items-center">
                            <input
                                type="radio"
                                name="numeroStage"
                                value={stageNumber}
                                checked={formData.numeroStage?.value === stageNumber}
                                onChange={(e) => handleChange("numeroStage", e.target.value)}
                                className="mr-2"
                            />
                            {stageNumber}
                        </label>
                    ))}
                </div>
                {formData.numeroStage?.hasError && (
                    <p className="text-red-700 text-sm">
                        {t("errorStageNumber")}
                    </p>
                )}
            </div>
        </div>
    );
};

export default FormulaireInformationsEntreprise;