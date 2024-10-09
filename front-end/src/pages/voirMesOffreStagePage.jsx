import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import ListOffreStageEmployeur from '../components/listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../components/pageIsLoading.jsx"
import { BsArrowLeft, BsArrowRight } from "react-icons/bs";

const fakeData = [
    {
        format: "file",
        id: "id1",
        created_at: "2024-01-10",
        data: "data1",
        filename: "rapport1.pdf",
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

    useEffect(() => {

        fetchOffreStage()

    }, []);

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

    return (
        <TokenPageContainer>
            <div className="bg-orange-light w-full min-h-screen flex flex-col items-center gap-10">
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className='w-4/5'>
                {
                    isFetching ? 
                        <PageIsLoading /> : 
                        data.length > 0 ? <ListOffreStageEmployeur data={data} /> :
                            <h1>Aucune offre</h1>
                }
                </div>
                <div className='w-full h-10 mb-12 flex justify-center'>
                    <div className='w-10/12 sm:w-1/2 md:w-1/3 flex gap-1'>
                        <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'><BsArrowLeft /></button>
                        <button className='w-1/6 h-full border rounded'>1</button>
                        <button className='w-1/6 h-full border border-deep-orange-100 rounded'>17</button>
                        <button className='w-1/6 h-full border rounded'>60</button>
                        <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'><BsArrowRight /></button>
                    </div>
                </div>
            </div>
        </TokenPageContainer>
    );
};

export default VoirMesOffreStagePage;