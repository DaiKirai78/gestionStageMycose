import {Input} from '@material-tailwind/react';
import Divider from './divider';
import InputErrorMessage from './inputErrorMesssage';
import { useState } from "react";
import { useTranslation } from 'react-i18next';
import ButtonConnexion from './buttonConnexion';

function FormInscription1({prenom, nom, setPrenom, setNom, setStep}) {

    const [errorKeyPrenom, setErrorKeyPrenom] = useState('');
    const [errorKeyNom, setErrorKeyNom] = useState('');

    // const valideEmail = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$');
    const valideName = new RegExp(String.raw`\D`);
    //const validePassword = new RegExp("[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}");

    const { t } = useTranslation();

    function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            return;
        }

        setStep('deuxiemeEtape');
    }

    function onReturn() {
        setStep('');
    }

    function validerChamps() {
        const prenomValide = verifierPrenom();
        const nomValide = verifierNom();

        return prenomValide && nomValide;
    }

    function verifierPrenom() {
        if(!valideName.test(prenom)) {
            setErrorKeyPrenom("errorMessagePrenom");
            return false
        }
        return true
    }

    function verifierNom() {
        if(!valideName.test(nom)) {
            setErrorKeyNom("errorMessageNom");
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
                            <Input label={t("inputLabelPrenom")} color='black' size='lg' 
                            onChange={(e) => {changePrenomValue(e);}}
                            type='text'
                            error={errorKeyPrenom.length > 0}
                            value={prenom}
                            />
                            <InputErrorMessage messageKey={errorKeyPrenom}/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label={t("inputLabelNom")} color='black' size='lg'
                            onChange={(e) => {changeNomValue(e);}}
                            type='text'
                            error={errorKeyNom.length > 0}
                            value={nom}
                            />
                            <InputErrorMessage messageKey={errorKeyNom}/>
                        </div>
                    </div>
                    <div className='flex justify-center items-center space-x-4'>
                        <button type='button' className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' onClick={onReturn}>{t("retour")}</button>
                        <button className='w-1/2 border p-2 border-black rounded-[7px] hover:shadow-lg' type='submit' onClick={onNext}>{t("suivant")}</button>
                    </div>
                    
                </form>
                <p className="text-center mt-3 text-sm text-gray-800">1/3</p>
                <Divider translateKey={"dejaCompte"}/>
                {/* <button className='p-2 border border-black bg-black rounded-[7px] text-white  hover:bg-gray-900 hover:shadow-lg'>{t("connexion")}</button> */}
                <ButtonConnexion/>
            </div>
        </>
    );
}

export default FormInscription1;