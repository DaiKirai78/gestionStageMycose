import {useEffect, useState} from "react";
import axios from "axios";

const listeStage = () => {

    const [stagesFormulaire, setStagesFormulaire] = useState(null);
    const [stagesFichier, setStagesFichier] = useState(null);

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
        const fetchStages = async () => {
            try {
                const response = await axios.get("/stage.json");
                console.log(response.data);
                setStagesFormulaire(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération des stages:", error);
            }
        };

        fetchStages();

    }, []);

    console.log("Valeur de stagesFormulaire après mise à jour:", stagesFormulaire);

    return (
        <div className="w-1/2">
            <h1 className="pt-12 text-4xl font-accueilTitreFont font-bold text-black">Explorez des stages dans des
                entreprises variées</h1>
            <div className="flex flex-row">
                <input type="search" placeholder="Chercher un stage" id="searchInput"
                       className="mt-6 mb-9 pt-3 pb-3 pl-14 text-xl rounded-l-3xl w-full outline-0"></input>
                <button id="buttonRechercherStage" className="mt-6 mb-9 pt-3 pb-3 pl-24 rounded-r-3xl">
                </button>
            </div>
            <div>
                <hr className="mb-3"/>
                {
                    stagesFormulaire && stagesFormulaire.length > 0 ?
                        stagesFormulaire.map((stage, index) => (
                            <>
                            <div className="mt-3">
                                    <h3 key={index} className="text-black text-xl">{stage.titre}</h3>
                                    <h4 key={index} className="text-lg">{stage.entreprise}</h4>
                                </div>
                                <h4 key={index}>{stage.date}</h4>
                                <hr className="mb-3 mt-3 bg-deep-orange-100"/>
                            </>
                        )) :
                        <h3>Aucun stage disponible</h3>
                }
            </div>
        </div>
    )
}

export default listeStage;