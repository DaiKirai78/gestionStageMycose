import {BsCloudArrowUpFill} from "react-icons/bs";
import InputErrorMessage from "../inputErrorMesssage.jsx";
import logoPdf from "../../assets/pdficon.png";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import Divider from "../divider.jsx";
import FileOffreStage from "../uploadOffreStagePage/fileOffreStage.jsx";
import FormOffreStage from "../uploadOffreStagePage/formOffreStage.jsx";

const AttribuerContrat = ({etudiant}) => {
    const {t} = useTranslation();

    const [file, setFile] = useState(null);
    const [fileExtensionError, setFileExtensionError] = useState("");

    const handleSubmitFile = async (e) => {
        e.preventDefault();
        let hasError = false;

        if (!file) {
            setFileExtensionError("fileRequired");
            hasError = true;
        } else {
            setFileExtensionError("");
        }

        //if (hasError) return;

        //await handleFileUpload();
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

    return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div className="w-full max-w-3xl bg-white py-14 px-12 rounded-lg shadow-lg border border-gray-200 mt-10">
                <h1 className="text-4xl font-extrabold mb-4 text-gray-800 text-center">
                    {t("uploadContract")}
                </h1>
                <h2 className="text-lg text-gray-600 mb-8 text-center">
                    {t("uploadContractFilePDF")}
                </h2>
                <div className="bg-[#f9fafb] rounded-lg py-6 px-5 mb-8 border-l-4 border-orange shadow-sm">
                    <h3 className="text-xl font-semibold text-gray-700">Étudiant :</h3>
                    <p className="text-lg font-medium text-gray-800">
                        {etudiant.prenom + " " + etudiant.nom}
                    </p>
                    <h3 className="text-xl font-semibold text-gray-700 mt-3">Programme :</h3>
                    <p className="text-lg font-medium text-gray-800">
                        {etudiant.programme}
                    </p>
                </div>
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
                        <InputErrorMessage messageKey={fileExtensionError} className="text-red-600 mt-2 text-sm"/>
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
                    <button type="submit"
                            className="w-full py-3 text-lg font-semibold text-white bg-orange rounded-lg hover:bg-orange-dark transition duration-200">
                        {t("submit")}
                    </button>
                </form>
            </div>
        </div>
    )
}

export default AttribuerContrat;