import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import React from "react"
import './index.css'
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import ConnectionPage from "./pages/connectionPage.jsx"
import AccueilPage from './pages/accueilPage.jsx';
import InscriptionPage from './pages/inscriptionPage.jsx';
import ValiderCVPage from './pages/validerCVPage.jsx';
import "./i18n"
import VoirMonCVPage from "./pages/voirMonCVPage.jsx";
import ValiderCV from "./components/validerCVPage/validerCV.jsx";
import UploadCvPage from "./pages/uploadCvPage.jsx";
import UploadOffreStagePage from "./pages/uploadOffreStagePage.jsx";
import ValiderOffreStage from "./components/validerOffreStagePage/validerOffreStage.jsx";
import ValiderOffreStagePage from "./pages/validerOffreStagePage.jsx";
import Layout from './components/layout.jsx';
import ProfilPage from './pages/profilPage.jsx';
import AttributionPage from './pages/attributionPage.jsx';
import AttributionEtudiant from './components/attribution/attributionEtudiant.jsx';
import AttributionProf from './components/attribution/attributionProf.jsx';
import AppliquerStagePage from './pages/appliquerStagePage.jsx';
import ContratPage from "./pages/contratPage.jsx";
import AttribuerContratPage from "./pages/attribuerContratPage.jsx";
import EtudiantConvocationPage from "./pages/etudiantConvocationPage.jsx";
import SignerContratPage from './pages/SignerContratPage.jsx';
import SignerContratPageLayout from './pages/signerContratPageLayout.jsx';
import SignerUnContratPage from './pages/SignerUnContratPage.jsx';

const router = createBrowserRouter([
  { path: "/", element: <ConnectionPage /> },
  { path: "/inscription", element: <InscriptionPage /> },
  {
    path: "/",
    element: <Layout />,
    children: [
      { path: "/accueil", element: <AccueilPage /> },
      { path: "/televerserOffreStage", element: <UploadOffreStagePage /> },
      { path: "/televerserCV", element: <UploadCvPage /> },
      { path: "/voirMonCV", element: <VoirMonCVPage /> },
      { path: "/validerCV", element: <ValiderCVPage /> },
      { path: "/validerCV/:id", element: <ValiderCV /> },
      { path: "/validerOffreStage/:name", element: <ValiderOffreStage /> },
      { path: "/validerOffreStage", element: <ValiderOffreStagePage /> },
      { path: "/profil", element: <ProfilPage /> },
      { path: "/appliquer", element: <AppliquerStagePage/> },
      { path: "/attribuerContrat", element: <ContratPage/> },
      { path: "/attribuerContrat/:id", element: <AttribuerContratPage/> },
      { path: "/convocations", element: <EtudiantConvocationPage /> },

      {
        path: "/contrats",
        element: <SignerContratPageLayout/>,
        children: [
          {
            path: "signer", element: <SignerUnContratPage />
          },
          {
            path: "", element: <SignerContratPage />
          }
        ],
      },
      {
        path: "/attribuer", 
        element: <AttributionPage />, 
        children: [
          {path: "eleve", element: <AttributionEtudiant />},
          {path: "prof", element: <AttributionProf />}
        ]
      }
    ]
  }

]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <React.Suspense fallback="Translating">
      <RouterProvider router={router} />
    </React.Suspense>
  </StrictMode>
);
