import {useTranslation} from "react-i18next";

const LoadingSpinner = () => {
    const {t} = useTranslation();
    return (
        <div className="flex flex-col items-center justify-center bg-transparent">
            <svg
                className="animate-spin h-12 w-12 text-orange-500 mb-4"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
            >
                <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                />
                <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C6.58 0 2 4.58 2 10h2zm2 5.3A7.963 7.963 0 014 12H0c0 4.42 3.58 8 8 8v-2.7z"
                />
            </svg>
            <p className="text-lg text-gray-700 font-semibold animate-pulse">
                {t("loading")}
            </p>
        </div>
    )
};

export default LoadingSpinner;