import React, { useEffect, useState } from "react";
import ModalConvocation from "./ModalConvocation";
import { useTranslation } from "react-i18next";

function ListeConvocations() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedOffer, setSelectedOffer] = useState(null);
    const [isRefusalModalOpen, setIsRefusalModalOpen] = useState(false);
    const [isAcceptanceModalOpen, setIsAcceptanceModalOpen] = useState(false);
    const [responseMessage, setResponseMessage] = useState("");
    const [convocations, setConvocations] = useState([]);

    const { t } = useTranslation();

    useEffect(() => {
        const token = localStorage.getItem("token");
        fetch(`http://localhost:8080/api/application-stage/my-applications/status/SUMMONED`, {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        })
            .then(response => response.json())
            .then(data => setConvocations(data))
            .catch(error => console.error("Erreur lors de la récupération des convocations:", error));
    }, []);

    const openModal = (offer) => {
        setSelectedOffer(offer);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedOffer(null);
    };

    const openRefusalModal = (offer) => {
        setSelectedOffer(offer);
        setIsRefusalModalOpen(true);
        setResponseMessage("");
    };

    const openAcceptanceModal = (offer) => {
        setSelectedOffer(offer);
        setIsAcceptanceModalOpen(true);
        setResponseMessage("");
    };

    const closeResponseModal = () => {
        setIsRefusalModalOpen(false);
        setIsAcceptanceModalOpen(false);
        setResponseMessage("");
    };

    const handleResponseSubmit = () => {
        const token = localStorage.getItem("token");
        const status = isAcceptanceModalOpen ? "ACCEPTED" : "REJECTED";
        const requestBody = {
            messageEtudiant: responseMessage,
            status: status
        };

        fetch(`http://localhost:8080/api/application-stage/answer-summon/${selectedOffer.id}`, {
            method: "PATCH",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(requestBody),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la mise à jour de la convocation');
                }
                return response.json();
            })
            .then(data => {
                console.log("Réponse du serveur:", data);
                setConvocations(prevConvocations => prevConvocations.filter(convocation => convocation.id !== selectedOffer.id));
                closeResponseModal();
            })
            .catch(error => console.error("Erreur:", error));
    };

    // Sépare les convocations selon le statut
    const acceptedOrRejectedConvocations = convocations.filter(convocation =>
        convocation.convocationStatus === "ACCEPTED" || convocation.convocationStatus === "REJECTED"
    );

    const otherConvocations = convocations.filter(convocation =>
        convocation.convocationStatus !== "ACCEPTED" && convocation.convocationStatus !== "REJECTED"
    );

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("convocationsList")}</h1>

                {/* Section des convocations acceptées ou refusées */}
                <h2 className="text-2xl font-semibold mb-4">{t("acceptedOrRejectedConvocations")}</h2>
                {acceptedOrRejectedConvocations.length > 0 ? (
                    <ul className="space-y-4">
                        {acceptedOrRejectedConvocations.map((convocation) => (
                            <li key={convocation.id} className="p-6 border border-gray-300 rounded-lg shadow-md">
                                <div className="flex justify-between items-center">
                                    <div>
                                        <h2 className="text-2xl font-semibold">{convocation.title}</h2>
                                        <p className="text-gray-700">{convocation.entrepriseName}</p>
                                    </div>
                                    <div className="flex items-center space-x-4">
                                        <button
                                            onClick={() => openModal(convocation)}
                                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                        >
                                            {t("infos")}
                                        </button>
                                        <span className="text-lg font-semibold text-gray-600">
                                            {convocation.convocationStatus === "ACCEPTED" ? t("ACCEPTED") : t("REFUSED")}
                                        </span>
                                    </div>
                                </div>
                                <p className="text-gray-600 mt-4">{t("place")} : {convocation.locationConvocation}</p>
                                <p className="text-gray-600">
                                    {t("dateAndTime")} : {new Date(convocation.scheduledAt).toLocaleString('fr-FR', {
                                    day: '2-digit',
                                    month: '2-digit',
                                    year: 'numeric',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                    hour12: false
                                })}
                                </p>
                                <p className="text-gray-600">{t("message")} : {convocation.messageConvocation}</p>
                            </li>

                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500">{t("noAcceptedOrRejectedConvocations")}</p>
                )}

                {/* Section des autres convocations */}
                <h2 className="text-2xl font-semibold mt-8 mb-4">{t("waitingConvocations")}</h2>
                {otherConvocations.length > 0 ? (
                    <ul className="space-y-4">
                        {otherConvocations.map((convocation) => (
                            <li key={convocation.id} className="p-6 border border-gray-300 rounded-lg shadow-md">
                                <div className="flex justify-between items-center">
                                    <div>
                                        <h2 className="text-2xl font-semibold">{convocation.title}</h2>
                                        <p className="text-gray-700">{convocation.entrepriseName}</p>
                                    </div>
                                    <div className="flex space-x-4">
                                        <button
                                            onClick={() => openModal(convocation)}
                                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                        >
                                            {t("infos")}
                                        </button>
                                        <button
                                            onClick={() => openAcceptanceModal(convocation)}
                                            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                                        >
                                            {t("accept")}
                                        </button>
                                        <button
                                            onClick={() => openRefusalModal(convocation)}
                                            className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                        >
                                            {t("refuse")}
                                        </button>
                                    </div>
                                </div>
                                <p className="text-gray-600 mt-4">{t("place")} : {convocation.locationConvocation}</p>
                                <p className="text-gray-600">
                                    {t("dateAndTime")} : {new Date(convocation.scheduledAt).toLocaleString('fr-FR', {
                                    day: '2-digit',
                                    month: '2-digit',
                                    year: 'numeric',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                    hour12: false
                                })}
                                </p>
                                <p className="text-gray-600">{t("message")} : {convocation.messageConvocation}</p>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500">{t("noWaitingConvocations")}</p>
                )}

                <ModalConvocation isOpen={isModalOpen} onClose={closeModal} offerDetails={selectedOffer} />

                {/* Modale d'acceptation ou de refus */}
                {(isRefusalModalOpen || isAcceptanceModalOpen) && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg">
                            <button
                                onClick={closeResponseModal}
                                className="text-xl absolute top-3 right-4 text-gray-500 hover:text-gray-800"
                            >
                                ×
                            </button>
                            <h2 className="text-2xl font-bold mb-4">
                                {isAcceptanceModalOpen ? t("acceptInternshipOffer") : t("refuseInternshipOffer")}
                            </h2>
                            <p className="mb-4">
                                {isAcceptanceModalOpen
                                    ? t("messageAcceptOffer") + " : "
                                    : t("messageRefuseOffer") + " : "}
                                <strong>{selectedOffer?.title}</strong>
                            </p>
                            <textarea
                                className="w-full p-2 border border-gray-300 rounded"
                                rows="4"
                                placeholder={isAcceptanceModalOpen ? "Message d'acceptation" : "Raison du refus"}
                                value={responseMessage}
                                onChange={(e) => setResponseMessage(e.target.value)}
                            ></textarea>
                            <div className="flex justify-end mt-4 space-x-2">
                                <button
                                    onClick={() => handleResponseSubmit(isAcceptanceModalOpen ? t("messageAcceptation") : t("messageRefus"))}
                                    className={`px-4 py-2 ${isAcceptanceModalOpen ? "bg-green-500" : "bg-red-500"} text-white rounded hover:${
                                        isAcceptanceModalOpen ? "bg-green-600" : "bg-red-600"
                                    }`}
                                >
                                    {t("submit")}
                                </button>
                                <button
                                    onClick={closeResponseModal}
                                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                                >
                                    {t("cancel")}
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ListeConvocations;
