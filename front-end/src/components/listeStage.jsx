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
                setStagesFormulaire(response.data);
            } catch (error) {
                console.error("Erreur lors de la récupération des stages:", error);
            }
        };

        fetchStages();

    }, []);

    console.log("Valeur de stagesFormulaire après mise à jour:", stagesFormulaire);

    return (
        <div className="w-2/3 lg:w-1/2">
            <h1 className="pt-12 text-3xl md:text-4xl text-pretty font-accueilTitreFont font-bold text-black">Explorez des stages dans des
                entreprises variées</h1>
            <div className="flex flex-row">
                <input type="search" placeholder="Chercher un stage" id="searchInput"
                       className="mt-6 mb-9 pt-3 pb-3 pl-14 text-md sm:text-xl rounded-l-3xl w-full outline-0"></input>
                <button id="buttonRechercherStage" className="mt-6 mb-9 pt-3 pb-3 pl-12 sm:pl-16 md:pl-24 rounded-r-3xl">
                </button>
            </div>
            <div>
                <hr/>
                {
                    stagesFormulaire && stagesFormulaire.length > 0 ?
                        stagesFormulaire.map((stage, index) => (
                            <>
                                <div id="elementStage" className="flex flex-col w-full hover:cursor-pointer pb-3 pt-3 bg-orange-light transition-all duration-300 ease-in-out transform md:hover:-translate-y-1 md:hover:shadow-lg md:hover:rounded-t-xl">
                                    <div>
                                        <h3 key={index} className="text-black text-xl pl-2">{stage.titre}</h3>
                                        <h4 className="text-lg pl-2">{stage.entreprise}</h4>
                                        <h4 className="pl-2">{stage.date}</h4>
                                    </div>
                                    <button className="bg-orange text-white md:absolute md:bottom-2 lg:bottom-12 lg:top-4 md:right-2 px-4 md:px-8 py-2 rounded-2xl mt-3 lg:mt-0 w-1/2 md:w-1/3 lg:w-36 hover:bg-orange-dark shadow-md">Appliquer</button>
                                </div>
                                <hr className="bg-deep-orange-100"/>
                            </>
                        )) :
                        <h3>Aucun stage disponible</h3>
                }
            </div>
        </div>
    )
}

export default listeStage;