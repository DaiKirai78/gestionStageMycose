import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import VoirMonCV from "../components/voirMonCVPage/voirMonCV.jsx";
const VoirMonCVPage = () => {



    return (
        <TokenPageContainer role={["ETUDIANT"]}>
            <div className="flex items-start justify-center min-h-screen">
                <VoirMonCV></VoirMonCV>
            </div>
        </TokenPageContainer>
    );
};

export default VoirMonCVPage;