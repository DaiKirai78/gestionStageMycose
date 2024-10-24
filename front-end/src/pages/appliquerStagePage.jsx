import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AppliquerStage from "../components/acceuil/appliquerStage.jsx";
import {useLocation, useOutletContext} from "react-router-dom";

const AppliquerStagePage = () => {

    const [userInfo, setUserInfo] = useOutletContext();
    const location = useLocation();
    const { idStage } = location.state || {};

    return (
        <TokenPageContainer role={["ETUDIANT"]} setUserInfo={setUserInfo}>
            <div className="bg-orange-light w-full min-h-screen">
                <div className="flex h-3/5 justify-center">
                    <AppliquerStage idStage={ idStage }/>
                </div>
            </div>
        </TokenPageContainer>
    );
}

export default AppliquerStagePage;