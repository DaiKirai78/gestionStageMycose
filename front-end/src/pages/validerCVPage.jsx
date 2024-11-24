import React from 'react';
import TokenPageContainer from "./tokenPageContainer.jsx";
import EtudiantsCV from "../components/validerCVPage/etudiantsCV.jsx";
import { useOutletContext } from 'react-router-dom';

const ValiderCVPage = () => {
    const { setUserInfo } = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <EtudiantsCV />
        </TokenPageContainer>
    )
}

export default ValiderCVPage;