import { useState } from "react";
import { useTranslation } from "react-i18next";
import FileOffreStage from "./fileOffreStage.jsx";
import FormOffreStage from "./formOffreStage.jsx";
import Divider from "./divider.jsx";

function UploadOffreStage() {
    const { t } = useTranslation();
    const [showFileUpload, setShowFileUpload] = useState(true);

    return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div className=" w-screen max-w-3xl bg-[#FFF8F2] py-14 px-8 rounded-lg shadow-md flex items-center justify-center flex-col gap-4">
                <h2 className="text-4xl font-bold mb-4 text-black">
                    {showFileUpload ? t("uploadFile") : t("fillForm")}
                </h2>
                <p>{showFileUpload ? "télversé un pdf offre de stage" : "Remplire les inputs"}</p>

                <div className="flex justify-center space-x-4 w-full">
                    <button
                        className="px-4 py-2 rounded-lg bg-[#FFF8F2] border-2 border-black w-full shadow-md"
                        onClick={() => setShowFileUpload(!showFileUpload)}
                    >
                        {showFileUpload ? t("showForm") : t("showUpload")}
                    </button>
                </div>
                <Divider texte={t("or")}></Divider>
                {showFileUpload ? <FileOffreStage /> : <FormOffreStage />}
            </div>
        </div>
    );
}

export default UploadOffreStage;
