import React, { useEffect } from 'react';
import { BsArrowRight } from "react-icons/bs";
import { useTranslation } from 'react-i18next';

const OffreStageCard = ({offre, getColorOffreStatus, activeOffer, setActiveOffer}) => {

    const { t } = useTranslation();

    function isLaMemeOffre() {
        return JSON.stringify(activeOffer) === JSON.stringify(offre);
    }

    return (
        <button id="elementStage" 
                    onClick={() => {setActiveOffer(offre)}}
                    className={`group relative px-6 flex items-center w-full  flex-col pb-3 pt-3 border rounded ${isLaMemeOffre() ? "border-deep-orange-200 cursor-default": "border-deep-orange-50 cursor-pointer"}`}>
                        <div className={`absolute left-0 top-0 w-full h-full flex items-center justify-center opacity-0 transition-all z-10 ${isLaMemeOffre() ? "group-hover:opacity-0" : "group-hover:opacity-100"}`}>
                        <div className='absolute left-0 top-0 w-full h-full bg-orange opacity-10'></div>
                            <p className='text-2xl text-orange relative ease-in-out flex items-center gap-1'>{t("details")} <BsArrowRight /></p>
                        </div>
                <div className={isLaMemeOffre() ? "" : "group-hover:opacity-20"}>
                    <div className='flex flex-col gap-1 items-center justify-center'>
                        <h3
                            className="text-black text-xl text-center font-bold">{offre.title}</h3>
                        <h3
                            className={`text-black text-sm text-center ${getColorOffreStatus(offre.status)}`}>{t(offre.status)}</h3>
                        <h3
                            className="text-black text-md text-center">{offre.updated_at}</h3>
                    </div>
                    <p className='text-center text-sm mt-5 text-orange underline'>{t("details")}</p>
                </div>
            </button>
    );
};

export default OffreStageCard;