import React, { useEffect, useState } from 'react';
import { BsArrowRight } from "react-icons/bs";


const ListOffreStageEmployeur = ({data}) => {

    const [activeOffer, setActiveOffer] = useState();

    useEffect(() => {
        setActiveOffer(data[0]);        
    }, []);

    function linkCard(offre, index) {
        return (
            <button id="elementStage" 
                    key={index}
                    onClick={() => {setActiveOffer(offre)}}
                    className="group relative px-6 flex items-center w-full cursor-pointer flex-col pb-3 pt-3 border border-deep-orange-50 rounded">
                        <div className='absolute left-0 top-0 w-full h-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all z-10'>
                        <div className='absolute left-0 top-0 w-full h-full bg-orange opacity-10'></div>
                            <p className='text-2xl text-orange relative ease-in-out flex items-center gap-1'>Détails <BsArrowRight /></p>
                        </div>
                <div className='group-hover:opacity-20'>
                    <div className='flex flex-col gap-1 items-center justify-center'>
                        <h3
                            className="text-black text-xl text-center font-bold">{offre.title} - {offre.format}</h3>
                        <h3
                            className={`text-black text-sm text-center ${getColorOffreStatus(offre.status)}`}>{offre.status}</h3>
                        <h3
                            className="text-black text-md text-center">{offre.updated_at}</h3>
                    </div>
                    <p className='text-center text-sm mt-5 text-orange underline'>Détails</p>
                </div>
            </button>
        )
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
        <div className='min-w-screen bg-orange-light flex gap-5 py-10'>
            <div className='flex flex-col gap-2 w-full h-full overflow-auto'>
                {
                    data.map((offre, index) => {
                        return linkCard(offre, index);
                    })
                }
            </div>
            <div className='bg-orange w-full h-[calc(100vh-1rem)] sticky top-2 flex flex-col'>
                {/* Contenu du deuxième div */}
            </div>
        </div>
    );
}

export default ListOffreStageEmployeur;