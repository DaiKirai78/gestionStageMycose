import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';
import EvaluerFormulaire from './evaluerFormulaire';

const forms = [
    {
        id: 'prod',
        title: 'PRODUCTIVITÉ',
        description: 'Capacité d\'optimiser son rendement au travail',
        criteria: [
            { id: 'prodQA', label: 'Planifier et organiser son travail de façon efficace' },
            { id: 'prodQB', label: 'Comprendre rapidement les directives relatives à son travail' },
            { id: 'prodQC', label: 'Maintenir un rythme de travail soutenu' },
            { id: 'prodQD', label: 'Établir ses priorités' },
            { id: 'prodQE', label: 'Respecter ses échéanciers' }
        ]
    },
    {
        id: 'qualTravail',
        title: 'QUALITÉ DU TRAVAIL',
        description: 'Capacité de s’acquitter des tâches sous sa responsabilité en s’imposant personnellement des normes de qualité',
        criteria: [
            { id: 'qualTravailQA', label: 'Respecter les mandats qui lui ont été confiés' },
            { id: 'qualTravailQB', label: 'Porter attention aux détails dans la réalisation de ses tâches' },
            { id: 'qualTravailQC', label: 'Vérifier son travail, s’assurer que rien n’a été oublié' },
            { id: 'qualTravailQD', label: 'Rechercher des occasions de se perfectionner' },
            { id: 'qualTravailQE', label: 'Faire une bonne analyse des problèmes rencontrés' }
        ]
    },
    {
        id: 'qualRel',
        title: 'QUALITÉS DES RELATIONS INTERPERSONNELLES',
        description: 'Capacité d’établir des interrelations harmonieuses dans son milieu de travail',
        criteria: [
            { id: 'qualRelQA', label: 'Établir facilement des contacts avec les gens' },
            { id: 'qualRelQB', label: 'Contribuer activement au travail d’équipe' },
            { id: 'qualRelQC', label: 'S’adapter facilement à la culture de l’entreprise' },
            { id: 'qualRelQD', label: 'Accepter les critiques constructives' },
            { id: 'qualRelQE', label: 'Être respectueux envers les gens' },
            { id: 'qualRelQF', label: 'Faire preuve d’écoute active en essayant de comprendre le point de vue de l’autre' }
        ]
    },
    {
        id: 'habPers',
        title: 'HABILITÉS PERSONNELLES',
        description: 'Capacité de faire preuve d’attitudes ou de comportements matures et responsables',
        criteria: [
            { id: 'habPersQA', label: 'Démontrer de l’intérêt et de la motivation au travail' },
            { id: 'habPersQB', label: 'Exprimer clairement ses idées' },
            { id: 'habPersQC', label: 'Faire preuve d’initiative' },
            { id: 'habPersQD', label: 'Travailler de façon sécuritaire' },
            { id: 'habPersQE', label: 'Démontrer un bon sens des responsabilités ne requérant qu’un minimum de supervision' },
            { id: 'habPersQF', label: 'Être ponctuel et assidu à son travail' }
        ]
    }
];

