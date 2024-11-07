import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import PageIsLoading from "../pageIsLoading";
import SignerContratCard from "./signerContratCard";
import BoutonAvancerReculer from "../listeOffreEmployeur/boutonAvancerReculer";
import printJS from "print-js";


function SignerContratGestionnaire({setSelectedContract}) {
    const { t } = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [isFetching, setIsFetching] = useState(true);
    const [contrats, setContrats] = useState([]);
    const [nomPrenom, setNomPrenom] = useState([]);
    const [isContratsSignesView, setIsContratsSignesView] = useState(true);
    const [listeAnneesDispo, setListeAnneesDispo] = useState([]);
    const [filtreAnnee, setFiltreAnnee] = useState();

    useEffect(() => {
        fetchPages();
    }, [])

    useEffect(() => {
        if(isContratsSignesView) {
            fetchContratsNonSignes();
        }
        else {
            fetchContratsSignes();
        }
            
    }, [pages.currentPage, isContratsSignesView, filtreAnnee])

    useEffect(() => {
        fetchPrenomNomEtudiants();
        fetchMinimumAnneeDisponible();
    }, [contrats])

    async function fetchMinimumAnneeDisponible() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch('http://localhost:8080/gestionnaire/contrats/signes/anneeminimum', {
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
            
            let annees = JSON.parse(data)
            annees = annees.reverse();
            
            if (!filtreAnnee || JSON.stringify(listeAnneesDispo) !== JSON.stringify(annees)) {
                setListeAnneesDispo(annees);
                setFiltreAnnee(annees.at(0));
            }


        } catch (e) {
            console.log("Une erreur est survenue " + e);
        }
    }


    async function fetchPages() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch('http://localhost:8080/gestionnaire/contrats/non-signes/pages', {
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
                const response = await fetch(`http://localhost:8080/utilisateur/getPrenomNom?id=${contrat.etudiantId}`, {
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
            finally{
                setIsFetching(false);
            }
        }

        setNomPrenom(listNomEtudiant);
    }

    async function fetchContratsNonSignes() {
        const token = localStorage.getItem("token");
        
        try {
            const response = await fetch(`http://localhost:8080/gestionnaire/contrats/non-signes?page=${pages.currentPage - 1}`, {
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

            setContrats(JSON.parse(data));
        } catch (e) {
            console.log("Une erreur est survenue " + e);       
            setContrats([]);
            setIsFetching(false);
        }
    }

    async function fetchContratsSignes() {
        const token = localStorage.getItem("token");
        console.log(filtreAnnee);
        
        try {
            const response = await fetch(`http://localhost:8080/gestionnaire/contrats/signes?page=${pages.currentPage - 1}&annee=${filtreAnnee}`, {
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

            setContrats(JSON.parse(data));
        } catch (e) {
            console.log("Une erreur est survenue " + e);       
            setContrats([]);
            setIsFetching(false);
        }
    }

    function getNomEtudiant(contrat) {
        const etudiant = nomPrenom.find(nom => nom.id === contrat.id);
        return etudiant ? etudiant.nom : '';
    }

    async function imprimer(contrat) {

        const contratToPrint = await fetchFullContrat(contrat.id);

        console.log(contratToPrint);
        
        printJS({
            printable: contratToPrint,
            type: 'pdf',
            base64: true,
        });
    }

    async function fetchFullContrat(contratId) {
        const token = localStorage.getItem("token");
        
        try {
            const response = await fetch(`http://localhost:8080/gestionnaire/contrat/print?id=${contratId}`, {
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

            return data;

        } catch (e) {
            console.log("Une erreur est survenue " + e);       
            setContrats([]);
            setIsFetching(false);
        }
    }
    
    if (isFetching)
        return (
            <div className='w-full h-full bg-orange-light flex flex-col justify-center items-center'>
                <PageIsLoading />
            </div>
        )

    return(
        <div className="flex-1 w-full h-full bg-orange-light flex flex-col items-center p-8">
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            <div className="space-x-10">
                <button className={`${isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`} onClick={() => setIsContratsSignesView(true)}>{t("contratsASigner")}</button>
                <button className={`${!isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`} onClick={() => setIsContratsSignesView(false)}>{t("contratsSignes")}</button>
            </div>
            <div className="my-10 w-1/2">
            {
                contrats && contrats.length > 0 ? (
                    <>
                    {!isContratsSignesView ?
                        <div className="flex flex-col w-1/2 mx-auto mb-6">
                            <select value={filtreAnnee} onChange={(e) => setFiltreAnnee(e.target.value)} className="bg-orange px-4 py-2 rounded text-white mt-3">
                                {listeAnneesDispo.map((annee) => {
                                    return(
                                        <option value={annee}>{annee}</option>
                                    );
                                })}
                            </select>
                        </div>
                     : null}
                        {
                            contrats.map((contrat, index) =>
                            {
                                return(
                                    isContratsSignesView ?
                                    
                                    <SignerContratCard
                                    contrat={contrat}
                                    nomPrenom={getNomEtudiant(contrat)}
                                    index={index} 
                                    setSelectedContract={setSelectedContract}
                                    key={"contrat" + index} />
                                    
                                    :
                                    
                                    <div className='flex w-full justify-between items-center p-4 shadow mb-4 rounded bg-white'>
                                        <p className='text-lg'>{getNomEtudiant(contrat)}</p>
                                        <button className='bg-orange rounded p-2 text-white hover:bg-opacity-90'
                                            onClick={() => imprimer(contrat)}>
                                            {t("imprimer")}
                                        </button>
                                    </div>
                                )   
                            }

                            )
                        }
                        <BoutonAvancerReculer pages={pages} setPages={setPages} margins={"my-4"} />
                    </>
                ) :
                <p className="mt-5">{t("noContratToSign")}</p>
            }
            </div>
        </div>
    );
}

export default SignerContratGestionnaire;