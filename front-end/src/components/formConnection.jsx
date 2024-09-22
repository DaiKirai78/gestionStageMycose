import React, { useState } from 'react';
import Divider from './divider';
import { Input } from '@material-tailwind/react';
import InputErrorMessage from './inputErrorMesssage';
import { useTranslation } from "react-i18next"

const FormConnection = () => {

    const  ERROR_CODE_UNAUTHORIZED = 401
    const  REPSONSE_CODE_OK = 200

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
    
    function onLogin(e) {
        e.preventDefault();

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
                        "courriel": "robyking@gmail.com",
                        "motDePasse": "$2a$10$puJ.SglyQgU3SwtZacWxJuWTflHBW5ZieT9oQ5d39mn377T8PLhL2"
                    })
                }
            );

            if (res.status === REPSONSE_CODE_OK) {
                console.log("LETS GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                
            } else if (res.status === ERROR_CODE_UNAUTHORIZED) {
                setErrorKeyResponse("wrongEmailOrPassword")
            } else {
                setErrorKeyResponse("errorOccurredNotCode")
            }

        } catch (e) {
            setErrorKeyResponse("errorOccurredNotCode")
        }

        setIsFetching(false)
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
        setErrorKeyEmail("");
    }

    function changePasswordValue(e) {
        setPassword(e.target.value);
        setErrorKeyPassword("");
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
                        <Input label={t("email")} color='black' size='lg' 
                        onChange={(e) => {changeEmailValue(e);}}
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
                        onChange={(e) => {changePasswordValue(e);}}
                        type='password'
                        autoComplete='on'
                        value={password}
                        error={errorKeyPassword.length > 0}
                        />
                        <InputErrorMessage messageKey={errorKeyPassword}/>
                    </div>
                </div>
                <button className='border p-2 border-black rounded-[7px] hover:shadow-md' onClick={onLogin} disabled={isFetching}>{isFetching ? t("loading") : t("login")}</button>
            </form>
            <Divider translateKey="newOnMycose"/>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white hover:shadow-lg hover:bg-gray-900' disabled={isFetching}>{t("signin")}</button>
        </div>
    );
};

export default FormConnection;