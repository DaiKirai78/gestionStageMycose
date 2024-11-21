import React, {useEffect, useRef, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import PageTitle from '../pageTitle';
import { useTranslation } from 'react-i18next';
import EvaluerFormulaire from './evaluerFormulaire';
import EvaluerFormulaireObsGenerales from './evaluerFormulaireObsGenerales';
import FormulaireInformationsEntreprise from "./formulaireInformationsEntreprise.jsx";
import axios from "axios";

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
            {
                id: 'salaireHoraire',
                label: 'salaireHoraire',
                inputType: 'number'
            },
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
    const canvasRef = useRef();
    const [errorKeySignature, setErrorKeySignature]= useState("");
    const [drewSomething, setDrewSomething] = useState(false);

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
                        formDataTemp[form.id][`evalQHoursMonth${index + 1}`] = getFormValue(0);
                    });
                }
            }
            formDataTemp[form.id][form.id + "Commentaires"] = getFormValue();
        }

        formDataTemp.informationsEntreprise = {
            nomEntreprise: getFormValue(),
            nomPersonneContact: getFormValue(),
            adresseEntreprise: getFormValue(),
            villeEntreprise: getFormValue(),
            codePostalEntreprise: getFormValue(),
            telephoneEntreprise: getFormValue(),
            telecopieurEntreprise: getFormValue(),
            dateDebutStage: getFormValue(),
            numeroStage: getFormValue()
        }

        formDataTemp.observationsGenerales = {
            quart1: { de: "", a: "" },
            quart2: { de: "", a: "" },
            quart3: { de: "", a: "" },
            milieuStage: "",
            nombreStagiaires: "",
            prochainStage: "",
            quartsVariables: ""
        }

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
        const numericValue = value !== '' && !isNaN(value) ? parseFloat(value) : 0;
        setFormData(prev => ({
            ...prev,
            [formId]: {
                ...prev[formId],
                [fieldId]: getFormValue(numericValue)
            }
        }));
    }

    function getFormsWithOnlyValue() {
        let modifiedFormData = {};

        for (const [formKey, form] of Object.entries(formData)) {
            let newForm = {};

            for (const [key, value] of Object.entries(form)) {
                if (key.startsWith('evalQHoursMonth')) {
                    const monthIndex = key.replace('evalQHoursMonth', '') - 1;
                    const newKey = `nombreHeuresParSemaineMois${monthIndex + 1}`;
                    newForm[newKey] = value.value;
                } else {
                    newForm[key] = value.value;
                }
            }
            modifiedFormData[formKey] = { ...newForm };
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

            const formDataToSend = new FormData();

            const body = getFormsWithOnlyValue();

            body.nombreHeuresParSemainePremierMois = formData.eval.evalQHoursMonth1.value;
            body.nombreHeuresParSemaineDeuxiemeMois = formData.eval.evalQHoursMonth2.value;
            body.nombreHeuresParSemaineTroisiemeMois = formData.eval.evalQHoursMonth3.value;
            body.evalQA = formData.eval.evalQA.value;
            body.evalQB = formData.eval.evalQB.value;
            body.evalQC = formData.eval.evalQC.value;
            body.evalQD = formData.eval.evalQD.value;
            body.evalQE = formData.eval.evalQE.value;
            body.evalQF = formData.eval.evalQF.value;
            body.evalQG = formData.eval.evalQG.value;
            body.salaireHoraire = formData.eval.salaireHoraire.value;
            body.evalQH = formData.eval.evalQH.value;
            body.evalQI = formData.eval.evalQI.value;
            body.evalQJ = formData.eval.evalQJ.value;
            body.commentaires = formData.eval.evalCommentaires.value;

            body.nomEntreprise = formData.informationsEntreprise.nomEntreprise.value;
            body.nomPersonneContact = formData.informationsEntreprise.nomPersonneContact.value;
            body.adresseEntreprise = formData.informationsEntreprise.adresseEntreprise.value;
            body.villeEntreprise = formData.informationsEntreprise.villeEntreprise.value;
            body.codePostalEntreprise = formData.informationsEntreprise.codePostalEntreprise.value;
            body.telephoneEntreprise = formData.informationsEntreprise.telephoneEntreprise.value;
            body.telecopieurEntreprise = formData.informationsEntreprise.telecopieurEntreprise.value;

            body.nomStagiaire = selectedStudent.prenom + " " + selectedStudent.nom;

            body.quartTravailDebut1 = formData.observationsGenerales.quart1.de || "";
            body.quartTravailFin1 = formData.observationsGenerales.quart1.a || "";
            body.quartTravailDebut2 = formData.observationsGenerales.quart2.de || "";
            body.quartTravailFin2 = formData.observationsGenerales.quart2.a || "";
            body.quartTravailDebut3 = formData.observationsGenerales.quart3.de || "";
            body.quartTravailFin3 = formData.observationsGenerales.quart3.a || "";

            body.milieuAPrivilegier = formData.observationsGenerales.milieuStage || "";
            body.milieuPretAAccueillirNombreStagiaires = formData.observationsGenerales.nombreStagiaires || "";
            body.milieuDesireAccueillirMemeStagiaire = formData.observationsGenerales.prochainStage || "";
            body.millieuOffreQuartsTravailVariables = formData.observationsGenerales.quartsVariables || "";

            body.dateDebutStage = formData.informationsEntreprise.dateDebutStage.value;
            body.numeroStage = formData.informationsEntreprise.numeroStage.value;

            Object.keys(body).forEach(key => {
                formDataToSend.append(key, body[key]);
            });

            const base64CanvasPng = await getBase64CanvasPng();
            const signatureBlob = await fetch(base64CanvasPng).then(res => res.blob());
            formDataToSend.append("signatureEnseignant", signatureBlob, "signature.png");

            console.log("body", body);

            const response = await axios.post("http://localhost:8080/enseignant/saveFicheEvaluationMilieuStage", formDataToSend, {
                params: {
                    etudiantId: selectedStudent.id
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 200) {
                setSelectedStudent(null);
                navigate("/evaluer");
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
        let modifiedFormData = { ...formData };
        let firstToHaveAnErrorId = null;

        for (const [formKey, form] of Object.entries(formData)) {
            let newForm = {};

            for (const [key, value] of Object.entries(form)) {
                // Ignorer les champs commentaires
                if (key.toLowerCase().includes("commentaires")) {
                    newForm[key] = value;
                    continue;
                }

                let newValue = value;

                // Validation spécifique pour les mois de `evalQHours`
                if (key === 'evalQHours') {
                    continue;
                } else if (key.startsWith('quart')) {
                    if (formData.observationsGenerales.quartsVariables === "OUI") {
                        if (!value) {
                            if (!firstToHaveAnErrorId) {
                                firstToHaveAnErrorId = key;
                            }
                            hasError = true;
                            newValue = getFormValue("", true);
                        }
                    }
                } else if (key === 'telephoneEntreprise' || key === 'telecopieurEntreprise') {
                    if (value && value.value && !/^\d+$/.test(value.value)) {
                        if (!firstToHaveAnErrorId) {
                            firstToHaveAnErrorId = key;
                        }
                        hasError = true;
                        newValue = getFormValue(value.value, true);
                    }

                } else if (key === 'milieuStage' || key === 'nombreStagiaires' || key === 'prochainStage' || key === 'quartsVariables') {
                    if (!value) {
                        if (!firstToHaveAnErrorId) {
                            firstToHaveAnErrorId = key;
                        }
                        hasError = true;
                        newValue = getFormValue("", true);
                    }
                } else if (!value.value) {
                    // Validation générale pour les autres champs
                    if (!firstToHaveAnErrorId) {
                        firstToHaveAnErrorId = key;
                    }
                    hasError = true;
                    newValue = getFormValue("", true);
                }

                newForm[key] = newValue;
            }
            modifiedFormData[formKey] = { ...newForm };
        }

        if (!drewSomething) {
            if (!firstToHaveAnErrorId) {
                firstToHaveAnErrorId = ""
            }
            hasError = true;
            setErrorKeySignature("erreurNoSignature");
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

    function getBase64CanvasPng() {
        return canvasRef.current
            .exportImage("png")
            .then(data => {
                return data
            })
            .catch(e => {
                console.log(e);
            });
    }

    return (
        <div className='flex flex-col flex-1 items-start sm:items-center bg-orange-light p-8 overflow-x-auto'>
            <PageTitle title={t("remplirFormulaireDeMilieuxStage") + getNomPrenom()} />

            <FormulaireInformationsEntreprise
                formData={formData.informationsEntreprise}
                handleChange={(field, value) =>
                    setFormData((prev) => ({
                        ...prev,
                        informationsEntreprise: {
                            ...prev.informationsEntreprise,
                            [field]: getFormValue(value)
                        }
                    }))
                }
            />


            {forms.map((form) =>
                <EvaluerFormulaire key={form.id} form={form}
                                   handleCommentChange={handleCommentChange}
                                   handleNumberChange={handleNumberChange}
                                   handleRadioChange={handleRadioChange}
                                   ratingOptions={ratingOptions}
                                   formData={formData}
                                   role={userInfo.role} />
            )}

            <EvaluerFormulaireObsGenerales
                canvasRef={canvasRef}
                errorKeySignature={errorKeySignature}
                setDrewSomething={setDrewSomething}
                setErrorKeySignature={setErrorKeySignature}
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