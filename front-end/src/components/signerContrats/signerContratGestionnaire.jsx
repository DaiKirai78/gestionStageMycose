import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import PageIsLoading from "../pageIsLoading";
import SignerContratCard from "./signerContratCard";
import BoutonAvancerReculer from "../listeOffreEmployeur/boutonAvancerReculer";
import printJS from "print-js";
import {PDFDocument, rgb} from 'pdf-lib';
import fontkit from '@pdf-lib/fontkit';
import axios from "axios";

function SignerContratGestionnaire({setSelectedContract}) {
    const {t} = useTranslation();
    const [pages, setPages] = useState({minPages: 1, maxPages: null, currentPage: 1});
    const [isFetching, setIsFetching] = useState(true);
    const [contrats, setContrats] = useState([]);
    const [contrat, setContrat] = useState(null);
    const [nomPrenom, setNomPrenom] = useState([]);
    const [employeur, setEmployeur] = useState(null);
    const [gestionnaire, setGestionnaire] = useState(null);
    const [offreStage, setOffreStage] = useState(null);
    const [isContratsSignesView, setIsContratsSignesView] = useState(true);
    const [listeAnneesDispo, setListeAnneesDispo] = useState([]);
    const [filtreAnnee, setFiltreAnnee] = useState();

    useEffect(() => {
        fetchPages();
    }, [])

    useEffect(() => {
        if (isContratsSignesView) {
            fetchContratsNonSignes();
        } else {
            fetchContratsSignes();
        }

    }, [pages.currentPage, isContratsSignesView, filtreAnnee])

    useEffect(() => {
        fetchPrenomNomEtudiants();
        fetchMinimumAnneeDisponible();
    }, [contrats])

    useEffect(() => {
        if (contrat) {
            if (contrat.employeurId) fetchEmployeur(contrat.employeurId);
            if (contrat.gestionnaireStageId) fetchGestionnaire(contrat.gestionnaireStageId);
        }
    }, [contrat]);


    useEffect(() => {
        if (employeur && employeur.id) {
            fetchOffreStage(employeur.id);
        }
    }, [employeur]);

    useEffect(() => {
        imprimer(contrat);
    }, [offreStage]);

    async function fetchMinimumAnneeDisponible() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch('http://localhost:8080/gestionnaire/contrats/signes/anneeminimum', {
                method: 'GET',
                headers: {Authorization: `Bearer ${token}`}
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }

            let annees = JSON.parse(data)
            annees = annees.reverse();

            if (!filtreAnnee || JSON.stringify(listeAnneesDispo) !== JSON.stringify(annees)) {
                setListeAnneesDispo(annees);
                setFiltreAnnee(annees.at(0));
            }


        } catch (e) {
            console.log("Une erreur est survenue " + e);
        }
    }


    async function fetchPages() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch('http://localhost:8080/gestionnaire/contrats/non-signes/pages', {
                method: 'GET',
                headers: {Authorization: `Bearer ${token}`}
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }

            setPages(prevPages => ({
                ...prevPages,
                maxPages: data
            }));
        } catch (e) {
            console.log("Une erreur est survenue " + e);
        }
    }

    async function fetchPrenomNomEtudiants() {
        const token = localStorage.getItem("token");

        const listNomEtudiant = [];

        for (const contrat of contrats) {
            try {
                const response = await fetch(`http://localhost:8080/utilisateur/getPrenomNom?id=${contrat.etudiantId}`, {
                    method: 'GET',
                    headers: {Authorization: `Bearer ${token}`}
                });

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.text();

                if (!data) {
                    throw new Error('No data');
                }

                listNomEtudiant.push({
                    id: contrat.id,
                    nom: data
                })
            } catch (e) {
                console.log("Une erreur est survenue " + e);
            } finally {
                setIsFetching(false);
            }
        }

        setNomPrenom(listNomEtudiant);
    }

    async function fetchContratsNonSignes() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch(`http://localhost:8080/gestionnaire/contrats/non-signes?page=${pages.currentPage - 1}`, {
                method: 'GET',
                headers: {Authorization: `Bearer ${token}`}
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }

            setContrats(JSON.parse(data));
        } catch (e) {
            console.log("Une erreur est survenue " + e);
            setContrats([]);
            setIsFetching(false);
        }
    }

    async function fetchContratsSignes() {
        const token = localStorage.getItem("token");
        console.log(filtreAnnee);

        try {
            const response = await fetch(`http://localhost:8080/gestionnaire/contrats/signes?page=${pages.currentPage - 1}&annee=${filtreAnnee}`, {
                method: 'GET',
                headers: {Authorization: `Bearer ${token}`}
            });


            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.text();

            if (!data) {
                throw new Error('No data');
            }

            setContrats(JSON.parse(data));
        } catch (e) {
            console.log("Une erreur est survenue " + e);
            setContrats([]);
            setIsFetching(false);
        }
    }

    function getNomEtudiant(contrat) {
        const etudiant = nomPrenom.find(nom => nom.id === contrat.id);
        return etudiant ? etudiant.nom : '';
    }

    const fetchEmployeur = async (employeurId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`http://localhost:8080/gestionnaire/getUtilisateurById?id=${employeurId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setEmployeur(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération de l'employeur : `, e);
        }
    };

    const fetchGestionnaire = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.post("http://localhost:8080/utilisateur/me", null, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setGestionnaire(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération du gestionnaire de stage : `, e);
        }
    };

    const fetchOffreStage = async (employeurId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`http://localhost:8080/api/offres-stages/getOffreStage/${employeurId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setOffreStage(response.data);
        } catch (e) {
            console.error(`Erreur lors de la récupération de l'offre de stage : `, e);
        }
    };

    const dureeStage = (session, year) => {
        if (session === "AUTOMNE")
            return `${t("21aout") + year} - ${t("27decembre") + year}`;
        if (session === "HIVER")
            return `${t("25janvier") + year} - ${t("30mai") + year}`;
        if (session === "ETE")
            return `${t("31mai") + year} - ${t("20aout") + year}`;
    }

    const nombreDeSemaine = (session) => {
        if (session === "AUTOMNE" || session === "HIVER")
            return "18";
        if (session === "ETE")
            return "11";
    }

    const returnPrettyDate = (date) => {
        return date.substring(0, 10);
    }

    async function imprimer(contrat) {
        try {
            setContrat(contrat);
            const pdfDoc = await PDFDocument.create();
            pdfDoc.registerFontkit(fontkit);
            const boldFontBytes = await fetch('/fonts/arialceb.ttf').then(res => res.arrayBuffer());
            const boldFont = await pdfDoc.embedFont(boldFontBytes);
            const signatureEtudiant = await pdfDoc.embedPng(contrat.signatureEtudiant);
            const signatureEmployeur = await pdfDoc.embedPng(contrat.signatureEmployeur);
            const signatureGestionnaire = await pdfDoc.embedPng(contrat.signatureGestionnaire);

            const page1 = pdfDoc.addPage();
            const page2 = pdfDoc.addPage();
            const page3 = pdfDoc.addPage();

            const sections = [
                {title: 'ENDROIT DU STAGE', content: `Adresse: ${offreStage.location || "Non spécifiée"}`},
                {
                    title: 'DURÉE DU STAGE',
                    content: `Dates: ${dureeStage(offreStage.session, offreStage.annee)} \nNombre total de semaines: ${nombreDeSemaine(offreStage.session)}`,
                },
                {
                    title: 'HORAIRE DE TRAVAIL',
                    content: `Horaire de travail: ${offreStage.horaireJournee} \nNombre total d’heures par semaine: ${offreStage.heuresParSemaine}`,
                },
                {
                    title: 'SALAIRE',
                    content: `Salaire horaire: ${offreStage.salary}$`,
                },
            ];

            const startX = 50;
            let startY = 400;
            const cellWidth = 500;
            const headerHeight = 25;
            const contentHeight = 50;

            sections.forEach((section) => {
                page2.drawRectangle({
                    x: startX,
                    y: startY,
                    width: cellWidth,
                    height: headerHeight,
                    color: rgb(0.85, 0.85, 0.85),
                });

                page2.drawText(section.title, {
                    x: startX + 5,
                    y: startY + 8,
                    size: 12,
                    font: boldFont,
                });

                startY -= headerHeight;

                page2.drawText(section.content, {
                    x: startX + 5,
                    y: startY + 10,
                    size: 10,
                });

                page2.drawLine({
                    start: {x: startX, y: startY - contentHeight},
                    end: {x: startX + cellWidth, y: startY - contentHeight},
                    thickness: 1,
                    color: rgb(0.75, 0.75, 0.75),
                });

                startY -= contentHeight;
            });

            page1.drawText("CONTRAT DE STAGE", {x: 155, y: 450, size: 30, font: boldFont});

            page2.drawText("ENTENTE DE STAGE INTERVENUE ENTRE LES PARTIES SUIVANTES", {
                x: 105,
                y: 770,
                size: 12,
                font: boldFont
            });
            page2.drawText("Dans le cadre de la formule ATE, les parties citées ci-dessous :", {
                x: 135,
                y: 720,
                size: 12
            })

            page2.drawText(`Le gestionnaire de stage : ${gestionnaire.prenom + " " + gestionnaire.nom}`, {
                x: 175,
                y: 690,
                size: 12
            });
            page2.drawText("et", {x: 300, y: 640, size: 12, font: boldFont})
            page2.drawText(`L'employeur : ${employeur.prenom + " " + employeur.nom}`, {x: 235, y: 595, size: 12});
            page2.drawText("et", {x: 300, y: 550, size: 12, font: boldFont})
            page2.drawText(`L'étudiant : ${getNomEtudiant(contrat)}`, {x: 235, y: 510, size: 12});
            page2.drawText("Conviennent des conditions de stage suivantes :", {x: 180, y: 460, size: 12});

            startY = 780;

            page3.drawText('TACHES ET RESPONSABILITES DU STAGIAIRE', {
                x: startX,
                y: startY,
                size: 12,
                font: boldFont,
            });

            startY -= 20;
            page3.drawRectangle({
                x: startX,
                y: startY - 40,
                width: 500,
                height: 40,
                borderColor: rgb(0, 0, 0),
                borderWidth: 1,
            });
            page3.drawText(`${offreStage.description || 'Pas de description'}`, {
                x: startX + 5,
                y: startY - 25,
                size: 10,
            });

            startY -= 70;
            page3.drawText('RESPONSABILITES', {
                x: startX + 190,
                y: startY,
                size: 12,
                font: boldFont,
            });

            const responsibilities = [
                {
                    titre: "Le Collège s'engage à :",
                    engagement: "Assurer un encadrement pédagogique en désignant un professeur référent pour l’étudiant.\n" +
                        "Veiller au bon déroulement du stage et s’assurer que les missions confiées respectent les objectifs pédagogiques.\n" +
                        "Évaluer le stage en recueillant les retours de l’entreprise et de l’étudiant pour valider les compétences acquises."
                },
                {
                    titre: "L’entreprise s’engage à :",
                    engagement: "Fournir à l’étudiant des missions formatrices en lien avec son programme d’études.\n" +
                        "Assurer un encadrement professionnel et des conditions de travail conformes aux normes.\n" +
                        "Communiquer au collège toute information pertinente sur le déroulement du stage."
                },
                {
                    titre: "L’étudiant s’engage à :",
                    engagement: "Respecter les règles et horaires de l’entreprise et faire preuve de professionnalisme.\n" +
                        "Réaliser les missions confiées avec sérieux et en informer régulièrement son tuteur.\n" +
                        "Remettre un rapport de stage détaillé au collège, selon les exigences fixées."
                }
            ];

            responsibilities.forEach((responibility, index) => {
                startY -= 30;
                page3.drawText(responibility.titre, {
                    x: startX,
                    y: startY,
                    size: 10,
                });
                startY -= 20;
                page3.drawText(responibility.engagement, {
                    x: startX + 20,
                    y: startY,
                    size: 10,
                });
                startY -= 40;
            });

            startY -= 40;
            page3.drawRectangle({
                x: startX,
                y: startY-30,
                width: 500,
                height: 20,
                color: rgb(0.85, 0.85, 0.85),
            });
            page3.drawText('SIGNATURES', {
                x: startX + 5,
                y: startY - 25,
                size: 12,
                font: boldFont,
            });

            startY -= 60;
            page3.drawText(`Les parties s’engagent a respecter cette entente de stage`, {
                x: startX + 10,
                y: startY + 12,
                size: 10,
                font: boldFont,
            });
            startY -= 15;
            page3.drawText("En foi de quoi les parties ont signé,", {
                x: startX + 10,
                y: startY + 12,
                size: 10,
            });

            const signatureSections = [
                {
                    title: "L’étudiant(e) :",
                    signature: signatureEtudiant,
                    date: returnPrettyDate(contrat.dateSignatureEtudiant),
                    name: nomPrenom[0].nom
                },
                {
                    title: "L’employeur :",
                    signature: signatureEmployeur,
                    date: returnPrettyDate(contrat.dateSignatureEmployeur),
                    name: employeur.prenom + " " + employeur.nom
                },
                {
                    title: "Le gestionnaire de stage :",
                    signature: signatureGestionnaire,
                    date: returnPrettyDate(contrat.dateSignatureGestionnaire),
                    name: gestionnaire.prenom + " " + gestionnaire.nom
                },
            ];

            startY -= 25;
            signatureSections.forEach((section) => {
                page3.drawRectangle({
                    x: startX,
                    y: startY + 10,
                    width: 500,
                    height: 15,
                    borderColor: rgb(0, 0, 0),
                    borderWidth: 1
                });
                page3.drawText(section.title, {
                    x: startX + 5,
                    y: startY + 14,
                    size: 10,
                    font: boldFont,
                });

                startY -= 15;
                page3.drawRectangle({
                    x: startX,
                    y: startY - 55,
                    width: 250,
                    height: 80,
                    borderColor: rgb(0, 0, 0),
                    borderWidth: 1
                });
                page3.drawRectangle({
                    x: startX + 250,
                    y: startY - 55,
                    width: 250,
                    height: 80,
                    borderColor: rgb(0, 0, 0),
                    borderWidth: 1
                });
                page3.drawRectangle({
                    x: startX,
                    y: startY - 55,
                    width: 500,
                    height: 20,
                    borderColor: rgb(0, 0, 0),
                    borderWidth: 1
                });

                if (section.signature) {
                    page3.drawImage(section.signature, {
                        x: startX + 45,
                        y: startY - 30,
                        width: 150, height: 50,

                    });
                }
                page3.drawText(`${section.date}`, {
                    x: startX + 255,
                    y: startY - 15,
                    size: 10,
                });

                startY -= 15;
                page3.drawText(`${section.name}`, {
                    x: startX + 5,
                    y: startY - 33,
                    size: 10,
                });
                page3.drawText("Date", {
                    x: startX + 255,
                    y: startY - 33,
                    size: 10,
                });

                startY -= 65;
            });

            const pdfBytes = await pdfDoc.saveAsBase64();

            printJS({printable: pdfBytes, type: 'pdf', base64: true});
        } catch (error) {
            console.log("An error occurred: ", error);
        }
    }

    if (isFetching)
        return (
            <div className='w-full h-full bg-orange-light flex flex-col justify-center items-center'>
                <PageIsLoading/>
            </div>
        )

    return (
        <div className="flex-1 w-full h-full bg-orange-light flex flex-col items-center p-8">
            <h1 className='text-3xl md:text-4xl font-bold text-center mb-5'>{t("signerContrats")}</h1>
            <div className="space-x-10">
                <button
                    className={`${isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`}
                    onClick={() => setIsContratsSignesView(true)}>{t("contratsASigner")}</button>
                <button
                    className={`${!isContratsSignesView ? 'underline decoration-2 decoration-deep-orange-300' : ''} border border-orange rounded p-2 hover:bg-opacity-90 hover:shadow-lg`}
                    onClick={() => setIsContratsSignesView(false)}>{t("contratsSignes")}</button>
            </div>
            <div className="my-10 w-1/2">
                {
                    contrats && contrats.length > 0 ? (
                            <>
                                {!isContratsSignesView ?
                                    <div className="flex flex-col w-1/2 mx-auto mb-6">
                                        <select value={filtreAnnee} onChange={(e) => setFiltreAnnee(e.target.value)}
                                                className="bg-orange px-4 py-2 rounded text-white mt-3">
                                            {listeAnneesDispo.map((annee) => {
                                                return (
                                                    <option value={annee}>{annee}</option>
                                                );
                                            })}
                                        </select>
                                    </div>
                                    : null}
                                {
                                    contrats.map((contrat, index) => {
                                            return (
                                                isContratsSignesView ?

                                                    <SignerContratCard
                                                        contrat={contrat}
                                                        nomPrenom={getNomEtudiant(contrat)}
                                                        index={index}
                                                        setSelectedContract={setSelectedContract}
                                                        key={"contrat" + index}/>

                                                    :

                                                    <div
                                                        className='flex w-full justify-between items-center p-4 shadow mb-4 rounded bg-white'>
                                                        <p className='text-lg'>{getNomEtudiant(contrat)}</p>
                                                        <button className='bg-orange rounded p-2 text-white hover:bg-opacity-90'
                                                                onClick={() => setContrat(contrat)}>
                                                            {t("imprimer")}
                                                        </button>
                                                    </div>
                                            )
                                        }
                                    )
                                }
                                <BoutonAvancerReculer pages={pages} setPages={setPages} margins={"my-4"}/>
                            </>
                        ) :
                        <p className="mt-5">{t("noContratToSign")}</p>
                }
            </div>
        </div>
    );
}

export default SignerContratGestionnaire;