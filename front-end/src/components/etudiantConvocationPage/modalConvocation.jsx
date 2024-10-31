import React from 'react';
import { useTranslation } from "react-i18next"

function ModalConvocations({ isOpen, onClose, offerDetails }) {
    if (!isOpen) return null;

    const { t } = useTranslation()

    const isPdf = Boolean(offerDetails.filename && offerDetails.fileData);

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
            <div
                className={`bg-white p-6 rounded-lg shadow-lg relative ${isPdf ? "w-3/4 max-w-5xl" : "w-full max-w-lg"}`}
            >
                <button onClick={onClose} className="text-xl absolute top-3 right-4 text-gray-500 hover:text-gray-800">
                    ×
                </button>
                {isPdf ? (
                    <iframe
                        src={`data:application/pdf;base64,${offerDetails.fileData}`}
                        className="pt-3 px-3 w-full h-[600px]"
                        title="Offre de stage PDF"
                    />
                ) : (
                    <div>
                        <h2 className="text-2xl font-bold mb-4">{offerDetails.title}</h2>
                        <p><strong>{t("entreprise")} :</strong> {offerDetails.entrepriseName}</p>
                        <p><strong>{t("description")} :</strong> {offerDetails.description}</p>
                        <p><strong>{t("place")} :</strong> {offerDetails.location}</p>
                        <p><strong>{t("salary")} :</strong> {offerDetails.salary}$/h</p>
                        <p>
                            <strong>{t("website")} :</strong>{" "}
                            <a href={offerDetails.website} className="text-blue-500 hover:underline" target="_blank" rel="noopener noreferrer">
                                {offerDetails.website}
                            </a>
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ModalConvocations;
