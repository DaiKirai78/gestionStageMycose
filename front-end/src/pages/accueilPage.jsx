import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import AcceuilEtudiant from '../components/acceuil/acceuilEtudiant';
import AcceuilEmployeur from '../components/acceuil/acceuilEmployeur';

const AccueilPage = () => {

    const [role, setRole] = useState();
    const navigate = useNavigate()

    useEffect(() => {
        getRole()
    });

    async function getRole() {
        let token = localStorage.getItem("token");

        if (!token) {
            return;
        }
            
        try {
            await fetch('http://localhost:8080/utilisateur/me', {
                method: "POST",
                headers: {Authorization: `Bearer ${token}`}
            })
                .then(async (res) => {
                    if (!res.ok) {
                        return;
                    }
                    const data = await res.json();
                    setRole(data.role);
                }
                )
    
            } catch (err) {
                return;
            }
    }

    function getAccueil() {
        switch (role) {
            case "ETUDIANT":
                return <AcceuilEtudiant />
            case "EMPLOYEUR":
                return <AcceuilEmployeur />
            default:
                navigate("/")
                break;
        }
    }

    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE"]}>
            {getAccueil()}
        </TokenPageContainer>
    );
};

export default AccueilPage;