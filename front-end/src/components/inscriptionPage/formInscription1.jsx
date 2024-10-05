import {Input} from '@material-tailwind/react';
import Divider from '../divider.jsx';
import InputErrorMessage from '../inputErrorMesssage.jsx';
import {useEffect, useState} from "react";
import { useTranslation } from 'react-i18next';
import ButtonConnection from '../connectionPage/buttonConnection.jsx';

function FormInscription1({prenom, nom, setPrenom, setNom, setStep, role, setNomOrganisation, nomOrganisation, programme, setProgramme}) {

    const [programmes, setProgrammes] = useState([]);
    const [errorKeyPrenom, setErrorKeyPrenom] = useState('');
    const [errorKeyNom, setErrorKeyNom] = useState('');
    const [errorKeyNomOrganisation, setErrorKeyNomOrganisation] = useState('');

    // const valideEmail = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$');
    const valideName = new RegExp(String.raw`\D`);
    const valideNomOrganisation = new RegExp(String.raw`^[A-Za-zÀ-ÖØ-öø-ÿ0-9'.,&\s-]{2,100}$`);

    const { t } = useTranslation();

    useEffect(() => {
        if (role === "etudiant") {
            fetch('http://localhost:8080/api/programme')
                .then((response) => response.json())
                .then((data) => setProgrammes(data))
                .catch((error) => console.error('Error fetching programmes:', error));
        }
    }, [role]);

    function onNext(e) {
        e.preventDefault();

        console.log("1");
        

        if(!validerChamps()) {
            return;
        }

        console.log("Programme sélectionn:", programme);
        console.log("2");

        setStep('deuxiemeEtape');
    }

    function onReturn() {
        setStep('');
    }

    function validerChamps() {
        const prenomValide = verifierPrenom();
        const nomValide = verifierNom();
        
        if (role === "entreprise") {
            const nomOrganisationValide = verifierNomOrganisation();
            return prenomValide && nomValide && nomOrganisationValide
        }

        return prenomValide && nomValide;
    }

    function verifierPrenom() {
        if(!valideName.test(prenom)) {
            setErrorKeyPrenom("errorMessagePrenom");
            return false
        }
        return true
    }

    function verifierNom() {
        if(!valideName.test(nom)) {
            setErrorKeyNom("errorMessageNom");
            return false
        }
        return true
    }
   
    function verifierNomOrganisation() {
        if(!valideNomOrganisation.test(nomOrganisation)) {
            setErrorKeyNomOrganisation("errorMessageNomOrganisation");
            return false
        }
        return true
    }

    function changePrenomValue(e) {
        setPrenom(e.target.value);
        setErrorKeyPrenom("");
    }
    
    function changeNomOrganisationValue(e) {
        setNomOrganisation(e.target.value);
        setErrorKeyNomOrganisation("");
    }

    function changeNomValue(e) {
        setNom(e.target.value);
        setErrorKeyNom("");
    }

    function ChangeProgrammeValue(e) {
        setProgramme(e.target.value);
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        <div className="w-full">
                            <Input label={t("inputLabelPrenom")} color='black' size='lg' 
                            onChange={(e) => {changePrenomValue(e);}}
                            type='text'
                            autoFocus={true}
                            error={errorKeyPrenom.length > 0}
                            value={prenom}
                            autoComplete='on'
                            />
                            <InputErrorMessage messageKey={errorKeyPrenom}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label={t("inputLabelNom")} color='black' size='lg'
                            onChange={(e) => {changeNomValue(e);}}
                            type='text'
                            error={errorKeyNom.length > 0}
                            value={nom}
                            autoComplete='on'
                            />
                            <InputErrorMessage messageKey={errorKeyNom}/>
                        </div>
                    </div>
                    {
                        role === "entreprise" &&
                        <div>
                            <div className="w-full">
                                <Input label={t("inputLabelNomOrganisation")} color='black' size='lg'
                                onChange={(e) => {changeNomOrganisationValue(e);}}
                                type='text'
                                error={errorKeyNomOrganisation.length > 0}
                                value={nomOrganisation}
                                autoComplete='on'
                                />
                                <InputErrorMessage messageKey={errorKeyNomOrganisation}/>
                            </div>
                        </div>
                    }
                    {role === "etudiant" &&
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-700">{t("choisirProgramme")}</label>
                            <select
                                className="block w-full p-2 border border-gray-300 rounded-md"
                                value={programme}
                                onChange={(e) => {ChangeProgrammeValue(e);}}
                            >
                                <option value="" className={"text-center"}>-- {t("choisirProgramme")} --</option>
                                {programmes.map((programme, index) => (
                                    <option key={index} value={programme}>
                                        {t(programme)}
                                    </option>
                                ))}
                            </select>
                        </div>
                    }
                    <div className='flex justify-center items-center space-x-4'>
                        <button type='button' className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onReturn}>{t("retour")}</button>
                        <button className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' type='submit' onClick={onNext}>{t("suivant")}</button>
                    </div>
                    
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">1/3</p>
                <Divider translateKey={"dejaCompte"}/>
                {/* <button className='p-2 border border-black bg-black rounded-[7px] text-white  hover:bg-gray-900 hover:shadow-lg'>{t("connexion")}</button> */}
                <ButtonConnection/>
            </div>
        </>
    );
}

export default FormInscription1;
