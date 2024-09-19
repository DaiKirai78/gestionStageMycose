import { useState } from "react";
import { useTranslation } from "react-i18next";

function UploadForm() {
    const { t } = useTranslation();

    const [file, setFile] = useState(null);
    const [formData, setFormData] = useState({
        entrepriseName: "",
        employerName: "",
        email: "",
        website: "",
        title: "",
        location: "",
        salary: "",
        description: ""
    });

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmitFile = (e) => {
        e.preventDefault();
        console.log(file);
    };

    const handleSubmitForm = (e) => {
        e.preventDefault();
        console.log(formData);
    };

    const handleRemoveFile = () => {
        setFile(null);
        // Reset the file input
        if (document.getElementById("file")) {
            document.getElementById("file").value = "";
        }
    };

    const [showUpload, setShowUpload] = useState(true);

    return (
        <div className="flex items-center justify-center min-h-screen bg-orange-light">
            <div className="w-[70%] max-w-screen-lg mx-auto bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-bold mb-4 text-orange">
                    {showUpload ? t("uploadFile") : t("fillForm")}
                </h2>
                <div className="mb-4 flex justify-center space-x-4">
                    <button
                        className={`px-4 py-2 rounded-md ${showUpload ? "bg-orange text-white" : "bg-white border border-orange text-orange"} hover:bg-orange hover:text-white`}
                        onClick={() => setShowUpload(true)}
                    >
                        {t("showUpload")}
                    </button>
                    <button
                        className={`px-4 py-2 rounded-md ${!showUpload ? "bg-orange text-white" : "bg-white border border-orange text-orange"} hover:bg-orange hover:text-white`}
                        onClick={() => setShowUpload(false)}
                    >
                        {t("showForm")}
                    </button>
                </div>

                {showUpload ? (
                    <form onSubmit={handleSubmitFile} className="space-y-4">
                        <div className="relative">
                            <label htmlFor="file" className="block text-sm font-medium text-orange">
                                {t("uploadAFile")}
                            </label>
                            <div className="custom-file-input">
                                <input
                                    type="file"
                                    id="file"
                                    onChange={handleFileChange}
                                    className="hidden-file-input"
                                    disabled={file !== null}
                                />
                            </div>
                            {file && (
                                <div className="mt-2 flex items-center">
                                    <span className="text-sm text-gray-700">{file.name}</span>
                                    <button
                                        type="button"
                                        onClick={handleRemoveFile}
                                        className="ml-2 text-red-500 bg-gray-200 p-0.5 rounded hover:text-red-700 hover:bg-gray-300"
                                    >
                                        <span className="text-xl">✖</span>
                                    </button>
                                </div>
                            )}
                        </div>

                        <div className="flex justify-center">
                            <button
                                type="submit"
                                className="max-w-xs w-full bg-orange text-white p-2 rounded-lg hover:bg-orange-dark"
                                disabled={file === null}
                            >
                                {t("submit")}
                            </button>
                        </div>
                    </form>
                ) : (
                    <form onSubmit={handleSubmitForm} className="space-y-4">
                        {/* Formulaire */}
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
                                value={formData.employerName }
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
                                autoComplete="off"
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
                )}
            </div>
        </div>
    );
}

export default UploadForm;
