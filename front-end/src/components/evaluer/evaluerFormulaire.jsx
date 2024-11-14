import React from 'react';

const EvaluerFormulaire = ({ form, ratingOptions, handleRadioChange, handleCommentChange, formData }) => {

    function hasError(critere) {
        return critere && critere.hasError;
    }

    function getValue(critere) {
        if (critere) {
            return critere.value;
        } else {
            return "";
        }
    }

    return (
        (
            <div className="max-w-4xl bg-white rounded-lg shadow-md p-6 mb-8">
                <h2 className="text-xl font-bold">{form.title}</h2>
                <hr className='mt-3 mb-4' />
                <p className="mb-4 text-gray-700">{form.description}</p>
                
                <div className="mb-6">
                    <div className="grid grid-cols-[2fr,repeat(5,1fr)] gap-4 mb-2">
                        <div className="font-semibold">Le stagiaire a été en mesure de :</div>
                        {ratingOptions.map((option) => (
                            <div key={option.value} className="text-center text-sm font-medium">
                                {option.label}
                            </div>
                        ))}
                    </div>

                    {form.criteria.map((criterion) => (
                        <div key={criterion.id}
                        id={criterion.id}
                        className="grid grid-cols-[2fr,repeat(5,1fr)] gap-4 items-center py-2 border-t">
                            <div className={hasError(formData[form.id][criterion.id]) ? "text-red-700" : "text-black"}
                            >{criterion.label}</div>
                            {ratingOptions.map((option) => (
                                <label key={option.value} 
                                    htmlFor={`${form.id}-${criterion.id}-${option.value}`} 
                                    className="flex justify-center h-full cursor-pointer hover:ring hover:ring-gray-300 rounded">
                                    <input
                                        type="radio"
                                        id={`${form.id}-${criterion.id}-${option.value}`}
                                        name={`${form.id}-${criterion.id}`}
                                        value={option.value}
                                        checked={getValue(formData[form.id][criterion.id]) === option.value}
                                        onChange={() => handleRadioChange(form.id, criterion.id, option.value)}
                                    />
                                </label>
                            ))}
                        </div>
                    ))}
                </div>

                <div className="space-y-2">
                    <label htmlFor={`${form.id}-commentaires`} className="block font-medium">Commentaires :</label>
                    <textarea
                        id={`${form.id}-commentaires`}
                        value={formData[form.id].commentaires ? formData[form.id].commentaires.value : ""}
                        onChange={(e) => handleCommentChange(form.id, e.target.value)}
                        className="w-full min-h-[100px] p-2 border rounded resize-none"
                    />
                </div>
            </div>
        )
    );
};

export default EvaluerFormulaire;