import React from 'react';
import { useTranslation } from 'react-i18next';
import EvaluerListEtudiant from './evaluerListEtudiant';

const students = [
    {
        prenom: "Jason",
        nom: "Jody",
    },
    {
        prenom: "Vicente",
        nom: "Cabezas",
    },
    {
        prenom: "Roberto",
        nom: "Berrios",
    }
]

const EvaluerEtudiantEmployeur = ({ selectedStudent, setSelectedStudent }) => {
    const { t } = useTranslation();

    return (
        <div className='flex flex-1 flex-col items-center bg-orange-light p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-10'>{t("evaluerEtudiant")}</h1>
            {
                students && students.length <= 0 ? <p>{t("noStudentToEvaluate")}</p> 
                : 
                <EvaluerListEtudiant students={students} setSelectedStudent={setSelectedStudent} />
            }
        </div>
    );
};

export default EvaluerEtudiantEmployeur;