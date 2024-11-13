import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import { useOutletContext } from "react-router-dom";
import EvaluerEtudiantEmployeur from '../components/evaluer/evaluerEtudiantEmployeur';
import EvaluerEtudiantEnseignant from '../components/evaluer/evaluerEtudiantEnseigant';

const EvaluerPage = () => {
    const navigate = useNavigate();
    const {userInfo, setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    function getPageContrat() {
        if (!userInfo) return;
             
        switch (userInfo.role) {
            case "EMPLOYEUR":
                return <EvaluerEtudiantEmployeur selectedStudent={selectedStudent} setSelectedStudent={setSelectedStudent} />;
            case "ENSEIGNANT":
                return <EvaluerEtudiantEnseignant selectedStudent={selectedStudent} setSelectedStudent={setSelectedStudent} />;
            default:
                navigate("/accueil");
                break;
        }
    }

    return (
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            {getPageContrat()}
        </TokenPageContainer>
    );
};

export default EvaluerPage;