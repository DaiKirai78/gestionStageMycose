import { useState } from "react";
import {Input} from '@material-tailwind/react';

const FormInscriptionMain = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [telephone, setTelephone] = useState('');

    function onNext(e) {

    }

    function changeEmailValue(e) {

    }

    function changePasswordValue(e) {

    }

    function changeTelephoneValue(e) {

    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        <div className="w-full">
                            <Input label="Courriel" color='black' size='lg' 
                            onChange={(e) => {changeEmailValue(e);}}
                            type='email'
                            />
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Nom" color='black' size='lg'
                            onChange={(e) => {changePasswordValue(e);}}
                            type='password'/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Téléphone" color='black' size='lg'
                            onChange={(e) => {changeTelephoneValue(e);}}
                            type='tel'/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px]' onClick={onNext}>S'inscrire</button>
                </form>
                <p>Déjà un compte</p>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
            </div>	
        </>
    );
}

export default FormInscriptionMain;