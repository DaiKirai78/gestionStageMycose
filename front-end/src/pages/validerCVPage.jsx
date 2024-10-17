import React from 'react';
import TokenPageContainer from "./tokenPageContainer.jsx";
import EtudiantsCV from "../components/validerCVPage/etudiantsCV.jsx";
import { useOutletContext } from 'react-router-dom';

const ValiderCVPage = () => {
    const [roleUser, setRoleUser] = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setRoleUser={setRoleUser}>
            <EtudiantsCV />
        </TokenPageContainer>
    )
}

export default ValiderCVPage;