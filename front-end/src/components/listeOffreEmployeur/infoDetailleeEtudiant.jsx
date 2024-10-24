import { BsX } from "react-icons/bs";
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';

function InfoDetailleeEtudiant({ isModalOpen, setIsModalOpen, infosEtudiant, summonEtudiant, summonMessage, setSummonMessage, studentInfo }) {
    const [isStudentSummoned, setIsStudentSummoned] = useState(false);
    const { t } = useTranslation();

    useEffect(() => {
        if (isModalOpen) {
            if (studentInfo.status === 'SUMMONED') {
                setIsStudentSummoned(true);
            } else {
                setIsStudentSummoned(false);
            }
        }
    }, [studentInfo]);

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSummonMessage('');
    };

    return (
        isModalOpen && (
            <div
                className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50 transition-opacity duration-300"
                onClick={handleCloseModal}
            >
                <div
                    className="w-11/12 max-w-lg sm:w-2/3 lg:w-1/2 bg-white rounded-2xl shadow-2xl p-6 relative transform transition-transform duration-500 ease-out scale-100 hover:scale-105"
                    onClick={(e) => e.stopPropagation()}
                >
                    <div className="flex justify-between items-center border-b pb-3">
                        <h2 className="text-2xl font-bold text-gray-800">Détails de l'étudiant</h2>
                        <button
                            id="closeStageDetails"
                            className="text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none"
                            onClick={handleCloseModal}
                        >
                            <BsX size={25}/>
                        </button>
                    </div>
                    <div className="pt-4">
                        {infosEtudiant ? (
                            <div>
                                <p><strong>Nom : </strong>{infosEtudiant.prenom} {infosEtudiant.nom}</p>
                                {/* Autres informations */}
                                <button
                                    onClick={() => summonEtudiant(infosEtudiant.id)}
                                    className={`bg-blue-500 text-white px-4 py-2 rounded mt-4 ${isStudentSummoned ? 'opacity-50' : 'hover:bg-blue-600'}`}
                                    disabled={isStudentSummoned}
                                >
                                    {isStudentSummoned ? t("alreadySummoned") : t("summon")}
                                </button>
                                {summonMessage && (
                                    <div
                                        className={`mt-4 text-sm ${summonMessage.includes('Err') ? 'text-red-600' : 'text-green-600'}`}>
                                        {summonMessage}
                                    </div>
                                )}
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