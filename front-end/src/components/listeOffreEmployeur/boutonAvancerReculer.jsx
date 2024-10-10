import React from "react";
import { BsArrowLeft, BsArrowRight } from "react-icons/bs";

const BoutonAvancerReculer = ({pages, setPages}) => {

    function pagesUp(amount = 1) {        
        if (pages.currentPage + amount > pages.maxPages)
            return;

        setPages({
            ...pages,
            currentPage: pages.currentPage + 1
        });

    }

    function pagesDown(amount = 1) {        
        if (pages.currentPage - amount < pages.minPages)
            return;
    
        setPages({
            ...pages,
            currentPage: pages.currentPage - 1
        });
    }

    function goTo(destinationPage) {
        setPages({
            ...pages,
            currentPage: destinationPage
        });
    }

    return (
        <div className='w-full h-10 mb-12 flex justify-center'>
            <div className='w-10/12 sm:w-1/2 md:w-1/3 flex gap-1'>
                <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'
                    onClick={() => { pagesDown(1); } }
                ><BsArrowLeft /></button>
                <button className='w-1/6 h-full border rounded'
                    onClick={() => (goTo(pages.minPages))}
                >{pages.minPages}</button>
                <div className='w-1/6 h-full border border-deep-orange-100 rounded flex justify-center items-center'>{pages.currentPage}</div>
                <button className='w-1/6 h-full border rounded'
                    onClick={() => (goTo(pages.maxPages))}
                >{pages.maxPages}</button>
                <button className='w-2/6 h-full bg-orange rounded cursor-pointer flex justify-center items-center'
                    onClick={() => { pagesUp(1); } }
                ><BsArrowRight /></button>
            </div>
        </div>
    );
}

export default BoutonAvancerReculer;