import { useState, useEffect } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";
import { BsCloudArrowUpFill } from "react-icons/bs";
import logoPdf from '../../assets/pdficon.png';
import InputErrorMessage from "../inputErrorMesssage.jsx";

function FileOffreStage() {
    const { t } = useTranslation();
    const [file, setFile] = useState(null);
    const [title, setTitle] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [programmes, setProgrammes] = useState([]);
    const [programme, setProgramme] = useState("");
    const [students, setStudents] = useState([]);
    const [selectedStudents, setSelectedStudents] = useState([]);
    const [fileExtensionError, setFileExtensionError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [uploadError, setUploadError] = useState("");
    const [titleError, setTitleError] = useState("");
    const [companyNameError, setCompanyNameError] = useState("");
    const [programmeError, setProgrammeError] = useState("");
    const [role, setRole] = useState("");
    const [isPrivate, setIsPrivate] = useState(false);

    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await axios.post("http://localhost:8080/utilisateur/me", {}, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const userData = response.data;
                setRole(userData.role);
            } catch (error) {
                console.error("Erreur lors de la récupération des informations de l'utilisateur :", error);
            }
        };

        fetchUserData();
    }, []);

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
        if (role === "GESTIONNAIRE_STAGE" && programme) {
            const fetchStudents = async () => {
                const token = localStorage.getItem("token"); // Récupérer le token du localStorage
                try {
                    const response = await axios.get(
                        `http://localhost:8080/gestionnaire/getEtudiantsParProgramme?programme=${programme}`, {
                            headers: { Authorization: `Bearer ${token}` } // Inclure le token dans les headers
                        }
                    );
                    const sortedStudents = response.data.sort((a, b) => {
                        const fullNameA = `${a.nom} ${a.prenom}`.toLowerCase();
                        const fullNameB = `${b.nom} ${b.prenom}`.toLowerCase();
                        return fullNameA.localeCompare(fullNameB);
                    });
                    setStudents(sortedStudents);
                    console.log("Étudiants récupérés avec succès :", sortedStudents);
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
    }, [role, programme]);




    const handleStudentSelection = (studentId) => {
        setSelectedStudents((prevSelected) => {
            if (prevSelected.includes(studentId)) {
                return prevSelected.filter((id) => id !== studentId);
            } else {
                return [...prevSelected, studentId];
            }
        });
    };


    const handleFileUpload = async () => {
        let token = localStorage.getItem("token");
        const formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);

        if (role === "GESTIONNAIRE_STAGE") {
            formData.append("entrepriseName", companyName);
            formData.append("programme", programme);
            if (isPrivate) {
                formData.append("etudiantsPrives", selectedStudents);
            }
        }

        try {
            const response = await axios.post("http://localhost:8080/api/offres-stages/upload-file", formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data",
                },
            });
            console.log("Fichier envoyé avec succès :", response.data);
            console.log("étudiants sélectionnés :", selectedStudents);
            setSuccessMessage(t("fileUploadSuccess"));
            setUploadError("");

            setFile(null);
            setTitle("");
            setCompanyName("");
            setProgramme("");
            setIsPrivate(false);
            setSelectedStudents([]);
            document.getElementById("file").value = "";

        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
            setUploadError(t("fileUploadError"));
            setSuccessMessage("");
        }
    };

    const handleSubmitFile = async (e) => {
        e.preventDefault();
        let hasError = false;

        if (!file) {
            setFileExtensionError("fileRequired");
            hasError = true;
        } else {
            setFileExtensionError("");
        }

        if (!title) {
            setTitleError("titleRequired");
            hasError = true;
        } else {
            setTitleError("");
        }

        if (role === "GESTIONNAIRE_STAGE" && !companyName) {
            setCompanyNameError("companyNameRequired");
            hasError = true;
        } else {
            setCompanyNameError("");
        }

        if (role === "GESTIONNAIRE_STAGE" && !programme) {
            setProgrammeError("programmeRequired");
            hasError = true;
        } else {
            setProgrammeError("");
        }

        if (hasError) return;

        await handleFileUpload();
    };

    function dropHandler(e) {
        e.preventDefault();
        const fichier = e.dataTransfer.files[0];
        if (!fichier.name.endsWith(".pdf")) {
            setFileExtensionError("wrongFileExtension");
            return;
        }
        setFileExtensionError("");
        setFile(fichier);
    }

    const handleFileChange = (e) => {
        const fichier = e.target.files[0];
        if (!fichier.name.endsWith(".pdf")) {
            setFileExtensionError("wrongFileExtension");
            return;
        }
        setFileExtensionError("");
        setFile(fichier);
    };

    const handleRemoveFile = () => {
        setFile(null);
        setFileExtensionError("");
        document.getElementById("file").value = "";
    };

    function ChangeProgrammeValue(e) {
        setProgramme(e.target.value);
    }

    return (
        <form onSubmit={handleSubmitFile} className="space-y-4 w-full">
            <div className="relative w-full">
                <label
                    htmlFor="file"
                    onDrop={dropHandler}
                    onDragOver={(e) => {
                        e.preventDefault()
                    }}
                    className="flex justify-center w-full h-32 px-4 transition border-2 border-gray-300 border-dashed rounded-md cursor-pointer hover:border-gray-400 focus:outline-none">
                    <span className="flex items-center space-x-2">
                        <BsCloudArrowUpFill/>
                        <span className="font-medium text-gray-600">
                            {t("dropFile")} &nbsp;
                            <span className="text-blue-600 underline">{t("browse")}</span>
                        </span>
                    </span>
                    <input type="file" id="file" className="hidden" onChange={handleFileChange}
                           accept="application/pdf"/>
                </label>
                <InputErrorMessage messageKey={fileExtensionError}/>
                {file && (
                    <div
                        className="mt-4 flex items-center justify-between py-2 px-4 bg-[#FFF8F2] border-2 border-gray-400 rounded-md">
                        <div className="flex gap-2 items-center">
                            <img src={logoPdf} alt="icone pdf" className="w-10"/>
                            <p>{file.name}</p>
                        </div>
                        <button type="button" onClick={handleRemoveFile} className="text-gray-400">✖</button>
                    </div>
                )}
            </div>

            {/* Champ pour le nom de l'entreprise */}
            {role === "GESTIONNAIRE_STAGE" && (
                <div className="relative w-full">
                    <label htmlFor="companyName"
                           className="block text-sm font-medium text-black mt-4">{t("companyName")}</label>
                    <input
                        type="text"
                        id="companyName"
                        name="companyName"
                        value={companyName}
                        onChange={(e) => {
                            setCompanyName(e.target.value);
                            setCompanyNameError("");
                        }}
                        className={`mt-1 p-2 w-full border ${companyNameError ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    />
                    {companyNameError && <p className="text-red-500 text-sm">{t("entrepriseNameRequired")}</p>}
                </div>
            )}

            {role === "GESTIONNAIRE_STAGE" && (
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
            )}

            {/* Champ pour le titre */}
            <div className="relative w-full">
                <label htmlFor="title" className="block text-sm font-medium text-black mt-4">{t("title")}</label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    value={title}
                    onChange={(e) => {
                        setTitle(e.target.value);
                        setTitleError("")
                    }}
                    className={`mt-1 p-2 w-full border ${titleError ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                />
                {titleError && <p className="text-red-500 text-sm">{t("titleRequired")}</p>}
            </div>

            {/* Section pour choisir si l'offre est privée ou publique */}
            {role === "GESTIONNAIRE_STAGE" && (
                <div className="mb-4">
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
            )}

            {isPrivate && role === "GESTIONNAIRE_STAGE" && (
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
                </div>
            )}

            {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
            {uploadError && <p className="text-red-500 mt-4">{uploadError}</p>}

            <div className="flex justify-center">
                <button type="submit" className="bg-[#FE872B] text-white p-2 rounded-lg">{t("submit")}</button>
            </div>
        </form>
    );
}

export default FileOffreStage;