import React from 'react';
import { useTranslation } from "react-i18next"

const Divider = ({translateKey}) => {

    const { t } = useTranslation()

    return (
        <div className='flex align-middle my-4'>
            <hr className='w-full h-[2px] bg-gris m-auto mr-2' />
            <p className='w-auto text-center text-nowrap text-gris text-xs mx-1'>{t(translateKey)}</p>
            <hr className='w-full h-[2px] bg-gris m-auto ml-2' />
        </div>
    );
};

export default Divider;