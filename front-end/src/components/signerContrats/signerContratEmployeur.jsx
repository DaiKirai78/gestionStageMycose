import React, { useState } from 'react';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';
import { useTranslation } from 'react-i18next';
import SignerContratCard from './signerContratCard';

const fakeData = [
    {
        id: 1,
        etudiantId: 101,
        etudiantNom: "Jason"
    },
    {
        id: 2,
        etudiantId: 102,
        etudiantNom: "Vicente"
    },
    {
        id: 3,
        etudiantId: 103,
        etudiantNom: "Roberto"
    }
];

const SignerContratEmployeur = ({ setSelectedContract }) => {

    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const { t } = useTranslation()

    return (
        <div className='w-full h-full bg-orange-light flex flex-col items-center p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            {
                fakeData && fakeData.length > 0 ? (

                    <>
                        {
                            fakeData.map((contrat, index) => 
                                <SignerContratCard contrat={contrat} index={index} setSelectedContract={setSelectedContract} />
                            )
                        }

                        <BoutonAvancerReculer pages={pages} setPages={setPages} />
                    </>
                ) :
                <p>{t("noContratToSign")}</p>
            }
        </div>
    );
};

export default SignerContratEmployeur;