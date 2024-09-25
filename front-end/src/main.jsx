import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import React from "react"
import './index.css'
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import ConnectionPage from "./pages/connectionPage.jsx"
import AcceuilPage from './pages/acceuilPage.jsx';

import "./i18n"
import UploadOffreStage from "./components/uploadOffreStage.jsx";
import UploadCV from "./components/uploadCV.jsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <ConnectionPage />,
  },
  {
    path: "/acceuil",
    element: <AcceuilPage />
  },
  {
    path: "/televerserOffreStage",
    element: <UploadOffreStage />
  },
  {
    path: "/televerserCV",
    element: <UploadCV />
  }
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <React.Suspense fallback="loading">
      <RouterProvider router={router} />
    </React.Suspense>
  </StrictMode>
);