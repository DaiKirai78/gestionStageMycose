import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import AccueilEtudiant from '../components/accueil/accueilEtudiant.jsx';
import AccueilEmployeur from '../components/accueil/accueilEmployeur.jsx';
import { useOutletContext } from "react-router-dom";
import UploadCvPage from '../pages/UploadCvPage';
import VoirMonCVPage from "./voirMonCVPage.jsx";
import PageIsLoading from '../components/pageIsLoading.jsx';
import AccueilGestionnaire from "../components/accueil/accueilGestionnaire.jsx";

const AccueilPage = () => {
    const [cvStatus, setCvStatus] = useState(null);
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useOutletContext();
    const [isFetching, setIsFetching] = useState(true);

    useEffect(() => {
        if (userInfo && userInfo.role === "ETUDIANT") {
            checkCvStatus();
        }
    }, [userInfo]);

    async function checkCvStatus() {
        const token = localStorage.getItem("token");
        
        if (!token) {
            return;
        }

        try {
            const res = await fetch('http://localhost:8080/api/cv/current', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const cvData = await res.json();
                console.log(cvData);
                console.log("data");
                
                setCvStatus(cvData.status);
            } else {
                setCvStatus(undefined);
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
        } finally {
            setIsFetching(false);
        }
    }

    function getEtudiantPage() {
        
        if (cvStatus === "ACCEPTED") {
            return <AccueilEtudiant />;
        }
        else if (cvStatus === "WAITING") {
            return <VoirMonCVPage />;
        }
        else {
            return <UploadCvPage />;
        }
    }

    function getAccueil() {
        if (!userInfo || (userInfo.role === "ETUDIANT" && isFetching)) return <PageIsLoading/>;
        
        switch (userInfo.role) {
            case "ETUDIANT":
                return getEtudiantPage()
            case "EMPLOYEUR":
                return <AccueilEmployeur />;
            case "GESTIONNAIRE_STAGE":
                return <AccueilGestionnaire />;
            case "ENSEIGNANT":
                return <p>Enseignant</p>;
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