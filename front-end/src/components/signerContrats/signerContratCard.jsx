import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

const SignerContratCard = ({ contrat, setSelectedContract, nomPrenom }) => {
    const { t } = useTranslation();
    const navigator = useNavigate();

    function selectContrat(contrat) {
        setSelectedContract({...contrat, nomPrenom: nomPrenom})
        navigator("/contrats/signer");
    }

    return (
        <div className='flex w-full justify-between items-center p-4 shadow mb-4 rounded bg-white'>
            <p className='text-lg'>{nomPrenom}</p>
            <button className='bg-orange rounded p-2 text-white hover:bg-opacity-90'
                onClick={() => selectContrat(contrat)}>
                {t("signContract")}
            </button>
        </div>
    );
};

export default SignerContratCard;