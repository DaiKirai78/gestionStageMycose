import {useEffect, useState} from "react";
import axios from "axios";

const listeStage = () => {

    const [stagesFormulaire, setStagesFormulaire] = useState(null);
    const [stagesFichier, setStagesFichier] = useState(null);

    const [rechercheStage, setRechercheStage] = useState("");
    const [buttonDisabled, setButtonDisabled] = useState(true);

    const [stageClique, setStageClique] = useState(null);
    const [unStageEstClique, setUnStageEstClique] = useState(false);

    let localhost = "http://localhost:8080/";
    let urlGetFormulaireStage = "/api/offres/get-all-forms";
    let urlGetFichierStage = "/api/offres-stage/get-all-files";

    // useEffect(async () => {
    //     const responseForms = await axios.get(localhost + urlGetFormulaireStage);
    //     setStagesFormulaire(responseForms.data);
    //     const responseFiles = await axios.get(localhost + urlGetFichierStage);
    //     setStagesFichier(responseFiles.data);
    // }, []);

    useEffect(() => {
        fetchStages();
    }, []);

    const fetchStages = async () => {
        try {
            const response = await axios.get("/stage.json");
            setStagesFormulaire(response.data);
            return response.data;
        } catch (error) {
            console.error("Erreur lors de la récupération des stages:", error);
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
        setRechercheStage(e.target.value)
    }

    function rechercher(e) {
        e.preventDefault();
        let mapStages = stageEtNbreDeCorrespondances();

        const mapStageSort = [...mapStages.entries()].sort((a, b) => b[1] - a[1]).map(([key, value]) => key);
        const stagesDetails = fetchStages();
        trierStages(stagesDetails, mapStageSort);
    }

    function stageEtNbreDeCorrespondances() {
        let mapStages = new Map();
        Object.entries(stagesFormulaire).forEach(([key, stage]) => {
            const arrayofStringTitre = stage.titre.match(/\b[\wÀ-ÿ]+\b/gu);
            const arrayofStringEntreprise = stage.entreprise.match(/\b[\wÀ-ÿ]+\b/gu);

            rechercheStageParTitre(stage, mapStages, rechercheStage, arrayofStringTitre);
            rechercheStageParEntreprise(stage, mapStages, rechercheStage, arrayofStringEntreprise);
        });
        return mapStages;
    }

    function rechercheStageParTitre(stage, mapStages, rechercheStage, arrayofStringTitre) {
        let nbreDeMots = 0;
        for (let i = 0; i < arrayofStringTitre.length; i++) {
            if (rechercheStage.toLowerCase().trim().includes("programmeur") && arrayofStringTitre[i].toLowerCase() === "développeur")
                nbreDeMots++;
            if (rechercheStage.toLowerCase().trim().includes("développeur") && arrayofStringTitre[i].toLowerCase() === "programmeur")
                nbreDeMots++
            if (rechercheStage.toLowerCase().trim().includes((arrayofStringTitre[i].toLowerCase()))) {
                nbreDeMots++;
            }
            mapStages.set(stage.titre, nbreDeMots);
        }
    }

    function rechercheStageParEntreprise(stage, mapStages, rechercheStage, arrayofStringEntreprise) {
        let nbreDeMots = 0;
        for (let i = 0; i < arrayofStringEntreprise.length; i++) {
            if (rechercheStage.toLowerCase().trim().includes((arrayofStringEntreprise[i].toLowerCase()))) {
                nbreDeMots++;
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

            setStagesFormulaire(resultatsFinals);
        });
    }


    return (
        <>
            <div className="w-2/3 lg:w-1/2">
                <h1 className="pt-12 text-3xl md:text-4xl text-pretty font-accueilTitreFont font-bold text-black">Explorez
                    des stages dans des
                    entreprises variées</h1>
                <div className="flex flex-row">
                    <input type="search" placeholder="Chercher un stage" id="searchInput" onChange={(e) => {
                        activerDesactiverRecherche(e);
                        changeRechercheStage(e);
                    }}
                           className="mt-6 mb-9 pt-3 pb-3 pl-14 text-md sm:text-xl rounded-l-3xl w-full outline-0"></input>
                    <button id="buttonRechercherStage"
                            className="mt-6 mb-9 pt-3 pb-3 pl-12 sm:pl-16 md:pl-24 rounded-r-3xl"
                            disabled={buttonDisabled} onClick={rechercher}>
                    </button>
                </div>
                <div>
                    <hr/>
                    {
                        stagesFormulaire && stagesFormulaire.length > 0 ?
                            stagesFormulaire.map((stage, index) => (
                                <>
                                    <div id="elementStage"
                                         className="flex flex-col w-full hover:cursor-pointer pb-3 pt-3 bg-orange-light transition-all duration-300 ease-in-out transform md:hover:-translate-y-1 md:hover:shadow-lg md:hover:rounded-t-xl"
                                         onClick={() => elementStageClique(stage)}>
                                        <div>
                                            <h3 key={index} className="text-black text-xl pl-2">{stage.titre}</h3>
                                            <h4 className="text-lg pl-2">{stage.entreprise}</h4>
                                            <h4 className="pl-2">{stage.date}</h4>
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
            </div>
            {
                //stageClique.type === "fichier" ? //TODO : AJOUTER LE TYPE DE FICHIER SELON SI C'EST FILE OU FORM
                unStageEstClique &&  (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50" onClick={() => setUnStageEstClique(false)}>
                        <div className="w-3/4 h-1/3 sm:w-2/3 sm:h-1/3 lg:w-1/3 lg:h-1/3 bg-white rounded-xl shadow-xl p-4 relative" onClick={(e) => e.stopPropagation()}>
                            <div className="flex justify-center items-center border-b pb-3">
                                <h2 className="text-2xl font-accueilTitreFont font-semibold">
                                    Détails du stage
                                </h2>
                                <button
                                    id="closeStageDetails"
                                    className="absolute top-4 right-4 text-gray-600 hover:text-gray-900 w-6 h-6"
                                    onClick={() => setUnStageEstClique(false)}
                                >
                                </button>
                            </div>
                            <div className="pt-4">
                                {stageClique && stageClique.titre ? (
                                    <>
                                        <h3 className="text-xl font-semibold text-center pt-4 pb-2">
                                            {stageClique.titre}
                                        </h3>
                                        <h4 className="text-center pb-2">
                                            Offre de {stageClique.entreprise}
                                        </h4>
                                        <h4 className="text-center">
                                            Publié le {stageClique.date}
                                        </h4>
                                    </>
                                ) : (
                                    <div></div>
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