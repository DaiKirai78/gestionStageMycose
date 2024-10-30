import React, { useState } from "react";
import ModalConvocation from "./ModalConvocation";

function ListeConvocations() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedOffer, setSelectedOffer] = useState(null);
    const [isRefusalModalOpen, setIsRefusalModalOpen] = useState(false);
    const [refusalMessage, setRefusalMessage] = useState("");

    const convocations = [
        {
            id: 1,
            jobTitle: "Développeur Full-Stack",
            companyName: "Tech Solutions Inc.",
            description: "Développer et maintenir des applications web.",
            location: "123 Rue Principale, Ville",
            locationConvocation: "Entrevue en ligne",
            salary: "22",
            website: "https://techsolutions.com",
            time: "10h00, 15 Novembre 2024",
            pdfUrl: null,
        },
        {
            id: 2,
            jobTitle: "Analyste de Données",
            companyName: "DataCorp",
            locationConvocation: "456 Avenue Centrale, Ville",
            time: "14h00, 17 Novembre 2024",
            pdfUrl: "fake_CV.pdf",
        },
    ];

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
    };

    const closeRefusalModal = () => {
        setIsRefusalModalOpen(false);
        setRefusalMessage("");
    };

    const handleRefusalSubmit = () => {
        console.log(`Message de refus pour ${selectedOffer.jobTitle}:`, refusalMessage);
        closeRefusalModal();
    };

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">Liste des Convocations</h1>
                <ul className="space-y-4">
                    {convocations.map((convocation) => (
                        <li key={convocation.id} className="p-6 border border-gray-300 rounded-lg shadow-md">
                            <div className="flex justify-between items-center">
                                <div>
                                    <h2 className="text-2xl font-semibold">{convocation.jobTitle}</h2>
                                    <p className="text-gray-700">{convocation.companyName}</p>
                                </div>
                                <div className="flex space-x-4">
                                    <button
                                        onClick={() => openModal(convocation)}
                                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                    >
                                        Infos
                                    </button>
                                    <button className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">Accepter</button>
                                    <button
                                        onClick={() => openRefusalModal(convocation)}
                                        className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                    >
                                        Refuser
                                    </button>
                                </div>
                            </div>
                            <p className="text-gray-600 mt-4">Lieu : {convocation.locationConvocation}</p>
                            <p className="text-gray-600">Heure : {convocation.time}</p>
                        </li>
                    ))}
                </ul>

                <ModalConvocation isOpen={isModalOpen} onClose={closeModal} offerDetails={selectedOffer} />

                {/* Modale de refus */}
                {isRefusalModalOpen && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg">
                            <button
                                onClick={closeRefusalModal}
                                className="text-xl absolute top-3 right-4 text-gray-500 hover:text-gray-800"
                            >
                                ×
                            </button>
                            <h2 className="text-2xl font-bold mb-4">Refuser l'Offre de stage</h2>
                            <p className="mb-4">
                                Vous pouvez saisir un message concernant le refus de l'offre:{" "}
                                <strong>{selectedOffer?.jobTitle}</strong>
                            </p>
                            <textarea
                                className="w-full p-2 border border-gray-300 rounded"
                                rows="4"
                                placeholder="Raison du refus"
                                value={refusalMessage}
                                onChange={(e) => setRefusalMessage(e.target.value)}
                            ></textarea>
                            <div className="flex justify-end mt-4 space-x-2">
                                <button
                                    onClick={handleRefusalSubmit}
                                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                >
                                    Soumettre
                                </button>
                                <button
                                    onClick={closeRefusalModal}
                                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
                                >
                                    Annuler
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
