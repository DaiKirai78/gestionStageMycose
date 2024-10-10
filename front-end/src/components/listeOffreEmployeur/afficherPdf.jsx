import React from "react";
import { useTranslation } from "react-i18next";

const AfficherPdf = ({setVoirPdf}) => {

    const { t } = useTranslation();
    
    return <div
        className="fixed left-0 top-0 w-full h-full p-8 bg-orange-light z-50 flex flex-col items-center gap-4"
    >
        <iframe
            src="/fake_CV.pdf"
            title="CV"
            className="w-full h-full border"
        ></iframe>
        <button
            className='bg-orange px-4 py-2 rounded text-white'
            onClick={() => { setVoirPdf(false); } }
        >{t("close")}</button>
    </div>
}

export default AfficherPdf;