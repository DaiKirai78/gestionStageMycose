import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";

function ValiderCV() {
    const { id } = useParams();
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
            <div className="bg-white shadow-lg rounded-lg flex w-full max-w-6xl">
                {/* Section PDF */}
                <div className="w-[70%] p-8 border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-6 mt-6 text-center">CV de l'étudiant</h1>
                    <h2 className="mb-8 text-xl text-center">Accepter ou refuser le CV de l'étudiant</h2>
                    <iframe
                        src="/fake_CV.pdf"
                        title="CV"
                        className="w-full h-[62vh] border"
                    ></iframe>
                </div>

                <div className="w-[30%] p-8 flex flex-col justify-center">
                    <div className="mb-4">
                        <button
                            className="bg-green-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleAccept}
                        >
                            Accepter
                        </button>
                        <button
                            className="bg-red-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleReject}
                        >
                            Refuser
                        </button>
                    </div>

                    <textarea
                        className="border border-gray-300 p-2 rounded w-full"
                        placeholder="Laisser un commentaire (facultatif)"
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
