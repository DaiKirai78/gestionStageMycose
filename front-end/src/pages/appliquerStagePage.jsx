import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AppliquerStage from "../components/acceuil/appliquerStage.jsx";
import {useLocation} from "react-router-dom";

const appliquerStagePage = () => {
    const location = useLocation();
    const { idStage } = location.state || {};

    return (
        <TokenPageContainer role={["ETUDIANT"]}>
            <div className="bg-orange-light w-full min-h-screen">
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className="flex h-3/5 justify-center">
                    <AppliquerStage idStage={ idStage }/>
                </div>
            </div>
        </TokenPageContainer>
    );
}

export default appliquerStagePage;