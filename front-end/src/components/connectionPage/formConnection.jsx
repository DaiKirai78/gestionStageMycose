import React, { useState } from 'react';
import Divider from '../divider.jsx';
import { button, Input } from '@material-tailwind/react';
import InputErrorMessage from '../inputErrorMesssage.jsx';
import { useTranslation } from "react-i18next"
import { useNavigate } from 'react-router-dom';
import { FaRegEye, FaRegEyeSlash } from 'react-icons/fa';

const FormConnection = () => {

    const navigate = useNavigate()

    const  ERROR_CODE_UNAUTHORIZED = 401
    const  REPSONSE_CODE_ACCEPTED = 202

    const { t } = useTranslation()

    const validEmail = new RegExp(
        '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$'
    );

    const validPassword = new RegExp(
        "[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}"
    );

    const [email, setEmail] = useState("");
    const [errorKeyEmail, setErrorKeyEmail] = useState("");

    const [password, setPassword] = useState("");
    const [errorKeyPassword, setErrorKeyPassword] = useState("");

    const [isFetching, setIsFetching] = useState(false)
    const [errorKeyResponse, setErrorKeyResponse] = useState(false)

    const [isPasswordHidden, setIsPasswordHidden] = useState();

    function onLogin(e) {
        e.preventDefault();
        emptyErrorKeys()

        if(!verifierInputs()) {
            return;
        }

        sendLoginInfo({email, password});
    }

    async function sendLoginInfo(loginInfo) {
        setIsFetching(true)
        try {
            const res = await fetch("http://localhost:8080/utilisateur/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        "courriel": loginInfo.email.toLowerCase(),
                        "motDePasse": loginInfo.password
                    })
                }
            );

            if (res.status === REPSONSE_CODE_ACCEPTED) {
                const data = await res.json();
                console.log(data);
                
                localStorage.setItem('token', data.accessToken);
                navigate("/accueil");
            } else if (res.status === ERROR_CODE_UNAUTHORIZED) {
                setErrorKeyResponse("wrongEmailOrPassword")
            } else {
                setErrorKeyResponse("errorOccurredNotCode")

            }

        } catch (e) {
            setErrorKeyResponse("errorOccurredNotCode")
            console.log(e);
        }

        setIsFetching(false)
    }

    function goToInscription() {
        navigate("/inscription");
    }

    function verifierInputs() {
        const emailValide = verifierCourriel();
        const passwordValide = verifierMotDePasse();

        return emailValide && passwordValide;
    }

    function verifierCourriel() {
        if (!validEmail.test(email)) {
            setErrorKeyEmail("errorMessageEmail");
            return false;
        }
        return true;
    }

    function verifierMotDePasse() {
        if (!validPassword.test(password)) {
            setErrorKeyPassword("errorMessagePassword");
            return false;
        }
        return true;
    }

    function changeEmailValue(e) {
        setEmail(e.target.value);
        emptyErrorKeys()
    }

    function changePasswordValue(e) {
        setPassword(e.target.value);
        emptyErrorKeys()
    }

    function emptyErrorKeys() {
        setErrorKeyEmail("");
        setErrorKeyPassword("");
        setErrorKeyResponse("");
    }

    function renderMessageErreur() {
        return(
            <div className="text-center mb-5 rounded bg-red-200 py-3 bg-opacity-30">
                <InputErrorMessage messageKey={errorKeyResponse}/>
            </div>
        );
    }

    return (

        <div className='flex flex-col px-10'>
            {(errorKeyResponse != "") ? renderMessageErreur() : null}
            <form method="get" className='flex flex-col sm:gap-5 gap-3'>
                <div>
                    <div className="w-full">
                        <Input
                            label={t("email")}
                            color='black'
                            size='lg'
                            onChange={(e) => {
                                changeEmailValue(e);
                            }}
                            type='email'
                            autoComplete='on'
                            value={email}
                            error={errorKeyEmail.length > 0}
                        />
                        <InputErrorMessage messageKey={errorKeyEmail}/>
                    </div>
                </div>
                <div>
                    <div className="w-full">
                        <Input label={t("password")} color='black' size='lg'
                               onChange={(e) => {
                                   changePasswordValue(e);
                               }}
                               type={isPasswordHidden ? "password" : "text"}
                               autoComplete='on'
                               value={password}
                               error={errorKeyPassword.length > 0}
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
                        <InputErrorMessage messageKey={errorKeyPassword}/>
                    </div>
                </div>
                <button className='border p-2 border-black rounded-[7px] hover:shadow-md text-md' onClick={onLogin} disabled={isFetching}>{isFetching ? t("loading") : t("login")}</button>
            </form>
            <Divider translateKey="newOnMycose"/>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white hover:shadow-lg hover:bg-gray-900 text-md' disabled={isFetching} onClick={goToInscription} >{t("signin")}</button>
        </div>
    );
};

export default FormConnection;