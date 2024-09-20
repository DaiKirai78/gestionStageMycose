import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";

function FormInscription3() {
    const [password, setPassword] = useState('');
    const [passwordConf, setPasswordConf] = useState('');

    function onSumbit(e) {
        e.preventDefault();

        if(!validerChamps()) {
            console.log("Erreur Form Inscription 3");
            return;
        }
    }

    function validerChamps() {

    }

    function changePasswordValue(e) {

    }

    function changePasswordConfValue(e) {

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
                            <InputErrorMessage messageKey={"errorPlaceholder"}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Confirmation du mot de passe" color='black' size='lg'
                            onChange={(e) => {changePasswordConfValue(e);}}
                            type='password'/>
                            <InputErrorMessage messageKey={"errorPlaceholder"}/>
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