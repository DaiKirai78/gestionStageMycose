import React from 'react';
import { useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import EvaluerEtudiantFormulairesList from '../components/evaluer/evaluerEtudiantFormulairesList';

const evaluerEtudiantFormulairePage = () => {
    const { userInfo, setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    return (
        
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <EvaluerEtudiantFormulairesList 
                            selectedStudent={selectedStudent} 
                            setSelectedStudent={setSelectedStudent}
                            userInfo={userInfo}
                            />
        </TokenPageContainer>
    );
};

export default evaluerEtudiantFormulairePage;