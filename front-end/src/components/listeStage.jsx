import {useEffect, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";

const listeStage = () => {

    const [stages, setStages] = useState(null);
    const [loading, setLoading] = useState(false);

    const [recherche, setRecherche] = useState("");
    const [buttonDisabled, setButtonDisabled] = useState(true);
    const [nextPageDisabled, setNextPageDisabled] = useState(false);
    const [previousPageDisabled, setPreviousPageDisabled] = useState(true);

    const [isSearching, setIsSearching] = useState(false);
    const [recherchePageActuelle, setRecherchePageActuelle] = useState(0);

    const [stageClique, setStageClique] = useState(null);
    const [unStageEstClique, setUnStageEstClique] = useState(false);
    const [uneRechercheEstFaite, setUneRechercheEstFaite] = useState(false);

    const [pageActuelle, setPageActuelle] = useState(0);
    const [nombreDePage, setNombreDePage] = useState(0);

    const {t} = useTranslation();

    let localhost = "http://localhost:8080/";
    let urlGetFormulaireStage = "etudiant/getStages?pageNumber=";
    let urlGetNombreDePage = "etudiant/pages"
    let urlRechercheOffres = "etudiant/recherche-offre"

    let token = localStorage.getItem("token");

    useEffect(() => {
        fetchStages(0);
    }, []);

    useEffect(() => {
        fetchNombrePages();
    }, []);

    useEffect(() => {
        isThereANextPage(false);
        isItTheFirstPage(false);
    }, [nombreDePage]);


    const fetchNombrePages = async () => {
        try {
            const response = await axios.get(localhost + urlGetNombreDePage, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setNombreDePage(response.data);
            return response.data;
        } catch (error) {
            console.error("Erreur lors de la récupération du nombre de page:", error);
        }
    };
    const fetchStages = async (pageNumber) => {
        setLoading(true);
        try {
            const responseForms = await axios.post(localhost + urlGetFormulaireStage + pageNumber, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                params: {
                    pageNumber,
                }
            });
            setStages(responseForms.data);
            setLoading(false);
            return responseForms.data;
        } catch (error) {
            console.error("Erreur lors de la récupération des stages:", error);
            setLoading(false);
        }
    };
    const fetchStagesByRecherche = async (pageNumber, doesItComeFromUseEffect) => {
        setLoading(true);
        try {
            const responseRecherche = await axios.post(localhost + urlRechercheOffres, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                params: {
                    pageNumber,
                    recherche
                }
            });
            if (!doesItComeFromUseEffect)
                setStages(responseRecherche.data);
            setLoading(false);
            return responseRecherche.data;
        } catch (error) {
            console.error("Erreur lors de la récupération des stages recherchés:", error);
            setLoading(false);
        }
    };

    function elementStageClique(stage) {
        setUnStageEstClique(true);
        setStageClique(stage);
    }

    function activerDesactiverRecherche(e) {
        if (e.target.value.length > 0)
            setButtonDisabled(false);
        else
            setButtonDisabled(true);
    }

    function changeRechercheStage(e) {
        setRecherche(e.target.value)
    }

    async function rechercher(e) {
        e.preventDefault();

        if (recherche !== "") {
            setUneRechercheEstFaite(true);
            setIsSearching(true);
            setRecherchePageActuelle(0);
            setStages([]);
            await fetchStagesByRecherche(0, false);
        }
    }

    useEffect(() => {
        if (isSearching) {
            isThereANextPage(false);
            isItTheFirstPage(false);
        }
    }, [isSearching]);

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && recherche !== "") {
            rechercher(e);
        }
    }

    function supprimerRecherche() {
        setUneRechercheEstFaite(false);
        setRecherche('');
        setIsSearching(false);
        setRecherchePageActuelle(0);
        setPageActuelle(0);
        fetchStages(0);
    }

    async function isThereANextPage(doesItComeFromNextPage) {
        if (isSearching) {
            const response = await fetchStagesByRecherche(recherchePageActuelle + 1, false);
            if (!doesItComeFromNextPage)
                await fetchStagesByRecherche(recherchePageActuelle, false);
            return response.length !== 0;
        } else {
            if (!doesItComeFromNextPage)
                await fetchStages(recherchePageActuelle);
            return pageActuelle < nombreDePage - 1;
        }
    }

    async function isItTheFirstPage(doesItComeFromPreviousPage) {
        if (isSearching) {
            const response = await fetchStagesByRecherche(recherchePageActuelle - 1, false)
            if (!doesItComeFromPreviousPage)
                await fetchStagesByRecherche(recherchePageActuelle, false);
            return response.length === 0;
        } else {
            if (!doesItComeFromPreviousPage)
                await fetchStages(recherchePageActuelle);
            return pageActuelle === 0;
        }
    }

    async function nextPage() {
        let nextPage = true;
        const hasNextPage = await isThereANextPage(nextPage);
        if (hasNextPage) {
            if (isSearching) {
                setRecherchePageActuelle((prevPage) => {
                    const newPage = prevPage + 1;
                    fetchStagesByRecherche(newPage, false);
                    return newPage;
                });
            } else {
                setPageActuelle((prevPage) => {
                    const newPage = prevPage + 1;
                    fetchStages(newPage);
                    return newPage;
                });
            }
        }
    }

    async function previousPage() {
        let previousPage = true;
        let isFirstPage = await isItTheFirstPage(previousPage);
        if (!isFirstPage) {
            if (isSearching) {
                setRecherchePageActuelle((prevPage) => {
                    const newPage = prevPage - 1;
                    fetchStagesByRecherche(newPage, false);
                    return newPage;
                });
            } else {
                setPageActuelle((prevPage) => {
                    const newPage = prevPage - 1;
                    fetchStages(newPage);
                    return newPage;
                });
            }
        }
    }

    useEffect(() => {
        const verifierBoutonsPagination = async () => {
            if (isSearching) {
                const hasNextPage = await fetchStagesByRecherche(recherchePageActuelle + 1, true);
                setNextPageDisabled(hasNextPage.length === 0);
                setPreviousPageDisabled(recherchePageActuelle <= 0);
            } else {
                setNextPageDisabled(pageActuelle >= nombreDePage - 1);
                setPreviousPageDisabled(pageActuelle <= 0);
            }
        };

        verifierBoutonsPagination();
    }, [pageActuelle, recherchePageActuelle, nombreDePage, isSearching]);

    function formaterDate(dateAFormater) {
        return new Date(dateAFormater).toISOString().split('T')[0];
    }

    let navigate = useNavigate();

    const changerDePage = (idStage) => {
        let path = '/appliquer';
        navigate(path, { state: { idStage } });
    };


    return (
        <>
            <div className="w-2/3 lg:w-1/2">
                <h1 className="pt-12 text-3xl md:text-4xl text-pretty font-accueilTitreFont font-bold text-black">{t("titrePageAfficherOffreStage")}</h1>
                <div className="flex flex-row">
                    <input type="search" placeholder={t("placeholderRechercherUnStage")} id="searchInput"
                           value={recherche}
                           onChange={(e) => {
                               activerDesactiverRecherche(e);
                               changeRechercheStage(e);
                           }}
                           onKeyDown={handleKeyDown}
                           className={`mt-6 mb-9 pt-3 pb-3 pl-14 text-md sm:text-xl rounded-l-3xl outline-0 w-full ${
                               uneRechercheEstFaite ? 'w-80' : 'w-full'
                           }`}></input>
                    <button id="buttonRechercherStage"
                            className="mt-6 mb-9 pt-3 pb-3 pl-12 sm:pl-16 md:pl-24 rounded-r-3xl"
                            disabled={buttonDisabled} onClick={rechercher}>
                    </button>
                    {
                        uneRechercheEstFaite && (
                            <button id="clearSearch"
                                    className="mt-6 mb-9 pt-3 pb-3 w-8 sm:ml-1 md:ml-4 pr-8 sm:mr-0"
                                    onClick={supprimerRecherche}>
                            </button>
                        )
                    }
                </div>
                <div className="mb-7">
                    <hr/>
                    {
                        stages && stages.length > 0 ?
                            stages.map((stage, index) => (
                                <>
                                    <div id="elementStage"
                                         className="flex flex-col w-full hover:cursor-pointer pb-3 pt-3 bg-orange-light transition-all duration-300 ease-in-out transform md:hover:-translate-y-1 md:hover:shadow-lg md:hover:rounded-t-xl"
                                         onClick={() => elementStageClique(stage)}>
                                        <div>
                                            <h3 key={index}
                                                className="text-black text-xl pl-2 max-w-max pr-2">{stage.title}</h3>
                                            <h4 className="text-lg pl-2 max-w-max pr-2">{stage.entrepriseName}</h4>
                                            <h4 className="pl-2 max-w-max pr-2">{formaterDate(stage.createdAt)}</h4>
                                        </div>
                                        <button
                                            className="bg-orange text-white md:absolute md:bottom-2 lg:bottom-12 lg:top-4 md:right-2 px-4 md:px-8 py-2 rounded-2xl mt-3 lg:mt-0 w-1/2 md:w-1/3 lg:w-36 hover:bg-orange-dark shadow-md"
                                            onClick={() => changerDePage(stage.id)}>{t("boutonAppliquerAUnStage")}
                                        >Appliquer
                                        </button>
                                    </div>
                                    <hr className="bg-deep-orange-100"/>
                                </>

                            )) :
                            <h3 className="text-xl mt-5">{t("messageAucunStageDisponible")}</h3>
                    }
                </div>
                {loading ? (
                    <div></div>
                ) : (
                    <div className="text-center mb-28">
                        <button
                            className="decoration-0 bg-orange pt-0 pb-1 pl-4 pr-4 mr-2 hover:bg-amber-900 justify-center items-center w-10 text-xl shadow-md disabled:bg-orange disabled:opacity-70"
                            disabled={previousPageDisabled} onClick={() => previousPage()}>&#8249;</button>
                        {
                            isSearching ?
                                recherchePageActuelle + 1 :
                                pageActuelle + 1
                        }
                        <button
                            className="decoration-0 bg-orange pt-0 pb-1 pl-4 pr-4 ml-2 hover:bg-amber-900 justify-center items-center w-10 text-xl shadow-md disabled:bg-orange disabled:opacity-70"
                            disabled={nextPageDisabled} onClick={() => nextPage()}>&#8250;</button>
                    </div>)
                }
            </div>
            {
                //stageClique.type === "fichier" ? //TODO : AJOUTER LE TYPE DE FICHIER SELON SI C'EST FILE OU FORM
                unStageEstClique && (
                    <div
                        className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50 transition-opacity duration-300"
                        onClick={() => setUnStageEstClique(false)}
                    >
                        <div
                            className="w-11/12 max-w-lg sm:w-2/3 lg:w-1/2 bg-white rounded-2xl shadow-2xl p-6 relative transform transition-transform duration-500 ease-out scale-100 hover:scale-105"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <div className="flex justify-between items-center border-b pb-3">
                                <h2 className="text-2xl font-bold text-gray-800">
                                    {t("modalTitreDetailsStage")}
                                </h2>
                                <button
                                    id="closeStageDetails"
                                    className="text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none"
                                    onClick={() => setUnStageEstClique(false)}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none"
                                         viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                              d="M6 18L18 6M6 6l12 12"/>
                                    </svg>
                                </button>
                            </div>

                            <div className="pt-4 space-y-4 text-center">
                                {stageClique ? (
                                    <>
                                        {stageClique.title ? (
                                            <h3 className="text-xl font-semibold text-gray-900 break-words">
                                                {stageClique.title}
                                            </h3>
                                        ) : (
                                            <h3 className="text-xl font-semibold text-gray-500">
                                                {t("titreDeOffreDeStageInexistant")}
                                            </h3>
                                        )}

                                        {stageClique.entrepriseName ? (
                                            <h4 className="text-lg font-medium text-gray-700 break-words">
                                                {t("offreDe") + stageClique.entrepriseName}
                                            </h4>
                                        ) : (
                                            <h4 className="text-lg text-gray-500">
                                                {t("entrepriseInconnue")}
                                            </h4>
                                        )}

                                        {stageClique.description && stageClique.location && stageClique.salary && stageClique.website ? (
                                            <div className="space-y-2 break-words">
                                                <p className="text-gray-700">{stageClique.description}</p>
                                                <p className="text-gray-700">{stageClique.location}</p>
                                                <p className="text-green-600 font-medium">{t("salaireDeOffre") + stageClique.salary}$/h</p>
                                                <a href={stageClique.website} className="text-blue-500 underline">
                                                    {stageClique.website}
                                                </a>
                                            </div>
                                        ) : (
                                            <p className="text-gray-500 break-words">
                                                {t("rendezVousSurPagePourInfos")}
                                            </p>
                                        )}

                                        <p className="text-gray-400 text-sm">
                                            {t("publieLe") + formaterDate(stageClique.createdAt)}
                                        </p>
                                    </>
                                ) : (
                                    <p className="text-gray-500">
                                        {t("aucunStageSelectionne")}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>
                )
            }
        </>
    )
}

export default listeStage;