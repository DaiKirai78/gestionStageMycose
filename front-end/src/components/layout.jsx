import React, { useState } from 'react';
import Navbar from './navbar';
import { Outlet } from 'react-router-dom';

const Layout = () => {

    const [roleUser, setRoleUser] = useState();

    return (
        <>
            <Navbar roleUser={roleUser} />
            <Outlet context={[roleUser, setRoleUser]}/>
        </>
    );
};

export default Layout;