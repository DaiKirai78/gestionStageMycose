import React, { useEffect } from 'react';


const ListOffreStageEmployeur = ({data}) => {

    useEffect(() => {
        console.log(data);
    }, [])

    function linkCard(offre, index) {
        return offre.format === "file" ? fileCard(offre, index) : formCard(offre, index);
    }

    function fileCard(offre, index) {
        return (
            <div id="elementStage"
                    className=" px-6 flex w-full hover:cursor-pointer flex-col sm:flex-row sm:justify-between pb-3 pt-3 border border-deep-orange-50 rounded">
                <div className='flex items-center justify-center'>
                    <h3 key={index}
                        className="text-black text-xl text-center">{offre.title}</h3>
                </div>
                <button
                    className="bg-orange text-white px-4 py-2 rounded-2xl w-full sm:w-36 hover:bg-orange-dark shadow-md">
                        Voir
                </button>
            </div>
    )
    }

    function formCard(offre, index) {
        return (
            <div id="elementStage"
                className=" px-6 flex w-full hover:cursor-pointer flex-col sm:flex-row sm:justify-between pb-3 pt-3 bg-orange-light border border-deep-orange-50 rounded">
            <div className='flex items-center justify-center'>
                <h3 key={index}
                    className="text-black text-xl text-center">{offre.title}</h3>
            </div>
            <button
                className="bg-orange text-white px-4 py-2 rounded-2xl w-full sm:w-36 hover:bg-orange-dark shadow-md">
                    Voir
            </button>
        </div>
    )
    }

    return (
        <div className='min-w-screen min-h-screen bg-orange-light flex flex-col gap-1'>
            {
                data.map((offre, index) => {
                    return linkCard(offre, index);
                })
            }
        </div>
    );
}

export default ListOffreStageEmployeur;