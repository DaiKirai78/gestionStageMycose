import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

function EtudiantsCV() {
    const { t } = useTranslation();
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchStudents = async () => {
            try {
                setLoading(true);
                const response = await fetch(`http://localhost:8080/api/cv/waitingcv?page=${currentPage}`);
                if (!response.ok) {
                    throw new Error("Erreur lors de la récupération des étudiants");
                }
                const data = await response.json();
                setStudents(data);
                setLoading(false);
            } catch (error) {
                console.error("Erreur lors de la récupération des étudiants:", error);
                setError("Erreur lors de la récupération des étudiants.");
                setLoading(false);
            }
        };

        const fetchTotalPages = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/cv/pages');
                if (!response.ok) {
                    throw new Error("Erreur lors de la récupération du nombre de pages");
                }
                const pages = await response.json();
                setTotalPages(pages);
            } catch (error) {
                console.error("Erreur lors de la récupération du nombre de pages:", error);
            }
        };

        fetchStudents();
        fetchTotalPages();
    }, [currentPage]);

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
                            <h2 className="text-2xl font-semibold">{student.studentFirstName} {student.studentLastName}</h2>
                            <p className="text-gray-700">Programme : {student.programme}</p>
                        </li>
                    ))}
                </ul>

                {/* Pagination */}
                <div className="flex justify-center mt-8">
                    <button
                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded-l"
                        onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                        disabled={currentPage === 1}
                    >
                        {t("previous")}
                    </button>
                    <span className="px-4 py-2">{t("page")} {currentPage} / {totalPages}</span>
                    <button
                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded-r"
                        onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                        disabled={currentPage === totalPages}
                    >
                        {t("next")}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default EtudiantsCV;
