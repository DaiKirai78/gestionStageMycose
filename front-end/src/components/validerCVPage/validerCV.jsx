import { useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";
import { useTranslation } from "react-i18next";

function ValiderCV() {
    const { t } = useTranslation();
    const { state } = useLocation();
    const { cv } = state || {};
    const navigate = useNavigate();
    const [commentaire, setCommentaire] = useState("");
    const [commentaireError, setCommentaireError] = useState("");
    const [error, setError] = useState(null);
    const token = localStorage.getItem("token");

    const handleAccept = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/cv/accept?id=${cv.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ commentaire: commentaire || "" }),
            });

            if (!response.ok) {
                throw new Error(t("ErrorAcceptingCV"));
            }
            navigate("/validerCV");
        } catch (error) {
            setError(t("ErrorAcceptingCV"));
        }
    };

    const handleReject = async () => {
        if (!commentaire.trim()) {
            setCommentaireError(t("commentRequired"));
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/cv/refuse?id=${cv.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ commentaire }),
            });

            if (!response.ok) {
                throw new Error(t("ErrorRefusingCV"));
            }
            console.log("CV rejeté");
            navigate("/validerCV");
        } catch (error) {
            console.error(error);
            setError(t("ErrorRefusingCV"));
        }
    };

    if (!cv) return <p>{t("noCVFound")}</p>;

    return (
        <div className="min-h-screen flex items-start justify-center p-8">
            <div className="bg-[#FFF8F2] shadow-lg rounded-lg flex flex-col md:flex-row w-full max-w-6xl">
                {/* Section PDF */}
                <div className="w-full md:w-[70%] p-8 border-b md:border-b-0 md:border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("studentCV")}</h1>
                    <h2 className="mb-8 text-xl text-center">{t("acceptOrRefuseCV")}</h2>
                    <iframe
                        src={`data:application/pdf;base64,${cv.fileData}`}
                        title="CV"
                        className="w-full h-[62vh] border"
                    ></iframe>
                </div>

                {/* Section des informations et des actions */}
                <div className="w-full md:w-[30%] p-8 flex flex-col items-center md:items-start">
                    <h2 className="text-2xl font-bold mb-4">{cv.studentFirstName} {cv.studentLastName}</h2>
                    <p className="mb-12"><strong>{t("program")}:</strong> {t(cv.programme)}</p>

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
                        className={`border p-2 rounded w-full ${commentaireError ? 'border-red-500' : 'border-gray-300'}`}
                        placeholder={t("leaveComment")}
                        rows={5}
                        value={commentaire}
                        onChange={(e) => {
                            setCommentaire(e.target.value);
                            if (commentaireError) {
                                setCommentaireError(""); // Efface l'erreur quand l'utilisateur tape
                            }
                        }}
                    ></textarea>
                    {commentaireError && (
                        <p className="text-red-500 mt-2">
                            {commentaireError}
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ValiderCV;
