import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadOffreStage from "../components/uploadOffreStagePage/uploadOffreStage.jsx";
import { useOutletContext } from 'react-router-dom';

const UploadOffreStagePage = () => {
    const [userInfo, setUserInfo] = useOutletContext();

    return (
        <TokenPageContainer role={["EMPLOYEUR", "GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <UploadOffreStage />
        </TokenPageContainer>
    );
};

export default UploadOffreStagePage;