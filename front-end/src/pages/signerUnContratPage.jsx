import React from 'react';
import { useOutletContext } from 'react-router-dom';
import SignerContrat from '../components/signerContrats/signerContrat';
import TokenPageContainer from './tokenPageContainer';

const SignerUnContratPage = () => {
    const { setSelectedContract, setUserInfo, selectedContract } = useOutletContext();
    
    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <SignerContrat selectedContract={selectedContract} setSelectedContract={setSelectedContract} />
        </TokenPageContainer>
    );
};

export default SignerUnContratPage;