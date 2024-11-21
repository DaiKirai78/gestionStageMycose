import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

const AfficherPdf = ({ setVoirPdf, activePdf }) => {
    const { t } = useTranslation();
    const [blobUrl, setBlobUrl] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        if (activePdf) {
            try {
                // Decode base64 string
                const byteCharacters = atob(activePdf);
                const byteNumbers = new Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                const byteArray = new Uint8Array(byteNumbers);
                const blob = new Blob([byteArray], { type: 'application/pdf' });
                const url = URL.createObjectURL(blob);
                setBlobUrl(url);
                setLoading(false);

                // Cleanup: Revoke Blob URL when component unmounts or activePdf changes
                return () => {
                    URL.revokeObjectURL(url);
                };
            } catch (err) {
                console.error("Error creating Blob URL:", err);
                setError("Failed to load PDF.");
                setLoading(false);
            }
        }
    }, [activePdf]);

    return (
        <div className="fixed left-0 top-0 w-full h-full p-8 bg-orange-light z-50 flex flex-col items-center gap-4">
            {loading && <p>Loading PDF...</p>}
            {error && <p className="text-red-500">{error}</p>}
            {!loading && !error && (
                <iframe
                    src={blobUrl}
                    title="CV"
                    className="w-full h-full border"
                ></iframe>
            )}
            <button
                className='bg-orange px-4 py-2 rounded text-white mt-4'
                onClick={() => { setVoirPdf(false); }}
            >
                {t("close")}
            </button>
        </div>
    );
}

export default AfficherPdf;
