import { BsX } from "react-icons/bs";
import { useEffect, useState } from "react";

function InfoDetailleeEtudiant({ isModalOpen, setIsModalOpen, infosEtudiant }) {
    const [cvEtudiantCourrant ,setCvEtudiantCourrant] = useState();
    
    useEffect(() => {
        fetchCVEtudiant();
    }, [infosEtudiant])

    async function fetchCVEtudiant() {
        let token = localStorage.getItem("token");
        const response = await fetch(
            `http://localhost:8080/api/cv/get-cv-by-etudiant-id/${infosEtudiant.id}`,
            {
                method: "GET",
                headers: {Authorization: `Bearer ${token}`}
            }
        );
        console.log(response);
        
        const base64String = await response.fileData;
        const dataUrl = `data:application/pdf;base64,${base64String}`;
        setCvEtudiantCourrant(dataUrl);
    }

    return (
        isModalOpen && (
            <div
                className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50 transition-opacity duration-300"
                onClick={() => setIsModalOpen(false)}
            >
                <div
                    className="w-full h-[85%] sm:w-2/3 lg:w-1/2 bg-white rounded-2xl shadow-2xl p-6 relative transform transition-transform duration-500 ease-out scale-100 hover:scale-105"
                    onClick={(e) => e.stopPropagation()}
                >
                    <div className="flex justify-between items-center border-b pb-3">
                        <h2 className="text-2xl font-bold text-gray-800">Détails de l'étudiant</h2>
                        <button
                            id="closeStageDetails"
                            className="text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none"
                            onClick={() => setIsModalOpen(false)}
                        >
                            <BsX size={25}/>
                        </button>
                    </div>
                    <div className="pt-4">
                        {infosEtudiant ? (
                            <div>
                                <div className="mb-12 flex flex-col gap-2">
                                    <p><strong>Nom : </strong>{infosEtudiant.prenom} {infosEtudiant.nom}</p>
                                    <p><strong>Email: </strong>{infosEtudiant.courriel}</p>
                                    <p><strong>Téléphone: </strong>{infosEtudiant.numeroDeTelephone}</p>
                                    <p><strong>Programme: </strong>{infosEtudiant.programme}</p>
                                </div>
                                <div>
                                <iframe
                                    src={`data:application/pdf;base64,${cvEtudiantCourrant}`}
                                    title="CV"
                                    className="w-full h-full border"
                                ></iframe>
                                </div>
                            </div>

                        ) : (
                            <p>Aucune information sur l'étudiant sélectionné</p>
                        )}
                    </div>
                </div>
            </div>
        )
    );
}

export default InfoDetailleeEtudiant;
