import { expect, test, vi } from 'vitest'
import { render } from 'vitest-browser-react'
import EvaluerListEtudiantCard from '../components/evaluer/evaluerListEtudiantCard'
import { MemoryRouter } from 'react-router-dom';
import { I18nextProvider, withTranslation } from 'react-i18next';
import i18n from 'i18next';

const student = {
    "id": 1,
    "prenom": "Roberto",
    "nom": "Berrios",
    "courriel": "roby@gmail.com",
    "numeroDeTelephone": "273-389-2937",
    "role": "ETUDIANT",
    "programme": "TECHNIQUE_INFORMATIQUE",
    "contractStatus": "ACTIVE"
};

test("test nom présent EvaluerListEtudiantCard", async () => {
    const { getByText } = render(
        <MemoryRouter>
            <EvaluerListEtudiantCard 
                destination="/next" 
                nomPrenom="Jason Jody" 
                setSelectedStudent={(etudiant) => {}} 
                student={student} />
        </MemoryRouter>
    )

    await expect.element(getByText('Jason Jody')).toBeInTheDocument()
});