const EvaluerEtudiantFormulairesList = ({ selectedStudent, setSelectedStudent, userInfo }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [formData, setFormData] = useState(getAllFormCritere());
    const [isFetching, setIsFetching] = useState(false);

    useEffect(() => {
        if (!selectedStudent) {
            navigate("/evaluer");
        }
    }, [selectedStudent, navigate]);

    function getAllFormCritere() {
        const formDataTemp = {};
    
        for (let form of forms) {            
            formDataTemp[form.id] = {};
            
            for (let critere of form.criteria) {
                formDataTemp[form.id][critere.id] = {hasError: false, value: ""};
            }
            formDataTemp[form.id][form.id + "Commentaires"] = {hasError: false, value: ""};
        }
        
        return formDataTemp;
    }

    function getNomPrenom() {
        return selectedStudent ? selectedStudent.prenom + " " + selectedStudent.nom : "";
    }

    if (!selectedStudent) return null;

    const ratingOptions = [
        { value: 'TOTALEMENT_EN_ACCORD', label: 'Totalement en accord' },
        { value: 'PLUTOT_EN_ACCORD', label: 'Plutôt en accord' },
        { value: 'PLUTOT_EN_DESACCORD', label: 'Plutôt en désaccord' },
        { value: 'TOTALEMENT_EN_DESACCORD', label: 'Totalement en désaccord' },
        { value: 'NA', label: 'N/A' }
    ];
    

    function handleRadioChange(formId, criterionId, value) {        
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [criterionId]: {hasError: false, value: value}
            }
        }));
    };

    function handleCommentChange(formId, value) {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [formId + "Commentaires"]: {hasError: false, value: value}
            }
        }));
    };

    function getUriStartString() {
        if (!userInfo.role) Error("Role est null");
             
        switch (userInfo.role) {
            case "EMPLOYEUR":
                return "entreprise";
            case "ENSEIGNANT":
                return "enseignant";
            default:
                Error("Mauvais role")
        }
    }

    function getFormsWithOnlyValue() {
        let modifiedFormData = {}

        for (const [formKey, form] of Object.entries(formData)) {
            let newForm = {}
            for (const [key, value] of Object.entries(form)) {

                newForm = {
                    ...newForm,
                    [key]: value.value
                }
            }
            modifiedFormData = {
                ...modifiedFormData,
                ...newForm
            }
        }

        return modifiedFormData;
    }

    async function sendForm() {
        let [hasError, firstToHaveAnErrorId] = allChampsValide();
        
        if (hasError) {
            scrollToId(firstToHaveAnErrorId)
            console.log("Erreur");
            return;
        }

        setIsFetching(true);
        
        try {
            
            const token = localStorage.getItem("token");

            const body = getFormsWithOnlyValue();

            const response = await fetch(
                `http://localhost:8080/${getUriStartString()}/saveFicheEvaluation?etudiantId=${selectedStudent.id}`,
                    {
                        method: "POST",
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(body)
                    }
            );
    
            if (response.ok) {
                setSelectedStudent(null);
            } else {
                console.log("erreur");
            }
            
        } catch (e) {
            console.log("Une erreur est survenu lors de l'envoie du formulaire: " + e);
        } finally {
            setIsFetching(false);
        }
        
        
    }

    function allChampsValide() {
        let hasError = false;
        let modifiedFormData = {...formData}
        let firstToHaveAnErrorId = null;

        for (const [formKey, form] of Object.entries(formData)) {
            let newForm = {}
            for (const [key, value] of Object.entries(form)) {
                
                if (key.toLowerCase().includes("commentaires")) {

                    newForm = {
                        ...newForm,
                        [key]: value
                    }
                    
                    continue;
                }

                let newValue = value;
                if (!value.value.trim()) {
                    if (!firstToHaveAnErrorId) {
                        firstToHaveAnErrorId = key
                    }                    
                    hasError = true;
                    newValue = {
                        hasError: true,
                        value: ""
                    }
                }

                newForm = {
                    ...newForm,
                    [key]: newValue
                }
            }
            modifiedFormData = {
                ...modifiedFormData,
                [formKey]: {...newForm}
            }
        }

        setFormData(modifiedFormData);
        return [hasError, firstToHaveAnErrorId];
    }

    function scrollToId(id) {
        const element = document.getElementById(id);
    
        if (!element) {
            return;
        }
    
        const elementRect = element.getBoundingClientRect();
        const offset = (window.innerHeight / 2) - (elementRect.height / 2);
    
        window.scrollTo({
            top: window.scrollY + elementRect.top - offset,
            behavior: "smooth"
        });
    }
    

    return (
        <div className='flex flex-col flex-1 items-start sm:items-center bg-orange-light p-8 overflow-x-auto'>
            <PageTitle title={t("remplirFormulaireDe") + getNomPrenom()} />

            {forms.map((form) =>
                <EvaluerFormulaire key={form.id} form={form} 
                    handleCommentChange={handleCommentChange} 
                    handleRadioChange={handleRadioChange}
                    ratingOptions={ratingOptions}
                    formData={formData} />
                )}
            <button
                onClick={sendForm}
                className='bg-orange py-3 px-5 rounded text-white disabled:bg-deep-orange-500 disabled:cursor-default'
                disabled={isFetching}
                >
                {t("sendForm")}
            </button>
        </div>
    );
};

export default EvaluerEtudiantFormulairesList;