import React from "react";
import TokenPageContainer from './tokenPageContainer';
import { useOutletContext } from 'react-router-dom';
import ListeConvocations from "../components/etudiantConvocationPage/listeConvocations.jsx";

function EtudiantConvocationPage() {
    const { setUserInfo } = useOutletContext();

    return (
        <TokenPageContainer role={["ETUDIANT"]} setUserInfo={setUserInfo}>
            <ListeConvocations />
        </TokenPageContainer>
    );
}

export default EtudiantConvocationPage;
