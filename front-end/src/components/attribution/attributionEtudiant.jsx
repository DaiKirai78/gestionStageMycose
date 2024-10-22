import React from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';

const AttributionEtudiant = () => {
    const [student, setStudent] = useOutletContext();
    const navigate = useNavigate()

    return (
        <div>
            
        </div>
    );
};

export default AttributionEtudiant;