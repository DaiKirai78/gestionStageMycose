import React, { useState } from 'react';
import Divider from './divider';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from './inputErrorMesssage';

const FormConnection = () => {

    const validEmail = new RegExp(
        '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$'
    );

    const [email, setEmail] = useState()
    const [errorKeyEmail, setErrorKeyEmail] = useState("")

    function verifierInputs() {
        verifierCourriel()
    }

    function verifierCourriel() {
        if (!validEmail.test(email)) {
            setErrorKeyEmail("errorMessageEmail")
        }
    }

    function changeEmaiLValue(e) {
        setEmail(e.target.value)
        setErrorKeyEmail("")
    }

    return (
        <div className='flex flex-col px-10'>
            <div className='flex flex-col sm:gap-5 gap-3'>
                <div>
                    <div className="w-full">
                        <Input label="Courriel" color='black' size='lg' 
                        onChange={(e) => {changeEmaiLValue(e)}}
                        type='email'
                        />
                    </div>
                    <InputErrorMessage messageKey={errorKeyEmail}/>
                </div>
                <div>
                    <div className="w-full">
                        <Input label="Mot de passe" color='black' size='lg'/>
                    </div>
                </div>
            </div>
            <a href="#" className='text-orange mt-2 mb-4 self-end text-xs'>Mot de passe oublié ?</a>
            <button className='border p-2 border-black rounded-[7px]' onClick={verifierInputs}>Connexion</button>
            <Divider texte="Nouveau sur Mycose ?"/>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white'>S'inscrire</button>
        </div>
    );
};

export default FormConnection;