import React from 'react';
import EvaluerListEtudiantCard from './evaluerListEtudiantCard';

const EvaluerListEtudiant = ({ students, setSelectedStudent, destination }) => {

    function getNomPrenom(student) {
        return student.prenom + " " + student.nom;
    }

    return (
        
        students.map((student, index) => <EvaluerListEtudiantCard 
                                                    key={index} 
                                                    student={student} 
                                                    nomPrenom={getNomPrenom(student)} 
                                                    setSelectedStudent={setSelectedStudent}
                                                    destination={destination} />)
        
    );
};

export default EvaluerListEtudiant;