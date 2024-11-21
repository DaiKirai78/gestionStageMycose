import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';
import EvaluerFormulaire from './evaluerFormulaire';
import EvaluerFormulaireObsGenerales from './evaluerFormulaireObsGenerales';

const forms = [
    {
        id: 'eval',
        title: 'evaluation',
        criteria: [
            { id: 'evalQA', label: 'evalQA' },
            { id: 'evalQB', label: 'evalQB' },
            { id: 'evalQC', label: 'evalQC' },
            {
                id: 'evalQHours',
                label: 'evalQHours',
                inputType: 'number',
                months: ['firstMonth', 'secondMonth', 'thirdMonth']
            },
            { id: 'evalQD', label: 'evalQD' },
            { id: 'evalQE', label: 'evalQE' },
            { id: 'evalQF', label: 'evalQF' },
            { id: 'evalQG', label: 'evalQG' },
            { id: 'evalQH', label: 'evalQH' },
            { id: 'evalQI', label: 'evalQI' },
            { id: 'evalQJ', label: 'evalQJ' }
        ]
    }
];


const EvaluerEtudiantFormulaireEnseignant = ({ selectedStudent, setSelectedStudent, userInfo }) => {
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

                if (critere.id === 'evalQHours') {
                    critere.months.forEach((month, index) => {
                        formDataTemp[form.id][`evalQHoursMonth${index + 1}`] = getFormValue();
                    });
                }
            }
            formDataTemp[form.id][form.id + "Commentaires"] = getFormValue();
        }

        formDataTemp.observationsGenerales = {
            milieuStage: "",
            nombreStagiaires: "",
            prochainStage: "",
            quartsVariables: "",
            quart1: { de: "", a: "" },
            quart2: { de: "", a: "" },
            quart3: { de: "", a: "" },
        };

        return formDataTemp;
    }

    function getNomPrenom() {
        return selectedStudent ? selectedStudent.prenom + " " + selectedStudent.nom : "";
    }

    if (!selectedStudent) return null;

    const ratingOptions = [
        "TOTALEMENT_EN_ACCORD",
        "PLUTOT_EN_ACCORD",
        "PLUTOT_EN_DESACCORD",
        "TOTALEMENT_EN_DESACCORD*",
        "IMPOSSIBLE_DE_SE_PRONONCER"
    ];


    function handleRadioChange(formId, criterionId, value) {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [criterionId]: getFormValue(value)
            }
        }));
    }

    function handleCommentChange(formId, value) {
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [formId + "Commentaires"]: getFormValue(value)
            }
        }));
    }

    function handleNumberChange(formId, fieldId, value) {
        const numericValue = value !== '' ? parseFloat(value) : '';
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [fieldId]: getFormValue(numericValue)
            }
        }));
    }

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

        if (discussion.value !== true && discussion.value !== false) {
            if (!firstToHaveAnErrorId) {
                firstToHaveAnErrorId = "input_discussion"
            }
            hasError = true;
            setDiscussion(getFormValue("", true))
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
                                   handleNumberChange={handleNumberChange}
                                   handleRadioChange={handleRadioChange}
                                   ratingOptions={ratingOptions}
                                   formData={formData}
                                   role={userInfo.role} />
            )}

            {userInfo.role === "ENSEIGNANT" && (
                <EvaluerFormulaireObsGenerales
                    formData={formData.observationsGenerales || {}}
                    handleChange={(field, value) =>
                        setFormData((prev) => ({
                            ...prev,
                            observationsGenerales: {
                                ...prev.observationsGenerales,
                                [field]: value,
                            },
                        }))
                    }
                />
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

export default EvaluerEtudiantFormulaireEnseignant;