import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import ListOffreStageEmployeur from '../components/listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../components/pageIsLoading.jsx"
import { BsArrowLeft, BsArrowRight } from "react-icons/bs";
import { useTranslation } from "react-i18next"

const fakeData = [
    {
        format: "file",
        id: "id1",
        created_at: "2024-01-10",
        data: "data1",
        filename: "fake_CV.pdf",
        updated_at: "2024-04-21",
        description: null,
        email: "jean.dupont@example.com",
        employer_name: "Solutions Tech",
        entreprise_name: "Solutions Tech Inc.",
        location: null,
        salary: "80 000 $",
        title: "Ingénieur Logiciel",
        website: null,
        status: "ACCEPTED"
    },
    {
        format: "form",
        id: "id2",
        created_at: "2024-04-03",
        data: null,
        filename: null,
        updated_at: "2024-04-01",
        description: "Nous recherchons un designer créatif pour rejoindre notre équipe.",
        email: "jane.smith@example.com",
        employer_name: "Agence Créative",
        entreprise_name: "Innovations Design SARL",
        location: "New York, NY",
        salary: "70 000 $",
        title: "Designer Graphique",
        website: "https://agencecreative.com",
        status: "ACCEPTED"
    },
    {
        format: "form",
        id: "id3",
        created_at: "2024-10-05",
        data: null,
        filename: null,
        updated_at: "2024-08-07",
        description: "Poste de vente disponible dans notre équipe dynamique.",
        email: "michael.johnson@example.com",
        employer_name: "Vente Co.",
        entreprise_name: "Vente Corp.",
        location: "Austin, TX",
        salary: "60 000 $",
        title: "Représentant Commercial",
        website: "https://venteco.com",
        status: "REFUSED"
    },
    {
        format: "form",
        id: "id4",
        created_at: "2024-07-18",
        data: null,
        filename: null,
        updated_at: "2024-11-12",
        description: "Recherche un chef de projet expérimenté pour superviser les opérations.",
        email: "alice.brown@example.com",
        employer_name: "Gestion de Projet Inc.",
        entreprise_name: "Solutions PM",
        location: "Los Angeles, CA",
        salary: "90 000 $",
        title: "Chef de Projet",
        website: "https://pm-solutions.com",
        status: "WAITING"
    },
    {
        format: "form",
        id: "id5",
        created_at: "2024-05-15",
        data: null,
        filename: null,
        updated_at: "2024-06-01",
        description: "Rejoignez notre équipe en tant que spécialiste du marketing.",
        email: "charles.davis@example.com",
        employer_name: "Experts Marketing",
        entreprise_name: "Experts Marketing Inc.",
        location: "Seattle, WA",
        salary: "75 000 $",
        title: "Spécialiste Marketing",
        website: "https://expertsmarketing.com",
        status: "ACCEPTED"
    },
    {
        format: "form",
        id: "id6",
        created_at: "2024-08-25",
        data: null,
        filename: null,
        updated_at: "2024-09-10",
        description: "Recherche un analyste de données pour analyser les tendances du marché.",
        email: "emily.wilson@example.com",
        employer_name: "Data Insights",
        entreprise_name: "Data Insights SARL",
        location: "Boston, MA",
        salary: "85 000 $",
        title: "Analyste de Données",
        website: "https://datainsights.com",
        status: "WAITING"
    }
];



const VoirMesOffreStagePage = () => {
    const [isFetching, setIsFetching] = useState(true);
    const [data, setData] = useState()
    const [voirPdf, setVoirPdf] = useState(false);
    const [pages, setPages] = useState({minPages: 1, maxPages: 11, actualPage: 1});
    
    const { t } = useTranslation()

    useEffect(() => {

        fetchOffreStage()

        window.onkeydown = (e) => {
            if (e.key == "Escape")
                setVoirPdf(false)
        }

    }, []);

    useEffect(() => {
        document.body.style.overflow = voirPdf ? "hidden" : "auto";
    }, [voirPdf])

    useEffect(() => {
        console.log("HEEEEEee");
        
        // fetch("")
    }, [pages])

    async function fetchOffreStage() {
        const token = localStorage.getItem("token");

        setIsFetching(true);
        
        try {
            const response = await fetch("http://localhost:8080/api/offres-stages/getMine", {
                method: "GET",
                headers: {Authorization: `Bearer ${token}`}
            });

            if (response.ok) {
                const fetchedData = await response.json();
                // setData(fetchedData);
                console.log(fetchedData);
                setData(fakeData)
            } else {
                setData(fakeData)
            }
        } catch {
            console.log("ERROR");
        } finally {
            setIsFetching(false);
        }
    }

    function pagesUp(amount = 1) {        
        if (pages.actualPage + amount > pages.maxPages)
            return;

        setPages({
            ...pages,
            actualPage: pages.actualPage + 1
        });

    }

    function pagesDown(amount = 1) {        
        if (pages.actualPage - amount < pages.minPages)
            return;
    
        setPages({
            ...pages,
            actualPage: pages.actualPage - 1
        });
    }

    function goTo(destinationPage) {
        setPages({
            ...pages,
            actualPage: destinationPage
        });
    }

    return (
        <TokenPageContainer>
            <div className={`bg-orange-light w-full min-h-screen flex flex-col items-center gap-10`}>
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className='w-4/5'>
                {
                    isFetching ? 
                        <PageIsLoading /> : 
                        data.length > 0 ? <ListOffreStageEmployeur data={data} voirPdf={voirPdf} setVoirPdf={setVoirPdf} /> :
                            <h1>{t("noOffer")}</h1>
                }
                </div>
                <div className='w-full h-10 mb-12 flex justify-center'>
                    <div className='w-10/12 sm:w-1/2 md:w-1/3 flex gap-1'>
                        <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'
                            onClick={() => {pagesDown(1)}}
                        ><BsArrowLeft /></button>
                        <button className='w-1/6 h-full border rounded'
                            onClick={() => (goTo(pages.minPages))}
                        >{pages.minPages}</button>
                        <div className='w-1/6 h-full border border-deep-orange-100 rounded flex justify-center items-center'>{pages.actualPage}</div>
                        <button className='w-1/6 h-full border rounded'
                            onClick={() => (goTo(pages.maxPages))}
                        >{pages.maxPages}</button>
                        <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'
                            onClick={() => {pagesUp(1)}}
                        ><BsArrowRight /></button>
                    </div>
                </div>
            </div>
            { voirPdf &&
                <div 
                    className="fixed left-0 top-0 w-full h-full p-8 bg-orange-light z-50 flex flex-col items-center gap-4"
                >
                    <iframe
                        src="/fake_CV.pdf"
                        title="CV"
                        className="w-full h-full border"
                    ></iframe>
                    <button 
                        className='bg-orange px-4 py-2 rounded text-white'
                        onClick={() => {setVoirPdf(false)}}
                    >{t("close")}</button>
                </div>
            }
        </TokenPageContainer>
    );
};

export default VoirMesOffreStagePage;