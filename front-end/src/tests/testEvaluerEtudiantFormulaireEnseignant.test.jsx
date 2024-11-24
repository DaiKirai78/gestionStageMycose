import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import EvaluerEtudiantFormulaireEnseignant from '../components/evaluer/EvaluerEtudiantFormulaireEnseignant';
import FormulaireInformationsEntreprise from "../components/evaluer/formulaireInformationsEntreprise.jsx";
import EvaluerFormulaire from "../components/evaluer/evaluerFormulaire.jsx";
import EvaluerFormulaireObsGenerales from "../components/evaluer/evaluerFormulaireObsGenerales.jsx";

// Mock des modules
vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
    }),
}));

vi.mock('axios', () => ({
    default: { post: vi.fn() },
}));

describe('EvaluerEtudiantFormulaireEnseignant', () => {
    it('affiche le formulaire avec les données initiales', () => {
        const selectedStudent = { prenom: 'John', nom: 'Doe' };

        render(
            <MemoryRouter>
                <EvaluerEtudiantFormulaireEnseignant
                    selectedStudent={selectedStudent}
                    setSelectedStudent={vi.fn()}
                    userInfo={{ role: 'enseignant' }}
                />
            </MemoryRouter>
        );

        // Vérifiez l'affichage du formulaire avec les données
        expect(screen.getByText(/remplirFormulaireDeMilieuxStage/i)).toBeInTheDocument();
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
    });

    it('met à jour les données du formulaire lorsqu\'un champ change', () => {
        const selectedStudent = { prenom: 'John', nom: 'Doe' };

        render(
            <MemoryRouter>
                <EvaluerEtudiantFormulaireEnseignant
                    selectedStudent={selectedStudent}
                    setSelectedStudent={vi.fn()}
                    userInfo={{ role: 'enseignant' }}
                />
            </MemoryRouter>
        );

        // Simulez un changement de champ
        const input = screen.getByLabelText(/nomEntreprise/i); // Ajoutez un label dans le composant
        fireEvent.change(input, { target: { value: 'Nouvelle entreprise' } });

        // Vérifiez que le champ est mis à jour
        expect(input.value).toBe('Nouvelle entreprise');
    });
});

describe("FormulaireInformationsEntreprise", () => {
    const mockHandleChange = vi.fn();
    const initialFormData = {
        nomEntreprise: { value: "", hasError: false },
        nomPersonneContact: { value: "Jane Doe", hasError: false },
        numeroStage: { value: "1", hasError: false },
    };

    it("affiche tous les champs avec les données initiales", () => {
        render(
            <FormulaireInformationsEntreprise
                formData={initialFormData}
                handleChange={mockHandleChange}
            />
        );

        // Vérifiez que les champs sont rendus avec les valeurs initiales
        expect(screen.getByLabelText(/nomEntreprise/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/nomPersonneContact/i)).toHaveValue("Jane Doe");
        expect(screen.getByLabelText(/1/i)).toBeChecked();
        expect(screen.getByLabelText(/2/i)).not.toBeChecked();
    });

    it("met à jour un champ lorsque l'utilisateur saisit une valeur", () => {
        render(
            <FormulaireInformationsEntreprise
                formData={initialFormData}
                handleChange={mockHandleChange}
            />
        );

        // Simulez un changement de valeur pour un champ
        const inputNomEntreprise = screen.getByLabelText(/nomEntreprise/i);
        fireEvent.change(inputNomEntreprise, { target: { value: "Nouvelle entreprise" } });

        // Vérifiez que handleChange est appelé avec les bonnes valeurs
        expect(mockHandleChange).toHaveBeenCalledWith("nomEntreprise", "Nouvelle entreprise");
    });

    it("affiche un message d'erreur si un champ obligatoire est vide", () => {
        const formDataWithError = {
            ...initialFormData,
            nomEntreprise: { value: "", hasError: true },
        };

        render(
            <FormulaireInformationsEntreprise
                formData={formDataWithError}
                handleChange={mockHandleChange}
            />
        );

        // Vérifiez que le message d'erreur est affiché
        expect(screen.getByText(/nomEntreprise ne peut pas être vide./i)).toBeInTheDocument();
    });

    it("change le numéro du stage lorsque l'utilisateur clique sur une autre option", () => {
        render(
            <FormulaireInformationsEntreprise
                formData={initialFormData}
                handleChange={mockHandleChange}
            />
        );

        // Simulez le changement du numéro de stage
        const stageRadio2 = screen.getByLabelText(/2/i);
        fireEvent.click(stageRadio2);

        // Vérifiez que handleChange est appelé avec le nouveau numéro
        expect(mockHandleChange).toHaveBeenCalledWith("numeroStage", "2");
    });
});

