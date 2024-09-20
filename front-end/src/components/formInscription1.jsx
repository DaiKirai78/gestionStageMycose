import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";

function FormInscription1({prenom, nom, setPrenom, setNom, setStep}) {

    const [errorKeyPrenom, setErrorKeyPrenom] = useState('');
    const [errorKeyNom, setErrorKeyNom] = useState('');

    // const valideEmail = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$');
    const valideName = new RegExp(String.raw`\D`);
    //const validePassword = new RegExp("[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}");

    function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            return;
        }

        setStep('deuxiemeEtape');
    }

    function validerChamps() {
        const prenomValide = verifierPrenom();
        const nomValide = verifierNom();

        return prenomValide && nomValide;
    }

    function verifierPrenom() {
        if(!valideName.test(prenom)) {
            setErrorKeyPrenom("Le prénom est invalide");
            return false
        }
        return true
    }

    function verifierNom() {
        if(!valideName.test(nom)) {
            setErrorKeyNom("Le nom est invalide");
            return false
        }
        return true
    }

    function changePrenomValue(e) {
        setPrenom(e.target.value);
        setErrorKeyPrenom("");
    }

    function changeNomValue(e) {
        setNom(e.target.value);
        setErrorKeyNom("");
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        <div className="w-full">
                            <Input label="Prénom" color='black' size='lg' 
                            onChange={(e) => {changePrenomValue(e);}}
                            type='text'
                            />
                            <InputErrorMessage messageKey={errorKeyPrenom}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Nom" color='black' size='lg'
                            onChange={(e) => {changeNomValue(e);}}
                            type='text'/>
                            <InputErrorMessage messageKey={errorKeyNom}/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px]' onClick={onNext}>Suivant</button>
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">1/3</p>
                <Divider texte={"Déjà un compte ?"}/>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
               
            </div>
        </>
    );
}

export default FormInscription1;