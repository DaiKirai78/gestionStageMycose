import React from 'react';
import { useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import EvaluerEtudiantFormulaires from '../components/evaluer/evaluerEtudiantFormulaires';

const evaluerEtudiantFormulairePage = () => {
    const {setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    return (
        
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <EvaluerEtudiantFormulaires selectedStudent={selectedStudent} setSelectedStudent={setSelectedStudent}/>
        </TokenPageContainer>
    );
};

export default evaluerEtudiantFormulairePage;