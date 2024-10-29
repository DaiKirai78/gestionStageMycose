import axios from "axios";
import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import LoadingSpinner from "../loadingSpinner.jsx";

const ListeEtudiantsSansContrat = () => {

    const localhost = "http://localhost:8080/";
    const apiUrlGetEtudiantsSansContrat = "gestionnaire/getEtudiantsSansContrat";
    const apiUrlGetNombreDePages = "gestionnaire/getEtudiantsSansContratPages";
    const token = localStorage.getItem("token");
    const {t} = useTranslation();
    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [etudiants, setEtudiants] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const etudiantsBootstrap = [
        {
            id: 1,
            nom: "Berrios",
            prenom: "Roberto",
            programme: "Technique de l'informatique"
        },
        {
            id: 2,
            nom: "Cabezas",
            prenom: "Vicente",
            programme: "Génie logiciel"
        },
        {
            id: 3,
            nom: "Mihoubi",
            prenom: "Karim",
            programme: "Technique de l'informatique"
        }
    ];


    useEffect(() => {
        // fetchEtudiants();
        // fetchTotalPages();
        setEtudiants(etudiantsBootstrap);
    }, []);
    //
    // const fetchEtudiants = async () => {
    //     try {
    //         setLoading(true);
    //         const response = await axios.get(localhost + apiUrlGetEtudiantsSansContrat,
    //             {
    //                 headers: {
    //                     Authorization: `Bearer ${token}`,
    //                     'Content-Type': 'application/json'
    //                 }
    //             });
    //         setEtudiants(response.data);
    //         setLoading(false);
    //     } catch (e) {
    //         console.error("Erreur lors de la récupération des étudiants sans contrat : " + e);
    //         setLoading(false);
    //     }
    // }

    // const fetchTotalPages = async () => {
    //     try {
    //         setLoading(true);
    //         const response = await fetch(localhost + apiUrlGetNombreDePages, {
    //             headers: { Authorization: `Bearer ${token}` },
    //         });
    //         if (!response.ok) {
    //             throw new Error(t("errorRetrievingNbPages"));
    //         }
    //         const pages = await response.json();
    //         setTotalPages(pages);
    //         setLoading(false);
    //     } catch (error) {
    //         console.error(t("errorRetrievingNbPages"), error);
    //         setLoading(false);
    //     }
    // }

    if (loading) return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div className="w-full max-w-3xl bg-white py-14 px-12 rounded-lg shadow-lg border border-gray-200 mt-10">
                <LoadingSpinner/>
            </div>
        </div>
    )

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("studentListWithoutContract")}</h1>
                <h2 className="mb-8 text-xl text-center">{t("clickStudentContract")}</h2>
                {etudiants.length === 1 ? (
                    <p className="text-center text-gray-700 mb-4">{etudiants.length} {t("waitingStudent")}</p>
                ) : etudiants.length > 1 ? (
                    <p className="text-center text-gray-700 mb-4">{etudiants.length} {t("waitingStudents")}</p>
                ) : (
                    <p className="text-center text-gray-700 mb-4">{t("noStudentWaiting")}</p>
                )}
                <ul className="space-y-4">
                    {etudiants.map((etudiant) => (
                        <li
                            key={etudiant.id}
                            className="p-6 border border-gray-300 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 cursor-pointer"
                            onClick={() => {
                                navigate(`/attribuerContrat/${etudiant.id}`, {state: {etudiant: etudiant}})
                            }}
                        >
                            <h2 className="text-2xl font-semibold">{etudiant.prenom} {etudiant.nom}</h2>
                            <p className="text-gray-700">{t("program")} : {t(etudiant.programme)}</p>
                        </li>
                    ))}
                </ul>
                <div className="flex justify-center mt-8">
                    <button
                        className={`px-4 py-2 ${
                            currentPage === 1 ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"
                        } rounded-l`}
                        onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                        disabled={currentPage === 1}
                    >
                        {t("previous")}
                    </button>
                    <span className="px-4 py-2">{t("page")} {currentPage} / {Math.max(totalPages, 1)}</span>
                    <button
                        className={`px-4 py-2 ${
                            currentPage === totalPages || etudiants.length === 0 ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"
                        } rounded-r`}
                        onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                        disabled={currentPage === totalPages || etudiants.length === 0}
                    >
                        {t("next")}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ListeEtudiantsSansContrat;