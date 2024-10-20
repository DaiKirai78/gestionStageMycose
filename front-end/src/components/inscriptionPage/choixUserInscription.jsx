import { Checkbox } from '@material-tailwind/react'
import Ripple from 'material-ripple-effects';
import bgOrange from "../../assets/bgOrange.jpg"
import businessman from "../../assets/businessman.png"
import etudiant from "../../assets/etudiant.png"
import professeur from "../../assets/professeurContent.png"
import { useEffect, useState } from 'react';
import detectElementOverflow from 'detect-element-overflow';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';


function ChoixUserInscription({role, setRole, setStep}) {
    
  const ripple = new Ripple();

  const initialChecks = {
    etudiant: role === 'etudiant',
    entreprise: role === 'entreprise',
    professeur: role === 'professeur'
  }

  const [entrepriseCheck, setEntrepriseCheck] = useState(initialChecks.entreprise)
  const [eleveCheck, setEleveCheck] = useState(initialChecks.etudiant)
  const [professeurCheck, setProfesseurCheck] = useState(initialChecks.professeur)

  const navigate = useNavigate("");
  const {t} = useTranslation();

  useEffect(() => {
    initCurrentSelection()
    function handleOnResize() {      
    
    const parentContentContainerRef = document.querySelector("#contentParent")
    const contentContainerRef = document.querySelector("#contentContainer")

    if (parentContentContainerRef == null || contentContainerRef == null) {
        return
    }

    const formCollisons = detectElementOverflow(contentContainerRef, parentContentContainerRef)
    const formWithBodyCollisons = detectElementOverflow(parentContentContainerRef, document.body)

    changeStyleContainer(formCollisons, formWithBodyCollisons)
    changeStyleBackground(formCollisons, formWithBodyCollisons)
    
    }
    
    handleOnResize()
    window.addEventListener('resize', handleOnResize)
}, [])

  function next() {
    setStep('premiereEtape')
  }

  function goToLogin() {
    navigate("/");
  }

  function initCurrentSelection() {    
    if(role === undefined) {
      setRole('etudiant');
      setEleveCheck(true);
    }
  }

  function checkEleveChange() {
    setEleveCheck(true)
    setEntrepriseCheck(false)
    setProfesseurCheck(false)
    setRole('etudiant');
  }

  function checkEntrepriseChange() {
    setEleveCheck(false)
    setEntrepriseCheck(true)
    setProfesseurCheck(false)
    setRole('entreprise');
  }

  function checkProfesseurChange() {
    setEleveCheck(false)
    setEntrepriseCheck(false)
    setProfesseurCheck(true)
    setRole('professeur');
  }

  const [addedStyleBackgroundImg, setAddedStyleBackgroundImg] = useState()
  const [addedstyleForContainer, setAddedstyleForContainer] = useState()

  function changeStyleContainer(collisionsForm, formWithBodyCollisons) {
    const styleContainerScroll = "sm:overflow-y-auto items-start"
    const styleContainerNoScroll = "sm:overflow-y-hidden"

    if (isOverflowing(collisionsForm) || isOverflowing(formWithBodyCollisons)) {
        setAddedstyleForContainer(styleContainerScroll)
    } else if (!isOverflowing(collisionsForm) && !isOverflowing(formWithBodyCollisons)) {
        setAddedstyleForContainer(styleContainerNoScroll)
    }
  }

  function changeStyleBackground(collisionsForm, formWithBodyCollisons) {
    const styleHidden = "hidden"
    const styleVisible = "inline"
    
    if (isOnAPhone()) {
        setAddedStyleBackgroundImg(styleVisible)
    } else if (isOverflowing(collisionsForm) || isOverflowing(formWithBodyCollisons)) {
        setAddedStyleBackgroundImg(styleHidden)
    } else if (!isOverflowing(collisionsForm) && !isOverflowing(formWithBodyCollisons)) {
        setAddedStyleBackgroundImg(styleVisible)
    }
  }

  function isOverflowing(formCollisons) {
    return formCollisons.collidedBottom || formCollisons.collidedTop
  }

  function isOnAPhone() {
      return screen.width <= 540
  }

  return (
    <>
      <div className={`${addedstyleForContainer} h-screen w-screen flex md:flex-row-reverse justify-start overflow-x-hidden flex-col`}>
        <div className="sm:h-screen w-full z-0 scale-125 flex min-h-20">
          <img src={bgOrange} alt="background orange" className={`${addedStyleBackgroundImg} w-full h-full object-cover`} />
        </div>
        <div id="contentParent" className="z-20 w-full md:rounded-r-2xl md:rounded-l-none bg-orange-light rounded-t-2xl flex flex-col justify-start pt-14 pb-10 px-[4%] md:box-content md:justify-center">
          <div id='contentContainer' className='flex md:flex-row flex-col gap-10 justify-start items-start mb-10'>


            <div className='border-gray-300 border-2 hover:bg-gray-100 shadow-[0_0_25px_-5px_rgb(0,0,0,0.3)] rounded-2xl bg-gray-50 w-full md:min-w-20 flex items-center justify-center md:p-5 px-5 py-8'
              onMouseUp={(e) => ripple.create(e, 'dark')}
            >
              <label htmlFor="checkEntreprise" className="flex flex-col items-center justify-center w-full">
                <Checkbox
                  checked={entrepriseCheck}
                  id='checkEntreprise'
                  ripple={false}
                  className='w-8 h-8 border-black md:border-[3px] border-[4px] bg-transparent transition-all hover:scale-105 hover:before:opacity-0'
                  containerProps={{
                    className: "p-0",
                  }}
                  onChange={checkEntrepriseChange}
                />
                <p className='font-bold text-2xl md:text-lg md:mt-2 mt-4 select-none'>{t("entreprise")}</p>
                <div className='flex justify-center items-center w-full relative mt-[5%]'>
                  <div className='w-[95%] aspect-square bg-light-blue-200 rounded-md mt-16 absolute left-1/2 translate-x-[-50%] bottom-0'></div>
                  <img src={businessman} alt="homme d'entreprise" className='z-30 select-none pointer-events-none w-[80%]'/>
                </div>
              </label>
            </div>

            <div className='border-gray-300 border-2 hover:bg-gray-100 shadow-[0_0_25px_-5px_rgb(0,0,0,0.3)] rounded-2xl bg-gray-50 w-full md:min-w-20 flex items-center justify-center md:p-5 px-5 py-8'
              onMouseUp={(e) => ripple.create(e, 'dark')}
            >
              <label htmlFor="checkEleve" className="flex flex-col items-center justify-center w-full">
                <Checkbox
                checked={eleveCheck}
                  id='checkEleve'
                  ripple={false}
                  className='w-8 h-8 border-black md:border-[3px] border-[4px] bg-transparent transition-all hover:scale-105 hover:before:opacity-0'
                  containerProps={{
                    className: "p-0",
                  }}
                  onChange={checkEleveChange}
                />
                <p className='font-bold text-2xl md:text-lg md:mt-2 mt-4 select-none'>{t("etudiant")}</p>
                <div className='flex justify-center items-center w-full relative mt-[5%]'>
                  <div className='w-[95%] aspect-square bg-orange rounded-md mt-16 absolute left-1/2 translate-x-[-50%] bottom-0'></div>
                  <img src={etudiant} alt="homme d'entreprise" className='z-30 select-none pointer-events-none w-[80%]'/>
                </div>
              </label>
            </div>


            <div className='border-gray-300 border-2 hover:bg-gray-100 shadow-[0_0_25px_-5px_rgb(0,0,0,0.3)] rounded-2xl bg-gray-50 w-full md:min-w-20 flex items-center justify-center md:p-5 px-5 py-8'
              onMouseUp={(e) => ripple.create(e, 'dark')}
            >
              <label htmlFor="checkProf" className="flex flex-col items-center justify-center w-full">
                <Checkbox
                  checked={professeurCheck}
                  id='checkProf'
                  ripple={false}
                  className='w-8 h-8 border-black md:border-[3px] border-[4px] bg-transparent transition-all hover:scale-105 hover:before:opacity-0'
                  containerProps={{
                    className: "p-0",
                  }}
                  onChange={checkProfesseurChange}
                />
                <p className='font-bold text-2xl md:text-lg md:mt-2 mt-4 select-none'>{t("professeur")}</p>
                <div className='flex justify-center items-center w-full relative mt-[5%]'>
                  <div className='w-[95%] aspect-square bg-red-200 rounded-md absolute left-1/2 translate-x-[-50%] bottom-0'></div>
                  <img src={professeur} alt="homme d'entreprise" className='z-30 select-none pointer-events-none w-[80%]'/>
                </div>
              </label>
            </div>

          </div>
          <div className='w-full flex flex-col justify-center items-center gap-3'>
            <button className='bg-transparent border p-2 border-black rounded-[7px] md:w-1/2 w-full' onClick={next}>{t("suivant")}</button>
            <button className='bg-black text-white border p-2 border-black rounded-[7px] md:w-1/3 w-full' onClick={goToLogin}>{t("connexion")}</button>
          </div>
        </div>
      </div>
    </>
  )
}

export default ChoixUserInscription;