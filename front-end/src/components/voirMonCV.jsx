// src/components/VoirMonCV.js
import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
//import pdfFile from "../assets/pdf.pdf";

const VoirMonCV = () => {
    const [isFullscreen, setIsFullscreen] = useState(false);
    const pdfContainerRef = useRef(null);
    const [pdfUrl, setPdfUrl] = useState(null);



    useEffect(() => {
        fetchCV();

        //Cleanup pour éviter de saturer la mémoire
        return () => {
            if (pdfUrl) {
                URL.revokeObjectURL(pdfUrl);
            }
        };

    }, [pdfUrl]);

    const fetchCV = async () => {
        let token = localStorage.getItem("token");
        try {
            const response = await axios.post("http://localhost:8080/api/cv/current", {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/pdf'
                },
                responseType: 'blob'
            });

            const blob = await response.blob();
            const url = URL.createObjectURL(blob);
            setPdfUrl(url);


        } catch (error) {
            console.error("Erreur lors de la récupération du CV:", error);
        }
    };








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
                {/* Container for the iframe */}
                <div
                    id="pdf-container"
                    className="w-full max-w-4xl h-[80vh] sm:h-[90vh] md:h-[100vh] overflow-hidden rounded shadow-lg"
                >
                    <iframe
                        src={pdfUrl}
                        title="Mon CV"
                        className="w-full h-full border-0"
                        allowFullScreen
                    ></iframe>
                </div>
                {/* Fullscreen Toggle Button */}
                <button
                    onClick={toggleFullscreen}
                    className="mt-4 px-4 py-2 bg-[#afafea] font-bold text-black p-2 rounded-lg hover:bg-[#7d7ded] cursor-pointer disabled:hover:bg-[#afafea] disabled:cursor-auto"
                >
                    {isFullscreen ? "Exit Full Screen" : "Full Screen"}
                </button>
            </div>
        </div>
    );
};

export default VoirMonCV;
