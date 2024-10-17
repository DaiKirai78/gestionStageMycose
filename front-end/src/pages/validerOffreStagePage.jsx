import React from 'react';
import TokenPageContainer from "./tokenPageContainer.jsx";
import EmployeursOffreStage from "../components/validerOffreStagePage/employeursOffreStage.jsx";
import { useOutletContext } from 'react-router-dom';

const ValiderOffreStagePage = () => {
    const [roleUser, setRoleUser] = useOutletContext();
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setRoleUser={setRoleUser}>
            <EmployeursOffreStage />
        </TokenPageContainer>
    )
}

export default ValiderOffreStagePage;