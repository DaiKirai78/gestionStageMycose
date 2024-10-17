import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import VoirMonCV from "../components/voirMonCV.jsx";
import { useOutletContext } from 'react-router-dom';
const VoirMonCVPage = () => {

    const [roleUser, setRoleUser] = useOutletContext();

    return (
        <TokenPageContainer role={["ETUDIANT"]} setRoleUser={setRoleUser}>
            <div className="flex items-start justify-center min-h-screen p-3">
                <VoirMonCV></VoirMonCV>
            </div>
        </TokenPageContainer>
    );
};

export default VoirMonCVPage;