import React from 'react';
import { useNavigate } from 'react-router-dom';

const CardStatusDuCv = ({ cvInfo }) => {
    const navigate = useNavigate()

    function getStatusCv() {        
        if (!cvInfo) return "Aucun cv envoyé"
        switch (cvInfo.status) {
            case "ACCEPTED":
                return "Votre cv est Accepté";
            case "REFUSED":
                return "Votre cv est refusé";
            case "WAITING":
                return "Votre cv est en attente";
            default:
                return "Aucun cv envoyé";
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
            <h2 className="text-2xl font-semibold mb-2">Status du CV</h2>
            <p className={`font-medium mb-4 ${getStatusCvTextColor()}`}>
                {getStatusCv()}
            </p>
            <div className={!cvInfo ? "hidden" : ""}>
                <h3 className="font-medium mb-2">Notes du gestionnaire:</h3>
                <p className="text-sm text-gray-600 mb-4">
                    {!cvInfo || cvInfo && !cvInfo.statusDescription || cvInfo && cvInfo.statusDescription.length === 0 ? "Aucunes notes" : cvInfo.statusDescription}
                </p>
            </div>
            <div className='flex gap-3 flex-col sm:flex-row'>
                {
                    cvInfo 
                    &&
                    <button 
                        onClick={() => {navigate("/voirMonCV")}}
                        className="w-full text-black border border-black py-2 rounded-md hover:bg-gray-200 transition-colors">
                        Voir mon CV
                    </button>
                }
                <button 
                    onClick={() => {navigate("/televerserCV")}}
                    className="w-full bg-black text-white py-2 rounded-md hover:bg-gray-800 transition-colors">
                    Téléversé un nouveau CV
                </button>
            </div>
        </div>
    );
};

export default CardStatusDuCv;