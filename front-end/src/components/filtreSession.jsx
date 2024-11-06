import React, { useEffect, useState } from 'react';
import { Button, Checkbox } from '@material-tailwind/react';
import { useTranslation } from 'react-i18next';

const FiltreSession = ({ annee, setAnnee, session, setSession}) => {
    const { t } = useTranslation();
    const [sessions, setSessions] = useState([]);
    const [annees, setAnnees] = useState([]);
    const [showFilters, setShowFilters] = useState(false);

    useEffect(() => {
        const fetchSessions = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/offres-stages/sessions");
                const data = await response.json();
                setSessions(data);
            } catch (error) {
                console.error('Erreur lors du chargement des sessions:', error);
            }
        };

        const fetchYears = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/offres-stages/years");
                const data = await response.json();
                setAnnees(data);
            } catch (error) {
                console.error('Erreur lors du chargement des années:', error);
            }
        };

        fetchSessions();
        fetchYears();
    }, []);

    const clearFilters = () => {
        setAnnee("");
        setSession("");
    }

    return (
        <div className="p-4 text-right">
            {/* Bouton pour afficher ou masquer les filtres */}
            <button
                onClick={() => setShowFilters(!showFilters)}
                className="text-black-500 font-bold mb-4"
            >
                {showFilters ? t("masquerFiltres") : t("afficherFiltres")}
                <span className="ml-1 text-xl">
                    {showFilters ? '🠕' : '🠗'} {/* Flèche vers le haut ou vers le bas */}
                </span>
            </button>

            {/* Contenu des filtres, masqué ou affiché selon l'état */}
            {showFilters && (
                <div>
                    <div className="mb-4 justify-items-start">
                        <h3 className="font-semibold">{t("annees")}</h3>
                        {annees.map((year) => (
                            <Checkbox
                                key={year}
                                label={year}
                                checked={annee === year.toString()}
                                onChange={() => {
                                    // Toggle the selection of the year
                                    setAnnee(annee === year.toString() ? "" : year.toString());
                                }}
                            />
                        ))}
                    </div>

                    <div className="mb-4 justify-items-start">
                        <h3 className="font-semibold">{t("sessions")}</h3>
                        {sessions.map((sess) => (
                            <Checkbox
                                key={sess}
                                label={t(sess)}
                                checked={session === sess}
                                onChange={() => {
                                    // Toggle the selection of the session
                                    setSession(session === sess ? "" : sess);
                                }}
                            />
                        ))}
                    </div>

                    {/* Bouton pour effacer tous les filtres */}
                    {(annee || session) && (
                        <div className="text-left mb-4">
                            <Button
                                onClick={clearFilters}
                                className="bg-gray-300 hover:bg-gray-400 text-black py-2 px-3 text-sm normal-case shadow-none rounded-md"
                            >
                                {t("eraseFilters")}
                            </Button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default FiltreSession;