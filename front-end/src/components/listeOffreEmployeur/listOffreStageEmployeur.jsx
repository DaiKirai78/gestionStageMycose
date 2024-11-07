import React, { useEffect } from 'react';
import OffreStageCard from './offreStageCard';
import InfoDetailleeOffreStage from './infoDetailleeOffreStage';
import BoutonAvancerReculer from '../listeOffreEmployeur/boutonAvancerReculer.jsx';



const ListOffreStageEmployeur = ({data, voirPdf, setVoirPdf, activeOffer, setActiveOffer, pages, setPages}) => {

    useEffect(() => {
        if (data && data.length > 0) {
            setActiveOffer(data[0]);
        }
    }, [data]);

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
        <div className='min-w-screen bg-orange-light flex gap-5'>
            <div className='flex flex-col gap-2 w-full h-full overflow-auto'>
                {
                    data.map((offre, index) => {
                        return <OffreStageCard key={index} setActiveOffer={setActiveOffer} activeOffer={activeOffer} offre={offre} index={index} getColorOffreStatus={getColorOffreStatus} />;
                    })
                }
                <BoutonAvancerReculer pages={pages} setPages={setPages}/>
            </div>
            <InfoDetailleeOffreStage getColorOffreStatus={getColorOffreStatus} setActiveOffer={setActiveOffer} activeOffer={activeOffer} setVoirPdf={setVoirPdf} voirPdf={voirPdf} />
        </div>
    );
}

export default ListOffreStageEmployeur;