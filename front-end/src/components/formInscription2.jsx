import {Input} from '@material-tailwind/react';
import Divider from './divider';
import { useState } from "react";
import InputErrorMessage from './inputErrorMesssage';

function FormInscription2({email, setEmail, telephone, setTelelphone, setStep}) {

    const validePhone = new RegExp(String.raw`[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}`)
    const valideEmail = new RegExp(String.raw`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$`);

    const [errorKeyEmail, setErrorKeyEmail] = useState('');
    const [errorKeyTelephone, setErrorKeyTelephone] = useState('');

    function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            console.log("Erreur Formulaire 2")
            return;
        }

        setStep("troisiemeEtape");

        console.log("c good")        
        //verifierEtudiantExiste();
    }

    // async function verifierEtudiantExiste() {
    //     const res = await fetch(`http://localhost:8080/etudiant/register/check-for-conflict/${email}_${telephone}`, {
    //         method: 'GET',
    //         headers: {
    //             'Content-type': 'application/json',
    //         }
    //     });
    // }

    function validerChamps() {
        const emailValid = validerEmail();
        const phoneValid = validerPhone();

        return emailValid && phoneValid;
    }

    function validerEmail() {
        if(!valideEmail.test(email)) {
            setErrorKeyEmail("L'email est invalide")
            return false;
        }
        return true;
    }

    function validerPhone() {
        if(!validePhone.test(telephone)) {
            setErrorKeyTelephone("Le téléphone est invalide")
            return false;
        }
        return true;
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
        setErrorKeyEmail("")
    }

    function changeTelephoneValue(e) {
        setTelelphone(e.target.value);
        setErrorKeyTelephone("")
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
                            <InputErrorMessage messageKey={errorKeyEmail}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Téléphone" color='black' size='lg'
                            onChange={(e) => {changeTelephoneValue(e);}}
                            type='tel'/>
                            <InputErrorMessage messageKey={errorKeyTelephone}/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px]' onClick={onNext}>S'inscrire</button>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">2/3</p>
                <Divider texte={"Déjà un compte ?"}/>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
            </div>	
        </>
    );
};

export default FormInscription2;