import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';
import EvaluerFormulaire from './evaluerFormulaire';

const forms = [
    {
        id: 'productivite',
        title: 'PRODUCTIVITÉ',
        description: 'Capacité d\'optimiser son rendement au travail',
        criteria: [
            { id: 'planification', label: 'Planifier et organiser son travail de façon efficace' },
            { id: 'comprehension', label: 'Comprendre rapidement les directives relatives à son travail' },
            { id: 'rythme', label: 'Maintenir un rythme de travail soutenu' },
            { id: 'priorites', label: 'Établir ses priorités' },
            { id: 'echeanciers', label: 'Respecter ses échéanciers' }
        ]
    },
    {
        id: 'qualiteTravail',
        title: 'QUALITÉ DU TRAVAIL',
        description: 'Capacité de s’acquitter des tâches sous sa responsabilité en s’imposant personnellement des normes de qualité',
        criteria: [
            { id: 'mandats', label: 'Respecter les mandats qui lui ont été confiés' },
            { id: 'details', label: 'Porter attention aux détails dans la réalisation de ses tâches' },
            { id: 'verification', label: 'Vérifier son travail, s’assurer que rien n’a été oublié' },
            { id: 'perfectionnement', label: 'Rechercher des occasions de se perfectionner' },
            { id: 'analyse', label: 'Faire une bonne analyse des problèmes rencontrés' }
        ]
    },
    {
        id: 'relationsInterpersonnelles',
        title: 'QUALITÉS DES RELATIONS INTERPERSONNELLES',
        description: 'Capacité d’établir des interrelations harmonieuses dans son milieu de travail',
        criteria: [
            { id: 'contacts', label: 'Établir facilement des contacts avec les gens' },
            { id: 'equipe', label: 'Contribuer activement au travail d’équipe' },
            { id: 'adaptation', label: 'S’adapter facilement à la culture de l’entreprise' },
            { id: 'critiques', label: 'Accepter les critiques constructives' },
            { id: 'respect', label: 'Être respectueux envers les gens' },
            { id: 'ecoute', label: 'Faire preuve d’écoute active en essayant de comprendre le point de vue de l’autre' }
        ]
    },
    {
        id: 'habilitesPersonnelles',
        title: 'HABILITÉS PERSONNELLES',
        description: 'Capacité de faire preuve d’attitudes ou de comportements matures et responsables',
        criteria: [
            { id: 'motivation', label: 'Démontrer de l’intérêt et de la motivation au travail' },
            { id: 'clarte', label: 'Exprimer clairement ses idées' },
            { id: 'initiative', label: 'Faire preuve d’initiative' },
            { id: 'securite', label: 'Travailler de façon sécuritaire' },
            { id: 'responsabilites', label: 'Démontrer un bon sens des responsabilités ne requérant qu’un minimum de supervision' },
            { id: 'ponctualite', label: 'Être ponctuel et assidu à son travail' }
        ]
    }
];

const EvaluerEtudiantFormulairesList = ({ selectedStudent, setSelectedStudent }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [formData, setFormData] = useState(getAllFormCritere());

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
        }        
        
        return formDataTemp;
    }

    function getNomPrenom() {
        return selectedStudent ? selectedStudent.prenom + " " + selectedStudent.nom : "";
    }

    if (!selectedStudent) return null;

    const ratingOptions = [
        { value: 'totalementEnAccord', label: 'Totalement en accord' },
        { value: 'plutotEnAccord', label: 'Plutôt en accord' },
        { value: 'plutotEnDesaccord', label: 'Plutôt en désaccord' },
        { value: 'totalementEnDesaccord', label: 'Totalement en désaccord' },
        { value: 'na', label: 'N/A' }
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
                commentaires: {hasError: false, value: value}
            }
        }));
    };

    function sendForm() {
        let [hasError, firstToHaveAnErrorId] = allChampsValide();
        
        if (hasError) {
            scrollToId(firstToHaveAnErrorId)
            console.log("Erreur");
            return;
        }
        console.log("Succes");
        
        
    }

    function allChampsValide() {
        let hasError = false;
        let modifiedFormData = {...formData}
        let firstToHaveAnErrorId = null;

        for (const [formKey, form] of Object.entries(formData)) {
            let newForm = {}
            for (const [key, value] of Object.entries(form)) {
                if (key === "commentaires") {
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
                className='bg-orange py-3 px-5 rounded text-white'>
                {t("sendForm")}
            </button>
        </div>
    );
};

export default EvaluerEtudiantFormulairesList;