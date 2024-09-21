import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";
import {sha256} from 'js-sha256';
import {redirect} from "react-router-dom";

function FormInscription3({prenom, nom, email, telephone}) {
    const [password, setPassword] = useState('');
    const [passwordConf, setPasswordConf] = useState('');

    const [errorKeyPassword, setErrorKeyPassword] = useState('');
    const validePassword = new RegExp(String.raw`[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}`);

    const RESPONSE_OK = 200;
    const [errorKeyResponse, setErrorKeyResponse] = useState('');

    async function onSumbit(e) {
        e.preventDefault();

        if(!validerPasswordsInputs()) {
            console.log("Erreur Form Inscription 3");
            return;
        }

        const passwordHash = sha256.create().update(password).hex();
        
        const reponseStatus = await envoyerInfos(passwordHash);

        if(reponseStatus != RESPONSE_OK) {
            setErrorKeyResponse("Une erreur est survenue, Code: " + reponseStatus)
        }

        
        redirect("/acceuil");
    }

    async function envoyerInfos(passwordHash) {
        const res = await fetch("http://localhost:8080/etudiant/register", {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                'prenom': prenom,
                'nom': nom,
                'numeroDeTelephone': telephone,
                'courriel': email,
                'motDePasse': passwordHash
            })
        })

        return res.status;
    }

    function validerPasswordsInputs() {
        if(!champsPassRegex()) {
            setErrorKeyPassword("Le mot de passe doit contenir minimum 8 charactères");
        }

        if(!isPasswordsSame()) {
            setErrorKeyPassword("Les mots de passes doivent être les mêmes");
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
                            <Input label="Mot de Passe" color='black' size='lg' 
                            onChange={(e) => {changePasswordValue(e);}}
                            type='password'
                            />
                            <InputErrorMessage messageKey={errorKeyPassword}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Confirmation du mot de passe" color='black' size='lg'
                            onChange={(e) => {changePasswordConfValue(e);}}
                            type='password'/>
                            <InputErrorMessage messageKey={errorKeyPassword}/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px]' onClick={onSumbit}>Suivant</button>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">3/3</p>
                <Divider texte={"Déjà un compte ?"}/>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
               
            </div>
        </>
    );
}


export default FormInscription3;