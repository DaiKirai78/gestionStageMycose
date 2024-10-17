import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

const NavbarMobile = ({ mobileMenuRef, userInfo, navLinks, toggleNotificationMenu, handleProfileItemClick }) => {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <div className="md:hidden" ref={mobileMenuRef}>
            <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
                <button
                    onClick={() => {
                        navigate("/accueil");
                    } }
                    className={`w-full text-left hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === "/accueil" ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                    Accueil
                </button>
                {userInfo ? navLinks[userInfo.role].map((infoBtn, index) => {
                    return (
                        <button
                            key={"nav" + index}
                            onClick={() => {
                                navigate(infoBtn["lien"]);
                            } }
                            className={`w-full text-left hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === infoBtn["lien"] ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                            {infoBtn["titre"]}
                        </button>
                    );
                }) : ""}
            </div>
            <div className="pt-4 pb-3 border-t border-gray-700">
                <div className="flex items-center pl-2 pr-5">
                    <div className="ml-3">
                        <div className="text-base font-medium leading-none text-black">{userInfo ? `${userInfo.prenom} ${userInfo.nom}` : "Attente"}</div>
                        <div className="font-medium leading-none text-gray-600">{userInfo ? userInfo.courriel : "Attente"}</div>
                    </div>
                    <button
                        onClick={toggleNotificationMenu}
                        className="ml-auto flex-shrink-0 p-2 rounded-full text-orange hover:text-orange-light hover:bg-orange">
                        <BsBellFill className="h-6 w-6" />
                        {isNotificationMenuOpen && (
                            <div className="origin-top-right absolute right-5 mt-4 w-48 rounded-md shadow-lg p-1 bg-orange-light ring-1 ring-orange ring-opacity-40">
                                <p className="px-4 py-2 text-black">Aucune notification pour le moment</p>
                            </div>
                        )}
                    </button>
                </div>
                <div className="mt-3 px-2 space-y-1">
                    <button
                        onClick={e => {
                            handleProfileItemClick(e);
                            navigate("/profil");
                        } }
                        className={`w-full text-left px-3 py-2 rounded-md text-base font-medium hover:bg-orange hover:bg-opacity-20 ${location.pathname === "/profil" ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                        Your Profile
                    </button>
                    <button
                        onClick={handleProfileItemClick}
                        className="w-full text-left px-3 py-2 rounded-md text-base font-medium hover:bg-orange hover:bg-opacity-20">
                        Sign out
                    </button>
                </div>
            </div>
        </div>
    )
}

export default NavbarMobile;