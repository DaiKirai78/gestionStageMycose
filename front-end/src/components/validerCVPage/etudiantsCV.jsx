import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

function EtudiantsCV() {
    const { t } = useTranslation();
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetch("students.json")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Erreur lors du chargement du fichier JSON");
                }
                return response.json();
            })
            .then((data) => {
                setStudents(data);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Erreur lors de la récupération des étudiants:", error);
                setError("Erreur lors de la récupération des étudiants.");
                setLoading(false);
            });
    }, []);

    if (loading) return <p className={"justify-center text-center"}>{t("loading")}</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("studentList")}</h1>
                <h2 className="mb-8 text-xl text-center">{t("clickStudentCV")}</h2>
                <ul className="space-y-4">
                    {students.map((student) => (
                        <li
                            key={student.id}
                            className="p-6 border border-gray-300 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 cursor-pointer"
                            onClick={() => navigate(`/validerCV/${student.id}`)}
                        >
                            <h2 className="text-2xl font-semibold">{student.prenom} {student.nom}</h2>
                            <p className="text-gray-700">Programme : {student.programme}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

export default EtudiantsCV;
