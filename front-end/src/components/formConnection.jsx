import React, { useState } from 'react';
import Divider from './divider';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from './inputErrorMesssage';

const FormConnection = () => {

    const validEmail = new RegExp(
        '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$'
    );

    const validPassword = new RegExp(
        "[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}"
    );

    const [email, setEmail] = useState("");
    const [errorKeyEmail, setErrorKeyEmail] = useState("");

    const [password, setPassword] = useState("");
    const [errorKeyPassword, setErrorKeyPassword] = useState("");
    
    function onLogin(e) {
        e.preventDefault();

        if(!verifierInputs()) {
            return;
        }
        
        sendLoginInfo({email, password});

        setEmail('');
        setPassword('');
    }

    async function sendLoginInfo(loginInfo) {
        //Url temporaire
        const res = await fetch(`urlLoginController/api?email=${loginInfo["email"]}&pass=${loginInfo["email"]}`);

        //Vérification - actions => code erreurs/succès
    }

    function verifierInputs() {
        const emailValide = verifierCourriel()
        const passwordValide = verifierMotDePasse()
        
        return emailValide && passwordValide;
    }

    function verifierCourriel() {
        if (!validEmail.test(email)) {
            setErrorKeyEmail("errorMessageEmail");
            return false;
        }
        return true;
    }

    function verifierMotDePasse() {
        if (!validPassword.test(password)) {
            setErrorKeyPassword("errorMessagePassword");
            return false;
        }
        return true;
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
        setErrorKeyEmail("");
    }

    function changePasswordValue(e) {
        setPassword(e.target.value);
        setErrorKeyPassword("");
    }

    return (
        <div className='flex flex-col px-10'>
            <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                <div>
                    <div className="w-full">
                        <Input label="Courriel" color='black' size='lg' 
                        onChange={(e) => {changeEmailValue(e);}}
                        type='email'
                        />
                        <InputErrorMessage messageKey={errorKeyEmail}/>
                    </div>
                </div>
                <div>
                    <div className="w-full">
                        <Input label="Mot de passe" color='black' size='lg'
                        onChange={(e) => {changePasswordValue(e);}}
                        type='password'/>
                        <InputErrorMessage messageKey={errorKeyPassword}/>
                    </div>
                </div>
                <button className='border p-2 border-black rounded-[7px]' onClick={onLogin}>Connexion</button>
            </form>
            <Divider texte="Nouveau sur Mycose ?"/>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white'>S'inscrire</button>
        </div>
    );
};

export default FormConnection;