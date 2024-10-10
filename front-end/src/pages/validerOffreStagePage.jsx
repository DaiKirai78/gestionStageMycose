import React from 'react';
import TokenPageContainer from "./tokenPageContainer.jsx";
import EmployeursOffreStage from "../components/validerOffreStagePage/employeursOffreStage.jsx";

const ValiderOffreStagePage = () => {
    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]}>
            <EmployeursOffreStage />
        </TokenPageContainer>
    )
}

export default ValiderOffreStagePage;