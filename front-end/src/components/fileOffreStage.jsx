import { useState } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";
import { BsCloudArrowUpFill } from "react-icons/bs";
import logoPdf from '../assets/pdficon.png'
import InputErrorMessage from "./inputErrorMesssage";

function FileOffreStage() {
    const { t } = useTranslation();
    const [file, setFile] = useState(null);
    const [fileExtensionError, setFileExtensionError] = useState("")
    const [successMessage, setSuccessMessage] = useState("");
    const [uploadError, setUploadError] = useState("");
    
    const handleFileUpload = async () => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post("http://localhost:8080/api/files/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            console.log("Fichier envoyé avec succès :", response.data);
            setSuccessMessage(t("fileUploadSuccess"));
            setUploadError("");
        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
            setUploadError(t("fileUploadError"));
            setSuccessMessage("");
        }
    };

    const handleSubmitFile = async (e) => {
        e.preventDefault();
        if (!file) {
            setFileExtensionError("fileRequired");
            return;
        }
        await handleFileUpload();
    };

    function dropHandler(e) {
        e.preventDefault()

        const fichier = e.dataTransfer.files[0];        

        if (!fichier.name.endsWith(".pdf")) {
            setFileExtensionError("wrongFileExtension");
            return
        }
        
        setFileExtensionError("")
        setFile(fichier)
    }

    const handleFileChange = (e) => {

        const fichier = e.target.files[0];

        if (!fichier.name.endsWith(".pdf")) {
            setFileExtensionError("wrongFileExtension");
            return
        }

        setFileExtensionError("")
        setFile(fichier);
    };

    const handleRemoveFile = () => {
        setFile(null);
        setFileExtensionError("");
        document.getElementById("file").value = "";
    };

    return (
        <form onSubmit={handleSubmitFile} className="space-y-4 w-full">
            <div className="relative w-full">
                <label
                    htmlFor="file"
                    onDrop={dropHandler}
                    onDragOver={(e) => {e.preventDefault()}}
                    onDragLeave={(e) => {e.preventDefault()}}
                    onDragEnter={(e) => {e.preventDefault()}}
                    className="flex justify-center w-full h-32 px-4 transition border-2 border-gray-300 border-dashed rounded-md appearance-none cursor-pointer hover:border-gray-400 focus:outline-none">
                    <span className="flex items-center space-x-2">
                        <BsCloudArrowUpFill></BsCloudArrowUpFill>
                        <span className="font-medium text-gray-600">
                            {t("dropFile")}&nbsp;
                            <span className="text-blue-600 underline">{t("browse")}</span>
                        </span>
                    </span>
                    <input type="file" name="file_upload" id="file" className="hidden" onChange={handleFileChange} accept="application/pdf"/>
                </label>
                <InputErrorMessage messageKey={fileExtensionError}></InputErrorMessage>
                {fileExtensionError === (
                    <p className={"text-red-500 text-sm"}>{t("fileRequired")}</p>
                )}
                {file && (
                    <div className="mt-4 flex items-center justify-between py-2 px-4 w-full bg-[#FFF8F2] shadow-md border-2 border-gray-400 rounded-md">
                        <div className="flex gap-2 items-center justify-center">
                            <img src={logoPdf} alt="icone pdf" className="w-10"/>
                            <p className="text-sm text-black">{file.name}</p>
                        </div>
                        <button
                            type="button"
                            onClick={handleRemoveFile}
                            className="rounded-full hover:text-red-700 hover:border-red-700 aspect-square w-8 h-8 align-middle border-2 border-gray-400 text-gray-400"
                        >
                            ✖
                        </button>
                    </div>
                )}
            </div>

            {successMessage && <p className="text-green-500 text-sm mt-4">{successMessage}</p>}
            {uploadError && <p className="text-red-500 text-sm mt-4">{uploadError}</p>}

            <div className="flex justify-center">
                <button
                    type="submit"
                    className="max-w-xs w-full bg-[#FE872B] text-white p-2 rounded-lg hover:bg-orange cursor-pointer disabled:hover:bg-[#FE872B] disabled:cursor-auto">
                    {t("submit")}
                </button>
            </div>
        </form>
    );
}

export default FileOffreStage;
