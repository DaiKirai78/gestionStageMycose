import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadCV from "../components/uploadCVPage/uploadCV.jsx";
import { useOutletContext } from 'react-router-dom';

const UploadCvPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    return (
        <TokenPageContainer role={["ETUDIANT"]} setUserInfo={setUserInfo}>
            <UploadCV />
        </TokenPageContainer>
    );
};

export default UploadCvPage;