import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';
import { useOutletContext } from 'react-router-dom';
import VoirMonCV from "../components/voirMonCVPage/voirMonCV.jsx";

const VoirMonCVPage = () => {

    const [roleUser, setRoleUser] = useOutletContext();
    const [hasCv, setHasCv] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        checkCv();
    }, []);

    async function checkCv() {
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
                setHasCv(true);
            } else {
                setHasCv(false);
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
            setHasCv(false);
        }
    }

    useEffect(() => {
        if (hasCv === false) {
            navigate("/accueil");
        }
    }, [hasCv, navigate]);

    if (hasCv === null) {
        return <div>{t("loading")}...</div>;
    }
    return (
        <TokenPageContainer role={["ETUDIANT"]} setRoleUser={setRoleUser}>
            <div className="flex items-start justify-center min-h-screen">
                <VoirMonCV />
            </div>
        </TokenPageContainer>
    );
};

export default VoirMonCVPage;