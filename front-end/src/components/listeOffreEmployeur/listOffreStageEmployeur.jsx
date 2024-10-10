import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { BsArrowRight } from "react-icons/bs";
import { BsX } from "react-icons/bs";


const ListOffreStageEmployeur = ({data, voirPdf, setVoirPdf}) => {

    const [activeOffer, setActiveOffer] = useState(null);
    const { t } = useTranslation();

    useEffect(() => {
        if (!isMediumScreen()) {
            setActiveOffer(data[0]);
        }
    }, []);

    function isMediumScreen() {        
        return screen.width <= 720
    }

    function linkCard(offre, index) {
        return (
            <button id="elementStage" 
                    key={index}
                    onClick={() => {setActiveOffer(offre)}}
                    className={`group relative px-6 flex items-center w-full  flex-col pb-3 pt-3 border rounded ${activeOffer === offre ? "border-deep-orange-200 cursor-default": "border-deep-orange-50 cursor-pointer"}`}>
                        <div className={`absolute left-0 top-0 w-full h-full flex items-center justify-center opacity-0 transition-all z-10 ${activeOffer === offre ? "group-hover:opacity-0" : "group-hover:opacity-100"}`}>
                        <div className='absolute left-0 top-0 w-full h-full bg-orange opacity-10'></div>
                            <p className='text-2xl text-orange relative ease-in-out flex items-center gap-1'>{t("details")} <BsArrowRight /></p>
                        </div>
                <div className={activeOffer === offre ? "" : "group-hover:opacity-20"}>
                    <div className='flex flex-col gap-1 items-center justify-center'>
                        <h3
                            className="text-black text-xl text-center font-bold">{offre.title} - {offre.format}</h3>
                        <h3
                            className={`text-black text-sm text-center ${getColorOffreStatus(offre.status)}`}>{t(offre.status)}</h3>
                        <h3
                            className="text-black text-md text-center">{offre.updated_at}</h3>
                    </div>
                    <p className='text-center text-sm mt-5 text-orange underline'>{t("details")}</p>
                </div>
            </button>
        )
    }

    function getColorOffreStatus(status) {
        switch (status) {
            case "ACCEPTED":
                return "text-green-500";

            case "WAITING":
                return "text-yellow-700";

            case "REFUSED":
                return "text-red-600";
        
            default:
                return "text-black";
        }
    }

    return (
        <div className='min-w-screen bg-orange-light flex gap-5 pt-10'>
            <div className='flex flex-col gap-2 w-full h-full overflow-auto'>
                {
                    data.map((offre, index) => {
                        return linkCard(offre, index);
                    })
                }
            </div>
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
            
        </div>
    );
}

export default ListOffreStageEmployeur;