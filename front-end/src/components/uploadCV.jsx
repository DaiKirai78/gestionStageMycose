import { useTranslation } from "react-i18next";
import FileCV from "./fileCV.jsx";

function UploadCV() {
    const { t } = useTranslation();

    return (
        <div className="flex items-start justify-center min-h-screen p-8">
            <div
                className=" w-screen max-w-3xl bg-[#FFF8F2] py-14 px-8 rounded-lg shadow-md flex items-center justify-center flex-col gap-4">
                <h2 className="text-4xl font-bold mb-3 text-black">
                    {t("uploadFile")}
                </h2>
                <h3 className="mt-0 mb-4 text-xl">{t("uploadCVFilePDF")}</h3>
                <FileCV />
            </div>
        </div>
    );
}

export default UploadCV;
