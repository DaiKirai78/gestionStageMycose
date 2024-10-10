import TokenPageContainer from "./tokenPageContainer.jsx";
import React from "react";
import AppliquerStage from "../components/appliquerStage.jsx";
import pdfUrl from "/EH-8.pdf";

const appliquerStagePage = (vraiProps) => {
    const props = {
        title: "Software Engineer",
        entrepriseName: "Google",
        location: "Mountain View, CA",
        description: "We are looking for a software engineer to join our team",
        createdAt: "2021-08-01",
        pdfUrl: pdfUrl
    }
    return (
        <TokenPageContainer role={["ETUDIANT"]}>
            <div className="bg-orange-light w-full min-h-screen">
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className="flex h-3/5 justify-center">
                    <AppliquerStage vraiProps={props}/>
                </div>
            </div>
        </TokenPageContainer>
    );
}

export default appliquerStagePage;