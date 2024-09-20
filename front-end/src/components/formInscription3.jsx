import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";

function FormInscription3() {
    const [password, setPassword] = useState('');
    const [passwordConf, setPasswordConf] = useState('');

    const [errorKeyPassword, setErrorKeyPassword] = useState('');
    const validePassword = new RegExp(String.raw`[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}`);

    function onSumbit(e) {
        e.preventDefault();

        if(!validerPasswordsInputs()) {
            console.log("Erreur Form Inscription 3");
            return;
        }

        // hasher password
        // faire post
        // if post successful(réponse ok), renvoyer acceuil
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