import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import ListOffreStageEmployeur from '../components/listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../components/pageIsLoading.jsx"

const fakeData = [
    {
        format: "file",
        id: "id1",
        created_at: "2024-01-10",
        data: "data1",
        filename: "filename1",
        updated_at: "2024-04-21",
        description: null,
        email: "email1",
        employer_name: "employer_name1",
        entreprise_name: "entreprise_name1",
        location: null,
        salary: null,
        title: "title1",
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
        description: "description2",
        email: "email2",
        employer_name: "employer_name2",
        entreprise_name: "entreprise_name2",
        location: "location2",
        salary: "salary2",
        title: "title2",
        website: "website2",
        status: "ACCEPTED"
    },
    {
        format: "form",
        id: "id3",
        created_at: "2024-10-05",
        data: null,
        filename: null,
        updated_at: "2024-08-07",
        description: "description3",
        email: "email3",
        employer_name: "employer_name3",
        entreprise_name: "entreprise_name3",
        location: "location3",
        salary: "salary3",
        title: "title3",
        website: "website3",
        status: "REFUSED"
    },
    {
        format: "form",
        id: "id4",
        created_at: "2024-07-18",
        data: null,
        filename: null,
        updated_at: "2024-11-12",
        description: "description4",
        email: "email4",
        employer_name: "employer_name4",
        entreprise_name: "entreprise_name4",
        location: "location4",
        salary: "salary4",
        title: "title4",
        website: "website4",
        status: "WAITING"
    },
]

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
            </div>
        </TokenPageContainer>
    );
};

export default VoirMesOffreStagePage;