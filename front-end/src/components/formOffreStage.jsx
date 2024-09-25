import {useRef, useState} from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";

function FormOffreStage() {
    const { t } = useTranslation();

    const [formData, setFormData] = useState({
        entrepriseName: "",
        employerName: "",
        email: "",
        website: "",
        title: "",
        location: "",
        salary: "",
        description: "",
    });

    const [error, setError] = useState({
        entrepriseName: "",
        employerName: "",
        email: "",
        website: "",
        title: "",
        location: "",
        salary: "",
        description: "",
    });

    const [successMessage, setSuccessMessage] = useState("");
    const [submitError, setSubmitError] = useState("");

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmitForm = async (e) => {
        e.preventDefault();

        let valid = true;
        let errors = {
            entrepriseName: "",
            employerName: "",
            email: "",
            website: "",
            title: "",
            location: "",
            salary: "",
            description: "",
        };

        // Validation pour 'entrepriseName'
        if (!formData.entrepriseName) {
            errors.entrepriseName = t("entrepriseNameRequired");
            valid = false;
        }

        // Validation pour 'employerName'
        if (!formData.employerName) {
            errors.employerName = t("employerNameRequired");
            valid = false;
        }

        // Validation pour 'email'
        if (!/\S+@\S+\.\S+/.test(formData.email)) {
            errors.email = t("emailInvalid");
            valid = false;
        }

        if (!formData.email) {
            errors.email = t("emailRequired");
            valid = false;
        }

        // Validation pour 'website'
        if (formData.website && !/^[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.website)) {
            errors.website = t("websiteInvalid");
            valid = false;
        }

        if (!formData.website) {
            errors.website = t("websiteRequired");
            valid = false;
        }

        // Validation pour 'title'
        if (!formData.title) {
            errors.title = t("titleRequired");
            valid = false;
        }

        // Validation pour 'location'
        if (!formData.location) {
            errors.location = t("locationRequired");
            valid = false;
        }

        // Validation pour 'salary'
        if (!formData.salary) {
            errors.salary = t("salaryRequired");

            valid = false;
        }

        if (formData.salary && isNaN(formData.salary)) {
            errors.salary = t("salaryInvalid");
            valid = false;
        }

        // Validation pour 'description'
        if (!formData.description) {
            errors.description = t("descriptionRequired");
            valid = false;
        }


        setError(errors);

        if (!valid) {
            return;
        }

        try {
            const response = await axios.post("http://localhost:8080/api/offres/upload", formData);
            console.log("Formulaire envoyé avec succès :", response.data);
            setSuccessMessage(t("formSubmissionSuccess"));
            setSubmitError("");
            setFormData({
                entrepriseName: "",
                employerName: "",
                email: "",
                website: "",
                title: "",
                location: "",
                salary: "",
                description: "",
            });
        } catch (error) {
            console.error("Erreur lors de l'envoi du formulaire :", error);
            setSubmitError(t("formSubmissionError"));
            setSuccessMessage("");
        }
    };



    return (
        <form onSubmit={handleSubmitForm} className="w-full">
            <h1 className="text-2xl text-black">{t("companyDetails")}</h1>
            <hr className="border-1 border-black"/>
            <div className="space-y-2">
                <label htmlFor="entrepriseName" className="block text-sm font-medium text-black mt-4">
                    {t("companyName")}
                </label>
                <input
                    type="text"
                    id="entrepriseName"
                    name="entrepriseName"
                    value={formData.entrepriseName}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.entrepriseName ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="organization"
                />
                {error.entrepriseName && <p className="text-red-500 text-sm mt-1">{error.entrepriseName}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="employerName" className="block text-sm font-medium text-black mt-4">
                    {t("employerName")}
                </label>
                <input
                    type="text"
                    id="employerName"
                    name="employerName"
                    value={formData.employerName}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.employerName ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="name"
                />
                {error.employerName && <p className="text-red-500 text-sm mt-1">{error.employerName}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="email" className="block text-sm font-medium text-black mt-4">
                    {t("email")}
                </label>
                <input
                    type="text"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.email ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="email"
                />
                {error.email && <p className="text-red-500 text-sm mt-1">{error.email}</p>}
            </div>


            <div className="space-y-2">
                <label htmlFor="website" className="block text-sm font-medium text-black mt-4">
                    {t("website")}
                </label>
                <input
                    type="text"
                    id="website"
                    name="website"
                    value={formData.website}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.website ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="url"
                />
                {error.website && <p className="text-red-500 text-sm mt-1">{error.website}</p>}
            </div>


            <h1 className="text-2xl text-black mt-4">{t("internshipDetails")}</h1>
            <hr className="border-1 border-black"/>

            <div className="space-y-2">
                <label htmlFor="title" className="block text-sm font-medium text-black mt-4">
                    {t("title")}
                </label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.title ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.title && <p className="text-red-500 text-sm mt-1">{error.title}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="location" className="block text-sm font-medium text-black mt-4">
                    {t("location")}
                </label>
                <input
                    type="text"
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.location ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.location && <p className="text-red-500 text-sm mt-1">{error.location}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="salary" className="block text-sm font-medium text-black mt-4">
                    {t("salary")}
                </label>
                <input
                    type="text"
                    id="salary"
                    name="salary"
                    value={formData.salary}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.salary ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    autoComplete="off"
                />
                {error.salary && <p className="text-red-500 text-sm mt-1">{error.salary}</p>}
            </div>

            <div className="space-y-2">
                <label htmlFor="description" className="block text-sm font-medium text-black mt-4">
                    {t("description")}
                </label>
                <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    className={`mt-1 p-2 block w-full border ${error.description ? 'border-red-500' : 'border-black'} rounded-md bg-transparent`}
                    rows={10}
                />
                {error.description && <p className="text-red-500 text-sm mt-1">{error.description}</p>}
            </div>

            {successMessage && <p className="text-green-500 text-sm mt-4">{successMessage}</p>}
            {submitError && <p className="text-red-500 text-sm mt-4">{submitError}</p>}

            <div className="flex justify-center mt-4">
                <button
                    type="submit"
                    className="max-w-xs w-full bg-[#FE872B] p-2 rounded-lg hover:bg-orange text-white"
                >
                    {t("submit")}
                </button>
            </div>
        </form>
    );
}

export default FormOffreStage;
