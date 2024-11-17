import React from 'react';
import { useTranslation } from 'react-i18next';
import EvaluerListEtudiant from './evaluerListEtudiant';
import PageTitle from '../pageTitle';

const students = [
    {
        id: 1,
        prenom: "Jason prof",
        nom: "Jody",
    },
    {
        id: 1,
        prenom: "Vicente prof",
        nom: "Cabezas",
    },
    {
        id: 1,
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