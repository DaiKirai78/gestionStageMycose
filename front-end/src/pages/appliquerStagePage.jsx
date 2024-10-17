import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AppliquerStage from "../components/appliquerStage.jsx";
import {useLocation, useOutletContext} from "react-router-dom";

const appliquerStagePage = () => {
    const location = useLocation();
    const { idStage } = location.state || {};
    const [roleUser, setRoleUser] = useOutletContext();

    return (
        <TokenPageContainer role={["ETUDIANT"]} setRoleUser={setRoleUser}>
            <div className="bg-orange-light w-full min-h-screen">
                <div className="flex h-3/5 justify-center">
                    <AppliquerStage idStage={ idStage }/>
                </div>
            </div>
        </TokenPageContainer>
    );
}

export default appliquerStagePage;