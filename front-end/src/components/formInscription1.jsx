import { useState } from "react";
import {Input} from '@material-tailwind/react';

const FormInscription1 = () => {

    // const valideEmail = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$');
    const valideName = new RegExp("[a-zA-Z -'éÉàÀ]");
    //const validePassword = new RegExp("[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}");

    const [prenom, setPrenom] = useState('');
    const [nom, setNom] = useState('');
    const [nomEntreprise, setNomEntreprise] = useState('');

    function onNext(e) {
        e.preventDefault();

        if(!validerChamps()) {
            console.log("Erreur inscription un")
            return;
        }
    }

    function validerChamps() {
        
    }

    function changePrenomValue(e) {
        setPrenom(e.target.value);
    }

    function changeNomValue(e) {
        setNom(e.target.value);
    }

    function changeNomEntrepriseValue(e) {
        setNomEntreprise(e.target.value);
    }

    return (
        <>
            <div className='flex flex-col px-10'>
                <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                    <div>
                        <div className="w-full">
                            <Input label="Prénom" color='black' size='lg' 
                            onChange={(e) => {changeEmailValue(e);}}
                            type='text'
                            />
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Nom" color='black' size='lg'
                            onChange={(e) => {changePasswordValue(e);}}
                            type='text'/>
                        </div>
                    </div>
                    <div>
                        <div className="w-full">
                            <Input label="Nom de l'entreprise" color='black' size='lg'
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

export default FormInscription1;