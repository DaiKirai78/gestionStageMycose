import React, { useEffect, useState } from 'react';
import TokenPageContainer from '../../pages/tokenPageContainer';
import ListOffreStageEmployeur from '../listeOffreEmployeur/listOffreStageEmployeur.jsx'
import PageIsLoading from "../pageIsLoading.jsx"
import { useTranslation } from "react-i18next"
import AfficherPdf from '../listeOffreEmployeur/afficherPdf.jsx';
import { useOutletContext } from 'react-router-dom';
import FiltreSession from "../filtreSession.jsx";

const STATUS_CODE_ACCEPTED = 202;
const STATUS_CODE_NO_CONTENT = 204;

const AccueilEmployeur = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    const [isFetching, setIsFetching] = useState(true);
    const [data, setData] = useState(null)
    const [voirPdf, setVoirPdf] = useState(false);
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [activeOffer, setActiveOffer] = useState(null);
    const [annee, setAnnee] = useState("");
    const [session, setSession] = useState("");
    
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
        if (annee && session) {
            fetchOffreStage();
            fetchNombrePage();
        } else if (!annee && !session) {
            fetchOffreStage();
            fetchNombrePage();
        }
    }, [annee, session]);

    useEffect(() => {
        document.body.style.overflow = voirPdf ? "hidden" : "auto";
    }, [voirPdf])

    async function fetchOffreStage() {
        const token = localStorage.getItem("token");
        const baseUrl = `http://localhost:8080/api/offres-stages`;

        try {
            let url = `${baseUrl}/getOffresPosted?pageNumber=${pages.currentPage - 1}`;

            // Ajouter les paramètres `annee` et `session` si définis
            if (annee && session) {
                url += `&annee=${annee}&session=${session}`;
            }

            const response = await fetch(url, {
                method: "GET",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === STATUS_CODE_ACCEPTED) {
                const text = await response.text(); // Lire la réponse en tant que texte
                if (text) {
                    const fetchedData = JSON.parse(text); // Analyser en JSON si ce n'est pas vide
                    setData(fetchedData);
                } else {
                    setData(null); // Aucun contenu dans la réponse
                    console.log("Nothing found");
                }
            } else if (response.status === STATUS_CODE_NO_CONTENT) {
                setData(null); // Aucun contenu, donc aucun résultat
                console.log("Nothing found");
            }
        } catch (e) {
            console.log(e);
            setData(null); // En cas d'erreur, définir `data` à null pour indiquer l'absence de résultats
        }
    }

    async function fetchNombrePage() {
        const token = localStorage.getItem("token");
        const baseUrl = `http://localhost:8080/api/offres-stages`;

        try {
            let url = `${baseUrl}/pagesForCreateur`;

            // Ajouter les paramètres `annee` et `session` si définis
            if (annee && session) {
                url += `?annee=${annee}&session=${session}`;
            }

            const response = await fetch(url, {
                method: "GET",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === STATUS_CODE_ACCEPTED) {
                const fetchedData = await response.json();
                if (fetchedData !== pages.maxPages) {
                    setPages({
                        ...pages,
                        maxPages: fetchedData,
                    });
                }
            } else if (response.status === STATUS_CODE_NO_CONTENT) {
                setData("Nothing found");
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
        <TokenPageContainer role={["EMPLOYEUR"]} setUserInfo={setUserInfo}>
            <div className={`bg-orange-light w-full min-h-full flex-1 flex flex-col items-center gap-10 p-5`}>
                <div className='w-4/5'>
                    {/* Ajout du composant FiltreSession au-dessus de ListOffreStageEmployeur */}
                    <FiltreSession
                        annee={annee}
                        setAnnee={setAnnee}
                        session={session}
                        setSession={setSession}
                    />

                    {
                        isFetching && !data ?
                            <PageIsLoading /> :
                            data ?
                                <ListOffreStageEmployeur
                                    pages={pages}
                                    setPages={setPages}
                                    data={data}
                                    voirPdf={voirPdf}
                                    setVoirPdf={setVoirPdf}
                                    activeOffer={activeOffer}
                                    setActiveOffer={setActiveOffer}
                                    annee={annee}
                                    session={session}
                                /> :
                                <h1>{t("noOffer")}</h1>
                    }
                </div>
            </div>
            {voirPdf && <AfficherPdf setVoirPdf={setVoirPdf} activePdf={activeOffer.fileData} />}
        </TokenPageContainer>
    );
};

export default AccueilEmployeur;
