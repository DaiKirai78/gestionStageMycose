import TokenPageContainer from "./tokenPageContainer.jsx";
import React, { useEffect, useState } from "react";
import AppliquerStage from "../components/acceuil/appliquerStage.jsx";
import {useLocation, useNavigate, useOutletContext} from "react-router-dom";
import {useTranslation} from "react-i18next";

const AppliquerStagePage = () => {
    const [hasAcceptedCv, setHasAcceptedCv] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { idStage } = location.state || {};
    const [userInfo, setUserInfo] = useOutletContext();
    const [roleUser, setRoleUser] = useOutletContext();
    const {t} = useTranslation();

    useEffect(() => {
        checkAcceptedCv();
    }, []);

    async function checkAcceptedCv() {
        const token = localStorage.getItem("token");
        if (!token) {
            navigate("/accueil");
            return;
        }

        try {
            const res = await fetch('http://localhost:8080/api/cv/current', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const cvData = await res.json();
                if (cvData.status === "ACCEPTED") {
                    setHasAcceptedCv(true);
                } else {
                    setHasAcceptedCv(false);
                }
            } else {
                setHasAcceptedCv(false);
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
            setHasAcceptedCv(false);
        }
    }

    useEffect(() => {
        if (hasAcceptedCv === false) {
            navigate("/accueil");
        }
    }, [hasAcceptedCv, navigate]);

    if (hasAcceptedCv === null) {
        return <div>{t("loading")}...</div>;
    }

    return (
        <TokenPageContainer role={["ETUDIANT"]} setUserInfo={setUserInfo}>
            <div className="bg-orange-light w-full min-h-screen">
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full mb-10">
                    (Logo) Mycose
                </div>
                <div className="flex h-3/5 justify-center">
                    <AppliquerStage idStage={idStage} />
                </div>
            </div>
        </TokenPageContainer>
    );
};

export default AppliquerStagePage;
