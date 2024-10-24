import React, { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';
import TokenPageContainer from './tokenPageContainer';

const AttributionPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    const [selectedStudent, setSelectedStudent] = useState(null);

    return (
        <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
            <div className='bg-orange-light min-h-full'>
                <Outlet context={[selectedStudent, setSelectedStudent]} />
            </div>
        </TokenPageContainer>
    );
};

export default AttributionPage;