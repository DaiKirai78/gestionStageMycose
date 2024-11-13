import React, { useEffect, useState } from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";

const FiltreSession = ({ setAnnee, setSession }) => {
    const [sessions, setSessions] = useState([]);
    const [selectedSession, setSelectedSession] = useState(null);

    const {t} = useTranslation();

    useEffect(() => {
        const fetchSessions = async () => {
            try {
                // Récupère toutes les sessions disponibles
                const allSessionsResponse = await axios.get("http://localhost:8080/api/offres-stages/get-all-sessions");
                const allSessions = allSessionsResponse.data;

                // Récupère la prochaine session et la sélectionne par défaut
                const nextSessionResponse = await axios.get("http://localhost:8080/api/offres-stages/get-next-session");
                const nextSession = nextSessionResponse.data;

                // Ajoute la prochaine session à la liste si elle n'est pas déjà incluse
                const sessionsList = allSessions.some(
                    session => session.session === nextSession.session && session.annee === nextSession.annee
                ) ? allSessions : [...allSessions, nextSession];

                setSessions(sessionsList);
                setSelectedSession(nextSession);

                // Met à jour les filtres dans le composant parent
                setAnnee(nextSession.annee);
                setSession(nextSession.session);
            } catch (error) {
                console.error("Erreur lors de la récupération des sessions:", error);
            }
        };

        fetchSessions();
    }, [setAnnee, setSession]);

    const handleSessionChange = (event) => {
        const selected = sessions.find(
            session => `${session.session} ${session.annee}` === event.target.value
        );
        setSelectedSession(selected);
        setAnnee(selected.annee);
        setSession(selected.session);
    };

    return (
        <div className="dropdown">
            <select
                id="sessionDropdown"
                value={`${selectedSession?.session || ""} ${selectedSession?.annee || ""}`}
                onChange={handleSessionChange}
                className="dropdown-select"
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
