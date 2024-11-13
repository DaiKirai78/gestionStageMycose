import React from 'react';
import { useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import EvaluerEtudiantFormulairesList from '../components/evaluer/evaluerEtudiantFormulairesList';

const evaluerEtudiantFormulairePage = () => {
    const {setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    return (
        
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <EvaluerEtudiantFormulairesList selectedStudent={selectedStudent} setSelectedStudent={setSelectedStudent}/>
        </TokenPageContainer>
    );
};

export default evaluerEtudiantFormulairePage;