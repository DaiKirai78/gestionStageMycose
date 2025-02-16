import {useLocation, useOutletContext} from "react-router-dom";
import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AttribuerContrat from "../components/contratPage/attribuerContrat.jsx";

const AttribuerContratPage = () => {
    const { setUserInfo } = useOutletContext();
    const location = useLocation();
    const { etudiant } = location.state || {};
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <AttribuerContrat etudiant={etudiant}/>
        </TokenPageContainer>
    )
}

export default AttribuerContratPage;