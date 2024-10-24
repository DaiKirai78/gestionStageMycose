import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useOutletContext } from 'react-router-dom';
import AssignCard from './assignCard';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from '../inputErrorMesssage';

const AttributionProf = () => {
    const { selectedStudent } = useOutletContext();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [searchTerm, setSearchTerm] = useState('');
    const [searchTermError, setSearchTermError] = useState(false);
    const [isFetching, setIsFetching] = useState(false);
    const [professeurs, setProfesseurs] = useState(false);

    async function handleSearch() {
        const regex = new RegExp("^[a-zA-Zà-ÿÀ-Ÿ -']+$")
        if (!regex.test(searchTerm.trim())) {
            setSearchTermError("onlyLettersForTeacherSearch");
            return;
        }
        setIsFetching(true)
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `http://localhost:8080/gestionnaire/rechercheEnseignants?search=${searchTerm.trim()}`,
                {
                    method: "POST",
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            if (response.ok) {
                const data = await response.text();                

                if (data) {
                    setProfesseurs(JSON.parse(data));
                } else {
                    setProfesseurs("");
                }
            }
        } catch (e) {
            console.log("Erreur fetch professeur" + e);
            
        } finally {
            setIsFetching(false)
        }
    }

    async function assignerProf(prof) {
        setIsFetching(true)
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `http://localhost:8080/gestionnaire/assignerEnseignantEtudiant?idEtudiant=${selectedStudent.id}&idEnseignant=${prof.id}`,
                {
                    method: "POST",
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            if (response.ok) {
                navigate("/attribuer/eleve")
            }

        } catch (e) {
            console.log("Erreur fetch professeur" + e);
            
        } finally {
            setIsFetching(false)
        }
    }


    return (
        
        !selectedStudent ?
        <div>{t("noStudentSelected")}</div>
        : 
        <div className='w-full min-h-full bg-orange-light flex flex-col items-center p-6 gap-y-8'>
            <div className="w-full max-w-md">
                <h1 className="text-3xl md:text-4xl font-bold text-center mb-8">{t("rechercheProf")}</h1>
                <div className="space-y-4">
                <Input
                    error={searchTermError.length > 0}
                    type="text"
                    label={t("nomPrenomEmail")}
                    className="w-full px-4 py-2 border border-gray-300 bg-white rounded-md"
                    value={searchTerm}
                    onChange={(e) => {
                        setSearchTerm(e.target.value)
                        setSearchTermError("")
                    }}
                />
                <InputErrorMessage messageKey={searchTermError} />
                <button
                    className="w-full px-4 py-2 text-white bg-orange rounded-md hover:bg-orange hover:bg-opacity-90 disabled:cursor-default"
                    onClick={handleSearch}
                    disabled={isFetching}
                >
                    {t("search")}
                </button>
                {
                !isFetching && professeurs &&
                <div className='flex flex-col gap-5'>
                    {
                    professeurs.map((prof, index) => (
                        <AssignCard key={index} action={assignerProf} personne={prof} />
                    ))
                    }
                </div>
                }
                {
                    !isFetching && professeurs === "" ? <p className='text-center'>{t("noTeacherFound")}</p> : ""
                }
                {
                    isFetching ? <p>{t("loading")}</p> : ""
                }
                </div>
            </div>
        </div>
        
    );
};

export default AttributionProf;