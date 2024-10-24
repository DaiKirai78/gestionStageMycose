import React from 'react';
import { useTranslation } from "react-i18next";

const AccueilEtudiant = () => {
    const { t } = useTranslation();


    return (
        <div className='w-full min-h-full bg-orange-light flex flex-col items-center p-6 gap-y-8 h-72'>
            <h1 className='text-3xl md:text-4xl font-bold text-center'>{t("accueil")}</h1>
            <div className="flex h-3/5 justify-center">
                <p>Bienvenue aux gestionnaires!</p>
            </div>
        </div>
    );
};

export default AccueilEtudiant;