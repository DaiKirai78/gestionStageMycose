import {BsCloudArrowUpFill} from "react-icons/bs";
import InputErrorMessage from "../inputErrorMesssage.jsx";
import logoPdf from "../../assets/pdficon.png";
import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import axios from "axios";
import LoadingSpinner from "../loadingSpinner.jsx";

const AttribuerContrat = () => {
    const {t} = useTranslation();

    const [file, setFile] = useState(null);
    const [fileExtensionError, setFileExtensionError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [uploadError, setUploadError] = useState("");
    const [loading, setLoading] = useState(true);
    const [applications, setApplications] = useState([]);
    const [etudiant, setEtudiant] = useState(null);
    const [offresStage, setOffreStage] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const localhost = "http://localhost:8080/";
    const apiUrlGetEtudiantFromApplicationId = "api/application-stage/getEtudiant/";
    const apiUrlGetOffreStageFromApplicationId = "api/application-stage/getOffreStage/";
    const apiUrlGetApplicationsAccepted = "api/application-stage/status/ACCEPTED";
    const apiUrlUploadContract = "contrat/upload"
    const token = localStorage.getItem("token");

    useEffect(() => {
        isLoading();
        fetchApplicationsAccepted();
    }, []);

    const fetchApplicationsAccepted = async () => {
        try {
            setLoading(true);
            const response = await axios.get(localhost + apiUrlGetApplicationsAccepted, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log("app : " + JSON.stringify(response.data));
            setApplications(response.data);
            setLoading(false);
        } catch (e) {
            console.error("Erreur lors de la récupération des applications acceptées : " + e);
            setLoading(false);
        }
    };

    useEffect(() => {
        if (applications.length > 0) {
            const applicationId = applications[currentPage - 1]?.id;
            if (applicationId) {
                fetchEtudiant(applicationId);
                fetchOffresStage(applicationId);
            }
        }
    }, [applications, currentPage]);

    useEffect(() => {
        setTotalPages(applications.length);
        if (currentPage > 1)
            setCurrentPage(currentPage - 1);
    }, [applications]);


    const fetchEtudiant = async (applicationId) => {
        try {
            setLoading(true);
            const response = await axios.get(localhost + apiUrlGetEtudiantFromApplicationId + applicationId, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log("Étudiant : " + response.data);
            setEtudiant(response.data);
            setLoading(false);
        } catch (e) {
            setLoading(false);
            console.error(`Erreur lors de la récupération de l'étudiant associé à l'application ${applicationId} : `, e);
        }
    };

    const fetchOffresStage = async (applicationId) => {
        try {
            const response = await axios.get(localhost + apiUrlGetOffreStageFromApplicationId + applicationId, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setOffreStage(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération de l'offre de stage associé à l'application ${applicationId} : `, e);
        }
    };

    function isLoading() {
        if (etudiant !== null)
            setLoading(false);
    }

    const handleSubmitFile = async (e) => {
        e.preventDefault();
        let hasError = false;

        if (!file) {
            setFileExtensionError("fileRequired");
            hasError = true;
        } else {
            setFileExtensionError("");
        }

        if (hasError) return;

        await handleFileUpload();
    };

    const handleFileUpload = async () => {
        const formData = new FormData();
        formData.append("contratPDF", file);
        formData.append("etudiantId", applications[currentPage - 1].etudiant_id);
        formData.append("employeurId", offresStage.createur_id);

        try {
            setLoading(true);
            const response = await axios.post(localhost + apiUrlUploadContract, formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data",
                    },
                });
            console.log("Fichier envoyé avec succès :", response.data);
            setSuccessMessage(t("fileUploadSuccess"));
            setUploadError("");
            setFile(null);
            setLoading(false);
            fetchApplicationsAccepted();
        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
            setUploadError(t("fileUploadError"));
            setSuccessMessage("");
            setLoading(false);
        }
    };

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

    if (loading) return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="w-full max-w-3xl bg-white py-14 px-12 rounded-lg shadow-lg border border-gray-200 mt-10">
                <LoadingSpinner/>
            </div>
        </div>
    )

    return (
        <div className="flex items-start justify-center min-h-full p-8">
            <div className="w-full max-w-3xl bg-white py-14 px-12 rounded-lg shadow-lg border border-gray-200 mt-10">
                {
                    applications.length > 0 ?
                        <div>
                            <h1 className="text-4xl font-extrabold mb-4 text-gray-800 text-center">
                                {t("uploadContract")}
                            </h1>
                            <h2 className="text-lg text-gray-600 mb-8 text-center">
                                {t("uploadContractFilePDF")}
                            </h2>
                            {
                                etudiant ?
                                    <div
                                        className="bg-[#f9fafb] rounded-lg py-6 px-5 mb-8 border-l-4 border-orange shadow-sm">
                                        <h3 className="text-xl font-semibold text-gray-700">{t("etudiant")} :</h3>
                                        <p className="text-lg font-medium text-gray-800">
                                            {etudiant.prenom + " " + etudiant.nom}
                                        </p>
                                        <h3 className="text-xl font-semibold text-gray-700 mt-3">{t("program")} :</h3>
                                        <p className="text-lg font-medium text-gray-800">
                                            {t(etudiant.programme)}
                                        </p>
                                    </div>
                                    : <div></div>
                            }
                            {
                                applications[currentPage - 1] ?
                                    <div
                                        className="bg-[#f9fafb] rounded-lg py-6 px-5 mb-8 border-l-4 border-red-300 shadow-sm">
                                        <h3 className="text-xl font-semibold text-gray-700">Stage :</h3>
                                        <p className="text-lg font-medium text-gray-800">
                                            {applications[currentPage - 1].title}
                                        </p>
                                        <h3 className="text-xl font-semibold text-gray-700 mt-3">Entreprise :</h3>
                                        <p className="text-lg font-medium text-gray-800">
                                            {applications[currentPage - 1].entrepriseName}
                                        </p>
                                    </div>
                                    : <div></div>
                            }
                            <form onSubmit={handleSubmitFile} className="space-y-6">
                                <div className="relative">
                                    <label
                                        htmlFor="file"
                                        onDrop={dropHandler}
                                        onDragOver={(e) => e.preventDefault()}
                                        className="flex flex-col items-center justify-center w-full h-40 px-6 py-4 bg-gray-50 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer hover:border-orange hover:bg-orange-light transition duration-200">
                                        <BsCloudArrowUpFill className="text-orange text-3xl mb-2"/>
                                        <span className="font-medium text-gray-600">
                        {t("dropFile")}{" "}
                                            <span className="text-orange-dark underline">{t("browse")}</span>
                    </span>
                                        <input type="file" id="file" className="hidden" onChange={handleFileChange}
                                               accept="application/pdf"/>
                                    </label>
                                    <InputErrorMessage messageKey={fileExtensionError}
                                                       className="text-red-600 mt-2 text-sm"/>
                                    {file && (
                                        <div
                                            className="mt-4 flex items-center justify-between p-3 bg-deep-orange-50 border border-deep-orange-200 rounded-lg shadow-sm">
                                            <div className="flex items-center gap-3">
                                                <img src={logoPdf} alt="PDF icon" className="w-8"/>
                                                <span className="text-gray-800 font-medium">{file.name}</span>
                                            </div>
                                            <button type="button" onClick={handleRemoveFile}
                                                    className="text-red-500 hover:text-red-700 transition duration-150">
                                                ✖
                                            </button>
                                        </div>
                                    )}
                                </div>
                                {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
                                {uploadError && <p className="text-red-500 mt-4">{uploadError}</p>}
                                <button type="submit"
                                        className="w-full py-3 text-lg font-semibold text-white bg-orange rounded-lg hover:bg-orange-dark transition duration-200">
                                    {t("submit")}
                                </button>
                            </form>
                            <div className="flex justify-center mt-8">
                                <button
                                    className={`px-4 py-2 ${currentPage === 1 ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"} rounded-l`}
                                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                                    disabled={currentPage === 1}>
                                    {t("previous")}
                                </button>
                                <span className="px-4 py-2">
                        {t("page")} {currentPage} / {Math.max(totalPages, 1)}
                    </span>
                                <button
                                    className={`px-4 py-2 ${currentPage === totalPages || applications.length === 0 ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900"} rounded-r`}
                                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                                    disabled={currentPage === totalPages || applications.length === 0}>
                                    {t("next")}
                                </button>
                            </div>
                        </div> : <div className="text-center">
                            <h1 className="mt-28 text-3xl font-bold text-gray-900 mb-6">Aucune demande de contrat</h1>
                            <h2 className="mb-28 text-lg text-gray-700">Vous serez alerté quand il y aura une nouvelle demande de
                                contrat !</h2>
                        </div>
                }
            </div>
        </div>
    )
}

export default AttribuerContrat;