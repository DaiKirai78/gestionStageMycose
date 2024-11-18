import React, { useState, useEffect } from "react";
import axios from "axios";
import {BsCloudArrowUpFill, BsX} from "react-icons/bs";
import AfficherPdf from "./afficherPdf";
import { useTranslation } from "react-i18next";
import logoPdf from '../../assets/pdficon.png';

const ModifierOffreModal = ({ offer, isOpen, onClose, onUpdate }) => {
    const { t } = useTranslation();

    const [form, setForm] = useState({
        email: offer.email || "",
        website: offer.website || "",
        title: offer.title || "",
        location: offer.location || "",
        salary: offer.salary || "",
        description: offer.description || "",
        file: offer.fileData || null,
    });

    const [voirPdf, setVoirPdf] = useState(false);

    useEffect(() => {
        setForm({
            email: offer.email || "",
            website: offer.website || "",
            title: offer.title || "",
            location: offer.location || "",
            salary: offer.salary || "",
            description: offer.description || "",
            file: offer.fileData || null,
        });
    }, [offer]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleFileChange = (e) => {
        setForm({ ...form, file: e.target.files[0] });
    };

    const hasChanges = () => {
        const isFileChanged = (() => {
            if (form.file instanceof File) {
                return true;
            }
            else if (form.file === null && offer.fileData !== null) {
                return false;
            }
            else if (typeof form.file === "string" && form.file !== offer.fileData) {
                return true;
            }
            return false;
        })();

        return (
            form.email.trim() !== (offer.email || "").trim() ||
            form.website.trim() !== (offer.website || "").trim() ||
            form.title.trim() !== (offer.title || "").trim() ||
            form.location.trim() !== (offer.location || "").trim() ||
            form.salary.trim() !== (offer.salary || "").trim() ||
            form.description.trim() !== (offer.description || "").trim() ||
            isFileChanged
        );
    };

    const handleSubmit = async () => {
        const token = localStorage.getItem("token");
        const url =
            offer.fileData !== undefined
                ? "http://localhost:8080/api/offres-stages/update-fichier"
                : "http://localhost:8080/api/offres-stages/update-formulaire";

        const updatedFields = {};
        Object.keys(form).forEach((key) => {
            if (key === "file" && form.file instanceof File) {
                updatedFields.file = form.file;
            } else if (key !== "file" && form[key] !== offer[key] && form[key].trim() !== "") {
                updatedFields[key] = form[key];
            }
        });

        if (Object.keys(updatedFields).length === 0) {
            console.log("Aucune modification détectée");
            return;
        }

        const formData = new FormData();

        if (form.file && form.file !== offer.fileData) {
            formData.append("file", form.file);
        }

        Object.entries(updatedFields).forEach(([key, value]) => {
            if (key !== "file") formData.append(key, value);
        });

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
            <div className="bg-white p-6 rounded-lg shadow-lg relative w-full max-w-md max-h-screen overflow-auto">
                <h2 className="text-2xl mb-4">{t("modifyInternshipOffer")}</h2>
                <button className="absolute top-4 right-4" onClick={onClose}>
                    <BsX size={35}/>
                </button>
                {offer.fileData ? (
                    <div>
                        <label className="block mb-2">{t("title")}</label>
                        <input
                            type="text"
                            name="title"
                            value={form.title}
                            onChange={handleChange}
                            className="border p-2 mb-4 w-full"
                        />
                        <label className="block mb-2">{t("filePDF")}</label>
                        <div className="relative w-full">
                            <label
                                htmlFor="file"
                                onDrop={(e) => {
                                    e.preventDefault();
                                    const droppedFile = e.dataTransfer.files[0];
                                    if (droppedFile && droppedFile.type === "application/pdf") {
                                        handleFileChange({target: {files: [droppedFile]}});
                                    }
                                }}
                                onDragOver={(e) => e.preventDefault()}
                                className="flex justify-center w-full h-32 px-4 transition border-2 border-gray-300 border-dashed rounded-md cursor-pointer hover:border-gray-400 focus:outline-none"
                            >
                                <span className="flex items-center space-x-2">
                                    <BsCloudArrowUpFill className="text-gray-500" size={24}/>
                                    <span className="font-medium text-gray-600">
                                        Déposez un fichier ici ou&nbsp;
                                        <span className="text-blue-600 underline">parcourir</span>
                                    </span>
                                </span>
                                <input
                                    type="file"
                                    id="file"
                                    className="hidden"
                                    onChange={handleFileChange}
                                    accept="application/pdf"
                                />
                            </label>

                            {/* Affichage du fichier sélectionné */}
                            {form.file && form.file instanceof File && (
                                <div
                                    className="mt-4 flex items-center justify-between py-2 px-4 bg-[#FFF8F2] border-2 border-gray-400 rounded-md">
                                    <div className="flex gap-2 items-center">
                                        <img
                                            src={logoPdf}
                                            alt="icone pdf"
                                            className="w-10"
                                        />
                                        <p>{form.file.name}</p>
                                    </div>
                                    <button
                                        type="button"
                                        onClick={() => setForm({...form, file: null})}
                                        className="text-gray-400 hover:text-red-500"
                                    >
                                        ✖
                                    </button>
                                </div>
                            )}

                            {offer.fileData && (
                                <div
                                    className="mt-4 flex items-center justify-between py-2 px-4 bg-[#EFF6FF] border-2 border-gray-300 rounded-md">
                                    <div className="flex gap-2 items-center">
                                        <img
                                            src={logoPdf}
                                            alt="icone pdf"
                                            className="w-10"
                                        />
                                        <p>{t("AfficherPDF")}</p>
                                    </div>
                                    <button
                                        type="button"
                                        onClick={() => setVoirPdf(true)}
                                        className="text-blue-500 underline"
                                    >
                                        {t("openPDF")}
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                ) : (
                    <>
                        {Object.keys(form).map((key) => {
                            if (key === "file" || key === "companyName") return null;
                            return (
                                <div key={key} className="mb-4">
                                    <label className="block mb-2">{t(key)}</label>
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
                    disabled={!hasChanges()}
                >
                    {t("saveModifications")}
                </button>
            </div>
            {voirPdf && (
                <AfficherPdf
                    setVoirPdf={setVoirPdf}
                    activePdf={offer.fileData}
                />
            )}
        </div>
    );
};

export default ModifierOffreModal;
