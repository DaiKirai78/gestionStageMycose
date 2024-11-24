import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useOutletContext } from "react-router-dom";
import EvaluerEtudiant from '../components/evaluer/evaluerEtudiant';

const EvaluerPage = () => {
    const {userInfo, setUserInfo, setSelectedStudent} = useOutletContext();

    return (
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <EvaluerEtudiant setSelectedStudent={setSelectedStudent} userInfo={userInfo} />
        </TokenPageContainer>
    );
};

export default EvaluerPage;