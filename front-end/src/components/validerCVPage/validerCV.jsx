import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { useTranslation } from "react-i18next";

function ValiderCV() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [commentaire, setCommentaire] = useState("");

    const handleAccept = () => {
        console.log("CV accepté");
        navigate("/validerCV");
    };

    const handleReject = () => {
        console.log("CV rejeté");
        navigate("/validerCV");
    };

    return (
        <div className="min-h-screen flex items-start justify-center p-8">
            <div className="bg-[#FFF8F2] shadow-lg rounded-lg flex flex-col md:flex-row w-full max-w-6xl">
                {/* Section PDF */}
                <div className="w-full md:w-[70%] p-8 border-b md:border-b-0 md:border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("studentCV")}</h1>
                    <h2 className="mb-8 text-xl text-center">{t("acceptOrRefuseCV")}</h2>
                    <iframe
                        src="/fake_CV.pdf"
                        title="CV"
                        className="w-full h-[62vh] border"
                    ></iframe>
                </div>

                {/* Section des boutons et texte */}
                <div className="w-full md:w-[30%] p-8 flex flex-col items-center justify-center md:items-start">
                    <div className="mb-4 w-full">
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

export default ValiderCV;
