import './App.css'
import FormContainer from './components/formContainer'

/*
Doc traduction:
import { useTranslation } from "react-i18next"

const { t, i18n } = useTranslation()

Pour aller chercher du texte traduit:
t("key")

Si une clé existe pas, le deuxième paramètre sera montré automatiquement
t("CleQuiExistePas", "Automatique")

Pour changer la langue :
i18n.changeLanguage(langue)

Récupérer la langue courrante
i18n.resolvedLanguage


Exemple de hashmap pour un language switcher:
const langues = {
    en: { nativeName: "English" },
    fr: { nativeName: "Français" }
  }
*/

function App() {
  return (
    <>
      <FormContainer></FormContainer>
    </>
  )
}

export default App
