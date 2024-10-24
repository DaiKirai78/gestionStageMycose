import React from "react";
import { useTranslation } from "react-i18next";

const PageIsLoading = () => {
    const { t } = useTranslation();
    return (
        <p>{t("loading")}</p>
    )
}

export default PageIsLoading;