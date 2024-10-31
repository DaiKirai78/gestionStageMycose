import React, { useEffect, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import PageIsLoading from '../pageIsLoading';
import { useTranslation } from 'react-i18next';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';
import AssignCard from './assignCard';

const AttributionEtudiant = () => {
    const { setSelectedStudent, programme, setProgramme } = useOutletContext();
    const [students, setStudents] = useState();
    const [programmes, setProgrammes] = useState([]);
    const [isFetching, setIsFetching] = useState();
    const { t } = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const navigate = useNavigate();

    useEffect(() => {
        fetchProgrammes()
    }, []);

    useEffect(() => {
        setPages({minPages: 1, maxPages: pages.maxPages === null ? undefined : null, currentPage: 1});
    }, [programme])

    useEffect(() => {
        fetchAll()
    }, [pages.currentPage, pages.maxPages])

    async function fetchProgrammes() {
        try {
            const response = await fetch("http://localhost:8080/api/programme");
            if (response.ok) {
                const data = await response.json();
                setProgrammes(data);
            } else {
                console.error("Erreur lors de la récupération des programmes");
            }
        } catch (e) {
            console.error("Erreur lors de la récupération des programmes " + e);
        }
    }

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
            console.error("Erreur lors de la récupération des élèves " + e);
        }
    }
    
    async function fetchPages() {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `http://localhost:8080/gestionnaire/getEtudiantsPages?programme=${programme}`,
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
            console.error("Erreur lors de la récupération des pages " + e);
        }
    }

    function assignerProf(student) {
        setSelectedStudent(student)
        navigate("/attribuer/prof");
    }

    return (
        <div className='w-full min-h-full bg-orange-light flex flex-col items-center p-6 gap-y-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center'>{t("attribuerProfEtudiant")}</h1>
            <select 
                disabled={isFetching}
                className='px-4 py-2 shadow rounded max-w-full'
                name="programmeDropDown" 
                id="programmeDropDown" 
                value={programme} 
                onChange={(e) => setProgramme(e.target.value)}>
                {programmes.map((programme, index) => (
                    <option key={index} value={programme}>
                        {t(programme)}
                    </option>
                ))}
            </select>
            {isFetching ? <PageIsLoading />
            :
            <div>
                <div className='flex flex-col gap-3 mb-5'>
                    {
                        students != null ? students.map((etudiant, index) => {
                            return <AssignCard key={index} action={assignerProf} personne={etudiant} text={t("searchTeacher")}/>
                        })
                        : <p className='text-center'>{t("noStudents")}</p>
                    }
                </div>
                {pages.maxPages ? <BoutonAvancerReculer pages={pages} setPages={setPages} /> : ""}
            </div>
            }
        </div>
    );
};

export default AttributionEtudiant;