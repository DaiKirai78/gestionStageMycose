import React from 'react';
import { useTranslation } from 'react-i18next';

const AssignCard = ({personne, action}) => {
    const { t } = useTranslation();

    return (
        <div className="px-4 py-3 flex items-center justify-between bg-white shadow rounded max-w-full flex-wrap gap-4 overflow-hidden">
            <div className="flex items-center space-x-3">
            <div className="flex flex-col">
                <span className="text-md font-medium text-gray-900">{personne.prenom} {personne.nom}</span>
                <span className="text-sm mt-[-5px] text-gray-500">{personne.courriel}</span>
            </div>
            </div>
            <button 
                onClick={() => {action(personne)}}
                className="px-4 py-2 bg-orange-500 text-white text-sm font-medium rounded-md bg-orange hover:bg-orange hover:bg-opacity-90">
            {t("attribuer")}
            </button>
        </div>
    );
};

export default AssignCard;