describe("EvaluerFormulaire", () => {
    const mockHandleRadioChange = vi.fn();
    const mockHandleCommentChange = vi.fn();
    const mockHandleNumberChange = vi.fn();

    const formMock = {
        id: "evaluation",
        title: "Evaluation Title",
        description: "This is a description",
        criteria: [
            { id: "criterion1", label: "Criterion 1" },
            { id: "criterion2", label: "Criterion 2" },
            { id: "salaireHoraire", label: "Salaire Horaire" },
        ],
    };

    const ratingOptions = ["option1", "option2", "option3"];

    const formDataMock = {
        evaluation: {
            criterion1: { value: "option1", hasError: false },
            criterion2: { value: "", hasError: true },
            salaireHoraire: { value: "", hasError: false },
            evaluationCommentaires: { value: "Some comments" },
        },
    };

    it("affiche les critères et les options de notation", () => {
        render(
            <EvaluerFormulaire
                form={formMock}
                ratingOptions={ratingOptions}
                handleRadioChange={mockHandleRadioChange}
                handleCommentChange={mockHandleCommentChange}
                handleNumberChange={mockHandleNumberChange}
                formData={formDataMock}
                role="EMPLOYEUR"
            />
        );

        // Vérification du titre et de la description
        expect(screen.getByText(/Evaluation Title/i)).toBeInTheDocument();
        expect(screen.getByText(/This is a description/i)).toBeInTheDocument();

        // Vérification des critères
        expect(screen.getByText(/Criterion 1/i)).toBeInTheDocument();
        expect(screen.getByText(/Criterion 2/i)).toBeInTheDocument();
        expect(screen.getByText(/Salaire Horaire/i)).toBeInTheDocument();

        // Vérification des options de notation
        ratingOptions.forEach((option) => {
            expect(screen.getByText(option)).toBeInTheDocument();
        });
    });


    it("affiche un message d'erreur si un critère obligatoire est vide", () => {
        render(
            <EvaluerFormulaire
                form={formMock}
                ratingOptions={ratingOptions}
                handleRadioChange={mockHandleRadioChange}
                handleCommentChange={mockHandleCommentChange}
                handleNumberChange={mockHandleNumberChange}
                formData={formDataMock}
                role="EMPLOYEUR"
            />
        );

        // Vérifiez l'affichage du message d'erreur pour le critère 2
        expect(screen.getByText(/Criterion 2/i).className).toContain("text-red-700");
    });

    it("met à jour la valeur du champ salaire horaire lorsque l'utilisateur saisit une valeur", () => {
        render(
            <EvaluerFormulaire
                form={formMock}
                ratingOptions={ratingOptions}
                handleRadioChange={mockHandleRadioChange}
                handleCommentChange={mockHandleCommentChange}
                handleNumberChange={mockHandleNumberChange}
                formData={formDataMock}
                role="EMPLOYEUR"
            />
        );

        // Simulez une saisie utilisateur pour le salaire horaire
        const salaireInput = screen.getByPlaceholderText("0");
        fireEvent.change(salaireInput, { target: { value: "20" } });

        // Vérifiez que la fonction handleNumberChange est appelée avec les bonnes valeurs
        expect(mockHandleNumberChange).toHaveBeenCalledWith("evaluation", "salaireHoraire", "20");
    });

    it("met à jour le commentaire lorsque l'utilisateur saisit du texte", () => {
        render(
            <EvaluerFormulaire
                form={formMock}
                ratingOptions={ratingOptions}
                handleRadioChange={mockHandleRadioChange}
                handleCommentChange={mockHandleCommentChange}
                handleNumberChange={mockHandleNumberChange}
                formData={formDataMock}
                role="EMPLOYEUR"
            />
        );

        // Simulez une saisie utilisateur dans le champ commentaires
        const commentTextarea = screen.getByLabelText(/commentaires/i);
        fireEvent.change(commentTextarea, { target: { value: "Updated comment" } });

        // Vérifiez que la fonction handleCommentChange est appelée avec la nouvelle valeur
        expect(mockHandleCommentChange).toHaveBeenCalledWith("evaluation", "Updated comment");
    });
});

