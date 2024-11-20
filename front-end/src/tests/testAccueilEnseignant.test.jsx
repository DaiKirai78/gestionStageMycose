import { describe, test, expect, vi } from 'vitest';

async function fetchEtudiants({ enseignantId, pageNumber, token }) {
  const response = await fetch(
    `http://localhost:8080/enseignant/getAllEtudiantsAEvaluer?enseignantId=${enseignantId}&pageNumber=${pageNumber}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.json();
}

globalThis.fetch = vi.fn();

function createFetchResponse(data) {
  return { json: () => new Promise((resolve) => resolve(data)) };
}

describe('Etudiants Service', () => {
  test('makes a GET request to fetch students and returns the result', async () => {
    const mockResponse = {
      content: [
        {
          id: 24,
          prenom: 'Roberto',
          nom: 'Berrios',
          courriel: 'roby5@gmail.com',
          numeroDeTelephone: '273-389-2937',
          role: 'ETUDIANT',
          programme: 'TECHNIQUE_INFORMATIQUE',
          contractStatus: 'ACTIVE',
        },
      ],
      totalPages: 2,
      totalElements: 11,
    };

    const token = 'sample-token';
    const enseignantId = 1;
    const pageNumber = 1;

    fetch.mockResolvedValue(createFetchResponse(mockResponse));

    const data = await fetchEtudiants({ enseignantId, pageNumber, token });

    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:8080/enseignant/getAllEtudiantsAEvaluer?enseignantId=${enseignantId}&pageNumber=${pageNumber}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    expect(data).toStrictEqual(mockResponse);
  });
});

