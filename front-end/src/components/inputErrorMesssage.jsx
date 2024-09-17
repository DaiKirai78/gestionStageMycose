import React from 'react';
import { useTranslation } from "react-i18next"

const InputErrorMessage = ({ message }) => {

    const { t } = useTranslation()

    return (
        <div className='px-2'>
            <p className={`${message.length > 0 ? "" : "hidden"} text-red-600 text-xs`}>{t(message)}</p>
        </div>
    );
};

export default InputErrorMessage;