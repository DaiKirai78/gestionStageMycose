import React, { useEffect, useState } from 'react';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';
import { useTranslation } from 'react-i18next';
import SignerContratCard from './signerContratCard';
import PageIsLoading from '../pageIsLoading';

const SignerContratEmployeur = ({ setSelectedContract }) => {

    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const { t } = useTranslation();
    const [contrats, setContrats] = useState([]);
    const [isFetching, setIsFetching] = useState(true);
    useEffect(() => {
        fetchContrats();
    }, [pages.currentPage])

    async function fetchContrats() {
        const token = localStorage.getItem("token");
        
        try {
            const response = await fetch(`http://localhost:8080/entreprise/getContratsNonSignees?pageNumber=${pages.currentPage - 1}`, {
                method: 'POST',
                headers: {Authorization: `Bearer ${token}`}
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }            
            
            setContrats(JSON.parse(data));
        } catch (e) {
            console.log("Une erreur est survenue " + e);
        } finally {
            setIsFetching(false);
        }
    }

    if (isFetching)
        return (
            <div className='w-full h-full bg-orange-light flex flex-col justify-center items-center'>
                <PageIsLoading />
            </div>
        )
    

    return (
        <div className='w-full h-full bg-orange-light flex flex-col items-center p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            {
                contrats && contrats.length > 0 ? (
                    <>
                        {
                            contrats.map((contrat, index) => 
                                <SignerContratCard 
                                    contrat={contrat} 
                                    index={index} 
                                    setSelectedContract={setSelectedContract}
                                    key={"contrat" + index} />
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