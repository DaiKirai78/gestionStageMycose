import { BsX } from "react-icons/bs";
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import ConvoquerModal from "./convoquerModal.jsx";

function InfoDetailleeEtudiant({ isModalOpen, setIsModalOpen, infosEtudiant, summonEtudiant, summonMessage, setSummonMessage, studentInfo, accepterEtudiant, refuserEtudiant, isFetching }) {
    const [isStudentSummoned, setIsStudentSummoned] = useState(false);
    const [isAcceptedOrRefused, setIsAcceptedOrRefused] = useState(false);
    const [cvEtudiantCourrant ,setCvEtudiantCourrant] = useState();
    const [isConvoquerModalOpen, setIsConvoquerModalOpen] = useState(false);
    const { t } = useTranslation();

    useEffect(() => {
        fetchCVEtudiant();
    }, [infosEtudiant])

    async function fetchCVEtudiant() {
        let token = localStorage.getItem("token");
        if (infosEtudiant == null) {
            return;
        }
        const response = await fetch(
            `http://localhost:8080/api/cv/get-cv-by-etudiant-id/${infosEtudiant.id}`,
            {
                method: "GET",
                headers: {Authorization: `Bearer ${token}`}
            }
        );

        const base64String = await response.json();

        const dataUrl = `data:application/pdf;base64,${base64String.fileData}`;
        setCvEtudiantCourrant(dataUrl);
    }

    useEffect(() => {
        if (isModalOpen) {
            if (studentInfo.status === 'SUMMONED') {
                setIsStudentSummoned(true);
            } else if (studentInfo.status === 'ACCEPTED' || studentInfo.status === 'REFUSED') {
                setIsAcceptedOrRefused(true);
            } else {
                setIsStudentSummoned(false);
            }
        }
    }, [studentInfo]);

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSummonMessage('');
    };

    const handleSummon = async (summonDetails) => {
        await summonEtudiant(summonDetails);
        setIsConvoquerModalOpen(false);
    };

    return (
        isModalOpen && (
            <div
                className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50 transition-opacity duration-300"
                onClick={handleCloseModal}
            >
                <div
                    className="overflow-auto w-full h-[85%] py-5 sm:w-2/3 lg:w-1/2 bg-white rounded-2xl shadow-2xl p-6 relative"
                    onClick={(e) => e.stopPropagation()}
                >
                    <div className="flex justify-between items-center border-b pb-3">
                        <h2 className="text-2xl font-bold text-gray-800">{t("detailsEtudiant")}</h2>
                        <button
                            id="closeStageDetails"
                            className="text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none"
                            onClick={handleCloseModal}
                        >
                            <BsX size={25}/>
                        </button>
                    </div>
                    <div className="pt-4 h-2/3">
                        {infosEtudiant ? (
                            <div className="h-full">
                                <div className="mb-12 flex flex-col gap-2">
                                    <p><strong>{t("inputLabelNom") + ": "} </strong>{infosEtudiant.prenom} {infosEtudiant.nom}</p>
                                    <p><strong>{t("inputLabelEmail") + ": "}</strong>{infosEtudiant.courriel}</p>
                                    <p><strong>{t("telephone") + ": "}</strong>{infosEtudiant.numeroDeTelephone}</p>
                                    <p><strong>{t("programme") + ": "}</strong>{t(infosEtudiant.programme)}</p>
                                </div>
                                {studentInfo && ((studentInfo.convocationStatus === 'ACCEPTED' || studentInfo.convocationStatus === 'REJECTED')) && (
                                    <div className="mt-4 p-4 bg-gray-100 rounded-md w-full text-center">
                                        <p><strong>{t("StatusConvocation")}:</strong> {t(studentInfo.convocationStatus)}</p>
                                        <p><strong>{t("Message")}:</strong> {studentInfo.convocationMessageEtudiant}</p>
                                    </div>
                                )}
                                {studentInfo && studentInfo.convocationStatus === 'PENDING' && (
                                    <div className="mt-4 p-4 bg-gray-100 rounded-md w-full text-center">
                                        <p><strong>{t("StatusConvocation")}:</strong> {t("WaitingForStudent")}</p>
                                    </div>
                                )}
                                <div className="h-full">
                                    <iframe
                                        src={`${cvEtudiantCourrant}`}
                                        title="CV"
                                        className="w-full h-full border"
                                    ></iframe>
                                </div>
                                <div className="flex flex-col gap-3 justify-center items-center mt-5 pb-7">
                                    {/* Autres informations */}
                                    <div className="flex gap-4">
                                        <button
                                            onClick={() => setIsConvoquerModalOpen(true)}
                                            className={`bg-blue-500 text-white px-4 py-2 rounded disabled:bg-gray-600 ${isStudentSummoned ? 'opacity-50' : 'hover:bg-blue-600'}`}
                                            disabled={isStudentSummoned || isFetching || isAcceptedOrRefused}
                                        >
                                            {isStudentSummoned ? t("alreadySummoned") : t("summon")}
                                        </button>
                                        <button
                                            onClick={() => accepterEtudiant()}
                                            className={`bg-green-500 text-white px-4 py-2 rounded disabled:bg-gray-600`}
                                            disabled={isFetching || isAcceptedOrRefused}
                                        >
                                            {t("accept")}
                                        </button>
                                        <button
                                            onClick={() => refuserEtudiant()}
                                            className={`bg-red-500 text-white px-4 py-2 rounded disabled:bg-gray-600`}
                                            disabled={isFetching || isAcceptedOrRefused}
                                        >
                                            {t("refuse")}
                                        </button>
                                    </div>
                                    {summonMessage && (
                                            <div
                                                className={`mt-4 text-sm ${summonMessage.includes('Err') ? 'text-red-600' : 'text-green-600'}`}>
                                                {summonMessage}
                                            </div>
                                        )}
                                    </div>
                                </div>
                        ) : (
                            <p>Aucune information sur l'étudiant sélectionné</p>
                        )}
                    </div>
                    <ConvoquerModal
                        isOpen={isConvoquerModalOpen}
                        onClose={() => setIsConvoquerModalOpen(false)}
                        onSummon={handleSummon}
                    />
                </div>
            </div>
        )
    );
}

export default InfoDetailleeEtudiant;