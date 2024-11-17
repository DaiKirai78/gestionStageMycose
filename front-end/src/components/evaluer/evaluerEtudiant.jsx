import React from 'react';
import { useTranslation } from 'react-i18next';
import EvaluerListEtudiant from './evaluerListEtudiant';
import PageTitle from '../pageTitle';

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

const EvaluerEtudiant = ({ setSelectedStudent }) => {
    const { t } = useTranslation();

    return (
        <div className='flex flex-1 flex-col items-center bg-orange-light p-8'>
            <PageTitle title={t("evaluerEtudiant")} />
            {
                students && students.length <= 0 ? <p>{t("noStudentToEvaluate")}</p> 
                : 
                <EvaluerListEtudiant students={students} setSelectedStudent={setSelectedStudent} destination={"/evaluer/formulaire"} />
            }
        </div>
    );
};

export default EvaluerEtudiant;