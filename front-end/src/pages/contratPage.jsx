import {useOutletContext} from "react-router-dom";
import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import ListeEtudiantsSansContrat from "../components/contratPage/listeEtudiantsSansContrat.jsx";

const ContratPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <ListeEtudiantsSansContrat/>
        </TokenPageContainer>
    )
}

export default ContratPage;