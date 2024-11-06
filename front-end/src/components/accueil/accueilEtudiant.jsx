import React from 'react';
import ListStage from "./listeStage.jsx"

const AccueilEtudiant = () => {
    return (
        <div className="bg-orange-light w-full min-h-full flex-1">
            <div className="flex h-3/5 justify-center">
                <ListStage />
            </div>
        </div>
    );
};

export default AccueilEtudiant;