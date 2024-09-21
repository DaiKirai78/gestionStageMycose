import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";
import {sha256} from 'js-sha256';

function FormInscription3({prenom, nom, email, telephone}) {
    const [password, setPassword] = useState('');
    const [passwordConf, setPasswordConf] = useState('');

    const [errorKeyPassword, setErrorKeyPassword] = useState('');
    const validePassword = new RegExp(String.raw`[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}`);

    async function onSumbit(e) {
        e.preventDefault();

        if(!validerPasswordsInputs()) {
            console.log("Erreur Form Inscription 3");
            return;
        }

        const passwordHash = sha256.create().update(password).hex();
        
        reponseStatus = await envoyerInfos(passwordHash);
        console.log(reponseStatus);
            
        // faire post
        // if post successful(réponse ok), renvoyer acceuil
    }

    async function envoyerInfos(passwordHash) {
        console.log(telephone);
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
    }

    function changePasswordConfValue(e) {
        setPasswordConf(e.target.value);
        setErrorKeyPassword("");
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
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