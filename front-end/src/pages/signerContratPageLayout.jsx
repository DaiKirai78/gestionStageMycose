import React, { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';

const SignerContratPageLayout = () => {
    const { userInfo, setUserInfo } = useOutletContext();
    const [selectedContract, setSelectedContract] = useState();

    return (
        <div className='w-full h-full flex-1 flex flex-col'>
            <Outlet context={{
                    "userInfo": userInfo,
                    "setUserInfo": setUserInfo,
                    "selectedContract": selectedContract,
                    "setSelectedContract": setSelectedContract
                }} />
        </div>
    );
};

export default SignerContratPageLayout;