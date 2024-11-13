import React from 'react';
import EvaluerListEtudiantCard from './evaluerListEtudiantCard';

const EvaluerListEtudiant = ({ students, setSelectedStudent }) => {

    function getNomPrenom(student) {
        return student.prenom + " " + student.nom;
    }

    return (
        
        students.map((student, index) => <EvaluerListEtudiantCard key={index} nomPrenom={getNomPrenom(student)} setSelectedStudent={setSelectedStudent} />)
        
    );
};

export default EvaluerListEtudiant;