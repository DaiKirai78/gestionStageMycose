import React, { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';

const AttributionPage = () => {
    const { setUserInfo } = useOutletContext();
    const [selectedStudent, setSelectedStudent] = useState(null);
    const [programme, setProgramme] = useState("NOT_SPECIFIED");

    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <div className='bg-orange-light min-h-full flex-1'>
                <Outlet context={{
                    "selectedStudent": selectedStudent,
                    "setSelectedStudent": setSelectedStudent,
                    "programme": programme,
                    "setProgramme": setProgramme
                }} />
            </div>
        </TokenPageContainer>
    );
};

export default AttributionPage;