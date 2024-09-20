import FormInscriptionEtudiant from '../components/formInscription2.jsx'
import FormInscription1 from '../components/formInscription1.jsx';
import FormContainer from '../components/formContainer.jsx';
import { useState } from "react";
import FormInscription2 from '../components/formInscription2.jsx';
import FormInscription3 from '../components/formInscription3.jsx';

const InscriptionPage = () => {
    const [prenom, setPrenom] = useState('');
    const [nom, setNom] = useState('');
    const [email, setEmail] = useState('');
    const [telephone, setTelephone] = useState('');
    const [step, setStep] = useState('premiereEtape')

    function switchComponent(param) {
        switch (param) {
            case 'premiereEtape':
                return <FormInscription1 prenom={prenom} nom={nom} setNom={setNom} setPrenom={setPrenom} setStep={setStep}/>;
            case 'deuxiemeEtape':
                return <FormInscription2 email={email} setEmail={setEmail} telephone={telephone} setTelelphone={setTelephone} setStep={setStep} />
            case 'troisiemeEtape':
                return <FormInscription3/>
            //default:
                //return <FormInscription1 prenom={prenom} nom={nom} setNom={setNom} setPrenom={setPrenom} setStep={setStep}/>;
        }
    }

    return (
        <>
        <FormContainer>
            {switchComponent(step)}
        </FormContainer>

        </>
    );
};


export default InscriptionPage;