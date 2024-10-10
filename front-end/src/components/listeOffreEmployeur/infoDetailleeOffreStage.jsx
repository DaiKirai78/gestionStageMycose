import React from 'react';
import { useTranslation } from 'react-i18next';
import { BsX } from "react-icons/bs";

const InfoDetailleeOffreStage = ({setActiveOffer, activeOffer, getColorOffreStatus, setVoirPdf, voirPdf}) => {

    const { t } = useTranslation();

    return (
        <div className={`pb-8 sm:pt-0 bg-orange-light z-20 rounded border border-deep-orange-200 w-full md:h-[calc(100vh-1rem)] h-[90vh] fixed left-0 md:sticky md:top-2 flex flex-col md:transition-none transition-all ease-in-out overflow-y-auto ${activeOffer === null ? "bottom-[-90vh]" : "bottom-0"}`}>
            <button className='absolute right-2 top-2 md:hidden'
                onClick={() => {setActiveOffer(null)}}>
                <BsX size={25}/>
            </button>
            { activeOffer &&
                <div className="p-6">
                    <h2 className="text-2xl font-bold mb-2">{activeOffer.title}</h2>
                    <p className="text-sm text-gray-600">{t("postedOn")} : <span className="font-medium">{activeOffer.created_at}</span></p>
                    <p className="text-sm text-gray-600">{t("updatedOn")} : <span className="font-medium">{activeOffer.updated_at}</span></p>
                    <h3 className="text-xl font-semibold mt-4">{t("offerDetails")} :</h3>
                    { activeOffer.description &&
                        <p className="mt-2"><strong>{t("description")} :</strong> {activeOffer.description}</p>
                    }
                    <p className="mt-2"><strong>{t("employer")} :</strong> {activeOffer.employer_name}</p>
                    <p className="mt-2"><strong>{t("company")} :</strong> {activeOffer.entreprise_name}</p>
                    { activeOffer.location &&
                        <p className="mt-2"><strong>{t("locationStage")} :</strong> {activeOffer.location}</p>
                    }
                    { activeOffer.salary &&
                        <p className="mt-2"><strong>{t("salaryStage")} :</strong> {activeOffer.salary}</p>
                    }
                    <p className="mt-2"><strong>{t("status")} :</strong> <span className={`font-semibold ${getColorOffreStatus(activeOffer.status)}`}>{t(activeOffer.status)}</span></p>
                    { activeOffer.website &&
                        <p className="mt-2">
                            <strong>{t("websiteStage")} :</strong> <a href={activeOffer.website} target="_blank" className="text-blue-500 underline cursor-pointer">{activeOffer.website}</a>
                        </p>
                    }
                    { activeOffer.format === "file" &&
                        <button
                            className='bg-orange px-4 py-2 rounded text-white mt-3 cursor-pointer'
                            onClick={() => setVoirPdf(!voirPdf)}
                        >
                            {t("seePDF")}</button>
                    }
                </div>
            }
        </div>
    );
};

export default InfoDetailleeOffreStage;