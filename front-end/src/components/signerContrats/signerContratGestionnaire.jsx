import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import PageIsLoading from "../pageIsLoading";
import SignerContratCard from "./signerContratCard";
import BoutonAvancerReculer from "../listeOffreEmployeur/boutonAvancerReculer";


function SignerContratGestionnaire({setSelectedContract}) {
    const { t } = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [isFetching, setIsFetching] = useState(true);
    const [contrats, setContrats] = useState([]);
    const [nomPrenom, setNomPrenom] = useState([]);
    const [isContratsSignesView, setIsContratsSignesView] = useState(true);

    useEffect(() => {
        fetchPages();
    }, [])

    useEffect(() => {
        fetchContratsNonSignes();
    }, [pages.currentPage])

    useEffect(() => {
        fetchPrenomNomEtudiants();
    }, [contrats])


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

    return(
        <div className="w-full h-full bg-orange-light flex flex-col items-center p-8">
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            <div className="space-x-10">
                <button className={`${isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`} onClick={() => setIsContratsSignesView(true)}>Contrats À Signer</button>
                <button className={`${!isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`} onClick={() => setIsContratsSignesView(false)}>Contrats Signés</button>
            </div>
            <div>
            {
                contrats && contrats.length > 0 ? (
                    <>
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
                                
                                : null
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