import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import SignerContratCanvas from './signerContratCanvas';
import { IoCloseSharp } from 'react-icons/io5';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from '../inputErrorMesssage';
import { FaRegEye, FaRegEyeSlash } from 'react-icons/fa';

const SignerContrat = ({ selectedContract, setSelectedContract }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const canvasRef = useRef();
    const [errorKeyMdp, setErrorKeyMdp]= useState("");
    const [errorKeySignature, setErrorKeySignature]= useState("");
    const [password, setPassword]= useState("");
    const [isPasswordHidden, setIsPasswordHidden]= useState(true);
    const [drewSomething, setDrewSomething] = useState(false);

    const validPassword = new RegExp(
        "[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}"
    );

    useEffect(() => {
        if (!selectedContract) {
            navigate("/contrats");
            return;
        }
    }, [])

    async function sendSignature() {
        if (!drewSomething) {
            setErrorKeySignature("erreurNoSignature");
            return;
        }
    
        if (!validPassword.test(password)) {
            setErrorKeyMdp("errorMessagePassword");
            return;
        }
    
        const token = localStorage.getItem("token");
    
        try {            
            let formData = new FormData();
            const signaturePngbase64 = await getBase64CanvasPng();            

            formData.append("signature", dataURLtoFile(signaturePngbase64, "signature.png"));
    
            const response = await fetch(
                `http://localhost:8080/entreprise/enregistrerSignature?contratId=${selectedContract.id}&password=${password}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                body: formData
            });
    
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
    
            const data = await response.text();
    
            if (!data) {
                setErrorKeyMdp("wrongPassword")
                throw new Error('No data');
            }
            setSelectedContract(null);
            navigate("/contrats");
        } catch (e) {
            console.log("Une erreur est survenue " + e);
        }
    }

    function dataURLtoFile(dataurl, filename) {
        var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, {type:mime});
    }

    function getBase64CanvasPng() {
        return canvasRef.current
            .exportImage("png")
            .then(data => {
                return data
            })
            .catch(e => {
                console.log(e);
            });
    }

    function changePasswordValue(e) {
        setPassword(e.target.value);
        setErrorKeyMdp("")
    }

    return (
        selectedContract ?
        <div className='w-full h-full bg-orange-light flex flex-col items-center p-8'>
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signContractOf")} {selectedContract.nomPrenom}</h1>
            <h1 className='mb-2'>{t("signature")} :</h1>
            <SignerContratCanvas 
                canvasRef={canvasRef} 
                setDrewSomething={setDrewSomething} 
                errorKeySignature={errorKeySignature} 
                setErrorKeySignature={setErrorKeySignature}/>
            <div className='w-full'>
                <InputErrorMessage messageKey={errorKeySignature} />
            </div>
            <button
                className="p-2 bg-orange hover:bg-opacity-90 text-white rounded mt-3 mb-5"
                onClick={() => {
                    canvasRef.current.clearCanvas();
                    setDrewSomething(false);
                    setErrorKeySignature("");
                }}
            >
                <IoCloseSharp />
            </button>
            <Input label={t("password")} 
                    color='black' 
                    className='bg-white'
                    onChange={(e) => {
                        changePasswordValue(e);
                    }}
                    type={isPasswordHidden ? "password" : "text"}
                    autoComplete='on'
                    value={password}
                    error={errorKeyMdp.length > 0}
                    icon={
                        <button onClick={(e) => {
                            e.preventDefault();
                            setIsPasswordHidden(!isPasswordHidden)
                        }}>
                            {
                                isPasswordHidden ? 
                                <FaRegEyeSlash />
                                :
                                <FaRegEye />
                            }
                        </button>
                    }
            />
            <div className='w-full'>
                <InputErrorMessage messageKey={errorKeyMdp} />
            </div>
            <button 
                className='p-2 bg-orange hover:bg-opacity-90 text-white rounded mt-5'
                onClick={sendSignature}>
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