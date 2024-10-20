import React from 'react';
import FormConnection from '../components/connectionPage/formConnection.jsx'
import FormContainer from '../components/formContainer'
import ButtonChangeLangConnIncr from '../components/buttonChangeLangConnIncr.jsx';

const ConnectionPage = () => {
    return (
        <>
            <FormContainer>
                <FormConnection/>
            </FormContainer>
            <ButtonChangeLangConnIncr />
        </>
    );
};

export default ConnectionPage;