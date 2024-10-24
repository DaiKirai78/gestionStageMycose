import React, { useEffect, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';

const AttributionProf = () => {
    const [selectedStudent, setSelectedStudent] = useOutletContext(); 
    const [studentVerified, setStudentVerified] = useState();
    const navigate = useNavigate();

    useEffect(() => {
        if (selectedStudent != null) {
            setStudentVerified(true)
        } else {
            navigate("/attribuer/eleve")
        }
    }, [selectedStudent])

    useEffect(() => {
        console.log(selectedStudent);
    }, [])


    return (
        <div>
            {
                selectedStudent && <div>{selectedStudent.prenom} allo</div>
            }
        </div>
    );
};

export default AttributionProf;