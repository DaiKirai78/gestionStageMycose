// src/components/Rapports.test.jsx
import { vi } from 'vitest';

// 1. Mock react-i18next first
vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key, // Simple translation function
        i18n: {
            changeLanguage: () => new Promise(() => {}),
        },
    }),
}));

// 2. Mock axios with explicit structure
vi.mock('axios', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn(),
    },
}));

import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import Rapports from '../components/rapportsPage/rapports';
import axios from 'axios';
import userEvent from '@testing-library/user-event';

describe('Rapports Component', () => {
    beforeEach(() => {
        axios.get.mockClear();
        axios.post.mockClear();
        axios.put.mockClear();
        axios.delete.mockClear();

        Object.defineProperty(window, 'localStorage', {
            value: {
                getItem: vi.fn(() => 'mocked-token'),
                setItem: vi.fn(),
                removeItem: vi.fn(),
                clear: vi.fn(),
            },
            writable: true,
        });
    });

    it('fetches and displays initial data', async () => {
        const mockData = [
            {
                id: 1,
                prenom: 'Roberto',
                nom: 'Berrios',
                programme: "Technique de l'informatique",
                courriel: 'roby@gmail.com',
                numeroDeTelephone: '273-389-2937',
                contractStatus: 'Aucun contrat',
            },
        ];

        axios.get.mockResolvedValueOnce({ data: mockData });

        render(<Rapports />);

        expect(screen.getByText('generateReports')).toBeInTheDocument();

        // Use findByText for asynchronous elements
        expect(await screen.findByText(/email/i)).toBeInTheDocument();
        expect(screen.getByText(/roby@gmail\.com/i)).toBeInTheDocument();
        expect(screen.getByText(/273-389-2937/i)).toBeInTheDocument();
        expect(screen.getByText(/Aucun contrat/i)).toBeInTheDocument();

        // Verify axios call
        expect(axios.get).toHaveBeenCalledTimes(1);
        expect(axios.get).toHaveBeenCalledWith('http://localhost:8080/api/rapports/all-etudiants', {
            headers: {
                Authorization: `Bearer mocked-token`,
            },
        });
    });

    it('updates data when a different report is selected', async () => {
        const initialData = [
            {
                id: 1,
                prenom: 'Jane',
                nom: 'Smith',
                programme: 'Mathematics',
                courriel: 'jane.smith@example.com',
                numeroDeTelephone: '0987654321',
                contractStatus: 'inactive',
            },
        ];

        const updatedData = [
            {
                id: 2,
                prenom: 'Alice',
                nom: 'Johnson',
                programme: 'Physics',
                courriel: 'alice.johnson@example.com',
                numeroDeTelephone: '5551234567',
                contractStatus: 'active',
            },
        ];

        axios.get.mockResolvedValueOnce({ data: initialData });

        render(<Rapports />);

        // Wait for initial data
        expect(await screen.findByText(/email/i)).toBeInTheDocument();
        expect(screen.getByText(/jane\.smith@example\.com/i)).toBeInTheDocument();
        expect(screen.getByText(/0987654321/i)).toBeInTheDocument();
        expect(screen.getByText(/inactive/i)).toBeInTheDocument();

        // Mock updated API response
        axios.get.mockResolvedValueOnce({ data: updatedData });

        const select = screen.getByRole('combobox');
        await userEvent.selectOptions(select, 'etudiants-sans-cv');

        // Wait for updated data
        expect(await screen.findByText(/email/i)).toBeInTheDocument();
        expect(screen.getByText(/alice\.johnson@example\.com/i)).toBeInTheDocument();
        expect(screen.getByText(/5551234567/i)).toBeInTheDocument();
        expect(screen.getByText(/active/i)).toBeInTheDocument();

        // Verify axios call with new URL
        expect(axios.get).toHaveBeenCalledTimes(2);
        expect(axios.get).toHaveBeenCalledWith('http://localhost:8080/api/rapports/etudiants-sans-cv', {
            headers: {
                Authorization: `Bearer mocked-token`,
            },
        });
    });

    it('displays "noDataAvailable" message when data is empty', async () => {
        // Mock empty data
        axios.get.mockResolvedValueOnce({ data: [] });

        render(<Rapports />);

        // Wait for the "noDataAvailable" message
        await waitFor(() => {
            expect(screen.getByText('noDataAvailable')).toBeInTheDocument();
        });
    });

    it('handles API errors gracefully', async () => {
        // Mock an API error
        axios.get.mockRejectedValueOnce(new Error('Network Error'));

        // Spy on console.error to suppress error logs during tests
        const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

        render(<Rapports />);

        // Wait to ensure the error is handled
        await waitFor(() => {
            // Since there's no UI feedback for errors in the component, check console.error was called
            expect(consoleSpy).toHaveBeenCalledWith(
                'Erreur lors du chargement des données:',
                expect.any(Error)
            );
        });
    })
});
