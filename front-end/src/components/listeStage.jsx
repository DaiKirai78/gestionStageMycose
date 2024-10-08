import {useEffect, useState} from "react";
import axios from "axios";

const listeStage = () => {

    const [stages, setStages] = useState(null);

    const [recherche, setRecherche] = useState("");
    const [motsContenantRechercheTitre, setMotsContenantRechercheTitre] = useState(null);
    const [motsContenantRechercheEntreprise, setMotsContenantRechercheEntreprise] = useState(null);
    const [buttonDisabled, setButtonDisabled] = useState(true);
    const [nextPageDisabled, setNextPageDisabled] = useState(false);
    const [previousPageDisabled, setPreviousPageDisabled] = useState(false);


    const [stageClique, setStageClique] = useState(null);
    const [unStageEstClique, setUnStageEstClique] = useState(false);
    const [uneRechercheEstFaite, setUneRechercheEstFaite] = useState(false);

    const [pageActuelle, setPageActuelle] = useState(0);
    const [nombreDePage, setNombreDePage] = useState(0);

    let localhost = "http://localhost:8080/";
    let urlGetFormulaireStage = "etudiant/getStages?pageNumber=";
    let urlGetNombreDePage = "etudiant/pages"
    let urlRechercheOffres = "etudiant/recherche-offre"

    let token = localStorage.getItem("token");

    useEffect(() => {
        fetchStages();
        fetchNombrePages();
        isThereANextPage();
        isItTheFirstPage();
    }, [pageActuelle]);

    const fetchNombrePages = async () => {
        try {
            const response = await axios.get(localhost + urlGetNombreDePage, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setNombreDePage(response.data);
            console.log("Nombre de page : " + response.data);
            return response.data;
        } catch (error) {
            console.error("Erreur lors de la récupération du nombre de page:", error);
        }
    };
    const fetchStages = async () => {
        try {
            const responseForms = await axios.post(localhost + urlGetFormulaireStage + pageActuelle, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
            });
            setStages(responseForms.data);
            return responseForms.data;
        } catch (error) {
            console.error("Erreur lors de la récupération des stages:", error);
        }
    };
    const fetchStagesByRecherche = async () => {
        let pageNumber = 0;
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
            console.log("Voici les infos : " + responseRecherche.data);
            setStages(responseRecherche.data);
            return responseRecherche.data;
        } catch (error) {
            console.error("Erreur lors de la récupération des stages recherchés:", error);
        }
    };

    function elementStageClique(stage) {
        setUnStageEstClique(true);
        setStageClique(stage);
    }

    console.log(stageClique);

    function activerDesactiverRecherche(e) {
        if (e.target.value.length > 0)
            setButtonDisabled(false);
        else
            setButtonDisabled(true);
    }

    function changeRechercheStage(e) {
        setRecherche(e.target.value)
    }

    function rechercher(e) {
        e.preventDefault();

        if (recherche !== "")
            setUneRechercheEstFaite(true);

        const stagesDetails = fetchStagesByRecherche();
        let mapStages = stageEtNbreDeCorrespondances();

        const mapStageSort = [...mapStages.entries()].sort((a, b) => b[1] - a[1]).map(([key, value]) => key);
        trierStages(stagesDetails, mapStageSort);
    }

    function stageEtNbreDeCorrespondances() {
        let mapStages = new Map();
        let arrayMotsTrouvesTitre = [];
        let arrayMotsTrouvesEntreprise = [];
        Object.entries(stages).forEach(([key, stage]) => {
            const arrayofStringTitre = stage.titre.match(/\b[\wÀ-ÿ]+\b/gu);
            const arrayofStringEntreprise = stage.entreprise.match(/\b[\wÀ-ÿ]+\b/gu);
            const arrayofStringRecherche = recherche.match(/\b[\wÀ-ÿ]+\b/gu);

            rechercheStageParTitre(stage, mapStages, arrayofStringRecherche, arrayofStringTitre, arrayMotsTrouvesTitre);
            rechercheStageParEntreprise(stage, mapStages, arrayofStringRecherche, arrayofStringEntreprise, arrayMotsTrouvesEntreprise);
        });

        setMotsContenantRechercheTitre(arrayMotsTrouvesTitre);
        setMotsContenantRechercheEntreprise(arrayMotsTrouvesEntreprise);
        return mapStages;
    }

    function rechercheStageParTitre(stage, mapStages, arrayofStringRecherche, arrayofStringTitre, arrayMotsTrouvesTitre) {
        let nbreDeMots = 0;
        for (let i = 0; i < arrayofStringTitre.length; i++) {
            for (let j = 0; j < arrayofStringRecherche.length; j++) {
                if (arrayofStringRecherche[j].toLowerCase().trim() === arrayofStringTitre[i].toLowerCase()) {
                    nbreDeMots++;
                    arrayMotsTrouvesTitre.push(arrayofStringTitre[i].toLowerCase().trim());
                }
            }
            mapStages.set(stage.titre, nbreDeMots);
        }
    }

    function rechercheStageParEntreprise(stage, mapStages, arrayofStringRecherche, arrayofStringEntreprise, arrayMotsTrouvesEntreprise) {
        let nbreDeMots = 0;
        for (let i = 0; i < arrayofStringEntreprise.length; i++) {
            for (let j = 0; j < arrayofStringRecherche.length; j++) {
                if (arrayofStringRecherche[j].toLowerCase().trim() === arrayofStringEntreprise[i].toLowerCase()) {
                    nbreDeMots++;
                    arrayMotsTrouvesEntreprise.push(arrayofStringEntreprise[i].toLowerCase().trim())
                }
            }
            mapStages.set(stage.entreprise, nbreDeMots);
        }
    }

    function trierStages(stagesDetails, mapStageSort) {
        stagesDetails.then(function (resultat) {
            const titresAjoutes = new Set();

            const resultatsFinals = mapStageSort
                .map(titreOuEntreprise =>
                    resultat.find(stage => {
                        const estCorrespondant = stage.titre === titreOuEntreprise || stage.entreprise === titreOuEntreprise;
                        if (estCorrespondant && !titresAjoutes.has(stage.titre)) {
                            titresAjoutes.add(stage.titre);
                            return stage;
                        }
                        return null;
                    })
                )
                .filter(Boolean);

            setStages(resultatsFinals);
        });
    }

    const surlignerRecherche = (texte, motsRecherche) => {
        if (!motsRecherche || motsRecherche.length === 0) return texte;

        const regex = new RegExp(`(${motsRecherche.join('|')})`, "gi");
        const parties = texte.split(regex);

        return (
            <>
                {parties.map((partie, index) =>
                    regex.test(partie) ? (
                        <span key={index} style={{
                            backgroundColor: "#ffd0ae",
                            paddingRight: "2px",
                            paddingLeft: "2px",
                            borderRadius: "8px"
                        }}>
                        {partie}
                    </span>
                    ) : (
                        <span key={index}>{partie}</span>
                    )
                )}
            </>
        );
    };

    function supprimerRecherche() {
        setUneRechercheEstFaite(false);
        setRecherche('');
        setMotsContenantRechercheTitre(null);
        setMotsContenantRechercheEntreprise(null);
        setButtonDisabled(true);
        fetchStages();
    }

    function isThereANextPage() {
        if (pageActuelle === nombreDePage) {
            setNextPageDisabled(true);
            return false;
        } else {
            setNextPageDisabled(false);
            return true;
        }
    }

    function isItTheFirstPage() {
        if (pageActuelle === 0) {
            setPreviousPageDisabled(true);
            return true;
        } else {
            setPreviousPageDisabled(false);
            return false;
        }
    }

    function nextPage() {
        if (isThereANextPage() === true) {
            setPageActuelle(pageActuelle + 1);
            fetchStages();
        }
    }

    function previousPage() {
        if (isItTheFirstPage() === false) {
            setPageActuelle(pageActuelle - 1);
            fetchStages();
        }
    }

    return (
        <>
            <div className="w-2/3 lg:w-1/2">
                <h1 className="pt-12 text-3xl md:text-4xl text-pretty font-accueilTitreFont font-bold text-black">Explorez
                    des stages dans des
                    entreprises variées</h1>
                <div className="flex flex-row">
                    <input type="search" placeholder="Chercher un stage" id="searchInput" value={recherche}
                           onChange={(e) => {
                               activerDesactiverRecherche(e);
                               changeRechercheStage(e);
                           }}
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
                                                className="text-black text-xl pl-2 max-w-max pr-2">{surlignerRecherche(stage.title, motsContenantRechercheTitre)}</h3>
                                            <h4 className="text-lg pl-2 max-w-max pr-2">{surlignerRecherche(stage.entrepriseName, motsContenantRechercheEntreprise)}</h4>
                                            <h4 className="pl-2 max-w-max pr-2">{stage.created_at}</h4>
                                        </div>
                                        <button
                                            className="bg-orange text-white md:absolute md:bottom-2 lg:bottom-12 lg:top-4 md:right-2 px-4 md:px-8 py-2 rounded-2xl mt-3 lg:mt-0 w-1/2 md:w-1/3 lg:w-36 hover:bg-orange-dark shadow-md">Appliquer
                                        </button>
                                    </div>
                                    <hr className="bg-deep-orange-100"/>
                                </>

                            )) :
                            <h3>Aucun stage disponible</h3>
                    }
                </div>
                <div className="text-center mb-7">
                    <button
                        className="decoration-0 bg-orange pt-0 pb-1 pl-4 pr-4 mr-2 hover:bg-amber-900 justify-center items-center w-10 text-xl shadow-md disabled:bg-orange disabled:opacity-70"
                        disabled={previousPageDisabled} onClick={() => previousPage()}>&#8249;</button>
                    {pageActuelle + 1}
                    <button
                        className="decoration-0 bg-orange pt-0 pb-1 pl-4 pr-4 ml-2 hover:bg-amber-900 justify-center items-center w-10 text-xl shadow-md disabled:bg-orange disabled:opacity-70"
                        disabled={nextPageDisabled} onClick={() => nextPage()}>&#8250;</button>
                </div>
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
                            {/* Modal Header */}
                            <div className="flex justify-between items-center border-b pb-3">
                                <h2 className="text-2xl font-bold text-gray-800">
                                    Détails du stage
                                </h2>
                                <button
                                    id="closeStageDetails"
                                    className="text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none"
                                    onClick={() => setUnStageEstClique(false)}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>

                            {/* Modal Content */}
                            <div className="pt-4 space-y-4 text-center">
                                {stageClique ? (
                                    <>
                                        {stageClique.title ? (
                                            <h3 className="text-xl font-semibold text-gray-900 break-words">
                                                {stageClique.title}
                                            </h3>
                                        ) : (
                                            <h3 className="text-xl font-semibold text-gray-500">
                                                Titre de l'offre inexistant
                                            </h3>
                                        )}

                                        {stageClique.entrepriseName ? (
                                            <h4 className="text-lg font-medium text-gray-700 break-words">
                                                Offre de {stageClique.entrepriseName}
                                            </h4>
                                        ) : (
                                            <h4 className="text-lg text-gray-500">
                                                Entreprise inconnue
                                            </h4>
                                        )}

                                        {stageClique.description && stageClique.location && stageClique.salary && stageClique.website ? (
                                            <div className="space-y-2 break-words">
                                                <p className="text-gray-700">{stageClique.description}</p>
                                                <p className="text-gray-700">{stageClique.location}</p>
                                                <p className="text-green-600 font-medium">Salaire : {stageClique.salary}$</p>
                                                <a href={stageClique.website} className="text-blue-500 underline">
                                                    {stageClique.website}
                                                </a>
                                            </div>
                                        ) : (
                                            <p className="text-gray-500 break-words">
                                                Rendez-vous sur la page d'application pour plus de détails concernant cette offre.
                                            </p>
                                        )}

                                        <p className="text-gray-400 text-sm">
                                            Publié le {new Date(stageClique.create_at).toLocaleDateString()}
                                        </p>
                                    </>
                                ) : (
                                    <p className="text-gray-500">
                                        Aucun stage sélectionné.
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