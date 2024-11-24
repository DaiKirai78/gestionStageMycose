import React from 'react';
import PropTypes from 'prop-types';

const PageTitle = ({ title }) => {
    return (
        <h1 className='text-3xl md:text-4xl font-bold text-center mb-10'>{title}</h1>
    );
};

PageTitle.propTypes = {
    title: PropTypes.string.isRequired,
};

export default PageTitle;