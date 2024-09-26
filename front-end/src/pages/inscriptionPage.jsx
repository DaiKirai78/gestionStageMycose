import FormInscriptionEtudiant from '../components/formInscription2.jsx'
import FormInscription1 from '../components/formInscription1.jsx';
import FormContainer from '../components/formContainer.jsx';
import { useState } from "react";
import FormInscription2 from '../components/formInscription2.jsx';
import FormInscription3 from '../components/formInscription3.jsx';
import ChoixUserInscription from '../components/choixUserInscription.jsx';

const InscriptionPage = () => {
    const [prenom, setPrenom] = useState('');
    const [nom, setNom] = useState('');
    const [email, setEmail] = useState('');
    const [telephone, setTelephone] = useState('');
    const [step, setStep] = useState('')
    const [role, setRole] = useState('');
    const [nomOrganisation, setNomOrganisation] = useState('');

    function switchComponent(param) {
        switch (param) {
            case 'premiereEtape':
                return <FormInscription1 prenom={prenom} nom={nom} setNom={setNom} setPrenom={setPrenom} setStep={setStep} role={role} setNomOrganisation={setNomOrganisation} nomOrganisation={nomOrganisation}/>;
            case 'deuxiemeEtape':
                return <FormInscription2 email={email} setEmail={setEmail} telephone={telephone} setTelephone={setTelephone} setStep={setStep} role={role}/>
            case 'troisiemeEtape':
                return <FormInscription3 prenom={prenom} nom={nom} email={email} telephone={telephone} setStep={setStep} role={role} nomOrganisation={nomOrganisation}/>
        }
    }

    return (
        <>
        {step == "" ? <ChoixUserInscription  setRole={setRole} setStep={setStep}/> :
            <FormContainer>
                {switchComponent(step)}
            </FormContainer>}
        </>
    );
};


export default InscriptionPage;