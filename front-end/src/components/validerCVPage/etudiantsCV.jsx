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
    const [totalCVs, setTotalCVs] = useState(0);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    useEffect(() => {
        const fetchStudents = async () => {
            try {
                setLoading(true);
                const response = await fetch(`http://localhost:8080/api/cv/waitingcv?page=${currentPage}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (!response.ok) {
                    throw new Error(t("errorRetrievingStudents"));
                }
                const data = await response.json();

                const sortedData = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                setStudents(sortedData);
                setLoading(false);
            } catch (error) {
                console.error(t("errorRetrievingStudents"), error);
                setError(t("errorRetrievingStudents"));
                setLoading(false);
            }
        };

        const fetchTotalPages = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/cv/pages', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (!response.ok) {
                    throw new Error(t("errorRetrievingNbPages"));
                }
                const pages = await response.json();
                setTotalPages(pages);
            } catch (error) {
                console.error(t("errorRetrievingNbPages"), error);
            }
        };

        const fetchTotalCVs = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/cv/totalwaitingcvs', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (!response.ok) {
                    throw new Error(t("errorRetrievingTotalCVs"));
                }
                const total = await response.json();
                setTotalCVs(total);
            } catch (error) {
                console.error(t("errorRetrievingTotalCVs"), error);
            }
        };


        fetchStudents();
        fetchTotalPages();
        fetchTotalCVs();
    }, [currentPage]);

    if (loading) return <p className={"justify-center text-center"}>{t("loading")}</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("studentList")}</h1>
                <h2 className="mb-8 text-xl text-center">{t("clickStudentCV")}</h2>
                {students.length > 0 ? (
                    <p className="text-center text-gray-700 mb-4">{totalCVs} {t("waitingCV")}</p>
                ) : (
                    <p className="text-center text-gray-700 mb-4">{t("noCVWaiting")}</p>
                )}
                <ul className="space-y-4">
                    {students.map((student) => (
                        <li
                            key={student.id}
                            className="p-6 border border-gray-300 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 cursor-pointer"
                            onClick={() => {
                                navigate(`/validerCV/${student.studentFirstName}${student.studentLastName}`, {state: {cv: student}})
                            }}
                        >
                            <h2 className="text-2xl font-semibold">{student.studentFirstName} {student.studentLastName}</h2>
                            <p className="text-gray-700">{t("program")} : {t(student.programme)}</p>
                        </li>
                    ))}
                </ul>

                {/* Pagination */}
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
                            currentPage === totalPages ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"
                        } rounded-r`}
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
