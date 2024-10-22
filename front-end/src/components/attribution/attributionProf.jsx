import React, { useEffect, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';

const AttributionProf = () => {
    const [student, setStudent] = useOutletContext(); 
    const [studentVerified, setStudentVerified] = useState();
    const navigate = useNavigate();

    useEffect(() => {
        if (student != null) {
            setStudentVerified(true)
        } else {
            navigate("/attribuer/eleve")
        }
    }, [student])


    return (
        <div>
            {
                studentVerified && <div>HEHE</div>
            }
        </div>
    );
};

export default AttributionProf;