import FormInscriptionEtudiant from '../components/formInscription2.jsx'
import FormInscription1 from '../components/formInscription1.jsx';
import FormContainer from '../components/formContainer.jsx';
import { useState } from "react";

const InscriptionPage = () => {
    const [prenom, setPrenom] = useState('');
    const [nom, setNom] = useState('');

    return (
        <>
        <FormContainer>
            <FormInscription1 prenom={prenom} nom={nom} setNom={setNom} setPrenom={setPrenom}/>
        </FormContainer>
            
            {/* <FormInscriptionEtudiant/> */}
        </>
    );
};


export default InscriptionPage;