import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const EvaluerEtudiantFormulaires = ({ selectedStudent, setSelectedStudent }) => {

    const navigate = useNavigate();

    useEffect(() => {
        if (!selectedStudent) {
            navigate("/evaluer");
        }
    })

    if (!selectedStudent)
        return <></>;

    return (
        <div className='flex flex-col flex-1 items-center bg-orange-light'>
            <p>Page formulaire</p>
        </div>
    );
};

export default EvaluerEtudiantFormulaires;