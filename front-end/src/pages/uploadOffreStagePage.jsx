import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadOffreStage from "../components/uploadOffreStagePage/uploadOffreStage.jsx";

const UploadOffreStagePage = () => {
    return (
        <TokenPageContainer role={["EMPLOYEUR"]}>
            <UploadOffreStage />
        </TokenPageContainer>
    );
};

export default UploadOffreStagePage;