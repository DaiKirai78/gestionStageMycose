import { useState } from "react";
import {Input} from '@material-tailwind/react';

const FormInscriptionMain = () => {

    const valideEmail = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$');
    const valideName = new RegExp("[a-zA-Z -'éÉàÀ]");
    const validePassword = new RegExp("[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}");

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [telephone, setTelephone] = useState('');

    function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            console.log("Erreur inscription main")
            return;
        }
    }

    function validerChamps() {
        
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
    }

    function changePasswordValue(e) {
        setTelephone(e.target.value);
    }

    function changeTelephoneValue(e) {
        setPassword(e.target.value);
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
                            <Input label="Mot de Passe" color='black' size='lg'
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
                    <button className='border p-2 border-black rounded-[7px]' onClick={onNext}>Suivant</button>
                </form>
                <p>Déjà un compte?</p>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
            </div>	
        </>
    );
}

export default FormInscriptionMain;