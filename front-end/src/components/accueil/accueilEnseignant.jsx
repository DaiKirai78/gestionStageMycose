import React, { useState } from 'react';
import EvaluerListEtudiant from '../evaluer/evaluerListEtudiant';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';

const students = [
    {
        prenom: "Jason prof",
        nom: "Jody",
    },
    {
        prenom: "Vicente prof",
        nom: "Cabezas",
    },
    {
        prenom: "Roberto prof",
        nom: "Berrios",
    }
]

const AccueilEnseignant = ({ setSelectedStudent }) => {
    const { t } = useTranslation();

    return (
        <div className='flex-1 flex flex-col bg-orange-light p-10'>
            <PageTitle title={t("evaluerEtudiant")} />
            <EvaluerListEtudiant students={students} setSelectedStudent={setSelectedStudent} destination="/ens/formulaire" />
        </div>
    );
};

export default AccueilEnseignant;