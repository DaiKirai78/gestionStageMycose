import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { BsX } from "react-icons/bs";
import InfoDetailleeEtudiant from './infoDetailleeEtudiant';
import axios from 'axios';

const InfoDetailleeOffreStage = ({ setActiveOffer, activeOffer, getColorOffreStatus, setVoirPdf, voirPdf }) => {
    const [listeEtudiantsAppliques, setListeEtudiantsAppliques] = useState([]);
    const [isModalCandidatureOpen, setIsModalCandidatureOpen] = useState(false);
    const [selectedEtudiant, setSelectedEtudiant] = useState(null);
    const [summonMessage, setSummonMessage] = useState('');
    const [studentInfo, setStudentInfo] = useState(null);
    const { t } = useTranslation();

    const format = getFormat();

    useEffect(() => {
        fetchCandidatures();
    }, [activeOffer]);

    async function fetchCandidatures() {
        const token = localStorage.getItem("token");

        const response = await fetch(
            `http://localhost:8080/api/offres-stages/offre-applications/${activeOffer.id}`,
            {
                method: "GET",
                headers: { Authorization: `Bearer ${token}` }
            }
        );
        let fetchedData = await response.text();
        if (fetchedData) {
            setListeEtudiantsAppliques(JSON.parse(fetchedData));
        }
        else {
            setListeEtudiantsAppliques([]);
        }
    }

    function getFormat() {
        if (activeOffer)
            return activeOffer.fileData ? "file" : "form";
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}-${month}-${year}`;
    }

    function ouvrirModal(etudiantId) {
        console.log(etudiantId);

        if(etudiantId !== "default") {
            const etudiant = listeEtudiantsAppliques.find(e => e.id === parseInt(etudiantId));
            setSelectedEtudiant(etudiant);
            setIsModalCandidatureOpen(true);
        }

    }

    useEffect(() => {
        // Fonction pour vérifier si l'étudiant est convoqué
        async function checkSummonStatus() {
            const token = localStorage.getItem("token");

            try {
                const response = await axios.get(`http://localhost:8080/api/application-stage/get/${selectedEtudiant.id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (response.status === 200) {
                    const applications = response.data;
                    console.log("applications", applications);
                    // On vérifie l'application avec le bon etudiantId et l'offreStageId
                    const studentApplication = applications.find(app => app.etudiant_id === selectedEtudiant.id && app.offreStage_id === activeOffer.id);

                    setStudentInfo(studentApplication);
                }
            } catch (error) {
                console.error('Erreur lors de la vérification du statut de convocation:', error);
            }
        }

        if (selectedEtudiant && activeOffer) {
            checkSummonStatus();
        }
    }, [selectedEtudiant, activeOffer]);

    // Fonction pour convoquer l'étudiant
    async function summonEtudiant() {
        const token = localStorage.getItem("token");

        try {
            const response = await axios.patch(`http://localhost:8080/api/application-stage/summon/${studentInfo.id}`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 200) {
                setSummonMessage(t("studentSummoned"));
            }
        } catch (error) {
            console.error("Erreur lors de la convocation de l'étudiant:", error);
            setSummonMessage(t("errorSummoningStudent"));
        }
    }
    async function accepterEtudiant() {
        const token = localStorage.getItem("token");

        try {
            const response = await axios.patch(`http://localhost:8080/api/application-stage/application/${studentInfo.id}/accepter`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 200) {
                setSummonMessage(t("studentAccepted"));
            }
        } catch (error) {
            console.error("Erreur lors de l'acceptation de l'étudiant:", error);
            setSummonMessage(t("errorAcceptingStudent"));
        }
    }
    async function refuserEtudiant() {
        const token = localStorage.getItem("token");

        try {
            const response = await axios.patch(`http://localhost:8080/api/application-stage/application/${studentInfo.id}/refuser`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 200) {
                setSummonMessage(t("studentRefused"));
            }
        } catch (error) {
            console.error("Erreur lors du refus de l'étudiant:", error);
            setSummonMessage(t("errorRefusingStudent"));
        }
    }

    const closeModal = () => {
        setIsModalCandidatureOpen(false);
        setSummonMessage('');
    };

    return (
        <div className={`pb-8 sm:pt-0 bg-orange-light z-20 rounded border border-deep-orange-200 w-full md:h-full h-[90vh] fixed left-0 md:sticky md:top-2 flex flex-col md:transition-none transition-all ease-in-out overflow-y-auto ${activeOffer === null ? "bottom-[-90vh]" : "bottom-0"}`}>
            <button className='absolute right-2 top-2 md:hidden'
                    onClick={() => { setActiveOffer(null) }}>
                <BsX size={25} />
            </button>
            {activeOffer &&
                <div className="p-6">
                    <h2 className="text-2xl font-bold mb-2">{activeOffer.title}</h2>
                    <p className="text-sm text-gray-600">{t("postedOn")} : <span className="font-medium">{formatDate(activeOffer.createdAt)}</span></p>
                    <p className="text-sm text-gray-600">{t("updatedOn")} : <span className="font-medium">{formatDate(activeOffer.updatedAt)}</span></p>
                    <h3 className="text-xl font-semibold mt-4">{t("offerDetails")} :</h3>
                    {activeOffer.description &&
                        <p className="mt-2"><strong>{t("description")} :</strong> {activeOffer.description}</p>
                    }
                    {activeOffer.employerName &&
                        <p className="mt-2"><strong>{t("employer")} :</strong> {activeOffer.employerName}</p>
                    }
                    <p className="mt-2"><strong>{t("company")} :</strong> {activeOffer.entrepriseName}</p>
                    {activeOffer.location &&
                        <p className="mt-2"><strong>{t("locationStage")} :</strong> {activeOffer.location}</p>
                    }
                    {activeOffer.salary &&
                        <p className="mt-2"><strong>{t("salaryStage")} :</strong> {activeOffer.salary}</p>
                    }
                    <p className="mt-2"><strong>{t("status")} :</strong> <span className={`font-semibold ${getColorOffreStatus(activeOffer.status)}`}>{t(activeOffer.status)}</span></p>
                    {activeOffer.website &&
                        <p className="mt-2">
                            <strong>{t("websiteStage")} :</strong> <a href={"https://" + activeOffer.website} target="_blank" className="text-blue-500 underline cursor-pointer">{activeOffer.website}</a>
                        </p>
                    }
                    <div className='flex flex-col w-full gap-5 mt-5'>
                        {format === "file" &&
                            <button
                                className='bg-orange px-4 py-2 rounded text-white mt-3 cursor-pointer w-1/2'
                                onClick={() => setVoirPdf(!voirPdf)}
                            >
                                {t("seePDF")}</button>
                        }
                        <div className='flex flex-col w-full mt-7'>
                            <select
                                className='bg-orange px-4 py-2 rounded text-white mt-3'
                                name='candidatures'
                                defaultValue="default"
                                onChange={(e) => ouvrirModal(e.target.value)}
                            >
                                <option value="default">
                                    --- {t("voir")} ({listeEtudiantsAppliques.length}) {t("candidatures")} ---
                                </option>
                                {listeEtudiantsAppliques.map((etudiant) => (
                                    <option key={etudiant.id} value={etudiant.id}>
                                        {etudiant.prenom} {etudiant.nom}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>
            }
            <InfoDetailleeEtudiant
                isModalOpen={isModalCandidatureOpen}
                setIsModalOpen={closeModal}
                infosEtudiant={selectedEtudiant}
                summonEtudiant={summonEtudiant}
                summonMessage={summonMessage}
                setSummonMessage={setSummonMessage}
                studentInfo={studentInfo}
                refuserEtudiant={refuserEtudiant}
                accepterEtudiant={accepterEtudiant}
            />
        </div>
    );
};

export default InfoDetailleeOffreStage;