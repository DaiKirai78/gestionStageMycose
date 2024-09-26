import {Input} from '@material-tailwind/react';
import Divider from './divider';
import { useState } from "react";
import InputErrorMessage from './inputErrorMesssage';
import { useTranslation } from 'react-i18next';
import ButtonConnexion from './buttonConnexion';

function FormInscription2({email, setEmail, telephone, setTelephone, setStep, role}) {

    const validePhone = new RegExp(String.raw`^[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}$`)
    const valideEmail = new RegExp(String.raw`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$`);
    const getDigitsOnTelephone = new RegExp(String.raw`(\d{3})[- ]?(\d{3})[- ]?(\d{4})`);

    const [errorKeyEmail, setErrorKeyEmail] = useState('');
    const [errorKeyTelephone, setErrorKeyTelephone] = useState('');
    
    const RESPONSE_OK = 200
    const RESPONSE_CONFLICT = 409;
    const RESPONSE_SERVER_ERROR = 500;
    const [errorKeyUserExiste, setErrorKeyUserExiste] = useState('');

    const { t } = useTranslation();
    let urlRole;

    async function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            return;
        }

        var reponseStatus;

        switch (role) {
            case 'etudiant':
                urlRole = "etudiant";           
                break;
            case 'professeur':
                // return progesseurMethodeVerif
            case 'entreprise':
                urlRole = "entreprise";
                break;
        }
        
        reponseStatus = await verifierUser();

        console.log(reponseStatus);

        if(reponseStatus === RESPONSE_CONFLICT) {
            setErrorKeyUserExiste("userExisteError");
            return;
        }
        
        if(reponseStatus !== RESPONSE_OK) {
            setErrorKeyUserExiste("errorOccurredNotCode");
            return;
        }
    
        setStep("troisiemeEtape");
    }


    async function verifierUser() {
        
        
        const telFormate = telephone.replace(getDigitsOnTelephone, '$1-$2-$3')
        console.log({
            'courriel': email.toLowerCase(),
            'telephone': telFormate
        });
        var res;
        try {
            res = await fetch("http://localhost:8080/" + urlRole + "/register/check-for-conflict", {
                method: 'POST',
                headers: {
                        'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    'courriel': email.toLowerCase(),
                    'telephone': telFormate
                })
            });
        } catch {
            return RESPONSE_SERVER_ERROR;
        }

        setTelephone(telFormate);

        return res.status;
    }

    function onReturn() {
        setStep('premiereEtape');
    }

    function resetEtudiantExisteMessage() {
        setErrorKeyUserExiste("");
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
                <InputErrorMessage messageKey={errorKeyUserExiste}/>
            </div>
        );
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        {(errorKeyUserExiste != "") ? renderMessageErreur() : null}

                        <div className="w-full">
                            <Input label={t("inputLabelEmail")} color='black' size='lg' 
                            onChange={(e) => {changeEmailValue(e);}}
                            type='email'
                            autoFocus={true}
                            error={errorKeyEmail.length > 0}
                            value={email}
                            autoComplete='on'
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
                            value={telephone}
                            autoComplete='on'
                            />
                            <InputErrorMessage messageKey={errorKeyTelephone}/>
                        </div>
                    </div>
                    <div className='flex justify-center items-center space-x-4'>
                        <button type='button' className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onReturn}>{t("retour")}</button>
                        <button className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onNext}>{t("suivant")}</button>
                    </div>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">2/3</p>
                <Divider translateKey={"dejaCompte"}/>
                <ButtonConnexion/>
            </div>	
        </>
    );
};

export default FormInscription2;
