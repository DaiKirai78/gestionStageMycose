import './App.css'

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
      <h1 className="text-3xl font-bold underline">
        Hello World!
      </h1>
    </>
  )
}

export default App
