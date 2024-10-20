import React, { useEffect, useState } from "react";
import { TfiReload } from "react-icons/tfi";
import langues from '../utils/langues'
import { useTranslation } from "react-i18next";

const ButtonChangeLangConnIncr = () => {

    const { i18n } = useTranslation();
    const [langueIndex, setLangueIndex] = useState(0)

    const toggleLangue = () => {
        setLangueIndex((langueIndex + 1) % langues.length)
    }

    useEffect(() => {
        i18n.changeLanguage(langues[langueIndex].suffix)
    }, [langueIndex])

    return (
        <button 
            onClick={() => {
                toggleLangue()
            }}  
            className="fixed sm:right-7 sm:bottom-7 sm:top-auto right-7 top-7 flex items-center gap-5 border border-orange border-opacity-40 bg-orange-light px-4 py-3 rounded">
            {langues[langueIndex].langue}
            <TfiReload />
        </button>
    );
}

export default ButtonChangeLangConnIncr;