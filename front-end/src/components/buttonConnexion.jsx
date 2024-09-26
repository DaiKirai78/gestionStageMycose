import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

function ButtonConnexion() {
    const { t } = useTranslation();
    const navigate = useNavigate();

    function redirectLogin() {
        navigate("/");
    }

    return (
        <>
            <button className='p-2 border border-black bg-black rounded-[7px] text-white hover:bg-gray-900 hover:shadow-lg' onClick={redirectLogin}>{t("connexion")}</button>
        </>
    );
}

export default ButtonConnexion;