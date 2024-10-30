import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const SignerContrat = ({ selectedContract }) => {
    const navigate = useNavigate();

    useEffect(() => {
        if (!selectedContract) {
            navigate("/contrats");
            return;
        }
    }, [])

    return (
        selectedContract ?
        <div className='w-full h-full bg-orange-light flex flex-col items-center p-8'>
            {selectedContract.etudiantNom}
        </div>
        :
        <div className='w-full h-full bg-orange-light flex items-center justify-center'>
          <p>X</p>
        </div>
    );
};

export default SignerContrat;