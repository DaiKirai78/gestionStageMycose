import {useOutletContext} from "react-router-dom";
import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AttribuerContratPage from "./attribuerContratPage.jsx";

const ContratPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <AttribuerContratPage/>
        </TokenPageContainer>
    )
}

export default ContratPage;