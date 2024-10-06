import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import ListOffreStageEmployeur from '../components/listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../components/pageIsLoading.jsx"

const fakeData = [
    {
        name: "Offre1"
    },
    {
        name: "Offre2"
    },
    {
        name: "Offre3"
    },
    {
        name: "Offre4"
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
            {
                isFetching ? <PageIsLoading /> : <ListOffreStageEmployeur data={data} />
            }
        </TokenPageContainer>
    );
};

export default VoirMesOffreStagePage;