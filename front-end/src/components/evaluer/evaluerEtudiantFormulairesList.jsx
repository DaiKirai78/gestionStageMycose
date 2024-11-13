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
                formDataTemp[form.id][critere.id] = "";
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
    

    const handleRadioChange = (formId, criterionId, value) => {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [criterionId]: value
            }
        }));
    };

    const handleCommentChange = (formId, value) => {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                commentaires: value
            }
        }));
    };

    return (
        <div className='flex flex-col flex-1 items-center bg-orange-light p-8'>
            <PageTitle title={t("remplirFormulaireDe") + getNomPrenom()} />

            {forms.map((form) => 
                <EvaluerFormulaire key={form.id} form={form} 
                    handleCommentChange={handleCommentChange} 
                    handleRadioChange={handleRadioChange}
                    ratingOptions={ratingOptions}
                    formData={formData} />
                )}
        </div>
    );
};

export default EvaluerEtudiantFormulairesList;