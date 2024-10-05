// src/components/VoirMonCV.js
import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import {data} from "autoprefixer";
//import pdfFile from "../assets/pdf.pdf";

const VoirMonCV = () => {
    const [isFullscreen, setIsFullscreen] = useState(false);
    const pdfContainerRef = useRef(null);
    const [pdfUrl, setPdfUrl] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);



    useEffect(() => {
        fetchCV();
    }, []);

    const fetchCV = async () => {
        let token = localStorage.getItem("token");
        try {
            const response = await axios.post("http://localhost:8080/api/cv/current", {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
            });
            const base64String = response.data.fileData;

            if (!base64String) {
                throw new Error("No PDF data found in the response.");
            }

            const dataUrl = `data:application/pdf;base64,${base64String}`;
            setPdfUrl(dataUrl);
            setLoading(false);


        } catch (error) {
            console.error("Erreur lors de la récupération du CV:", error);
            setError("Impossible de charger le CV. Veuillez réessayer plus tard.");
            setLoading(false);
        }
    };

    const supprimerCV = async () => {
        let token = localStorage.getItem("token");
        try {
            const response = await axios.patch("http://localhost:8080/api/cv/delete_current", {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
            });


            setError("PDF supprimé avec succès.");




        } catch (error) {
            console.error("Erreur lors de la suppression du CV:", error);
            setError("Erreur lors de la suppression du CV");
        }
    }








    const toggleFullscreen = () => {
        const container = pdfContainerRef.current;
        if (!document.fullscreenElement) {
            container.requestFullscreen().then(() => {
                setIsFullscreen(true);
            }).catch(err => {
                console.error(`Error attempting to enable fullscreen mode: ${err.message} (${err.name})`);
            });
        } else {
            document.exitFullscreen().then(() => {
                setIsFullscreen(false);
            }).catch(err => {
                console.error(`Error attempting to exit fullscreen mode: ${err.message} (${err.name})`);
            });
        }
    };

    useEffect(() => {
        const handleFullscreenChange = () => {
            if (!document.fullscreenElement) {
                setIsFullscreen(false);
            }
        };

        document.addEventListener("fullscreenchange", handleFullscreenChange);

        return () => {
            document.removeEventListener("fullscreenchange", handleFullscreenChange);
        };
    }, []);

    return (
        <div className="flex flex-col items-center w-full p-4">
            <div className="flex flex-col items-center w-full py-1" id="pdf-parent"
                  ref={pdfContainerRef}>
                <div
                    id="pdf-container"
                    className="w-full max-w-4xl h-[80vh] sm:h-[90vh] md:h-[100vh] overflow-hidden rounded shadow-lg"
                >
                    {loading ? (
                        <div className="flex items-center justify-center w-full h-full">
                            <p>Loading PDF...</p>
                        </div>
                    ) : error ? (
                        <div className="flex items-center justify-center w-full h-full text-red-500">
                            {error}
                        </div>
                    ) : (
                    <iframe
                        src={pdfUrl}
                        title="Mon CV"
                        className="w-full h-full border-0"
                        allowFullScreen
                    ></iframe>
                    )}
                </div>
                <div className="flex space-x-4">
                    <button
                        onClick={toggleFullscreen}
                        className="mt-4 px-4 py-2 bg-[#afafea] font-bold text-black p-2 rounded-lg hover:bg-[#7d7ded] cursor-pointer disabled:hover:bg-[#afafea] disabled:cursor-auto"
                    >
                        {isFullscreen ? "Exit Full Screen" : "Full Screen"}
                    </button>
                    <button
                        onClick={supprimerCV}
                        className="mt-4 px-4 py-2 bg-[#dc3545] font-bold text-black p-2 rounded-lg hover:bg-[#c43540] cursor-pointer disabled:hover:bg-[#dc3545] disabled:cursor-auto"
                    >
                        Supprimer CV
                    </button>
                </div>
            </div>
        </div>
    );
};

export default VoirMonCV;
