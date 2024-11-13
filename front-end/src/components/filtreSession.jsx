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
                const allSessionsResponse = await axios.get("http://localhost:8080/api/offres-stages/get-all-sessions");
                const allSessions = allSessionsResponse.data;

                const nextSessionResponse = await axios.get("http://localhost:8080/api/offres-stages/get-next-session");
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
