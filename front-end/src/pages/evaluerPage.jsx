import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useOutletContext } from "react-router-dom";
import EvaluerEtudiant from '../components/evaluer/evaluerEtudiant';

const students = [
    {
        prenom: "Jason prof",
        nom: "Jody",
    },
    {
        prenom: "Vicente prof",
        nom: "Cabezas",
    },
    {
        prenom: "Roberto prof",
        nom: "Berrios",
    }
]

const EvaluerPage = () => {
    const {userInfo, setUserInfo, selectedStudent, setSelectedStudent} = useOutletContext();

    return (
        <TokenPageContainer role={["EMPLOYEUR", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            <EvaluerEtudiant setSelectedStudent={setSelectedStudent} />
        </TokenPageContainer>
    );
};

export default EvaluerPage;