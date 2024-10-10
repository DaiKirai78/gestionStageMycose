import React from 'react';
import TokenPageContainer from "./tokenPageContainer.jsx";
import EtudiantsCV from "../components/validerCVPage/etudiantsCV.jsx";

const ValiderCVPage = () => {
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]}>
            <EtudiantsCV />
        </TokenPageContainer>
    )
}

export default ValiderCVPage;