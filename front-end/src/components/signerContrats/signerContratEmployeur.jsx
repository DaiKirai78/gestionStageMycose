import React, { useEffect, useState } from 'react';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';
import { useTranslation } from 'react-i18next';
import SignerContratCard from './signerContratCard';
import PageIsLoading from '../pageIsLoading';

const SignerContratEmployeur = ({ setSelectedContract }) => {

    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const { t } = useTranslation();
    const [contrats, setContrats] = useState([]);
    const [nomPrenom, setNomPrenom] = useState([]);
    const [isFetching, setIsFetching] = useState(true);

    useEffect(() => {
        fetchPages()
    }, [])

    useEffect(() => {
        fetchContrats();
    }, [pages.currentPage])
    
    useEffect(() => {
        fetchPrenomNomEtudiants();
    }, [contrats])

    async function fetchPages() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch('http://localhost:8080/entreprise/pagesContrats', {
                method: 'GET',
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }            

            setPages(prevPages => ({
                ...prevPages,
                maxPages: data
            }));
        } catch (e) {
            console.log("Une erreur est survenue " + e);
        }
    }

    async function fetchPrenomNomEtudiants() {
        const token = localStorage.getItem("token");
        
        const listNomEtudiant= [];

        for (const contrat of contrats) {
            try {
                const response = await fetch(`http://localhost:8080/utilisateur/getPrenomNomEtudiant?id=${contrat.etudiantId}`, {
                    method: 'GET',
                    headers: {Authorization: `Bearer ${token}`}
                });
    
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
    
                const data = await response.text();
    
                if (!data) {
                    throw new Error('No data');
                }                

                listNomEtudiant.push({
                    id: contrat.id,
                    nom: data
                })
            } catch (e) {
                console.log("Une erreur est survenue " + e);
            }
        }

        setNomPrenom(listNomEtudiant);
        setIsFetching(false);
    }

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
        }
    }

    function getNomEtudiant(contrat) {
        const etudiant = nomPrenom.find(nom => nom.id === contrat.id);
        return etudiant ? etudiant.nom : '';
    }

    if (isFetching)
        return (
            <div className='w-full h-full bg-orange-light flex flex-col justify-center items-center'>
                <PageIsLoading />
            </div>
        )
    

    return (
        <div className='w-full h-full bg-orange-light flex-1 flex flex-col items-center p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            {
                contrats && contrats.length > 0 ? (
                    <>
                        {
                            contrats.map((contrat, index) => 
                                <SignerContratCard
                                    contrat={contrat}
                                    nomPrenom={getNomEtudiant(contrat)}
                                    index={index} 
                                    setSelectedContract={setSelectedContract}
                                    key={"contrat" + index} />
                            )
                        }

                        <BoutonAvancerReculer pages={pages} setPages={setPages} margins={"my-4"} />
                    </>
                ) :
                <p>{t("noContratToSign")}</p>
            }
        </div>
    );
};

export default SignerContratEmployeur;