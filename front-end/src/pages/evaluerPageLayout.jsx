import React, { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';

const EvaluerPageLayout = () => {
    const [userInfo, setUserInfo] = useOutletContext();
    const [selectedStudent, setSelectedStudent] = useState();

    return (
        <div className='w-full h-full flex-1 flex flex-col'>
            <Outlet context={{
                    "userInfo": userInfo,
                    "setUserInfo": setUserInfo,
                    "selectedStudent": selectedStudent,
                    "setSelectedStudent": setSelectedStudent
                }} />
        </div>
    );
};

export default EvaluerPageLayout;