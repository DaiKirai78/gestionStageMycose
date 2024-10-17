import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadCV from "../components/uploadCVPage/uploadCV.jsx";
import { useOutletContext } from 'react-router-dom';

const UploadCvPage = () => {
    const [roleUser, setRoleUser] = useOutletContext();
    return (
        <TokenPageContainer role={["ETUDIANT"]} setRoleUser={setRoleUser}>
            <UploadCV />
        </TokenPageContainer>
    );
};

export default UploadCvPage;