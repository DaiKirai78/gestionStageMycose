import React, { useEffect, useState } from 'react';
import Navbar from './navbar/navbar.jsx';
import { Outlet } from 'react-router-dom';

const Layout = () => {

    const [userInfo, setUserInfo] = useState();

    return (
        <>
            <Navbar userInfo={userInfo} />
            <Outlet context={[userInfo, setUserInfo]}/>
        </>
    );
};

export default Layout;