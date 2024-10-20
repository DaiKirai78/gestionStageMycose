import React, { useState } from 'react';

const CardStatusStage = () => {
    const [internshipStatus, setInternshipStatus] = useState("J'ai un stage en ce moment")

    return (
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-2xl font-semibold mb-2">Status du Stage</h2>
          <p className={`font-medium mb-4 ${
            internshipStatus === "J'ai un stage en ce moment" ? 'text-green-600' : 'text-red-600'
          }`}>
            {internshipStatus}
          </p>
          <div className="space-y-2 mb-4">
            <button
              className="group w-full py-2 px-4 rounded-full text-left transition-colors relative bg-transparent text-gray-800 hover:bg-green-50"
              onClick={() => setInternshipStatus("J'ai un stage en ce moment")}
            >
              <span className={`absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 rounded-full transition-colors ${
                internshipStatus === "J'ai un stage en ce moment" ? 'bg-green-500' : 'bg-gray-300 group-hover:bg-green-500'
              }`}></span>
              <span className="ml-6">J'ai un stage</span>
            </button>
            <button
              className="group w-full py-2 px-4 rounded-full text-left transition-colors relative bg-transparent text-gray-800 hover:bg-red-50"
              onClick={() => setInternshipStatus("Je n'ai pas de stage")}
            >
              <span className={`absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 rounded-full transition-colors ${
                internshipStatus === "Je n'ai pas de stage" ? 'bg-red-500' : 'bg-gray-300 group-hover:bg-red-500'
              }`}></span>
              <span className="ml-6">Je n'ai pas de stage</span>
            </button>
          </div>
          <button className="w-full bg-black text-white py-2 rounded-md hover:bg-gray-800 transition-colors">
            Sauvegarder mon nouveau status
          </button>
        </div>
    );
};

export default CardStatusStage;