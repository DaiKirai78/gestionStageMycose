import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import PageIsLoading from "../pageIsLoading";
import SignerContratCard from "./signerContratCard";
import BoutonAvancerReculer from "../listeOffreEmployeur/boutonAvancerReculer";
import printJS from "print-js";
import { PDFDocument } from 'pdf-lib';
import axios from "axios";


function SignerContratGestionnaire({setSelectedContract}) {
    const { t } = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [isFetching, setIsFetching] = useState(true);
    const [contrats, setContrats] = useState([]);
    const [contrat, setContrat] = useState(null);
    const [nomPrenom, setNomPrenom] = useState([]);
    const [employeur, setEmployeur] = useState(null);
    const [gestionnaire, setGestionnaire] = useState(null);
    const [offreStage, setOffreStage] = useState(null);
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

    useEffect(() => {
        if (contrat) {
            if (contrat.employeurId) fetchEmployeur(contrat.employeurId);
            if (contrat.gestionnaireStageId) fetchGestionnaire(contrat.gestionnaireStageId);
        }
    }, [contrat]);


    useEffect(() => {
        if (employeur && employeur.id) {
            fetchOffreStage(employeur.id);
        }
    }, [employeur]);

    useEffect(() => {
        imprimer(contrat);
    }, [offreStage]);

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

    const fetchEmployeur = async (employeurId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`http://localhost:8080/gestionnaire/getUtilisateurById?id=${employeurId}`,  {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            console.log("ges2 : " + JSON.stringify(response.data));
            setEmployeur(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération de l'employeur : `, e);
        }
    };

    const fetchGestionnaire = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.post("http://localhost:8080/utilisateur/me", null, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setGestionnaire(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération du gestionnaire de stage : `, e);
        }
    };

    const fetchOffreStage = async (employeurId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`http://localhost:8080/api/offres-stages/getOffreStage/${employeurId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setOffreStage(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération de l'offre de stage : `, e);
        }
    };

    async function imprimer(contrat) {
        try {
            setContrat(contrat);
            const pdfDoc = await PDFDocument.create();
            const page = pdfDoc.addPage();

            console.log("employeur : " + JSON.stringify(employeur));
            console.log("gestionnaire : " + JSON.stringify(gestionnaire));

            page.drawText(`Nom de l'étudiant: ${getNomEtudiant(contrat)}`, { x: 50, y: 700, size: 12 });
            page.drawText(`Nom de l'employeur: ${employeur.prenom + " " + employeur.nom}`, { x: 50, y: 680, size: 12 });
            page.drawText(`Nom du gestionnaire: ${gestionnaire.prenom + " " + gestionnaire.nom}`, { x: 50, y: 660, size: 12 });

            page.drawText(`Adresse: ${offreStage.location ? offreStage.location : "Non spécifiée"}`, { x: 50, y: 640, size: 12 });
            // page.drawText(`Date de début: ${contrat.dateDebut}`, { x: 50, y: 620, size: 12 });
            // page.drawText(`Date de fin: ${contrat.dateFin}`, { x: 50, y: 600, size: 12 });

            if (contrat.signatureEtudiant) {
                const signatureEtudiant = await pdfDoc.embedPng(contrat.signatureEtudiant);
                page.drawText(`Signature de l'étudiant : `, { x: 50, y: 520, size: 12 });
                page.drawImage(signatureEtudiant, { x: 175, y: 500, width: 150, height: 50 });
            }

            if (contrat.signatureEmployeur) {
                const signatureEmployeur = await pdfDoc.embedPng(contrat.signatureEmployeur);
                page.drawText(`Signature de l'employeur : `, { x: 50, y: 470, size: 12 });
                page.drawImage(signatureEmployeur, { x: 190, y: 450, width: 150, height: 50 });
            }

            if (contrat.signatureGestionnaire) {
                const signatureGestionnaire = await pdfDoc.embedPng(contrat.signatureGestionnaire);
                page.drawText(`Gestionnaire de stage : `, { x: 50, y: 420, size: 12 });
                page.drawImage(signatureGestionnaire, { x: 175, y: 400, width: 150, height: 50 });
            }

            // Convert PDF document to base64 and print
            const pdfBytes = await pdfDoc.saveAsBase64();

            printJS({ printable: pdfBytes, type: 'pdf', base64: true });
        } catch (error) {
            console.log("An error occurred: ", error);
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
                                            onClick={() =>  setContrat(contrat) }>
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