import {Input} from '@material-tailwind/react';
import Divider from '../divider.jsx';
import InputErrorMessage from '../inputErrorMesssage.jsx';
import { useState } from "react";
import { useTranslation } from 'react-i18next';
import ButtonConnection from '../connectionPage/connexionPage/buttonConnection.jsx';
import { useNavigate } from 'react-router-dom';

function FormInscription3({prenom, nom, email, telephone, setStep, role, nomOrganisation}) {
    const [password, setPassword] = useState('');
    const [passwordConf, setPasswordConf] = useState('');

    const [errorKeyPassword, setErrorKeyPassword] = useState('');
    const validePassword = new RegExp(String.raw`[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}`);

    const RESPONSE_OK = 201;
    const  REPSONSE_CODE_ACCEPTED = 202
    const [errorKeyResponse, setErrorKeyResponse] = useState('');

    const { t } = useTranslation();

    const navigate = useNavigate();
    let urlRole;

    async function onSumbit(e) {
        e.preventDefault();

        if(!validerPasswordsInputs()) {
            console.log("Erreur Form Inscription 3");
            return;
        }
        
        let reponseStatus;


        switch (role) {
            case 'etudiant':
                urlRole = "etudiant";
                break;
            case 'professeur':
                urlRole = "enseignant";
                break;
            case 'entreprise':
               urlRole = "entreprise";
               break;
        }

        reponseStatus = await envoyerInfos();

        if(reponseStatus != RESPONSE_OK) {
            setErrorKeyResponse("errorOccurredNotCode")
            return;
        }


           sendLoginInfo({email, password})
    
    }


    async function envoyerInfos() {
        const res = await fetch("http://localhost:8080/" + urlRole + "/register", {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify(getBody())
        })

        return res.status;
    }

    function getBody() {
        let body = {
            'prenom': prenom,
            'nom': nom,
            'numeroDeTelephone': telephone,
            'courriel': email.toLowerCase(),
            'motDePasse': password,
        };

        if (urlRole == "entreprise") {
            body.nomOrganisation = nomOrganisation;
        }

        console.log(body);
        

        return body;
    }

    async function sendLoginInfo(loginInfo) {
        try {
            const res = await fetch("http://localhost:8080/utilisateur/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        "courriel": loginInfo.email.toLowerCase(),
                        "motDePasse": loginInfo.password
                    })
                }
            );

            if (res.status === REPSONSE_CODE_ACCEPTED) {
                const data = await res.json();
                localStorage.setItem('token', data.accessToken);
                navigate("/acceuil")                
            }

        } catch (e) {
            setErrorKeyResponse("errorOccurredNotCode")
            console.log(e);
        }

    }

    function onReturn() {
        setStep("deuxiemeEtape");
    }

    function validerPasswordsInputs() {
        if(!champsPassRegex()) {
            setErrorKeyPassword("errorMessagePassword");
            return false;
        }

        if(!isPasswordsSame()) {
            setErrorKeyPassword("errorPasswordNotSame");
            return false;
        }

        return true;
    }

    function champsPassRegex() {
        const passwordValid = validePassword.test(password);
        const passwordConfValid = validePassword.test(passwordConf);

        return passwordValid && passwordConfValid;
    }

    function isPasswordsSame() {
        return password === passwordConf;
    }

    function changePasswordValue(e) {
        setPassword(e.target.value);
        setErrorKeyPassword("");
        resetResponseError();
    }

    function changePasswordConfValue(e) {
        setPasswordConf(e.target.value);
        setErrorKeyPassword("");
        resetResponseError();
    }

    function resetResponseError() {
        setErrorKeyResponse("");
    }

    function renderMessageErreur() {
        return(
            <div className="text-center mb-5 rounded bg-red-200 py-3 bg-opacity-30">
                <InputErrorMessage messageKey={errorKeyResponse}/>
            </div>
        );
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        {(errorKeyResponse != "") ? renderMessageErreur() : null}
                        <div className="w-full">
                            <Input label={t("inputLabelPassword")} color='black' size='lg' 
                            onChange={(e) => {changePasswordValue(e);}}
                            type='password'
                            autoFocus={true}
                            error={errorKeyPassword.length > 0}
                            value={password}
                            autoComplete='on'
                            />
                            <InputErrorMessage messageKey={errorKeyPassword}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label={t("inputLabelPasswordConfirmation")} color='black' size='lg'
                            onChange={(e) => {changePasswordConfValue(e);}}
                            type='password'
                            error={errorKeyPassword.length > 0}
                            value={passwordConf}
                            autoComplete='on'
                            />
                            <InputErrorMessage messageKey={errorKeyPassword}/>
                        </div>
                    </div>
                    <div className='flex justify-center items-center space-x-4'>
                        <button type='button' className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onReturn}>{t("retour")}</button>
                        <button className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onSumbit}>{t("suivant")}</button>
                    </div>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">3/3</p>
                <Divider translateKey={"dejaCompte"}/>
                <ButtonConnection/>
               
            </div>
        </>
    );
}


export default FormInscription3;
