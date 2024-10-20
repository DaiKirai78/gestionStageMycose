import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

function EmployeursOffreStage() {
    const { t } = useTranslation();
    const [employeurs, setEmployeurs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [totalOffres, setTotalOffres] = useState(0);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    useEffect(() => {
        const fetchEmployeurs = async () => {
            try {
                setLoading(true);
                const response = await fetch(`http://localhost:8080/api/offres-stages/waiting?page=${currentPage}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (!response.ok) {
                    throw new Error(t("errorRetrievingEmployeurs"));
                }
                const data = await response.json();

                const sortedData = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                setEmployeurs(sortedData);
                setLoading(false);
            } catch (error) {
                console.error(t("errorRetrievingEmployeurs"), error);
                setError(t("errorRetrievingEmployeurs"));
                setLoading(false);
            }
        };

        const fetchTotalPages = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/offres-stages/pages', {
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

        const fetchTotalOffres = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/offres-stages/totalwaitingoffres', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (!response.ok) {
                    throw new Error(t("errorRetrievingTotalOffres"));
                }
                const total = await response.json();
                setTotalOffres(total);
            } catch (error) {
                console.error(t("errorRetrievingTotalOffres"), error);
            }
        };

        fetchEmployeurs();
        fetchTotalPages();
        fetchTotalOffres()
    }, [currentPage]);

    if (loading) return <p className={"justify-center text-center"}>{t("loading")}</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="bg-[#FFF8F2] rounded-lg shadow-lg p-8 w-screen max-w-3xl">
                <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("internshipOfferList")}</h1>
                <h2 className="mb-8 text-xl text-center">{t("clickEmployeurOffreStage")}</h2>
                {employeurs.length > 0 ? (
                    <p className="text-center text-gray-700 mb-4">{totalOffres} {t("waitingInternship")}</p>
                ) : (
                    <p className="text-center text-gray-700 mb-4">{t("noInternshipWaiting")}</p>
                )}
                <ul className="space-y-4">
                    {employeurs.map((employeur) => (
                        <li
                            key={employeur.id}
                            className="p-6 border border-gray-300 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 cursor-pointer"
                            onClick={() => {
                                navigate(`/validerOffreStage/${employeur.entrepriseName}`, { state: { offreStage: employeur } })}}
                        >
                            <h2 className="text-2xl font-semibold">{employeur.title}</h2>
                            <p className="text-gray-700">{t("companyName")} : {t(employeur.entrepriseName)}</p>
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
                            currentPage === totalPages || employeurs.length === 0 ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"
                        } rounded-r`}
                        onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                        disabled={currentPage === totalPages || employeurs.length === 0}
                    >
                        {t("next")}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default EmployeursOffreStage;
