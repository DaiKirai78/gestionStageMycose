import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import verifToken from "../utils/verifTokenValid"

const AccueilPage = () => {

    const navigate = useNavigate();

    let token = localStorage.getItem('token');

    useEffect(() => {
        
        if (verifToken(navigate, token)) {
            return;
        }
        
    }, [token]);

    return (
        <div>
            <h1>Accueil</h1>
        </div>
    );
};

export default AccueilPage;