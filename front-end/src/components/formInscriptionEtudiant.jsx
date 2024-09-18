import React, {useState} from 'react'
import {Input} from '@material-tailwind/react';

const FormInscriptionEtudiant = () => {

    const [prenom, setPrenom] = useState("");
    const [nom, setNom] = useState("");
    const [telephone, setTelephone] = useState("");

    function onInscription(e) {
        e.preventDefault();
        
        console.log(prenom + nom + telephone);
    }

    function changeNomValue(e) {
        setNom(e.target.value);
    }

    function changePrenomValue(e) {
        setPrenom(e.target.value);
    }

    function changeTelephoneValue(e) {
        setTelephone(e.target.value);
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
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Nom" color='black' size='lg'
                            onChange={(e) => {changeNomValue(e);}}
                            type='text'/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Téléphone" color='black' size='lg'
                            onChange={(e) => {changeTelephoneValue(e);}}
                            type='text'/>
                        </div>
                    </div>
                    <button className='border p-2 border-black rounded-[7px]' onClick={onInscription}>S'inscrire</button>
                </form>
                <p>Déjà un compte</p>
                <button className='p-2 border border-black bg-black rounded-[7px] text-white'>Connexion</button>
            </div>	
        </>
    );
};

export default FormInscriptionEtudiant;