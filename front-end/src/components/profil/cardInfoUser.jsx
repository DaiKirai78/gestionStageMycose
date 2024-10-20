import React from "react";
import { useTranslation } from "react-i18next";

const CardInfoUser = ({userInfo}) => {
    const { t } = useTranslation();

    return (
        <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-semibold">{t('details')}</h2>
            <hr className="my-2"/>
            <p className="text-lg font-semibold">{userInfo.prenom} {userInfo.nom}</p>
            <p className="mb-2 text-gray-600 text-sm">{userInfo.courriel}</p>
            <div className="flex flex-col gap-2 w-min mt-1">
                {userInfo.programme && <p className="px-3 py-2 border shadow text-sm rounded text-nowrap">{t(userInfo.programme)}</p>}
                <p className="px-3 py-2 border shadow text-sm rounded text-nowrap">{t(userInfo.role)}</p>
            </div>
        </div>
    )
}

export default CardInfoUser;