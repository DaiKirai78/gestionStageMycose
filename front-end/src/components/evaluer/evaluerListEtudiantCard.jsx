import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

const EvaluerListEtudiantCard = ({ student, setSelectedStudent, nomPrenom, destination }) => {
    const { t } = useTranslation();
    const navigator = useNavigate();

    function selectStudent() {
        setSelectedStudent(student)
        navigator(destination);
    }

    return (
        <div className='flex w-full justify-between items-center p-4 shadow mb-4 rounded bg-white'>
            <p className='text-lg'>{nomPrenom}</p>
            <button className='bg-orange rounded p-2 text-white hover:bg-opacity-90'
                onClick={() => selectStudent()}>
                {t("evaluer")}
            </button>
        </div>
    );
};

export default EvaluerListEtudiantCard;