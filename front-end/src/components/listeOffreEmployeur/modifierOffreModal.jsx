import React, { useState } from "react";
import axios from "axios";
import { BsX } from "react-icons/bs";
import AfficherPdf from "./afficherPdf";

const ModifierOffreModal = ({ offer, isOpen, onClose, onUpdate }) => {
    const [form, setForm] = useState({
        email: offer.email || "",
        website: offer.website || "",
        title: offer.title || "",
        location: offer.location || "",
        salary: offer.salary || "",
        description: offer.description || "",
        companyName: offer.companyName || "",
        file: offer.fileData || undefined,
    });

    const [voirPdf, setVoirPdf] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleFileChange = (e) => {
        setForm({ ...form, file: e.target.files[0] });
    };

    const handleSubmit = async () => {
        const token = localStorage.getItem("token");
        const url =
            offer.fileData !== undefined
                ? "http://localhost:8080/api/offres-stages/update-fichier"
                : "http://localhost:8080/api/offres-stages/update-formulaire";

        const formData = new FormData();
        if (offer.fileData) {
            formData.append("file", form.file);
            formData.append("title", form.title);
        } else {
            Object.entries(form).forEach(([key, value]) => {
                if (key !== "file") formData.append(key, value);
            });
        }

        try {
            const response = await axios.patch(url, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data",
                },
                params: { offreStageId: offer.id },
            });

            if (response.status === 201) {
                onUpdate(response.data);
                onClose();
            }
        } catch (error) {
            console.error("Error updating offer:", error);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50">
            {voirPdf && (
                <AfficherPdf
                    setVoirPdf={setVoirPdf}
                    activePdf={form.file}
                />
            )}
            <div className="bg-white p-6 rounded-lg shadow-lg relative w-full max-w-md max-h-screen overflow-auto">
                <button className="absolute top-2 right-2" onClick={onClose}>
                    <BsX size={24} />
                </button>
                <h2 className="text-2xl mb-4">Modifier l'offre</h2>
                {offer.fileData ? (
                    <div>
                        <label className="block mb-2">Titre de l'offre de stage</label>
                        <input
                            type="text"
                            name="title"
                            value={form.title}
                            onChange={handleChange}
                            className="border p-2 mb-4 w-full"
                        />
                        <label className="block mb-2">Fichier PDF</label>
                        <div className="flex items-center gap-4">
                            <input
                                type="file"
                                name="file"
                                onChange={handleFileChange}
                                className="border p-2 w-full"
                                accept="application/pdf"
                            />
                            <button
                                className="bg-orange px-4 py-2 rounded text-white whitespace-nowrap cursor-pointer"
                                onClick={() => setVoirPdf(true)}
                            >
                                Voir PDF
                            </button>
                        </div>
                    </div>
                ) : (
                    <>
                        {Object.keys(form).map((key) => {
                            if (key === "file" || key === "companyName") return null;
                            return (
                                <div key={key} className="mb-4">
                                    <label className="block mb-2">{key}</label>
                                    <input
                                        type="text"
                                        name={key}
                                        value={form[key]}
                                        onChange={handleChange}
                                        className="border p-2 w-full"
                                    />
                                </div>
                            );
                        })}
                    </>
                )}
                <button
                    onClick={handleSubmit}
                    className="bg-blue-500 text-white px-4 py-2 rounded mt-4 w-full"
                >
                    Sauvegarder les modifications
                </button>
            </div>
        </div>
    );
};

export default ModifierOffreModal;