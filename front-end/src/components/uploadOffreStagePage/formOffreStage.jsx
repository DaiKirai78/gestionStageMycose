import {useState, useEffect } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";

function FormOffreStage() {
    const { t } = useTranslation();

    const [formData, setFormData] = useState({
        entrepriseName: "",
        employerName: "",
        email: "",
        website: "",
        title: "",
        location: "",
        salary: "",
        description: "",
        programme: "",
        annee: "",
        session: ""
    });

    const [error, setError] = useState({
        entrepriseName: "",
        employerName: "",
        email: "",
        website: "",
        title: "",
        location: "",
        salary: "",
        description: "",
        programme: "",
        annee: "",
        session: ""
    });

    const [programmes, setProgrammes] = useState([]);
    const [successMessage, setSuccessMessage] = useState("");
    const [submitError, setSubmitError] = useState("");
    const [role, setRole] = useState("");
    const [students, setStudents] = useState([]);
    const [selectedStudents, setSelectedStudents] = useState([]);
    const [sessions, setSessions] = useState([]);
    const [years, setYears] = useState([]);
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
        const fetchYears = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/offres-stages/years");
                setYears(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération des années :", error);
            }
        };
        const fetchSessions = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/offres-stages/sessions");
                setSessions(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération des sessions :", error);
            }
        };
        fetchYears();
        fetchSessions();
    }, []);

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
        if (role === "GESTIONNAIRE_STAGE" && formData.programme) {
            const fetchStudents = async () => {
                const token = localStorage.getItem("token");
                try {
                    const response = await axios.get(
                        `http://localhost:8080/gestionnaire/getEtudiantsParProgramme?programme=${formData.programme}`,
                        {
                            headers: { Authorization: `Bearer ${token}` },
                        }
                    );
                    const sortedStudents = response.data.sort((a, b) => {
                        const fullNameA = `${a.nom} ${a.prenom}`.toLowerCase();
                        const fullNameB = `${b.nom} ${b.prenom}`.toLowerCase();
                        return fullNameA.localeCompare(fullNameB);
                    });
                    setStudents(sortedStudents);
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
    }, [role, formData.programme]);

    const handleStudentSelection = (studentId) => {
        setSelectedStudents((prevSelected) => {
            const newSelected = prevSelected.includes(studentId)
                ? prevSelected.filter((id) => id !== studentId)
                : [...prevSelected, studentId];

            if (isPrivate && newSelected.length > 0) {
                setSubmitError("");
            }

            return newSelected;
        });
    };


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });

        if (value !== "") {
            setError((prevError) => ({
                ...prevError,
                [name]: "",
            }));
        }
    };

    const handleSubmitForm = async (e) => {
        let token = localStorage.getItem("token");
        e.preventDefault();

        let valid = true;
        let errors = {
            entrepriseName: "",
            employerName: "",
            email: "",
            website: "",
            title: "",
            location: "",
            salary: "",
            description: "",
            programme: "",
            annee: "",
            session: ""
        };

        if (role === "GESTIONNAIRE_STAGE" && !formData.programme) {
            errors.programme = t("programRequired");
            valid = false;
        }

        // Validation pour 'entrepriseName'
        if (role === "GESTIONNAIRE_STAGE" && !formData.entrepriseName) {
            errors.entrepriseName = t("entrepriseNameRequired");
            valid = false;
        }

        // Validation pour 'employerName'
        if (role === "GESTIONNAIRE_STAGE" && !formData.employerName) {
            errors.employerName = t("employerNameRequired");
            valid = false;
        }

        // Validation pour 'email'
        if (!/\S+@\S+\.\S+/.test(formData.email)) {
            errors.email = t("emailInvalid");
            valid = false;
        }

        if (!formData.email) {
            errors.email = t("emailRequired");
            valid = false;
        }

        // Validation pour 'website'
        if (formData.website && !/^(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/[^\s]*)?$/.test(formData.website)) {
            errors.website = t("websiteInvalid");
            valid = false;
        }

        if (!formData.website) {
            errors.website = t("websiteRequired");
            valid = false;
        }

        // Validation pour 'title'
        if (!formData.title) {
            errors.title = t("titleRequired");
            valid = false;
        }

        // Validation pour 'location'
        if (!formData.location) {
            errors.location = t("locationRequired");
            valid = false;
        }

        // Validation pour 'salary'
        if (!formData.salary) {
            errors.salary = t("salaryRequired");
            valid = false;
        }

        if (formData.salary && isNaN(formData.salary)) {
            errors.salary = t("salaryInvalid");
            valid = false;
        }

        if (formData.salary && !/^\d{1,10}(\.\d{1,2})?$/.test(formData.salary)) {
            errors.salary = t("salaryInvalidMaxNb");
            valid = false;
        }

        // Validation pour 'description'
        if (!formData.description) {
            errors.description = t("descriptionRequired");
            valid = false;
        }

        // Vérifier si l'offre est privée et s'il y a des étudiants sélectionnés
        if (isPrivate && selectedStudents.length === 0) {
            setSubmitError(t("selectStudentError"));
            valid = false;
        }

        if (!formData.annee) {
            errors.annee = t("yearRequired");
            valid = false;
        }

        if (!formData.session) {
            errors.session = t("sessionRequired");
            valid = false;
        }

        setError(errors);

        if (!valid) {
            return;
        }

        try {
            const response = await axios.post(
                "http://localhost:8080/api/offres-stages/upload-form",
                {
                    ...formData,
                    etudiantsPrives: isPrivate ? selectedStudents : null,
                    programme: !formData.programme ? "NOT_SPECIFIED" : formData.programme 
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );
            console.log("Formulaire envoyé avec succès :", response.data);
            setSuccessMessage(t("formSubmissionSuccess"));
            setSubmitError("");
            setFormData({
                entrepriseName: "",
                employerName: "",
                email: "",
                website: "",
                title: "",
                location: "",
                salary: "",
                description: "",
                programme: "",
                annee: "",
                session: ""
            });
            setSelectedStudents([]);
            setIsPrivate(false);
        } catch (error) {
            console.error("Erreur lors de l'envoi du formulaire :", error);
            setSubmitError(t("formSubmissionError"));
            setSuccessMessage("");
        }
    };

    const isPrivateEnabled = role === "GESTIONNAIRE_STAGE" && formData.programme;

    return (
        <form onSubmit={handleSubmitForm} className="w-full">
            <h1 className="text-2xl text-black">{t("companyDetails")}</h1>
            <hr className="border-1 border-black"/>

            {role === "GESTIONNAIRE_STAGE" && (
                <div>
                    <div className="space-y-2">
                        <label htmlFor="entrepriseName" className="block text-sm font-medium text-black mt-4">
                            {t("companyName")}
                        </label>
                        <input
                            type="text"
                            id="entrepriseName"
                            name="entrepriseName"
                            value={formData.entrepriseName}
                            onChange={handleInputChange}
                            className={`mt-1 p-2 block w-full border ${error.entrepriseName ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                            autoComplete="organization"
                        />
                        {error.entrepriseName && <p className="text-red-500 text-sm mt-1">{error.entrepriseName}</p>}
                    </div>
                    <div className="space-y-2">
                        <label htmlFor="employerName" className="block text-sm font-medium text-black mt-4">
                            {t("employerName")}
                        </label>
                        <input
                            type="text"
                            id="employerName"
                            name="employerName"
                            value={formData.employerName}
                            onChange={handleInputChange}
                            className={`mt-1 p-2 block w-full border ${error.employerName ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                            autoComplete="name"
                        />
                        {error.employerName && <p className="text-red-500 text-sm mt-1">{error.employerName}</p>}
                    </div>
                </div>
            )}

            <div className="space-y-2">
                <label htmlFor="email" className="block text-sm font-medium text-black mt-4">
                    {t("email")}
                </label>
                <input
                    type="text"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.email ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="email"
                />
                {error.email && <p className="text-red-500 text-sm mt-1">{error.email}</p>}
            </div>


            <div className="space-y-2">
            <label htmlFor="website" className="block text-sm font-medium text-black mt-4">
                    {t("website")}
                </label>
                <input
                    type="text"
                    id="website"
                    name="website"
                    value={formData.website}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.website ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="url"
                />
                {error.website && <p className="text-red-500 text-sm mt-1">{error.website}</p>}
            </div>


            <h1 className="text-2xl text-black mt-4">{t("internshipDetails")}</h1>
            <hr className="border-1 border-black"/>

            <div className="space-y-2">
                <label htmlFor="title" className="block text-sm font-medium text-black mt-4">
                    {t("title")}
                </label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.title ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.title && <p className="text-red-500 text-sm mt-1">{error.title}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="location" className="block text-sm font-medium text-black mt-4">
                    {t("location")}
                </label>
                <input
                    type="text"
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.location ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.location && <p className="text-red-500 text-sm mt-1">{error.location}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="salary" className="block text-sm font-medium text-black mt-4">
                    {t("salary")}
                </label>
                <input
                    type="text"
                    id="salary"
                    name="salary"
                    value={formData.salary}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.salary ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.salary && <p className="text-red-500 text-sm mt-1">{error.salary}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="description" className="block text-sm font-medium text-black mt-4">
                    {t("description")}
                </label>
                <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.description ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    rows={10}
                />
                {error.description && <p className="text-red-500 text-sm mt-1">{error.description}</p>}
            </div>

            {/* Input et label pour l'année */}
            <div>
                <label htmlFor="annee" className="block text-sm font-medium text-black mt-4">{t("choisirAnnee")}</label>
                <select
                    id="annee"
                    name="annee"
                    className={`block w-full p-2 border border-black rounded-md ${error.annee ? 'border-red-500' : 'border-black'} bg-transparent`}
                    value={formData.annee}
                    onChange={handleInputChange}
                >
                    <option value="" className={"text-center"}>-- {t("choisirAnnee")} --</option>
                    {years.map((year, index) => (
                        <option key={index} value={year}>
                            {year}
                        </option>
                    ))}
                </select>
                {error.annee && <p className="text-red-500 text-sm">{error.annee}</p>}
            </div>

            {/* Input et label pour la session */}
            <div>
                <label htmlFor="session" className="block text-sm font-medium text-black mt-4">{t("choisirSession")}</label>
                <select
                    id="session"
                    name="session"
                    className={`block w-full p-2 border border-black rounded-md ${error.session ? 'border-red-500' : 'border-black'} bg-transparent`}
                    value={formData.session}
                    onChange={handleInputChange}
                >
                    <option value="" className={"text-center"}>-- {t("choisirSession")} --</option>
                    {sessions.map((session, index) => (
                        <option key={index} value={session}>
                            {t(session)}
                        </option>
                    ))}
                </select>
                {error.session && <p className="text-red-500 text-sm">{error.session}</p>}
            </div>

            {role === "GESTIONNAIRE_STAGE" && (
                <div className="space-y-2">
                    <label htmlFor="programme" className="block text-sm font-medium text-black mt-4">
                        {t("choisirProgramme")}
                    </label>
                    <select
                        id="programme"
                        name="programme"
                        value={formData.programme}
                        onChange={handleInputChange}
                        className={`mt-1 p-2 block w-full border ${error.programme ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    >
                        <option value="" className={"text-center"}>-- {t("choisirProgramme")} --</option>
                        {programmes.map((programme, index) => (
                            <option key={index} value={programme}>
                                {t(programme)}
                            </option>
                        ))}
                    </select>
                    {error.programme && <p className="text-red-500 text-sm mt-1">{error.programme}</p>}
                </div>
            )}

            {role === "GESTIONNAIRE_STAGE" && (
                <div className="mt-4">
                    <label className="flex items-center">
                        <input
                            type="checkbox"
                            className="mr-2"
                            checked={isPrivate}
                            onChange={() => isPrivateEnabled && setIsPrivate(!isPrivate)}
                            disabled={!isPrivateEnabled}
                        />
                        <span
                            className={`${!isPrivateEnabled ? "text-gray-400" : ""}`}>{t("makeOfferPrivate")}</span>
                    </label>
                </div>
            )}

            {isPrivate && (
                <div className="mt-4">
                    <h2 className="block mb-2 text-sm font-medium text-black">{t("selectStudent")}</h2>
                    {students.map((student) => (
                        <div key={student.id}>
                            <label className="flex items-center cursor-pointer">
                                <input
                                    type="checkbox"
                                    className="mr-2"
                                    checked={selectedStudents.includes(student.id)}
                                    onChange={() => handleStudentSelection(student.id)}
                                />
                                {`${student.nom}, ${student.prenom}`}
                            </label>
                        </div>
                    ))}
                </div>
            )}

            {isPrivate && students.length === 0 && (
                <p className="text-black text-m mt-4">
                    {t("noStudentInProgram")}
                </p>
            )}

            {successMessage && <p className="text-green-500 text-sm mt-4">{successMessage}</p>}
            {submitError && <p className="text-red-500 text-sm mt-4">{submitError}</p>}

            <div className="flex justify-center mt-4">
                <button
                    type="submit"
                    className="max-w-xs w-full bg-[#FE872B] p-2 rounded-lg hover:bg-orange text-white"
                >
                    {t("submit")}
                </button>
            </div>
        </form>
    );
}

export default FormOffreStage;
