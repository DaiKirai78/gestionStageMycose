import React, { useEffect, useState } from 'react';
import TokenPageContainer from '../../pages/tokenPageContainer';
import ListOffreStageEmployeur from '../listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../pageIsLoading.jsx"
import { useTranslation } from "react-i18next"
import AfficherPdf from '../listeOffreEmployeur/afficherPdf.jsx';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer.jsx';

const STATUS_CODE_ACCEPTED = 202;
const STATUS_CODE_NO_CONTENT = 204;

const AcceuilEmployeur = () => {
    const [isFetching, setIsFetching] = useState(true);
    const [data, setData] = useState(null)
    const [voirPdf, setVoirPdf] = useState(false);
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [activeOffer, setActiveOffer] = useState(null);
    
    const { t } = useTranslation()

    useEffect(() => {
        window.onkeydown = (e) => {
            if (e.key == "Escape")
                setVoirPdf(false)
        }
    }, [])

    useEffect(() => {

        fetchAll()

        window.scrollTo({
            top: 0,
        });

    }, [pages]);

    useEffect(() => {
        document.body.style.overflow = voirPdf ? "hidden" : "auto";
    }, [voirPdf])

    async function fetchOffreStage() {
        const token = localStorage.getItem("token");
        
        try {
            const response = await fetch(
                `http://localhost:8080/entreprise/getOffresPosted?pageNumber=${pages.currentPage - 1}`,
                {
                    method: "POST",
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            if (response.status == STATUS_CODE_ACCEPTED) {                
                const fetchedData = await response.json();
                setData(fetchedData);
                
            } else if (response.status == STATUS_CODE_NO_CONTENT) {
                console.log("Nothing found");
                
            }
        } catch (e) {
            console.log(e);
        }
    }

    async function fetchNombrePage() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch(
                "http://localhost:8080/entreprise/pages",
                {
                    method: "GET",
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            if (response.status == STATUS_CODE_ACCEPTED) {                
                const fetchedData = await response.json();                
                setPages({
                    ...pages,
                    maxPages: fetchedData
                });
                
            } else if (response.status == STATUS_CODE_NO_CONTENT) {
                setData("Nothing found")
            }
        } catch (e) {
            console.log(e);
        }
    }

    async function fetchAll() {
        setIsFetching(true);        

        if (!pages.maxPages)
            await fetchNombrePage();
        await fetchOffreStage();

        setIsFetching(false);
    }

    return (
        <TokenPageContainer role={["EMPLOYEUR"]}>
            <div className={`bg-orange-light w-full min-h-screen flex flex-col items-center gap-10`}>
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className='w-4/5'>
                    {   
                        isFetching && !data ? 
                            <PageIsLoading /> : 
                            data.length > 0 ?
                                <ListOffreStageEmployeur data={data} voirPdf={voirPdf} setVoirPdf={setVoirPdf} activeOffer={activeOffer} setActiveOffer={setActiveOffer} /> :
                                <h1>{t("noOffer")}</h1>
                    }
                </div>
                <BoutonAvancerReculer pages={pages} setPages={setPages}/>
            </div>
            { voirPdf && <AfficherPdf setVoirPdf={setVoirPdf} activePdf={activeOffer.fileData} /> }
        </TokenPageContainer>
    );
};

export default AcceuilEmployeur;
