import React, { useEffect, useState } from 'react'
import CardStatusDuCv from './cardStatusDuCv'
import { useTranslation } from 'react-i18next';
import { useOutletContext } from 'react-router-dom';
import CardInfoUser from './cardInfoUser';
import axios from "axios";

const Profil = () => {
    const [isFetching, setIsFetching] = useState(true);
    const [cvInfo, setCvInfo] = useState();
    const { t } = useTranslation();
    const [userInfo, setUserInfo] = useOutletContext();
    const [role, setRole] = useState("");

    const cards = [
        {
            "role": [],
            "card": <CardInfoUser userInfo={userInfo} />
        },
        {
            "role": ["ETUDIANT"],
            "card": <CardStatusDuCv cvInfo={cvInfo}/>
        }
    ]

    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await axios.post("http://localhost:8080/utilisateur/me", {}, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                const userData = response.data;
                setRole(userData.role);
            } catch (error) {
                console.error("Erreur lors de la récupération des informations de l'utilisateur :", error);
            }
        };

        fetchUserData();
    }, []);

    useEffect(() => {
        if (role === "ETUDIANT") {
            fetchInfoCv();
        } else {
            setIsFetching(false);
        }
    }, [role]);

    async function fetchInfoCv() {
        const token = localStorage.getItem("token");

        if (!token) {
            setIsFetching(false);
            return;
        }

        try {
            const res = await fetch('http://localhost:8080/api/cv/current', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const cvData = await res.json();
                setCvInfo(cvData);
            } else {
                setCvInfo(null)
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
        } finally {
            setIsFetching(false);
        }
    }

    return (
        !isFetching 
        &&
        <div className="min-h-full bg-orange-light p-4 md:p-8 lg:p-12 flex-1">
            <h1 className="text-3xl md:text-4xl font-bold text-center mb-8">{t("profil")}</h1>
            
            <div className="max-w-3xl mx-auto space-y-8">
            {
                cards.map(card => {
                    if (card.role.length === 0 || card.role.includes(userInfo.role)) {
                        return card.card
                    }
                })
            }
            </div>
        </div>
    )
}

export default Profil