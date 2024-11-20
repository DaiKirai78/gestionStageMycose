import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import EvaluerListEtudiant from './evaluerListEtudiant';
import PageTitle from '../pageTitle';
import PageIsLoading from '../pageIsLoading';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer';

const EvaluerEtudiant = ({ setSelectedStudent, userInfo }) => {
    const { t } = useTranslation();
    const [students, setStudents] = useState();
    const [isFetching, setIsFetching] = useState(true);
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});


    useEffect(() => {
        fetchStudents();
        setIsFetching(false);
    }, [pages.currentPage])

    async function fetchStudents() {
        setIsFetching(true);
        
        try {
            
            const token = localStorage.getItem("token");

            const response = await fetch(
                `http://localhost:8080/entreprise/getAllEtudiantsNonEvalues?employeurId=${userInfo.id}&pageNumber=${pages.currentPage - 1}`,
                    {
                        method: "GET",
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
            );
    
            if (!response.ok) {
                throw Error("Aucun étudiant trouvé")
            }
            
            const data = await response.json();
            
            console.log(data);
            
            setPages((prev) => ({
                ...prev,
                maxPages: data.totalPages
            }));
            setStudents(data.content);
            
        } catch (e) {
            console.log("Une erreur est survenu lors de l'envoie du formulaire: " + e);
        } finally {            
            setIsFetching(false);
        }
    }

    if (isFetching) {
        return (
            <div className='flex flex-1 flex-col items-center justify-center bg-orange-light p-8'>
                <PageIsLoading />
            </div>
        );
    }

    return (
        <div className='flex flex-1 flex-col items-center bg-orange-light p-8'>
            <PageTitle title={t("evaluerEtudiant")} />
            {
                students && students.length > 0 ? 
                <>
                    <EvaluerListEtudiant students={students} setSelectedStudent={setSelectedStudent} destination={"/evaluer/formulaire"} /> 
                    <BoutonAvancerReculer pages={pages} setPages={setPages} margins={"my-10"} />
                </>
                :
                <p>{t("noStudentToEvaluate")}</p>
            }
        </div>
    );
};

export default EvaluerEtudiant;