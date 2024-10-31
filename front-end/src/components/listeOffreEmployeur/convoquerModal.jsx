import React, { useState } from 'react';
import { BsX } from "react-icons/bs";
import { useTranslation } from 'react-i18next';

function ConvoquerModal({ isOpen, onClose, onSummon }) {
    const { t } = useTranslation();
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [location, setLocation] = useState('');
    const [messageConvocation, setMessageConvocation] = useState('');

    const handleSummon = () => {
        const scheduledAt = (`${date}T${time}`);
        const summonDetails = { scheduledAt, location, messageConvocation };
        onSummon(summonDetails);
        onClose();
    };

    return (
        isOpen && (
            <div className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50">
                <div className="bg-white p-6 rounded-lg shadow-lg relative w-full max-w-md">
                    <button className="absolute top-2 right-2" onClick={onClose}>
                        <BsX size={24} />
                    </button>
                    <h2 className="text-2xl mb-4">{t("summonStudent")}</h2>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">{t("date")}</label>
                        <input
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            className="border p-2 w-full"
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">{t("time")}</label>
                        <input
                            type="time"
                            value={time}
                            onChange={(e) => setTime(e.target.value)}
                            className="border p-2 w-full"
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">{t("location")}</label>
                        <input
                            type="text"
                            value={location}
                            onChange={(e) => setLocation(e.target.value)}
                            className="border p-2 w-full"
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">{t("message")}</label>
                        <textarea
                            value={messageConvocation}
                            onChange={(e) => setMessageConvocation(e.target.value)}
                            className="border p-2 w-full"
                        />
                    </div>
                    <button
                        onClick={handleSummon}
                        className="bg-blue-500 text-white px-4 py-2 rounded mt-4 w-full"
                    >
                        {t("summon")}
                    </button>
                </div>
            </div>
        )
    );
}

export default ConvoquerModal;
