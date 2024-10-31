import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import SignerContratCanvas from './signerContratCanvas';
import { IoCloseSharp } from 'react-icons/io5';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from '../inputErrorMesssage';

const SignerContrat = ({ selectedContract }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const canvasRef = useRef();
    const [errorKeyMdp, setErrorKeyMdp]= useState("");

    useEffect(() => {
        if (!selectedContract) {
            navigate("/contrats");
            return;
        }
    }, [])

    function getBase64CanvasPng() {
        return canvasRef.current
            .exportImage("png")
            .then(data => {
            console.log(data);
            })
            .catch(e => {
            console.log(e);
            });
    }

    return (
        selectedContract ?
        <div className='w-full h-full bg-orange-light flex flex-col items-center p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signContractOf")} {selectedContract.nomPrenom}</h1>
            <h1 className='mb-2'>{t("signature")} :</h1>
            <SignerContratCanvas canvasRef={canvasRef}/>
            <button
                className="p-2 bg-orange hover:bg-opacity-90 text-white rounded mt-3 mb-5"
                onClick={() => {
                canvasRef.current.clearCanvas()
                }}
            >
                <IoCloseSharp />
            </button>
            <Input 
                className='bg-white'
                label={t("password")}
                error={errorKeyMdp.length > 0}
                />
            <InputErrorMessage messageKey={errorKeyMdp} />
            <button className='p-2 bg-orange hover:bg-opacity-90 text-white rounded mt-5'>
                {t("envoyerSignature")}
            </button>
        </div>
        :
        <div className='w-full h-full bg-orange-light flex items-center justify-center'>
          <p>X</p>
        </div>
    );
};

export default SignerContrat;