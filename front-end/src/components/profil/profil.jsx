import React, { useEffect, useState } from 'react'
import CardStatusDuCv from './cardStatusDuCv'
import CardStatusStage from './cardStatusStage'

const Profil = () => {
    const [isFetching, setIsFetching] = useState(true);
    const [cvInfo, setCvInfo] = useState()

    useEffect(() => {
        fetchInfoCv();
    }, []);

    async function fetchInfoCv() {
        const token = localStorage.getItem("token");

        if (!token) {
            setIsFetching(false);
            return;
        }

        try {
            const res = await fetch('http://localhost:8080/api/cv/current', {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const cvData = await res.json();
                setCvInfo(cvData);
            } else {
                setCvInfo(null)
            }
        } catch (err) {
            console.error("Erreur lors de la récupération du CV", err);
        } finally {
            setIsFetching(false);
        }
    }

    return (
        !isFetching 
        &&
        <div className="min-h-screen bg-orange-light p-4 md:p-8 lg:p-12">
            <h1 className="text-3xl md:text-4xl font-bold text-center mb-8">Profile</h1>
            
            <div className="max-w-3xl mx-auto space-y-8">
            <CardStatusDuCv cvInfo={cvInfo}/>

            <CardStatusStage />
            </div>
        </div>
    )
}

export default Profil