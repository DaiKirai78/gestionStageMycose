import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import InscriptionPage from './pages/inscriptionPage.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <InscriptionPage/>
  </StrictMode>,
)
