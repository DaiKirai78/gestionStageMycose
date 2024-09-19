import React from 'react';
import { useTranslation } from "react-i18next"

const InputErrorMessage = ({ messageKey }) => {

    const { t } = useTranslation()

    return (
        <div className='px-2'>
            <p className={`${messageKey.length > 0 ? "" : "hidden"} text-red-600 text-xs`}>{t(messageKey)}</p>
        </div>
    );
};

export default InputErrorMessage;