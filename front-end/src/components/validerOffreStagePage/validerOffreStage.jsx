import { useNavigate, useLocation } from "react-router-dom";
import {useEffect, useState} from "react";
import { useTranslation } from "react-i18next";
import axios from "axios";

function ValiderOffreStage() {
    const { t } = useTranslation();
    const { state } = useLocation();
    const { offreStage } = state || {};
    const navigate = useNavigate();
    const [commentaire, setCommentaire] = useState("");
    const [error, setError] = useState(null);
    const [programmeError, setProgrammeError] = useState("");
    const [studentSelectionError, setStudentSelectionError] = useState("");
    const [noStudentsInProgram, setNoStudentsInProgram] = useState("");
    const token = localStorage.getItem("token");
    const [programmes, setProgrammes] = useState([]);
    const [programme, setProgramme] = useState("");
    const [students, setStudents] = useState([]);
    const [selectedStudents, setSelectedStudents] = useState([]);
    const [isPrivate, setIsPrivate] = useState(false);

    useEffect(() => {
        const fetchProgrammes = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/programme");
                setProgrammes(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération des programmes :", error);
            }
        };

        fetchProgrammes();
    }, []);

    useEffect(() => {
        if (programme) {
            const fetchStudents = async () => {
                const token = localStorage.getItem("token");
                try {
                    const response = await axios.get(
                        `http://localhost:8080/gestionnaire/getEtudiantsParProgramme?programme=${programme}`, {
                            headers: { Authorization: `Bearer ${token}` }
                        }
                    );
                    const sortedStudents = response.data.sort((a, b) => {
                        const fullNameA = `${a.nom} ${a.prenom}`.toLowerCase();
                        const fullNameB = `${b.nom} ${b.prenom}`.toLowerCase();
                        return fullNameA.localeCompare(fullNameB);
                    });
                    setStudents(sortedStudents);

                    if (sortedStudents.length === 0) {
                        setNoStudentsInProgram(t("noStudentsInProgram"));
                    } else {
                        setNoStudentsInProgram("");
                    }
                } catch (error) {
                    console.error("Erreur lors de la récupération des étudiants :", error);
                }
            };

            fetchStudents();
        } else {
            setStudents([]);
            setSelectedStudents([]);
            setIsPrivate(false);
        }
    }, [programme]);

    const handleAccept = async () => {
        if (!programme) {
            setProgrammeError(t("programRequired"));
            return;
        }

        if (isPrivate && selectedStudents.length === 0) {
            setStudentSelectionError(t("selectStudentError"));
            return;
        }

        try {
            const payload = {
                id: offreStage.id,
                programme: programme,
                statusDescription: commentaire,
                // Only include etudiantsPrives if isPrivate is true
                ...(isPrivate && { etudiantsPrives: selectedStudents }),
            };

            // Log the payload for debugging
            console.log("Payload to send:", payload);

            const response = await axios.patch(
                `http://localhost:8080/api/offres-stages/accept`,
                payload, // Send the payload directly
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );
            navigate("/validerOffreStage");
        } catch (error) {
            console.error(error);
            setError(t("errorAcceptingInternship"));
        }
    };


    const handleReject = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/offres-stages/refuse?id=${offreStage.id}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    commentaire
                }),
            });

            if (!response.ok) {
                throw new Error(t("errorRefusingInternship"));
            }
            navigate("/validerOffreStage");
        } catch (error) {
            console.error(error);
            setError(t("errorRefusingInternship"));
        }
    };

    const handleStudentSelection = (studentId) => {
        setSelectedStudents((prevSelected) => {
            const newSelected = prevSelected.includes(studentId)
                ? prevSelected.filter((id) => id !== studentId)
                : [...prevSelected, studentId];

            if (newSelected.length > 0) {
                setStudentSelectionError("");
            }

            return newSelected;
        });
    };
    function ChangeProgrammeValue(e) {
        setProgramme(e.target.value);
    }

    if (!offreStage) return <p>{t("noInternshipFound")}</p>;

    return (
        <div className="min-h-screen flex items-start justify-center p-8">
            <div className="bg-[#FFF8F2] shadow-lg rounded-lg flex flex-col md:flex-row w-full max-w-6xl">
                {/* Section PDF ou Informations par défaut */}
                <div className="w-full md:w-[70%] p-8 border-b md:border-b-0 md:border-r border-gray-300">
                    <h1 className="text-4xl font-bold mb-6 mt-6 text-center">{t("employerInternship")}</h1>
                    <h2 className="mb-8 text-xl text-center">{t("acceptOrRefuseInternship")}</h2>
                    {offreStage.fileData ? (
                        <iframe
                            src={`data:application/pdf;base64,${offreStage.fileData}`}
                            title="offreStage"
                            className="w-full h-[62vh] border"
                        ></iframe>
                    ) : (
                        <div>
                            {offreStage.description && (
                                <p><strong>{t("description")}:</strong> {offreStage.description}</p>
                            )}
                            {offreStage.location && (
                                <p><strong>{t("location")}:</strong> {offreStage.location}</p>
                            )}
                            {offreStage.salary && (
                                <p><strong>{t("salary")}:</strong> {offreStage.salary}$/h</p>
                            )}
                            {offreStage.website && (
                                <p>
                                    <strong>{t("website")}: </strong>
                                    <a
                                        href={offreStage.website.startsWith("http") ? offreStage.website : `https://${offreStage.website}`}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        onClick={(e) => e.stopPropagation()}
                                    >
                                        {offreStage.website}
                                    </a>
                                </p>
                            )}
                        </div>
                    )}
                </div>

                {/* Section des informations et des actions */}
                <div className="w-full md:w-[30%] p-8 flex flex-col items-center md:items-start">
                    <h2 className="text-2xl font-bold mb-4">{offreStage.title}</h2>
                    <p className="mb-12"><strong>{t("companyName")}:</strong> {t(offreStage.entrepriseName)}</p>

                    <div>
                        <label className="block mb-2 text-sm font-medium text-black">{t("choisirProgramme")}</label>
                        <select
                            className={`block w-full p-2 border border-black rounded-md ${programmeError ? 'border-red-500' : 'border-black'} bg-transparent`}
                            value={programme}
                            onChange={(e) => {
                                ChangeProgrammeValue(e);
                                setProgrammeError("");
                            }}
                        >
                            <option value="" className={"text-center"}>-- {t("choisirProgramme")} --</option>
                            {programmes.map((programme, index) => (
                                <option key={index} value={programme}>
                                    {t(programme)}
                                </option>
                            ))}
                        </select>
                        {programmeError && <p className="text-red-500 text-sm">{t("programRequired")}</p>}
                    </div>

                    <div className="mt-2">
                        <label htmlFor="privateOffer" className="flex items-center space-x-2">
                            <input
                                type="checkbox"
                                id="privateOffer"
                                checked={isPrivate}
                                onChange={() => setIsPrivate(!isPrivate)}
                                disabled={!programme}
                            />
                            <span
                                className={`${!programme ? "text-gray-400" : ""}`}>{t("makeOfferPrivate")}</span>
                        </label>
                    </div>

                    {isPrivate && (
                        <div className="relative w-full">
                            <label className="block mb-2 text-sm font-medium text-black">{t("selectStudent")}</label>
                            <div className="space-y-2">
                                {students.map((student) => (
                                    <div key={student.id} className="flex items-center space-x-2">
                                        <input
                                            type="checkbox"
                                            id={`student-${student.id}`}
                                            checked={selectedStudents.includes(student.id)}
                                            onChange={() => handleStudentSelection(student.id)}
                                        />
                                        <label
                                            htmlFor={`student-${student.id}`}
                                            className="cursor-pointer"
                                        >
                                            {student.nom}, {student.prenom}
                                        </label>
                                    </div>
                                ))}
                            </div>
                            {noStudentsInProgram && <p className="text-black text-m">{t("noStudentInProgram")}</p>}
                            {studentSelectionError && <p className="text-red-500 text-sm">{t("selectStudentError")}</p>}
                        </div>
                    )}

                    {/* Boutons d'acceptation et de refus */}
                    <div className="mt-12 mb-4 w-full">
                        <button
                            className="bg-green-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleAccept}
                        >
                            {t("accept")}
                        </button>
                        <button
                            className="bg-red-500 text-white py-2 px-4 rounded mb-4 w-full"
                            onClick={handleReject}
                        >
                            {t("refuse")}
                        </button>
                    </div>

                    {/* Zone de texte pour les commentaires */}
                    <textarea
                        className="border border-gray-300 p-2 rounded w-full"
                        placeholder={t("leaveComment")}
                        rows={5}
                        value={commentaire}
                        onChange={(e) => setCommentaire(e.target.value)}
                    ></textarea>
                </div>
            </div>
        </div>
    );
}

export default ValiderOffreStage;
