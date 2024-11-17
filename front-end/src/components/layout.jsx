import React, { useState } from 'react';
import Navbar from './navbar/navbar.jsx';
import { Outlet } from 'react-router-dom';

const Layout = () => {

    const [userInfo, setUserInfo] = useState();
    const [selectedStudent, setSelectedStudent] = useState();
    
    return (
        <>
            <Navbar userInfo={userInfo} />
            <Outlet context={{
                    "userInfo": userInfo,
                    "setUserInfo": setUserInfo,
                    "selectedStudent": selectedStudent,
                    "setSelectedStudent": setSelectedStudent
                }}/>
        </>
    );
};

export default Layout;