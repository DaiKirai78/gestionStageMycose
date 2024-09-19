import React from 'react';

const Divider = ({texte}) => {
    return (
        <div className='flex align-middle my-4'>
            <hr className='w-full h-[2px] bg-gris m-auto mr-2' />
            <p className='w-auto text-center text-nowrap text-gris text-xs mx-1'>{texte}</p>
            <hr className='w-full h-[2px] bg-gris m-auto ml-2' />
        </div>
    );
};

export default Divider;