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
import AppliquerStagePage from "./pages/appliquerStagePage.jsx";
import ValiderOffreStage from "./components/validerOffreStagePage/validerOffreStage.jsx";
import ValiderOffreStagePage from "./pages/validerOffreStagePage.jsx";
import Layout from './components/layout.jsx';

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
      { path: "/appliquer", element: <AppliquerStagePage /> },
      { path: "/validerOffreStage/:name", element: <ValiderOffreStage /> },
      { path: "/validerOffreStage", element: <ValiderOffreStagePage /> }
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
