import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import React from "react"
import './index.css'
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import ConnectionPage from "./pages/connectionPage.jsx"
import AcceuilPage from './pages/accueilPage.jsx';
import InscriptionPage from './pages/inscriptionPage.jsx';
import ValiderCVPage from './pages/validerCVPage.jsx';

import "./i18n"
import UploadOffreStage from "./components/uploadOffreStage.jsx";
import UploadCV from "./components/uploadCV.jsx";
import voirMonCV from "./components/voirMonCV.jsx";
import VoirMonCV from "./components/voirMonCV.jsx";
import VoirMonCVPage from "./pages/voirMonCVPage.jsx";
import ValiderCV from "./components/validerCVPage/validerCV.jsx";
import UploadCvPage from "./pages/uploadCvPage.jsx";
import UploadOffreStagePage from "./pages/uploadOffreStagePage.jsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <ConnectionPage />,
  },
  {
    path: "/accueil",
    element: <AcceuilPage />
  },
  {
    path: "/televerserOffreStage",
    element: <UploadOffreStagePage />
  },
  {
    path: "/televerserCV",
    element: <UploadCvPage />
  },
  {
    path: "/inscription",
    element: <InscriptionPage />
  },
  {
    path: "/voirMonCV",
    element: <VoirMonCVPage />
  },
  {
    path: "/validerCV",
    element: <ValiderCVPage />
  },
  { path: "/validerCV/:id",
    element: <ValiderCV />
  }

]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <React.Suspense fallback="Translating">
      <RouterProvider router={router} />
    </React.Suspense>
  </StrictMode>
);
