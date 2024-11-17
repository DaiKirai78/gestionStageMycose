import React, { useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import Profil from '../components/profil/profil';

const ProfilPage = () => {
    const { setUserInfo } = useOutletContext();

    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <Profil />
        </TokenPageContainer>
    );
};

export default ProfilPage;