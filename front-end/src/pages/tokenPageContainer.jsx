import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import verifToken from "../utils/verifTokenValid"

const TokenPageContainer = ({children, role, setUserInfo}) => {
    
    const navigate = useNavigate();
    const [isFetching, setIsFetching] = useState(true)

    let token = localStorage.getItem('token');


    useEffect(() => {
        
        fetchToken()

    }, []);

    async function fetchToken() {
        if (!token) {
            navigate("/");
        }
        
        const is_tokenValid = await verifToken(token, role, setUserInfo);        

        setIsFetching(false)        
        
        if (is_tokenValid.error || !is_tokenValid.userValid) {
            navigate("/");

            return;
        } else if (!is_tokenValid.userWrongPage) {
            navigate("/accueil")
        }
    }

    function showContent() {
        if (isFetching) {
            return  <p>Loading</p>
        }
        
        return (
            <div className='h-full w-full'>
                {children}
            </div>
        );
    }

    return showContent();
};

export default TokenPageContainer;