describe("EvaluerFormulaireObsGenerales", () => {
    it("affiche les champs initiaux correctement", () => {
        const formData = {
            milieuStage: "",
            nombreStagiaires: "",
            prochainStage: "",
            quartsVariables: "",
        };

        render(
            <EvaluerFormulaireObsGenerales
                formData={formData}
                handleChange={vi.fn()}
                setErrorKeySignature={vi.fn()}
                errorKeySignature=""
                setDrewSomething={vi.fn()}
                canvasRef={{ current: { clearCanvas: vi.fn() } }}
            />
        );

        expect(screen.getByText(/generalObservations/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/environmentPreferredFor/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/openToWelcoming/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/variableWorkShifts/i)).toBeInTheDocument();
        expect(screen.getByText(/signature/i)).toBeInTheDocument();
    });

    it("met à jour le champ 'milieuStage' lorsque l'utilisateur sélectionne une option", () => {
        const mockHandleChange = vi.fn();
        const formData = {
            milieuStage: "",
        };

        render(
            <EvaluerFormulaireObsGenerales
                formData={formData}
                handleChange={mockHandleChange}
                setErrorKeySignature={vi.fn()}
                errorKeySignature=""
                setDrewSomething={vi.fn()}
                canvasRef={{ current: { clearCanvas: vi.fn() } }}
            />
        );

        const premierStageOption = screen.getByLabelText(/firstInternship/i);
        fireEvent.click(premierStageOption);

        expect(mockHandleChange).toHaveBeenCalledWith("milieuStage", "PREMIER_STAGE");
    });

    it("affiche un message d'erreur si un champ obligatoire n'est pas rempli", () => {
        const formData = {
            milieuStage: { value: "", hasError: true },
        };

        render(
            <EvaluerFormulaireObsGenerales
                formData={formData}
                handleChange={vi.fn()}
                setErrorKeySignature={vi.fn()}
                errorKeySignature=""
                setDrewSomething={vi.fn()}
                canvasRef={{ current: { clearCanvas: vi.fn() } }}
            />
        );

        expect(screen.getByText(/fieldRequired/i)).toBeInTheDocument();
    });

    it("affiche les plages horaires lorsque 'quartsVariables' est 'OUI'", () => {
        const formData = {
            quartsVariables: "OUI",
            quart1: { de: "2024-11-01T08:00", a: "2024-11-01T16:00" },
        };

        render(
            <EvaluerFormulaireObsGenerales
                formData={formData}
                handleChange={vi.fn()}
                setErrorKeySignature={vi.fn()}
                errorKeySignature=""
                setDrewSomething={vi.fn()}
                canvasRef={{ current: { clearCanvas: vi.fn() } }}
            />
        );

        expect(screen.getByLabelText(/De/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/à/i)).toBeInTheDocument();
        expect(screen.getByDisplayValue("2024-11-01T08:00")).toBeInTheDocument();
        expect(screen.getByDisplayValue("2024-11-01T16:00")).toBeInTheDocument();
    });
});
