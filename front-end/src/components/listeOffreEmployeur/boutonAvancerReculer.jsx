import React from "react";
import { useTranslation } from "react-i18next";

const BoutonAvancerReculer = ({ pages, setPages, margins: marginsClass }) => {
    const { t } = useTranslation();

    function pagesUp(amount = 1) {
        if (pages.currentPage + amount > pages.maxPages) return;

        setPages({
            ...pages,
            currentPage: pages.currentPage + amount,
        });
    }

    function pagesDown(amount = 1) {
        if (pages.currentPage - amount < pages.minPages) return;

        setPages({
            ...pages,
            currentPage: pages.currentPage - amount,
        });
    }

    // Classes pour les boutons basées sur l'état
    const previousButtonClass = pages.currentPage <= pages.minPages ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900";
    const nextButtonClass = pages.currentPage >= pages.maxPages ? "bg-gray-200 text-gray-700" : "bg-gray-400 text-gray-900";

    return (
        <div className={`flex justify-center ${marginsClass ? marginsClass : "mb-28 mt-4"}`}>
            {/* Previous Page Button */}
            <button
                className={`px-4 py-2 rounded-l ${previousButtonClass}`}
                onClick={() => { pagesDown(1); }}
                disabled={pages.currentPage <= pages.minPages}
            >
                {t("previous")}
            </button>

            {/* Current Page and Total Pages */}
            <span className="px-4 py-2">
                {t("page")} {pages.currentPage} / {pages.maxPages}
            </span>

            {/* Next Page Button */}
            <button
                className={`px-4 py-2 rounded-r ${nextButtonClass}`}
                onClick={() => { pagesUp(1); }}
                disabled={pages.currentPage >= pages.maxPages}
            >
                {t("next")}
            </button>
        </div>
    );
};

export default BoutonAvancerReculer;
