import React, { useEffect } from 'react';


const ListOffreStageEmployeur = ({data}) => {

    useEffect(() => {
        console.log(data);
        console.log("LAWS");
        
    }, [])

    return (
        <div>
            {
                data.map(offre => {
                    return (
                        <div>{offre.name}</div>
                    );
                })
            }
        </div>
    );
}

export default ListOffreStageEmployeur;