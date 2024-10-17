import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import AcceuilEtudiant from '../components/acceuil/acceuilEtudiant';
import AcceuilEmployeur from '../components/acceuil/acceuilEmployeur';
import { useOutletContext } from "react-router-dom";

const AccueilPage = () => {

    const [roleUser, setRoleUser] = useOutletContext();
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
            case "GESTIONNAIRE_STAGE":
                return <div>Acceuil</div>
            default:
                navigate("/")
                break;
        }
    }

    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE", "ENSEIGNANT"]} setRoleUser={setRoleUser}>
            {getAccueil()}
        </TokenPageContainer>
    );
};

export default AccueilPage;