import React, { useEffect, useState } from 'react';
import TokenPageContainer from './tokenPageContainer';
import { useNavigate } from 'react-router-dom';
import { useOutletContext } from "react-router-dom";
import PageIsLoading from '../components/pageIsLoading.jsx';
import SignerContratEmployeur from '../components/signerContrats/signerContratEmployeur.jsx';
import SignerContratGestionnaire from '../components/signerContrats/signerContratGestionnaire.jsx';
import SignerContratEtudiant from '../components/signerContrats/signerContratEtudiant.jsx';

const AccueilPage = () => {
    const [cvStatus, setCvStatus] = useState(null);
    const navigate = useNavigate();
    const {userInfo, setUserInfo, setSelectedContract } = useOutletContext();
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
            return <SignerContratEtudiant setSelectedContract={setSelectedContract} />;
        }
        else {
            navigate("/accueil")
            return;
        }
    }

    function getPageContrat() {
        if (!userInfo || (userInfo.role === "ETUDIANT" && isFetching)) return <PageIsLoading/>;
        
        switch (userInfo.role) {
            case "ETUDIANT":
                return getEtudiantPage();
            case "EMPLOYEUR":
                return <SignerContratEmployeur setSelectedContract={setSelectedContract} />;
            case "GESTIONNAIRE_STAGE":
                return <SignerContratGestionnaire setSelectedContract={setSelectedContract} />
            default:
                navigate("/accueil");
                break;
        }
    }

    return (
        <TokenPageContainer role={["ETUDIANT", "EMPLOYEUR", "GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            {getPageContrat()}
        </TokenPageContainer>
    );
};

export default AccueilPage;