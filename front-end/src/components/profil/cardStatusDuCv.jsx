import React from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

const CardStatusDuCv = ({ cvInfo }) => {
    const navigate = useNavigate()
    const { t } = useTranslation();

    function getStatusCv() {        
        if (!cvInfo) return t("noCv");
  
        switch (cvInfo.status) {
            case "ACCEPTED":
                return t("cvAccepted");
            case "REFUSED":
                return t("cvRefused");
            case "WAITING":
                return t("cvWaiting");
            default:
                return t("noCv");
        }
    }

    function getStatusCvTextColor() {
        if (!cvInfo) return "text-red-500"
        switch (cvInfo.status) {
            case "ACCEPTED":
                return "text-green-600";
            case "REFUSED":
                return "text-red-500";
            case "WAITING":
                return "text-yellow-700";
            default:
                return "text-red-500";
        }
    }

    return (
        <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-semibold mb-2">{t("cvStatusTitle")}</h2>
            <p className={`font-medium mb-4 ${getStatusCvTextColor()}`}>
                {getStatusCv()}
            </p>
            <div className={!cvInfo ? "hidden" : ""}>
                <h3 className="font-medium mb-2">{t("managerNotes")}</h3>
                <p className="text-sm text-gray-600 mb-4">
                {!cvInfo || cvInfo && !cvInfo.statusDescription || cvInfo && cvInfo.statusDescription.length === 0 
                    ? t("noNotes") 
                    : cvInfo.statusDescription
                }
                </p>
            </div>
            <div className='flex gap-3 flex-col sm:flex-row'>
                {cvInfo && (
                <button 
                    onClick={() => { navigate("/voirMonCV") }}
                    className="w-full text-black border border-black py-2 rounded-md hover:bg-gray-200 transition-colors">
                    {t("viewCV")}
                </button>
                )}
                <button 
                onClick={() => { navigate("/televerserCV") }}
                className="w-full bg-black text-white py-2 rounded-md hover:bg-gray-800 transition-colors">
                {t("uploadNewCV")}
                </button>
            </div>
            </div>
    );
};

export default CardStatusDuCv;