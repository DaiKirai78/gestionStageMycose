import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';
import EvaluerFormulaire from './evaluerFormulaire';
import AppreciacionFormulaire from './autreInformationsFormulaire';

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

    const [rating, setRating] = useState(getFormValue())
    const [discussion, setDiscussion] = useState(getFormValue())
    const [appreciation, setAppreciation] = useState(getFormValue())
    const [hoursTotal, setHoursTotal] = useState(getFormValue())
    const [futureInternship, setFutureInternship] = useState(getFormValue())
    const [formationGoodEnough, setFormationGoodEnough] = useState(getFormValue())

    useEffect(() => {
        if (!selectedStudent) {
            navigate("/evaluer");
        }
    }, [selectedStudent, navigate]);

    function getFormValue(value = "", hasError = false) {
        return {value: value, hasError: hasError};
    }

    function getAllFormCritere() {
        const formDataTemp = {};
    
        for (let form of forms) {            
            formDataTemp[form.id] = {};
            
            for (let critere of form.criteria) {
                formDataTemp[form.id][critere.id] = getFormValue();
            }
            formDataTemp[form.id][form.id + "Commentaires"] = getFormValue();
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
                [criterionId]: getFormValue(value)
            }
        }));
    };

    function handleCommentChange(formId, value) {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [formId + "Commentaires"]: getFormValue(value)
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
            return;
        }

        setIsFetching(true);
        
        try {
            
            const token = localStorage.getItem("token");

            const body = getFormsWithOnlyValue();
            body.appreciationGlobale = rating.value;
            body.precisionAppreciationReponse = appreciation.value;
            body.discuteeStagiaireReponse = discussion.value;
            body.heuresAccordeStagiaireReponse = hoursTotal.value;
            body.aimeraitAccueillirProchainStage = futureInternship.value;
            body.formationSuffisanteReponse = formationGoodEnough.value;

            

            body.nomEtudiant = selectedStudent.prenom + " " + selectedStudent.nom;
            body.programmeEtude = selectedStudent.programme;
            body.nomEntreprise = userInfo.entrepriseName;
            body.numeroTelephone = userInfo.numeroDeTelephone.replaceAll("-", "");
            body.nomSuperviseur = userInfo.prenom + " " + userInfo.nom;
            body.fonctionSuperviseur = "Employeur";

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
                    newValue = getFormValue("", true)
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

        const allOtherChamps = [
            {getter: rating, setter: setRating, id: "input_ratings"},
            {getter: appreciation, setter: setAppreciation, id: "input_appreciation"},
            {getter: discussion, setter: setDiscussion, id: "input_discussion"},
            {getter: hoursTotal, setter: setHoursTotal, id: "input_hourTotal"},
            {getter: futureInternship, setter: setFutureInternship, id: "input_futureInternship"},
            {getter: formationGoodEnough, setter: setFormationGoodEnough, id: "input_goodEnough"},
        ];

        for (const champ of allOtherChamps) {            
            if (!champ.getter.value) {
                if (!firstToHaveAnErrorId) {
                    firstToHaveAnErrorId = champ.id
                }
                hasError = true;
                champ.setter(getFormValue("", true))
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

            <AppreciacionFormulaire 
                rating={rating} 
                appreciation={appreciation} 
                discussion={discussion} 
                setRating={setRating} 
                setAppreciation={setAppreciation} 
                setDiscussion={setDiscussion} 
                hoursTotal={hoursTotal}
                setHoursTotal={setHoursTotal}
                futureInternship={futureInternship}
                setFutureInternship={setFutureInternship}
                formationGoodEnough={formationGoodEnough}
                setFormationGoodEnough={setFormationGoodEnough}
                getFormValue={getFormValue}
            />

            
            
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