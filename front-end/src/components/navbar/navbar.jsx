import { useState, useEffect, useRef } from 'react'
import { BsBellFill, BsList, BsXLg } from "react-icons/bs";
import { IoMdArrowDown } from "react-icons/io";
import logo from '../../assets/LogoMycose.png'
import { useNavigate, useLocation } from 'react-router-dom';

const navLinks = {
    "ETUDIANT": [],
    "EMPLOYEUR": [
        {
            "titre": "testEmployeur",
            "lien": "/emp"
        }
    ],
    "GESTIONNAIRE_STAGE": [
        {
            "titre": "testGestionnaireStage",
            "lien": "/gest"
        }
    ],
    "ENSEIGNANT": []
}

const Navbar = ({ userInfo }) => {
    const navigate = useNavigate();
    const location = useLocation();

    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false)
    const [isNotificationMenuOpen, setIsNotificationMenuOpen] = useState(false)

    const profileMenuRef = useRef(null)
    const mobileMenuRef = useRef(null)
    const notificationMenuRef = useRef(null)

    const toggleMobileMenu = () => setIsMobileMenuOpen(!isMobileMenuOpen)    
    const toggleProfileMenu = () => setIsProfileMenuOpen(!isProfileMenuOpen)
    const toggleNotificationMenu = () => setIsNotificationMenuOpen(!isNotificationMenuOpen)

    const handleProfileItemClick = (e) => {
        e.preventDefault()
        setIsProfileMenuOpen(false)
    }

    useEffect(() => {
        function handleClickOutside(event) {
            if (profileMenuRef.current && !profileMenuRef.current.contains(event.target)) {
                setIsProfileMenuOpen(false)
            }
            if (notificationMenuRef.current && !notificationMenuRef.current.contains(event.target)) {
                setIsNotificationMenuOpen(false)
            }
        }

        function handleScroll() {
            setIsProfileMenuOpen(false)
            setIsNotificationMenuOpen(false)
        }

        document.addEventListener('mousedown', handleClickOutside)
        window.addEventListener('scroll', handleScroll)

        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
            window.removeEventListener('scroll', handleScroll)
        }
    }, [isMobileMenuOpen])

    function signOut(e) {
        handleProfileItemClick(e);
        navigate("/");
        localStorage.removeItem("token");
    }

    return (
    <nav className="bg-orange-light border-b border-orange border-opacity-40 text-black">
        {console.log(userInfo)}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
            <div className="flex items-center">
            <div className="flex-shrink-0">
                <img src={logo} alt="(Logo)" className='w-10' />
            </div>
            <div className="hidden md:block">
                <div className="ml-10 flex items-baseline space-x-4">
                    <button
                        onClick={() => {
                            navigate("/accueil")
                        }}
                        className={`hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === "/accueil" ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                            Acceuil
                    </button>
                        {
                            userInfo ? navLinks[userInfo.role].map((infoBtn, index) => {
                                return (
                                    <button
                                    key={"nav"+index}
                                        onClick={() => {
                                            navigate(infoBtn["lien"])
                                        }}
                                        className={`hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === infoBtn["lien"] ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                                        {infoBtn["titre"]}
                                    </button>
                                );
                            }) : ""
                        }
                </div>
            </div>
            </div>
            <div className="hidden md:block">
            <div className="ml-4 flex items-center md:ml-6">
                <div className="relative" ref={notificationMenuRef}>
                <button 
                    onClick={toggleNotificationMenu}
                    className="p-2 rounded-full text-orange hover:text-orange-light hover:bg-orange"
                >
                    <BsBellFill className="h-6 w-6" />
                </button>
                {isNotificationMenuOpen && (
                    <div className="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg p-1 bg-orange-light ring-1 ring-orange ring-opacity-40">
                    <p className="px-4 py-2 text-black">Aucune notification pour le moment</p>
                    </div>
                )}
                </div>
                <div className="ml-3 relative" ref={profileMenuRef}>
                <div>
                    <button 
                    onClick={toggleProfileMenu}
                    className="rounded flex items-center"
                    >
                        <div className="h-8 bg-orange rounded flex items-center gap-4 p-2 text-white hover:bg-opacity-90">
                            <p className='overflow-hidden truncate max-w-24'>{userInfo.prenom}</p>
                            <IoMdArrowDown />
                        </div>
                    </button>
                </div>
                {isProfileMenuOpen && (
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
                )}
                </div>
            </div>
            </div>
            <div className="-mr-2 flex md:hidden">
            <button
                onClick={toggleMobileMenu}
                className="inline-flex items-center justify-center p-2 rounded-md text-orange hover:text-white hover:bg-orange"
            >
                {isMobileMenuOpen ? <BsXLg className="block h-6 w-6" /> : <BsList className="block h-6 w-6" />}
            </button>
            </div>
        </div>
        </div>

        {isMobileMenuOpen && (
        <div className="md:hidden" ref={mobileMenuRef}>
            <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            <button 
                onClick={() => {
                    navigate("/accueil")
                }}
                className={`w-full text-left hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === "/accueil" ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                    Accueil
                </button>
                {
                    userInfo ? navLinks[userInfo.role].map((infoBtn, index) => {
                        return (
                            <button
                            key={"nav"+index}
                                onClick={() => {
                                    navigate(infoBtn["lien"])
                                }}
                                className={`w-full text-left hover:bg-orange hover:bg-opacity-20 px-3 py-2 rounded-md font-medium ${location.pathname === infoBtn["lien"] ? "cursor-default ring-1 ring-orange text-orange hover:bg-opacity-0" : ""}`}>
                                {infoBtn["titre"]}
                            </button>
                        );
                    }) : ""
                }
            </div>
            <div className="pt-4 pb-3 border-t border-gray-700">
            <div className="flex items-center pl-2 pr-5">
                <div className="ml-3">
                <div className="text-base font-medium leading-none text-black">{userInfo.prenom} {userInfo.nom}</div>
                <div className="font-medium leading-none text-gray-600">{userInfo.courriel}</div>
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
                        handleProfileItemClick(e)
                        navigate("/profil")
                    }} 
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
        )}
    </nav>
    )
}

export default Navbar;
