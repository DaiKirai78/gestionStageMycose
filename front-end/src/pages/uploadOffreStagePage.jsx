import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import UploadOffreStage from "../components/uploadOffreStagePage/uploadOffreStage.jsx";
import { useOutletContext } from 'react-router-dom';

const UploadOffreStagePage = () => {
    const [roleUser, setRoleUser] = useOutletContext();

    return (
        <TokenPageContainer role={["EMPLOYEUR", "GESTIONNAIRE_STAGE"]} setRoleUser={setRoleUser}>
            <UploadOffreStage />
        </TokenPageContainer>
    );
};

export default UploadOffreStagePage;