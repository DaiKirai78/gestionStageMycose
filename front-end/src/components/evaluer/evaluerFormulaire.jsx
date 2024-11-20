import React from 'react';
import { useTranslation } from 'react-i18next';

const EvaluerFormulaire = ({ form, ratingOptions, handleRadioChange, handleCommentChange, formData }) => {
    const { t } = useTranslation();

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
                <h2 className="text-xl font-bold">{t(form.title).toUpperCase()}</h2>
                <hr className='mt-3 mb-4' />
                <p className="mb-4 text-gray-700">{t(form.description)}</p>
                
                <div className="mb-6">
                    <div className="grid grid-cols-[2fr,repeat(5,1fr)] gap-4 mb-2">
                        <div className="font-semibold">{t("stagiaireAEteEnMesureDe")} :</div>
                        {ratingOptions.map((option) => (
                            <div key={option} className="text-center text-sm font-medium">
                                {t(option)}
                            </div>
                        ))}
                    </div>

                    {form.criteria.map((criterion) => (
                        <div key={criterion.id}
                        id={criterion.id}
                        className="grid grid-cols-[2fr,repeat(5,1fr)] gap-4 items-center py-2 border-t">
                            <div className={hasError(formData[form.id][criterion.id]) ? "text-red-700" : "text-black"}
                            >{t(criterion.label)}</div>
                            {ratingOptions.map((option) => (
                                <label key={option} 
                                    htmlFor={`${form.id}-${criterion.id}-${option}`} 
                                    className="flex justify-center h-full cursor-pointer hover:ring hover:ring-gray-300 rounded">
                                    <input
                                        type="radio"
                                        id={`${form.id}-${criterion.id}-${option}`}
                                        name={`${form.id}-${criterion.id}`}
                                        value={option}
                                        checked={getValue(formData[form.id][criterion.id]) === option}
                                        onChange={() => handleRadioChange(form.id, criterion.id, option)}
                                    />
                                </label>
                            ))}
                        </div>
                    ))}
                </div>

                <div className="space-y-2">
                    <label htmlFor={`${form.id}-commentaires`} className="block font-medium">{t("commentaires")} :</label>
                    <textarea
                        id={`${form.id}-commentaires`}
                        value={formData[form.id][form.id + "Commentaires"].value}
                        onChange={(e) => handleCommentChange(form.id, e.target.value)}
                        className="w-full min-h-[100px] p-2 border rounded resize-none"
                    />
                </div>
            </div>
        )
    );
};

export default EvaluerFormulaire;