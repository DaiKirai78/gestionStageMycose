import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadCV from "../components/uploadCVPage/uploadCV.jsx";

const UploadCvPage = () => {
    return (
        <TokenPageContainer>
            <UploadCV />
        </TokenPageContainer>
    );
};

export default UploadCvPage;