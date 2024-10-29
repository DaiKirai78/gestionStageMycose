import {useOutletContext} from "react-router-dom";
import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import ListeEtudiants from "../components/etudiantsSansContratPage/listeEtudiants.jsx";

const EtudiantsSansContratPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <ListeEtudiants/>
        </TokenPageContainer>
    )
}

export default EtudiantsSansContratPage;