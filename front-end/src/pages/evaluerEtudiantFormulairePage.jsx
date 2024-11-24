import React from 'react';
import { useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import EvaluerEtudiantFormulairesList from '../components/evaluer/evaluerEtudiantFormulairesList';
import EvaluerEtudiantFormulaireEnseignant from "../components/evaluer/evaluerEtudiantFormulaireEnseignant.jsx";

const evaluerEtudiantFormulairePage = () => {
    const { userInfo, setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    return (
        
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            {userInfo.role === "EMPLOYEUR" ? (
                <EvaluerEtudiantFormulairesList
                    selectedStudent={selectedStudent}
                    setSelectedStudent={setSelectedStudent}
                    userInfo={userInfo}
                />
            ) : userInfo.role === "ENSEIGNANT" ? (
                <EvaluerEtudiantFormulaireEnseignant
                    selectedStudent={selectedStudent}
                    setSelectedStudent={setSelectedStudent}
                    userInfo={userInfo}
                />
            ) : null
            }
        </TokenPageContainer>
    );
};

export default evaluerEtudiantFormulairePage;