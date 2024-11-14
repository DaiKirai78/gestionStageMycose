import FiltreSession from "../filtreSession.jsx";
import PageIsLoading from "../pageIsLoading.jsx";
import ListeOffreStageEmployeurEtGestionnaire from "./listeOffreStageEmployeurEtGestionnaire.jsx";
import AfficherPdf from "./afficherPdf.jsx";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";

const STATUS_CODE_ACCEPTED = 202;
const STATUS_CODE_NO_CONTENT = 204;

const ListeOffreStageContainer = () => {
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
        if (annee || session) {
            setPages(prevState => ({
                ...prevState,
                currentPage: 1
            }));
            fetchOffreStage();
            fetchNombrePage();
        } else if (!annee || !session) {
            setPages(prevState => ({
                ...prevState,
                currentPage: 1
            }));
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

            if (annee && session) {
                url += `&annee=${annee}&session=${session}`;
            } else if (annee && !session) {
                url += `&annee=${annee}`;
            } else if (!annee && session) {
                url += `&session=${session}`;
            }

            const response = await fetch(url, {
                method: "GET",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === STATUS_CODE_ACCEPTED) {
                const text = await response.text();
                if (text) {
                    const fetchedData = JSON.parse(text);
                    setData(fetchedData);

                    if (fetchedData && fetchedData.length > 0) {
                        setActiveOffer(fetchedData[0]);
                    }
                } else {
                    setData(null);
                    console.log("Nothing found");
                }
            } else if (response.status === STATUS_CODE_NO_CONTENT) {
                setData(null);
                console.log("Nothing found");
            }
        } catch (e) {
            console.log(e);
            setData(null);
        }
    }

    async function fetchNombrePage() {
        const token = localStorage.getItem("token");
        const baseUrl = `http://localhost:8080/api/offres-stages`;

        try {
            let url = `${baseUrl}/pagesForCreateur`;

            if (annee && session) {
                url += `?annee=${annee}&session=${session}`;
            } else if (annee && !session) {
                url += `?annee=${annee}`;
            } else if (!annee && session) {
                url += `?ssession=${session}`;
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
        <>
            <div className={`bg-orange-light w-full min-h-full flex-1 flex flex-col items-center gap-10 p-5`}>
                <div className='w-4/5'>
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
                                <ListeOffreStageEmployeurEtGestionnaire
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
        {   voirPdf && <AfficherPdf setVoirPdf={setVoirPdf} activePdf={activeOffer.fileData} />}
        </>
    )
}

export default ListeOffreStageContainer;