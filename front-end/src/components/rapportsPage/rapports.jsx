import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import axios from "axios";

const Rapports = () => {
    const { t } = useTranslation();
    const [selectedReport, setSelectedReport] = useState("all-etudiants");
    const [data, setData] = useState([]);

    const handleSelectChange = (event) => {
        setSelectedReport(event.target.value);
    };

    useEffect(() => {
        const token = localStorage.getItem("token");
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/rapports/${selectedReport}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                setData(response.data);
            } catch (error) {
                console.error("Erreur lors du chargement des données:", error);
            }
        };
        fetchData();
    }, [selectedReport]);

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">
                    {t("generateReports")}
                </h1>
                <div className="dropdown text-right">
                    <select
                        className="dropdown-select bg-orange-light border-2 border-gray-600 font-bold py-2 px-4 rounded-lg"
                        value={selectedReport}
                        onChange={handleSelectChange}
                    >
                        <option value="all-etudiants">{t("allStudents")}</option>
                        <option value="etudiants-avec-cv-waiting">{t("studentsWithPendingCV")}</option>
                        <option value="etudiants-sans-cv">{t("studentsWithoutCV")}</option>
                        <option value="etudiants-avec-convocation">{t("studentsWithSummon")}</option>
                        <option value="etudiants-sans-convocation">{t("studentsWithoutSummon")}</option>
                        <option value="etudiants-interviewed">{t("interviewedStudents")}</option>
                        <option value="offres-validees">{t("approvedInternships")}</option>
                        <option value="offres-non-validees">{t("unapprovedInternships")}</option>
                    </select>
                </div>

                <div className="report-data">
                    {data.length === 0 ? (
                        <p>{t("noDataAvailable")}</p>
                    ) : (
                        data.map((item) => (
                            <div key={item.id} className="border-b border-gray-300 py-2">
                                {item.entrepriseName ? (
                                    // Affichage pour une offre de stage
                                    <>
                                        <h2 className="font-semibold">
                                            {item.title} - {item.entrepriseName}
                                        </h2>
                                        <p>{t("status")}: {t(item.status)}</p>
                                        <p>{t("annee")}: {item.annee}</p>
                                        <p>{t("session")}: {t(item.session)}</p>
                                        <p>{t("program")}: {t(item.programme)}</p>
                                        <p>{t("visibility")}: {t(item.visibility)}</p>
                                        <p>{t("createdThe")}: {new Date(item.createdAt).toLocaleDateString()}</p>
                                        <p>{t("modifiedLastThe")}: {new Date(item.updatedAt).toLocaleDateString()}</p>
                                    </>
                                ) : (
                                    // Affichage pour un étudiant
                                    <>
                                        <h2 className="font-semibold">
                                            {item.prenom} {item.nom} - {t(item.programme)}
                                        </h2>
                                        <p>{t("email")}: {item.courriel}</p>
                                        <p>{t("telephone")}: {item.numeroDeTelephone}</p>
                                        <p>{t("contractStatus")}: {t(item.contractStatus)}</p>
                                    </>
                                )}
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default Rapports;