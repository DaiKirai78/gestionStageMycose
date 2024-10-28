import axios from "axios";
import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";

const ListeEtudiants = () => {

    const localhost = "http://localhost:8080/";
    const apiUrl = "gestionnaire/getEtudiantsSansContrat"
    const token = localStorage.getItem("token");
    const { t } = useTranslation();

    const [etudiants, setEtudiants] = useState({})

    useEffect(() => {
        fetchEtudiants();
    }, []);

    const fetchEtudiants = async () => {
        try {
            const response = await axios.get(localhost + apiUrl,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
            setEtudiants(response.data);
        } catch (e) {
            console.error("Erreur lors de la récupération des étudiants sans contrat : " + e);
        }
    }

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
            </div>
        </div>
    )
}

export default ListeEtudiants;