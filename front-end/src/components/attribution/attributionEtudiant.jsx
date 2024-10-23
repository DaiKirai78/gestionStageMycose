import React, { useEffect, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import PageIsLoading from '../pageIsLoading';
import { useTranslation } from 'react-i18next';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';

const AttributionEtudiant = () => {
    const [selectedStudent, setSelectedStudent] = useOutletContext();
    const [students, setStudents] = useState();
    const [isFetching, setIsFetching] = useState();
    const [programme, setProgramme] = useState("NOT_SPECIFIED");
    const { t } = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const navigate = useNavigate();
    
    useEffect(() => {
        setPages({minPages: 1, maxPages: null, currentPage: 1});
    }, [programme])

    useEffect(() => {
        fetchAll()
    }, [pages.currentPage, pages.maxPages])

    async function fetchAll() {
        setIsFetching(true);
        await fetchEleves()
        await fetchPages()
        setIsFetching(false);
    }

    async function fetchNewPage() {
        setIsFetching(true);
        await fetchEleves()
        setIsFetching(false);
    }



    async function fetchEleves() {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `http://localhost:8080/gestionnaire/getEtudiants?pageNumber=${pages.currentPage - 1}&programme=${programme}`,
                {
                    method: "POST",
                    headers: {Authorization: `Bearer ${token}`}
                }
            );            

            if (response.ok) {
                const data = await response.text();
                
                if (data) {
                    setStudents(JSON.parse(data));
                } else {
                    setStudents(null);
                }
            }
        } catch (e) {
            console.error("Erreur lors de la récupération des élèves" + e);
        }
    }
    
    async function fetchPages() {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `http://localhost:8080/gestionnaire/getEtudiantsPages`,
                {
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            if (response.ok) {
                const data = await response.text();
                
                if (data) {
                    setPages((prev) => ({
                        ...prev,
                        maxPages: JSON.parse(data)
                    }));
                } else {
                    setStudents(null);
                }
            }
        } catch (e) {
            console.error("Erreur lors de la récupération des élèves" + e);
        }
    }

    function assignerProf(student) {
        setSelectedStudent(student)
        navigate("/attribuer/prof");
    }

    return (
        <div className='w-full min-h-full bg-orange-light flex flex-col items-center p-6 gap-y-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center'>{t("attribuer")}</h1>
            <select 
                disabled={isFetching}
                className='px-4 py-2 mx-auto shadow rounded'
                name="programmeDropDown" 
                id="programmeDropDown" 
                value={programme} 
                onChange={(e) => setProgramme(e.target.value)}>
                <option value="NOT_SPECIFIED">{t("NOT_SPECIFIED")}</option>
                <option value="TECHNIQUE_INFORMATIQUE">{t("TECHNIQUE_INFORMATIQUE")}</option>
                <option value="GENIE_LOGICIEL">{t("GENIE_LOGICIEL")}</option>
                <option value="RESEAU_TELECOMMUNICATION">{t("RESEAU_TELECOMMUNICATION")}</option>
            </select>
            {isFetching ? <PageIsLoading />
            :
            <div>
                <div className='mb-5'>
                    {
                        students != null ? students.map(etudiant => (
                            <div key={etudiant.id} className="max-w-md mx-auto bg-white shadow-sm rounded-lg overflow-hidden">
                                <div className="px-4 py-3 flex items-center justify-between">
                                    <div className="flex items-center space-x-3">
                                    <div className="flex flex-col">
                                        <span className="text-md font-medium text-gray-900">{etudiant.prenom} {etudiant.nom}</span>
                                        <span className="text-sm mt-[-5px] text-gray-500">{etudiant.courriel}</span>
                                    </div>
                                    </div>
                                    <button 
                                        onClick={() => {assignerProf(etudiant)}}
                                        className="px-4 py-2 ml-8 bg-orange-500 text-white text-sm font-medium rounded-md bg-orange hover:bg-orange hover:bg-opacity-90">
                                    {t("attribuer")}
                                    </button>
                                </div>
                            </div>
                        ))
                        : <p className='text-center'>{t("noStudents")}</p>
                    }
                </div>
                {pages.maxPages && <BoutonAvancerReculer pages={pages} setPages={setPages} />}
            </div>
            }
        </div>
    );
};

export default AttributionEtudiant;