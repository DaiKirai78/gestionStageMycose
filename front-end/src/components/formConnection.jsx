import React from 'react';
import Divider from './divider';
import { Input } from '@material-tailwind/react';

const FormConnection = () => {
    return (
        <div className='flex flex-col px-10'>
            <div className='flex flex-col sm:gap-5 gap-3'>
                <div className="w-full">
                    <Input label="Courriel" color='black' size='lg'/>
                </div>
                <div className="w-full">
                    <Input label="Mot de passe" color='black' size='lg'/>
                </div>
            </div>
            <a href="#" className='text-orange mt-2 mb-4 self-end text-xs'>Mot de passe oublié ?</a>
            <button className='border p-2 border-black rounded-[7px]'>Connexion</button>
            <Divider texte="Nouveau sur Mycose ?"/>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white'>S'inscrire</button>
        </div>
    );
};

export default FormConnection;