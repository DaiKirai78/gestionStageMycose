import { useState } from "react";
import { useTranslation } from "react-i18next";
import FileOffreStage from "./fileOffreStage.jsx";
import FormOffreStage from "./formOffreStage.jsx";

function UploadOffreStage() {
    const { t } = useTranslation();
    const [showUpload, setShowUpload] = useState(true);

    return (
        <div className="flex items-center justify-center min-h-screen bg-orange-light">
            <div className="w-[70%] max-w-screen-lg mx-auto bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-bold mb-4 text-orange">
                    {showUpload ? t("uploadFile") : t("fillForm")}
                </h2>

                <div className="mb-4 flex justify-center space-x-4">
                    <button
                        className={`px-4 py-2 rounded-md ${showUpload ? "bg-orange text-white" : "bg-white border border-orange text-orange"} hover:bg-orange hover:text-white`}
                        onClick={() => setShowUpload(true)}
                    >
                        {t("showUpload")}
                    </button>
                    <button
                        className={`px-4 py-2 rounded-md ${!showUpload ? "bg-orange text-white" : "bg-white border border-orange text-orange"} hover:bg-orange hover:text-white`}
                        onClick={() => setShowUpload(false)}
                    >
                        {t("showForm")}
                    </button>
                </div>

                {showUpload ? <FileOffreStage /> : <FormOffreStage />}
            </div>
        </div>
    );
}

export default UploadOffreStage;
