import {useState} from "react";
import {useTranslation} from "react-i18next";

function UploadForm() {

    const{t} = useTranslation()

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

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Form submitted", formData, file);
    };

    const handleRemoveFile = () => {
        setFile(null);
        document.querySelector("input[type=file]").value = "";
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
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-orange">
                                {t("uploadAFile")}
                            </label>
                            <input
                                type="file"
                                onChange={handleFileChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                disabled={file !== null}
                            />
                            {file && (
                                <div className="mt-2 flex items-center">
                                    <span className="text-sm text-gray-700">{file.name}</span>
                                    <button type="button" onClick={handleRemoveFile}
                                            className="ml-2 text-red-500 bg-gray-200 p-0.5 rounded hover:text-red-700 hover:bg-gray-300">
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
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* Formulaire */}
                        <div>
                            <label className="block text-sm font-medium text-orange">{t("companyName")}</label>
                            <input
                                type="text"
                                name="entrepriseName"
                                value={formData.entrepriseName}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("employerName")}</label>
                            <input
                                type="text"
                                name="employerName"
                                value={formData.employerName}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("email")}</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("website")}</label>
                            <input
                                type="url"
                                name="website"
                                value={formData.website}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("title")}</label>
                            <input
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("location")}</label>
                            <input
                                type="text"
                                name="location"
                                value={formData.location}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("salary")}</label>
                            <input
                                type="text"
                                name="salary"
                                value={formData.salary}
                                onChange={handleInputChange}
                                className="mt-1 p-2 block w-full border border-orange rounded-md"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-orange">{t("description")}</label>
                            <textarea
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
                )}
            </div>
        </div>
    );
}

export default UploadForm;