import React, { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';

const AttributionPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    const [student, setStudent] = useState(null);

    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <p>HEELLO</p>
            <Outlet context={[student, setStudent]} />
        </TokenPageContainer>
    );
};

export default AttributionPage;