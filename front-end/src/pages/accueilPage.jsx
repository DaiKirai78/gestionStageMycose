import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import AcceuilEtudiant from '../components/acceuil/acceuilEtudiant';
import AcceuilEmployeur from '../components/acceuil/acceuilEmployeur';
import { useOutletContext } from "react-router-dom";
import UploadCvPage from '../pages/UploadCvPage';
import VoirMonCVPage from "./voirMonCVPage.jsx";

const AccueilPage = () => {
    const [role, setRole] = useState(null);
    const [cvStatus, setCvStatus] = useState(null);
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useOutletContext();

    useEffect(() => {
        getRole();
    }, []);

    async function getRole() {
        const token = localStorage.getItem("token");
        if (!token) {
            return;
        }

        try {
            const res = await fetch('http://localhost:8080/utilisateur/me', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!res.ok) {
                return;
            }

            const data = await res.json();
            setRole(data.role);

            if (data.role === "ETUDIANT") {
                checkCvStatus(token);
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du rôle", err);
        }
    }

    async function checkCvStatus(token) {
        try {
            const res = await fetch('http://localhost:8080/api/cv/current', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const cvData = await res.json();
                setCvStatus(cvData.status);
            } else {
                setCvStatus("NONE");
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
        }
    }

    function getAccueil() {
        switch (role) {
            case "ETUDIANT":
                if (cvStatus === "ACCEPTED") {
                    return <AcceuilEtudiant />;
                }
                else if (cvStatus === "WAITING") {
                    return <VoirMonCVPage />;
                }
                else {
                    return <UploadCvPage />;
                }
            case "EMPLOYEUR":
                return <AcceuilEmployeur />;
            case "GESTIONNAIRE_STAGE":
                return <div>Acceuil Gestionnaire</div>;
            default:
                navigate("/");
                break;
        }
    }

    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE", "ENSEIGNANT"]} setUserInfo={setUserInfo}>
            {getAccueil()}
        </TokenPageContainer>
    );
};

export default AccueilPage;