import detectElementOverflow from 'detect-element-overflow';
import React, { useEffect, useState } from 'react';
import bgOrange from "../assets/bgOrange.jpg"
import logoMycose from "../assets/LogoMycose.png"
import logoTitreMycose from "../assets/MycoseTitreLogo.png"

const FormContainer = (props) => {    

    const [addedStyleBackgroundImg, setAddedStyleBackgroundImg] = useState()
    const [addedStyleForContentContainer, setaAddedStyleForContentContainer] = useState()
    const [addedstyleForContainer, setAddedstyleForContainer] = useState()

    useEffect(() => {
        
        function handleOnResize() {
        
        const parentContentContainerRef = document.querySelector("#contentParent")
        const contentContainerRef = document.querySelector("#contentContainer")

        if (parentContentContainerRef == null || contentContainerRef == null) {
            return
        }

        const formCollisons = detectElementOverflow(contentContainerRef, parentContentContainerRef)
        const formWithBodyCollisons = detectElementOverflow(parentContentContainerRef, document.body)

        changeStyleContentContainerParent(formCollisons, formWithBodyCollisons)
        changeStyleBackground(formCollisons, formWithBodyCollisons)
        changeStyleContainer(formCollisons, formWithBodyCollisons)
        
        }
        
        handleOnResize()
        window.addEventListener('resize', handleOnResize)
    }, [])


    function changeStyleContainer(collisionsForm, formWithBodyCollisons) {
        const styleContainerScroll = "sm:overflow-y-auto items-start"
        const styleContainerNoScroll = "sm:overflow-y-hidden items-center"

        if (isOverflowing(collisionsForm) || isOverflowing(formWithBodyCollisons)) {
            setAddedstyleForContainer(styleContainerScroll)
        } else if (!isOverflowing(collisionsForm) && !isOverflowing(formWithBodyCollisons)) {
            setAddedstyleForContainer(styleContainerNoScroll)
        }
    }

    function changeStyleContentContainerParent(collisionsForm, formWithBodyCollisons) {
        const styleForVerticalTablet = "sm:h-fit sm:py-14"
        const styleForOtherWhenScreenNormal = "sm:h-screen sm:py-0"
        const styleForOtherWhenScreenThin = "sm:h-fit sm:py-14"

        if (isOnAPhone()) {
            return
        }

        if (isScreenTabletVertical()) {
            setaAddedStyleForContentContainer(styleForVerticalTablet)
        } else if (!isOverflowing(collisionsForm) && !isOverflowing(formWithBodyCollisons)) {
            setaAddedStyleForContentContainer(styleForOtherWhenScreenNormal)
        } else if (isOverflowing(collisionsForm) || isOverflowing(formWithBodyCollisons)) {
            setaAddedStyleForContentContainer(styleForOtherWhenScreenThin)
        }
    }

    function changeStyleBackground(collisionsForm, formWithBodyCollisons) {
        const styleHidden = "hidden"
        const styleVisible = "inline"
        
        if (isOnAPhone()) {
            setAddedStyleBackgroundImg(styleVisible)
        } else if (isOverflowing(collisionsForm) || isOverflowing(formWithBodyCollisons) || isScreenTabletVertical()) {
            setAddedStyleBackgroundImg(styleHidden)
        } else if (!isOverflowing(collisionsForm) && !isOverflowing(formWithBodyCollisons)) {
            setAddedStyleBackgroundImg(styleVisible)
        }
    }

    function isOverflowing(formCollisons) {
        return formCollisons.collidedBottom || formCollisons.collidedTop
    }
    
    function isScreenTabletVertical() {    
        return !isOnAPhone() && screen.height > screen.width
    }

    function isOnAPhone() {
        return screen.width <= 540
    }
    
    return (
        <div className={`${addedstyleForContainer} sm:flex-row-reverse h-screen w-screen flex flex-col justify-start overflow-x-hidden`}>
        <div className="sm:h-screen h-full w-full z-0 scale-125 flex min-h-20">
          <img src={bgOrange} alt="background orange" className={`${addedStyleBackgroundImg} w-full h-full object-cover`} />
        </div>
        <div id="contentParent" className={`${addedStyleForContentContainer} sm:w-10/12 sm:rounded-r-2xl sm:rounded-l-none z-10 md:w-7/12 bg-orange-light rounded-t-2xl flex flex-col justify-center pt-14 pb-10 box-content`}>
          <div id="contentContainer">
            <div className='flex flex-col sm:px-30pct px-30pct pb-5'>
                <img src={logoMycose} alt="logo mycose" />
                <img src={logoTitreMycose} alt="nom logo mycose" className='px-20pct py-10pct' />
            </div>
            {props.children}
          </div>
        </div>
      </div>
    );
};

export default FormContainer;