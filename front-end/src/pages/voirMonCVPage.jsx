import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import VoirMonCV from "../components/voirMonCV.jsx";
const VoirMonCVPage = () => {



    return (
        <TokenPageContainer>
            <div className="flex items-start justify-center min-h-screen p-3">
                <VoirMonCV></VoirMonCV>
            </div>
        </TokenPageContainer>
    );
};

export default VoirMonCVPage;