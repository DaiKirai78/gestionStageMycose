import { useState } from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";

function FileOffreStage() {
    const { t } = useTranslation();
    const [file, setFile] = useState(null);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleFileUpload = async () => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post("http://localhost:8080/api/files/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            console.log("Fichier envoyé avec succès :", response.status);
        } catch (error) {
            console.error("Erreur lors de l'envoi du fichier :", error);
        }
    };

    const handleSubmitFile = async (e) => {
        e.preventDefault();
        if (file) {
            await handleFileUpload();
        }
    };

    const handleRemoveFile = () => {
        setFile(null);
        document.getElementById("file").value = "";
    };

    return (
        <form onSubmit={handleSubmitFile} className="space-y-4">
            <div className="relative">
                <label htmlFor="file" className="block text-sm font-medium text-orange">
                    {t("uploadAFile")}
                </label>
                <div className="custom-file-input">
                    <input
                        type="file"
                        id="file"
                        onChange={handleFileChange}
                        className="hidden-file-input"
                        disabled={file !== null}
                    />
                </div>
                {file && (
                    <div className="mt-2 flex items-center">
                        <span className="text-sm text-gray-700">{file.name}</span>
                        <button
                            type="button"
                            onClick={handleRemoveFile}
                            className="ml-2 text-red-500 bg-gray-200 p-0.5 rounded hover:text-red-700 hover:bg-gray-300"
                        >
                            <span className="text-xl">✖</span>
                        </button>
                    </div>
                )}
            </div>

            <div className="flex justify-center">
                <button
                    type="submit"
                    className="max-w-xs w-full bg-orange text-white p-2 rounded-lg hover:bg-orange-dark"
                    disabled={file === null}
                >
                    {t("submit")}
                </button>
            </div>
        </form>
    );
}

export default FileOffreStage;
