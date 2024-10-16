import { useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";
import { useTranslation } from "react-i18next";

function ValiderOffreStage() {
    const { t } = useTranslation();
    const { state } = useLocation();
    const { offreStage } = state || {};
    const navigate = useNavigate();
    const [commentaire, setCommentaire] = useState("");
    const [error, setError] = useState(null);
    const token = localStorage.getItem("token");

    const handleAccept = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/offres-stages/accept?id=${offreStage.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ commentaire }),
            });

            if (!response.ok) {
                throw new Error(t("errorAcceptingInternship"));
            }
            navigate("/validerOffreStage");
        } catch (error) {
            console.error(error);
            setError(t("errorAcceptingInternship"));
        }
    };

    const handleReject = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/offres-stages/refuse?id=${offreStage.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ commentaire }),
            });

            if (!response.ok) {
                throw new Error(t("errorRefusingInternship"));
            }
            navigate("/validerOffreStage");
        } catch (error) {
            console.error(error);
            setError(t("errorRefusingInternship"));
        }
    };

    if (!offreStage) return <p>{t("noInternshipFound")}</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="min-h-screen flex items-start justify-center p-8">
            <div className="bg-[#FFF8F2] shadow-lg rounded-lg flex flex-col md:flex-row w-full max-w-6xl">
                {/* Section PDF ou Informations par défaut */}
                <div className="w-full md:w-[70%] p-8 border-b md:border-b-0 md:border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("employerInternship")}</h1>
                    <h2 className="mb-8 text-xl text-center">{t("acceptOrRefuseInternship")}</h2>
                    {offreStage.fileData ? (
                        <iframe
                            src={`data:application/pdf;base64,${offreStage.fileData}`}
                            title="offreStage"
                            className="w-full h-[62vh] border"
                        ></iframe>
                    ) : (
                        <div>
                            {offreStage.description && (
                                <p><strong>{t("description")}:</strong> {offreStage.description}</p>
                            )}
                            {offreStage.location && (
                                <p><strong>{t("location")}:</strong> {offreStage.location}</p>
                            )}
                            {offreStage.salary && (
                                <p><strong>{t("salary")}:</strong> {offreStage.salary}$/h</p>
                            )}
                            {offreStage.website && (
                                <p>
                                    <strong>{t("website")}: </strong>
                                    <a
                                        href={offreStage.website.startsWith("http") ? offreStage.website : `https://${offreStage.website}`}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        onClick={(e) => e.stopPropagation()}
                                    >
                                        {offreStage.website}
                                    </a>
                                </p>

                            )}
                        </div>

                    )}
                </div>

                {/* Section des informations et des actions */}
                <div className="w-full md:w-[30%] p-8 flex flex-col items-center md:items-start">
                    <h2 className="text-2xl font-bold mb-4">{offreStage.title}</h2>
                    <p className="mb-12"><strong>{t("companyName")}:</strong> {t(offreStage.entrepriseName)}</p>

                    {/* Boutons d'acceptation et de refus */}
                    <div className="mt-12 mb-4 w-full">
                        <button
                            className="bg-green-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleAccept}
                        >
                            {t("accept")}
                        </button>
                        <button
                            className="bg-red-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleReject}
                        >
                            {t("refuse")}
                        </button>
                    </div>

                    {/* Zone de texte pour les commentaires */}
                    <textarea
                        className="border border-gray-300 p-2 rounded w-full"
                        placeholder={t("leaveComment")}
                        rows={5}
                        value={commentaire}
                        onChange={(e) => setCommentaire(e.target.value)}
                    ></textarea>
                </div>
            </div>
        </div>
    );
}

export default ValiderOffreStage;
