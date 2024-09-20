import React, {useState} from 'react'
import {Input} from '@material-tailwind/react';
import Divider from './divider';

function FormInscription2({email, setEmail, telephone, setTelelphone}) {

    const validePhone = new RegExp("[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}");

    function onNext(e) {
        e.preventDefault();

        // if(!validerChamps()) {
        //     console.log("ERREUUUUUR!!!")
        //     return;
        // }

        console.log(email);
        console.log(telephone);
        
        
        //inscriptionInfo({prenom, nom, telephone});
    }

    // async function inscriptionInfo(inscriptionInfo) {
    //     const res = await fetch('url', {
    //         method: 'POST',
    //         headers: {
    //             'Content-type': 'application/json',
    //         },
    //         body: JSON.stringify(inscriptionInfo)
    //     });
    // }

    function validerChamps() {
        // const nameValid = validerName();
        // const phoneValid = validerPhone();

        // return nameValid && phoneValid;
    }

    function validerPhone() {
        if(!validePhone.test(telephone)) {
            //setErrorKey
            return false;
        }
        return true;
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
        //setErrorKey
    }

    function changeTelephoneValue(e) {
        setTelelphone(e.target.value);
        //setErrorKey
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
                            <Input label="Téléphone" color='black' size='lg'
                            onChange={(e) => {changeTelephoneValue(e);}}
                            type='tel'/>
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