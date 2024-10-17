import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

const ProfilMenu = ({ handleProfileItemClick, signOut }) => {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <div className="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg p-1 bg-orange-light ring-1 ring-orange ring-opacity-40">
            <button
                onClick={e => {
                    (location.pathname != "/profil" && handleProfileItemClick(e))
                    navigate("/profil")
                }} 
                className={`mb-1 w-full text-left px-3 py-2 rounded-md text-base font-medium hover:bg-orange hover:bg-opacity-20 ${location.pathname === "/profil" ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                Profile
            </button>
            <button
                onClick={signOut} 
                className="w-full text-left px-4 py-2 rounded text-black hover:bg-orange hover:bg-opacity-20">
                Sign out
            </button>
        </div>
    )
}

export default ProfilMenu;