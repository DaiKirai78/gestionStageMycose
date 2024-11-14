import React, { useEffect, useState } from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";

const FiltreSession = ({ setAnnee, setSession }) => {
    const [sessions, setSessions] = useState([]);
    const [selectedSession, setSelectedSession] = useState(null);
    const [role, setRole] = useState("");

    const {t} = useTranslation();

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
        const fetchSessions = async () => {
            const token = localStorage.getItem("token");
            try {
                let sessionsResponse;

                if (role === "ETUDIANT") {
                    sessionsResponse = await axios.get(
                        "http://localhost:8080/api/offres-stages/get-all-sessions",
                        {
                            headers: { Authorization: `Bearer ${token}` },
                        }
                    );
                } else {
                    // Endpoint pour récupérer les sessions pour les créateurs
                    sessionsResponse = await axios.get(
                        "http://localhost:8080/api/offres-stages/get-sessions-for-createur",
                        {
                            headers: { Authorization: `Bearer ${token}` },
                        }
                    );
                }

                const allSessions = sessionsResponse.data;

                const nextSessionResponse = await axios.get(
                    "http://localhost:8080/api/offres-stages/get-next-session",
                    {
                        headers: { Authorization: `Bearer ${token}` },
                    }
                );
                const nextSession = nextSessionResponse.data;

                const sessionsList = allSessions.some(
                    session => session.session === nextSession.session && session.annee === nextSession.annee
                ) ? allSessions : [...allSessions, nextSession];

                setSessions(sessionsList);
                setSelectedSession(nextSession);

                setAnnee(nextSession.annee);
                setSession(nextSession.session);
            } catch (error) {
                console.error("Erreur lors de la récupération des sessions:", error);
            }
        };

        if (role) {
            fetchSessions();
        }
    }, [role, setAnnee, setSession]);

    const handleSessionChange = (event) => {
        const selected = sessions.find(
            session => `${session.session} ${session.annee}` === event.target.value
        );
        setSelectedSession(selected);
        setAnnee(selected.annee);
        setSession(selected.session);
    };

    return (
        <div className="dropdown text-right mb-4">
            <select
                id="sessionDropdown"
                value={`${selectedSession?.session || ""} ${selectedSession?.annee || ""}`}
                onChange={handleSessionChange}
                className="dropdown-select bg-orange-light border-2 border-gray-600 font-bold py-2 px-4 rounded-lg"
            >
                {sessions.map((session, index) => (
                    <option key={index} value={`${session.session} ${session.annee}`}>
                        {t(session.session)} {session.annee}
                    </option>
                ))}
            </select>
        </div>
    );
};

export default FiltreSession;
