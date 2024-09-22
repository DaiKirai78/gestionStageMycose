import {Input} from '@material-tailwind/react';
import Divider from './divider';
import { useState } from "react";
import InputErrorMessage from './inputErrorMesssage';
import { useTranslation } from 'react-i18next';

function FormInscription2({email, setEmail, telephone, setTelephone, setStep}) {

    const validePhone = new RegExp(String.raw`[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}`)
    const valideEmail = new RegExp(String.raw`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$`);
    const getDigitsOnTelephone = new RegExp(String.raw`(\d{3})[- ]?(\d{3})[- ]?(\d{4})`);

    const [errorKeyEmail, setErrorKeyEmail] = useState('');
    const [errorKeyTelephone, setErrorKeyTelephone] = useState('');
    
    const RESPONSE_OK = 200
    const RESPONSE_CONFLICT = 409;
    const RESPONSE_SERVER_ERROR = 500;
    const [errorKeyEtudiantExiste, setErrorKeyEtudiantExiste] = useState('');

    const { t } = useTranslation();

    async function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            return;
        }

        const reponseStatus = await verifierEtudiantExiste();

        if(reponseStatus === RESPONSE_CONFLICT) {
            setErrorKeyEtudiantExiste("userExisteError");
            return;
        }
        
        if(reponseStatus !== RESPONSE_OK) {
            setErrorKeyEtudiantExiste("errorOccurred");
            return;
        }
    
        setStep("troisiemeEtape");
    }


    async function verifierEtudiantExiste() {
        const telFormate = telephone.replace(getDigitsOnTelephone, '$1-$2-$3')
        var res;
        try {
            res = await fetch("http://localhost:8080/etudiant/register/check-for-conflict", {
                method: 'POST',
                headers: {
                        'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    'courriel': email,
                    'telephone': telFormate
                })
            });
        } catch {
            return RESPONSE_SERVER_ERROR;
        }

        setTelephone(telFormate);

        return res.status;
    }

    function resetEtudiantExisteMessage() {
        setErrorKeyEtudiantExiste("");
    }

    function validerChamps() {
        const emailValid = validerEmail();
        const phoneValid = validerPhone();

        return emailValid && phoneValid;
    }

    function validerEmail() {
        if(!valideEmail.test(email)) {
            setErrorKeyEmail("errorMessageEmail")
            return false;
        }
        return true;
    }

    function validerPhone() {
        if(!validePhone.test(telephone)) {
            setErrorKeyTelephone("errorMessageTelephone")
            return false;
        }
        return true;
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
        setErrorKeyEmail("")
        resetEtudiantExisteMessage();
    }

    function changeTelephoneValue(e) {
        setTelephone(e.target.value);
        setErrorKeyTelephone("")
        resetEtudiantExisteMessage();
    }

    function renderMessageErreur() {
        return(
            <div className="text-center mb-5 rounded bg-red-200 py-3 bg-opacity-30">
                <InputErrorMessage messageKey={errorKeyEtudiantExiste}/>
            </div>
        );
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        {(errorKeyEtudiantExiste != "") ? renderMessageErreur() : null}

                        <div className="w-full">
                            <Input label={t("inputLabelEmail")} color='black' size='lg' 
                            onChange={(e) => {changeEmailValue(e);}}
                            type='email'
                            autoFocus="true"
                            error={errorKeyEmail.length > 0}
                            />
                            <InputErrorMessage messageKey={errorKeyEmail}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label={t("telephone")} color='black' size='lg'
                            onChange={(e) => {changeTelephoneValue(e);}}
                            type='tel'
                            error={errorKeyTelephone.length > 0}
                            />
                            <InputErrorMessage messageKey={errorKeyTelephone}/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onNext}>{t("suivant")}</button>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">2/3</p>
                <Divider texte={t("dejaCompte")}/>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white hover:bg-gray-900 hover:shadow-lg'>{t("connexion")}</button>
            </div>	
        </>
    );
};

export default FormInscription2;