import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import verifToken from "../utils/verifTokenValid"

const TokenPageContainer = (props) => {

    const navigate = useNavigate();
    const [isFetching, setIsFetching] = useState(true)

    let token = localStorage.getItem('token');

    useEffect(() => {
        
        const isTokenValid = async () => {
            return await verifToken(token)
        }

        isTokenValid()
            .then(async (tokenValid) => {
                setIsFetching(false)
                if (!tokenValid) {
                    navigate("/");
                    return;
                }
            })
        
    }, [token]);

    function showContent() {
        if (isFetching) {
            return  <p>Loading</p>
        }
        
        return (
            <div>
                {props.children}
            </div>
        );
    }

    return showContent();
};

export default TokenPageContainer;