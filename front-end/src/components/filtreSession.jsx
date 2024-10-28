import React, { useEffect } from 'react';
import { Input, Option, Select } from '@material-tailwind/react';
import { useTranslation } from 'react-i18next';
import InputErrorMessage from './inputErrorMesssage';

const FiltreSession = ({annee, setAnnee, session, setSession, anneeErrorKey, setAnneeErrorKey}) => {
    
    const { t } = useTranslation()

    const getSaison = () => {
        const month = new Date().getMonth() + 1;
        if (month >= 3 && month <= 5) {
            return "ETE";
        } else if (month >= 6 && month <= 8) {
            return "HIVER";
        } else if (month >= 9 && month <= 11) {
            return "AUTOMNE";
        } else {
            return "HIVER";
        }
    };

    const setCurrentAnnee = () => {
        const currentYear = new Date().getFullYear();
        setAnnee(currentYear.toString());
    };

    useEffect(() => {
        setSession(getSaison);
        setCurrentAnnee();
    }, [])

    function changeAnnee(e) {
        setAnnee(e.target.value);
        setAnneeErrorKey("");
    }

    return (
        <div className='flex gap-5 mb-5 flex-wrap'>
            <div className='flex flex-col w-full'>
                <Input
                    className='w-full'
                    error={anneeErrorKey.length > 0}
                    type="text"
                    label={t("annee")}
                    value={annee}
                    onChange={(e) => {changeAnnee(e)}}
                />
                <InputErrorMessage messageKey={anneeErrorKey} />
            </div>
            <Select
                className='w-full'
                label={t("selectSession")}
                onChange={(value) => {setSession(value)}}
                value={session}
            >
                <Option value="ETE">{t("ete")}</Option>
                <Option value="HIVER">{t("hiver")}</Option>
                <Option value="AUTOMNE">{t("automne")}</Option>
            </Select>
        </div>
    );
};

export default FiltreSession;