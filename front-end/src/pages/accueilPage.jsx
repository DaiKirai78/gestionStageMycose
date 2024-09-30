import React from 'react';
import TokenPageContainer from './tokenPageContainer';
import ListeStage from "../components/listeStage.jsx";
const AccueilPage = () => {



    return (
        <TokenPageContainer>
            <div className="bg-orange-light w-full h-full absolute flex flex-col">
                <div className="h-20 border-b-2 border-deep-orange-100 pl-8 items-center flex w-full">
                    (Logo) Mycose
                </div>
                <div className="justify-center flex h-3/5">
                    <ListeStage/>
                </div>
            </div>
        </TokenPageContainer>
    );
};

export default AccueilPage;