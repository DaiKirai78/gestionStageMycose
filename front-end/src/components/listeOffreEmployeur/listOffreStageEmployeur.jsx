import React, { act, useEffect, useState } from 'react';
import OffreStageCard from './offreStageCard';
import InfoDetailleeOffreStage from './infoDetailleeOffreStage';


const ListOffreStageEmployeur = ({data, voirPdf, setVoirPdf}) => {

    const [activeOffer, setActiveOffer] = useState(null);

    useEffect(() => {
        if (!isMediumScreen()) {
            setActiveOffer(data[0]);
        }
    }, []);

    function isMediumScreen() {        
        return screen.width <= 720
    }

    function getColorOffreStatus(status) {
        switch (status) {
            case "ACCEPTED":
                return "text-green-500";

            case "WAITING":
                return "text-yellow-700";

            case "REFUSED":
                return "text-red-600";
        
            default:
                return "text-black";
        }
    }

    return (
        <div className='min-w-screen bg-orange-light flex gap-5 pt-10'>
            <div className='flex flex-col gap-2 w-full h-full overflow-auto'>
                {
                    data.map((offre, index) => {
                        return <OffreStageCard setActiveOffer={setActiveOffer} activeOffer={activeOffer} offre={offre} index={index} getColorOffreStatus={getColorOffreStatus} />;
                    })
                }
            </div>
            <InfoDetailleeOffreStage getColorOffreStatus={getColorOffreStatus} setActiveOffer={setActiveOffer} activeOffer={activeOffer} setVoirPdf={setVoirPdf} voirPdf={voirPdf} />
        </div>
    );
}

export default ListOffreStageEmployeur;