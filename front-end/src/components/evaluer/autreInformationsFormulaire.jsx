import { Input } from '@material-tailwind/react';
import React from 'react';
import { useTranslation } from 'react-i18next';

const AppreciacionFormulaire = ({ 
  setRating, 
  rating, 
  appreciation, 
  setAppreciation, 
  discussion, 
  setDiscussion,
  hoursTotal,
  setHoursTotal,
  futureInternship,
  setFutureInternship,
  formationGoodEnough,
  setFormationGoodEnough,
  getFormValue
}) => {
  const { t } = useTranslation();

    const ratings = [
        "HABILITES_DEPASSENT_BEAUCOUP_ATTENTES",
        "HABILITES_DEPASSENT_ATTENTES",
        "HABILITES_REPONDENT_PLEINEMENT_ATTENTES",
        "HABILITES_REPONDENT_PARTIELLEMENT_ATTENTES",
        "HABILITES_REPONDENT_PAS_ATTENTES"
      ]

    return (
        <div className="w-full max-w-4xl bg-white rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-xl font-bold text-center mb-6">
        {t("appreciationGlobaleDuStagiaire").toUpperCase()}
      </h2>
      <form>
        <fieldset id='input_ratings'>
          {ratings.map((ratingObj, index) => (
            <label key={index} className="flex items-center space-x-3 p-3 rounded-lg cursor-pointer">
              <input
                type="radio"
                name="rating"
                value={ratingObj}
                checked={rating.value === ratingObj}
                onChange={() => setRating(getFormValue(ratingObj))}
                className="w-5 h-5"
              />
              <span className={`${rating.hasError ? "text-red-500" : ""}`}>{t(ratingObj)}</span>
            </label>
          ))}
        </fieldset>

        <div className="mt-6 space-y-2" id='input_appreciation'>
          <label htmlFor="appreciation" className={`font-bold block ${appreciation.hasError ? "text-red-500" : ""}`}>{t("preciserVotreAppreciation").toUpperCase()} :</label>
          <textarea
            id="appreciation"
            value={appreciation.value}
            onChange={(e) => setAppreciation(getFormValue(e.target.value))}
            className="w-full h-32 p-2 border rounded-md resize-none"
          />
        </div>

        <hr className='my-4' />

        <div className="">
          <p className={`font-medium mb-2 ${discussion.hasError ? "text-red-500" : ""}`}>
            {t("cetteEvaluationAEteDiscutee")} :
          </p>
          <div className="flex space-x-4" id="input_discussion">
            {[true,  false].map((option) => (
              <label key={option ? "oui_discu" : "non_discu"} className="flex items-center space-x-2 cursor-pointer">
                <input
                  type="radio"
                  name="discussion"
                  value={option}
                  checked={discussion.value === option}
                  onChange={(e) => setDiscussion(getFormValue(option))}
                  className="w-5 h-5 text-blue-600 border-gray-300 focus:ring-blue-500"
                />
                <span>{t(option ? "OUI" : "NON")}</span>
              </label>
            ))}
          </div>
        </div>

        <hr className='my-4' />

        <div id="input_hourTotalhoursTotal">
          <label className="block">
            <span className={`block ${hoursTotal.hasError ? "text-red-500" : ""}`}>{t("nombreDheureReelDencadrement")} :</span>
            <Input
              type="text"
              labelProps={{
                className: "hidden",
              }}
              value={hoursTotal.value}
              onChange={(e) => setHoursTotal(getFormValue(e.target.value))}
              className="focus:ring-1 focus:ring-gray-900 border-none ring-1 ring-gray-400"
              />
          </label>
        </div>

        <hr className='my-4' />

        <div className="space-y-4">
          <p className={`text-base ${futureInternship.hasError ? "text-red-500" : ""}`}>{t("entrepriseVoudraisAccueillirDautreStagiereProchaineFois")} :</p>
          <div className="flex space-x-4" id="input_futureInternship">
            {['OUI', 'NON', 'PEUT_ETRE'].map((option) => (
              <label key={option} className="flex items-center space-x-2 cursor-pointer">
                <input
                  type="radio"
                  name="futureInternship"
                  value={option}
                  checked={futureInternship.value === option}
                  onChange={(e) => setFutureInternship(getFormValue(e.target.value))}
                  className="w-5 h-5 text-blue-600 border-gray-300 focus:ring-blue-500"
                />
                <span>{t(option)}</span>
              </label>
            ))}
          </div>
          <p className={`text-sm ${formationGoodEnough.hasError ? "text-red-500" : ""}`}>{t("formationTechniqueSuvisantePourMandaStage")}?</p>
          <textarea
            id="input_goodEnough"
            value={formationGoodEnough.value}
            onChange={(e) => setFormationGoodEnough(getFormValue(e.target.value))}
            className="w-full h-32 p-2 border rounded-md resize-none"
          />
        </div>
      </form>
    </div>
    );
};

export default AppreciacionFormulaire;