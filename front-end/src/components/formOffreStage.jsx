import { useState } from "react";
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

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmitForm = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post("http://localhost:8080/api/offres/upload", formData, {
            });
            console.log("Formulaire envoyé avec succès :", response.status);
        } catch (error) {
            console.error("Erreur lors de l'envoi du formulaire :", error);
        }
    };

    return (
        <form onSubmit={handleSubmitForm} className="space-y-4">
            <div>
                <label htmlFor="entrepriseName" className="block text-sm font-medium text-orange">
                    {t("companyName")}
                </label>
                <input
                    type="text"
                    id="entrepriseName"
                    name="entrepriseName"
                    value={formData.entrepriseName}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="organization"
                />
            </div>

            <div>
                <label htmlFor="employerName" className="block text-sm font-medium text-orange">
                    {t("employerName")}
                </label>
                <input
                    type="text"
                    id="employerName"
                    name="employerName"
                    value={formData.employerName}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="name"
                />
            </div>

            <div>
                <label htmlFor="email" className="block text-sm font-medium text-orange">
                    {t("email")}
                </label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="email"
                />
            </div>

            <div>
                <label htmlFor="website" className="block text-sm font-medium text-orange">
                    {t("website")}
                </label>
                <input
                    type="url"
                    id="website"
                    name="website"
                    value={formData.website}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    autoComplete="url"
                />
            </div>

            <div>
                <label htmlFor="title" className="block text-sm font-medium text-orange">
                    {t("title")}
                </label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="off"
                />
            </div>

            <div>
                <label htmlFor="location" className="block text-sm font-medium text-orange">
                    {t("location")}
                </label>
                <input
                    type="text"
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="off"
                />
            </div>

            <div>
                <label htmlFor="salary" className="block text-sm font-medium text-orange">
                    {t("salary")}
                </label>
                <input
                    type="text"
                    id="salary"
                    name="salary"
                    value={formData.salary}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    required
                    autoComplete="off"
                />
            </div>

            <div>
                <label htmlFor="description" className="block text-sm font-medium text-orange">
                    {t("description")}
                </label>
                <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    className="mt-1 p-2 block w-full border border-orange rounded-md"
                    rows={10}
                    required
                />
            </div>

            <div className="flex justify-center">
                <button
                    type="submit"
                    className="max-w-xs w-full bg-orange text-white p-2 rounded-lg hover:bg-orange-dark"
                >
                    {t("submit")}
                </button>
            </div>
        </form>
    );
}

export default FormOffreStage;
