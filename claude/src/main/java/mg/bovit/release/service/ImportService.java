package mg.bovit.release.service;

import mg.bovit.release.model.*;
import mg.bovit.release.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    private CaisseRepository caisseRepository;
    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;
    @Autowired
    private CauseCaisseRepository causeCaisseRepository;

    @Autowired
    private BovinRepository bovinRepository;
    @Autowired
    private PeseBovinRepository peseBovinRepository;
    @Autowired
    private RaceRepository raceRepository;

    @Autowired
    private InventaireRepository inventaireRepository;
    @Autowired
    private InventaireDetailRepository inventaireDetailRepository;
    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ContratRepository contratRepository;
    @Autowired
    private PayementEmployeeRepository payementEmployeeRepository;
    @Autowired
    private TypePayementEmployeeRepository typePayementEmployeeRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ------------------------------------------------------------
    // 1. IMPORT CAISSE + MOUVEMENTS
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importCaisseEtMouvements(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet caisseSheet = workbook.getSheet("Caisse");
            Sheet mouvementSheet = workbook.getSheet("Mouvement");

            Map<String, Caisse> caisses = new HashMap<>();
            if (caisseSheet != null && caisseSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des caisses à partir de la feuille 'Caisse'");
                for (Row row : caisseSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String libelle = getStringCell(row, 0);
                    Double solde = getDoubleCell(row, 1);
                    Caisse c = new Caisse();
                    c.setLibelle(libelle);
                    c.setMontant_actuelle(solde);
                    caisses.put(libelle, caisseRepository.save(c));
                    logger.debug("Caisse créée : {}", libelle);
                }
            } else {
                logger.info("Feuille 'Caisse' vide ou absente, aucune caisse créée.");
            }

            if (mouvementSheet != null && mouvementSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des mouvements à partir de la feuille 'Mouvement'");
                for (Row row : mouvementSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String libelleCaisse = getStringCell(row, 0);
                    Date date = parseDate(getStringCell(row, 1), "Mouvement", row);
                    Double montant = getDoubleCell(row, 2);
                    String causeLibelle = getStringCell(row, 3);

                    Caisse caisse = caisses.get(libelleCaisse);
                    if (caisse == null) {
                        caisse = uniqueResult(
                            () -> caisseRepository.findByLibelle(libelleCaisse)
                                    .orElseThrow(() -> new RuntimeException("Caisse introuvable : " + libelleCaisse)),
                            "Mouvement",
                            row,
                            libelleCaisse
                        );
                    }

                    CauseCaisse cause = uniqueResult(
                        () -> causeCaisseRepository.findByLibelle(causeLibelle)
                                .orElseGet(() -> {
                                    CauseCaisse newCause = new CauseCaisse();
                                    newCause.setLibelle(causeLibelle);
                                    return causeCaisseRepository.save(newCause);
                                }),
                        "Mouvement",
                        row,
                        causeLibelle
                    );

                    double nouveauSolde = caisse.getMontant_actuelle() + montant;
                    if (nouveauSolde < 0) {
                        throw new RuntimeException(
                            String.format("Solde négatif pour la caisse '%s' après mouvement de %.2f (solde actuel : %.2f) - ligne %d",
                                    libelleCaisse, montant, caisse.getMontant_actuelle(), row.getRowNum()+1)
                        );
                    }
                    caisse.setMontant_actuelle(nouveauSolde);
                    caisseRepository.save(caisse);

                    MvtCaisse mvt = new MvtCaisse();
                    mvt.setCaisse(caisse);
                    mvt.setCauseCaisse(cause);
                    mvt.setMontant(montant);
                    mvt.setDate(date);
                    mvtCaisseRepository.save(mvt);
                    logger.debug("Mouvement ajouté : {} - {} - {}", libelleCaisse, montant, date);
                }
            } else {
                logger.info("Feuille 'Mouvement' vide ou absente, aucun mouvement importé.");
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // 2. IMPORT BOVINS + PESÉES
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importBovinsEtPesees(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet bovinSheet = workbook.getSheet("Bovin");
            Sheet peseeSheet = workbook.getSheet("Pesee");

            Map<String, Bovin> bovins = new HashMap<>();
            if (bovinSheet != null && bovinSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des bovins à partir de la feuille 'Bovin'");
                for (Row row : bovinSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    Double refDouble = getDoubleCell(row, 0);
                    String ref = (refDouble == null) ? "" : String.valueOf(refDouble.longValue());
                    String raceNom = getStringCell(row, 1);
                    Date dateAchat = parseDate(getStringCell(row, 2), "Bovin", row);
                    Date dateVente = getStringCell(row, 3).isEmpty() ? null : parseDate(getStringCell(row, 3), "Bovin", row);
                    Double prixAchat = getDoubleCell(row, 4);
                    Double prixVente = getDoubleCell(row, 5);
                    Double poidsAchat = getDoubleCell(row, 6);
                    Double poidsVente = getDoubleCell(row, 7);

                    Race race = getOrCreateRace(raceNom, row);

                    Bovin bovin = new Bovin();
                    bovin.setRace(race);
                    bovin.setDate_achat(dateAchat);
                    bovin.setDate_vente(dateVente);
                    bovin.setPrix_achat(prixAchat);
                    bovin.setPrix_vente(prixVente);
                    bovin.setPoids_achat(poidsAchat);
                    bovin.setPoids_vente(poidsVente);
                    bovins.put(ref, bovinRepository.save(bovin));
                    logger.debug("Bovin créé avec ref : {}", ref);
                }
            } else {
                logger.info("Feuille 'Bovin' vide ou absente, aucun bovin créé.");
            }

            if (peseeSheet != null && peseeSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des pesées à partir de la feuille 'Pesee'");
                for (Row row : peseeSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    Double bovinRefDouble = getDoubleCell(row, 0);
                    String bovinRef = (bovinRefDouble == null) ? "" : String.valueOf(bovinRefDouble.longValue());
                    Date datePese = parseDate(getStringCell(row, 1), "Pesee", row);
                    Double poidsApres = getDoubleCell(row, 2);

                    Bovin bovin = bovins.get(bovinRef);
                    if (bovin == null) {
                        Long id = Long.parseLong(bovinRef);
                        bovin = bovinRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Bovin introuvable avec l'ID : " + bovinRef));
                    }

                    PeseBovin pesee = new PeseBovin();
                    pesee.setBovin(bovin);
                    pesee.setDate_pese(datePese);
                    pesee.setPoids_apres(poidsApres);
                    peseBovinRepository.save(pesee);
                    logger.debug("Pesée ajoutée pour bovin ref {} le {}", bovinRef, datePese);
                }
            } else {
                logger.info("Feuille 'Pesee' vide ou absente, aucune pesée importée.");
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    private Race getOrCreateRace(String nom, Row row) {
        List<Race> races = raceRepository.findAllByNom(nom);
        if (races.isEmpty()) {
            Race newRace = new Race();
            newRace.setNom(nom);
            newRace.setDescriptions("");
            Race saved = raceRepository.save(newRace);
            logger.info("Nouvelle race créée : {}", nom);
            return saved;
        } else if (races.size() > 1) {
            races.sort(Comparator.comparingLong(Race::getId));
            Race selected = races.get(0);
            logger.warn("⚠️ Plusieurs races trouvées pour '{}' (ligne {}). Utilisation de l'ID {}.",
                        nom, row.getRowNum() + 1, selected.getId());
            return selected;
        }
        return races.get(0);
    }

    // ------------------------------------------------------------
    // 3. IMPORT INVENTAIRE + DÉTAILS
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importInventaireEtDetails(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet inventaireSheet = workbook.getSheet("Inventaire");
            Sheet detailSheet = workbook.getSheet("Inventaire_detail");

            Map<String, Inventaire> inventaires = new HashMap<>();
            if (inventaireSheet != null && inventaireSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des inventaires à partir de la feuille 'Inventaire'");
                for (Row row : inventaireSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    Double refDouble = getDoubleCell(row, 0);
                    String ref = (refDouble == null) ? "" : String.valueOf(refDouble.longValue());
                    Date date = parseDate(getStringCell(row, 1), "Inventaire", row);
                    String libelle = getStringCell(row, 2);

                    Inventaire inv = new Inventaire();
                    inv.setDateInventaire(date);
                    inv.setLibelle(libelle);
                    inventaires.put(ref, inventaireRepository.save(inv));
                    logger.debug("Inventaire créé : {}", libelle);
                }
            } else {
                logger.info("Feuille 'Inventaire' vide ou absente, aucun inventaire créé.");
            }

            if (detailSheet != null && detailSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des détails d'inventaire à partir de la feuille 'Inventaire_detail'");
                for (Row row : detailSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String refInventaire = getStringCell(row, 0);
                    String materielLibelle = getStringCell(row, 1);
                    Double quantiteInitiale = getDoubleCell(row, 2);
                    Double quantiteFinale = getDoubleCell(row, 3);
                    String observations = getStringCell(row, 4);

                    Inventaire inv = inventaires.get(refInventaire);
                    if (inv == null) {
                        Long id = Long.parseLong(refInventaire);
                        inv = inventaireRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Inventaire introuvable avec l'ID : " + refInventaire));
                    }

                    Materiel materiel = uniqueResult(
                        () -> materielRepository.findByLibelle(materielLibelle)
                                .orElseGet(() -> {
                                    Materiel m = new Materiel();
                                    m.setLibelle(materielLibelle);
                                    return materielRepository.save(m);
                                }),
                        "Inventaire_detail",
                        row,
                        materielLibelle
                    );

                    InventaireDetail detail = new InventaireDetail();
                    detail.setInventaire(inv);
                    detail.setMateriel(materiel);
                    detail.setQuantiteInitiale(quantiteInitiale);
                    detail.setQuantiteFinale(quantiteFinale);
                    detail.setObservations(observations);
                    inventaireDetailRepository.save(detail);
                    logger.debug("Détail d'inventaire ajouté pour matériel {}", materielLibelle);
                }
            } else {
                logger.info("Feuille 'Inventaire_detail' vide ou absente, aucun détail importé.");
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // 4. IMPORT PAIEMENTS + EMPLOYÉS + CONTRATS
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importPaiementsEmployesContrats(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet employeeSheet = workbook.getSheet("Employé");
            Sheet contratSheet = workbook.getSheet("Contrat");
            Sheet paiementSheet = workbook.getSheet("Paiement");

            Map<String, Employee> employees = new HashMap<>();
            if (employeeSheet != null && employeeSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des employés à partir de la feuille 'Employé'");
                for (Row row : employeeSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateNaissance = parseDate(getStringCell(row, 2), "Employé", row);
                    Date dateEntree = parseDate(getStringCell(row, 3), "Employé", row);

                    Employee e = new Employee();
                    e.setNom(nom);
                    e.setPrenom(prenom);
                    e.setDateNaissance(dateNaissance);
                    e.setDateEntree(dateEntree);
                    employees.put(nom + "_" + prenom, employeeRepository.save(e));
                    logger.debug("Employé créé : {} {}", nom, prenom);
                }
            } else {
                logger.info("Feuille 'Employé' vide ou absente, aucun employé créé.");
            }

            Map<String, Contrat> contrats = new HashMap<>();
            if (contratSheet != null && contratSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des contrats à partir de la feuille 'Contrat'");
                for (Row row : contratSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateDebut = parseDate(getStringCell(row, 2), "Contrat", row);
                    Date dateFin = getStringCell(row, 3).isEmpty() ? null : parseDate(getStringCell(row, 3), "Contrat", row);
                    Double salaire = getDoubleCell(row, 4);

                    Employee emp = employees.get(nom + "_" + prenom);
                    if (emp == null) {
                        emp = uniqueResult(
                            () -> employeeRepository.findByNomAndPrenom(nom, prenom)
                                    .orElseThrow(() -> new RuntimeException("Employé introuvable : " + nom + " " + prenom)),
                            "Contrat",
                            row,
                            nom + " " + prenom
                        );
                    }

                    Contrat c = new Contrat();
                    c.setEmployee(emp);
                    c.setDateDebut(dateDebut);
                    c.setDateFin(dateFin);
                    c.setSalaire(java.math.BigDecimal.valueOf(salaire));
                    contrats.put(nom + "_" + prenom + "_" + dateDebut.toString(), contratRepository.save(c));
                    logger.debug("Contrat créé pour {} {} débutant le {}", nom, prenom, dateDebut);
                }
            } else {
                logger.info("Feuille 'Contrat' vide ou absente, aucun contrat créé.");
            }

            if (paiementSheet != null && paiementSheet.getPhysicalNumberOfRows() > 1) {
                logger.info("Import des paiements à partir de la feuille 'Paiement'");
                for (Row row : paiementSheet) {
                    if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateDebutContrat = parseDate(getStringCell(row, 2), "Paiement", row);
                    Date mois = parseDate(getStringCell(row, 3), "Paiement", row);
                    Double montant = getDoubleCell(row, 4);
                    String typePaiementLibelle = getStringCell(row, 5);

                    String key = nom + "_" + prenom + "_" + dateDebutContrat.toString();
                    Contrat contrat = contrats.get(key);
                    if (contrat == null) {
                        Employee emp = uniqueResult(
                            () -> employeeRepository.findByNomAndPrenom(nom, prenom)
                                    .orElseThrow(() -> new RuntimeException("Employé introuvable : " + nom + " " + prenom)),
                            "Paiement",
                            row,
                            nom + " " + prenom
                        );
                        contrat = uniqueResult(
                            () -> contratRepository.findByEmployeeAndDateDebut(emp, dateDebutContrat)
                                    .orElseThrow(() -> new RuntimeException("Contrat introuvable pour " + nom + " " + prenom + " débutant le " + dateDebutContrat)),
                            "Paiement",
                            row,
                            "contrat"
                        );
                    }

                    TypePayementEmployee type = uniqueResult(
                        () -> typePayementEmployeeRepository.findByLibelle(typePaiementLibelle)
                                .orElseGet(() -> {
                                    TypePayementEmployee t = new TypePayementEmployee();
                                    t.setLibelle(typePaiementLibelle);
                                    return typePayementEmployeeRepository.save(t);
                                }),
                        "Paiement",
                        row,
                        typePaiementLibelle
                    );

                    PayementEmployee paiement = new PayementEmployee();
                    paiement.setEmployee(contrat.getEmployee());
                    paiement.setTypePayementEmployee(type);
                    paiement.setMois(mois);
                    paiement.setMontant(java.math.BigDecimal.valueOf(montant));
                    paiement.setRestePaye(java.math.BigDecimal.ZERO);
                    paiement.setDatePayement(new java.sql.Timestamp(System.currentTimeMillis()));
                    payementEmployeeRepository.save(paiement);
                    logger.debug("Paiement enregistré pour {} {} - mois {}", nom, prenom, mois);
                }
            } else {
                logger.info("Feuille 'Paiement' vide ou absente, aucun paiement importé.");
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // MÉTHODE UNIQUE RESULT POUR GÉRER LES DOUBLONS
    // ------------------------------------------------------------
    private <T> T uniqueResult(Supplier<T> supplier, String feuille, Row row, String valeur) {
        try {
            return supplier.get();
        } catch (IncorrectResultSizeDataAccessException | NonUniqueResultException e) {
            throw new RuntimeException(
                "Erreur ligne " + (row.getRowNum() + 1) + " feuille " + feuille +
                " : plusieurs enregistrements trouvés pour '" + valeur + "'."
            );
        }
    }

    // ------------------------------------------------------------
    // MÉTHODES DE PARSING AVEC GESTION D'ERREURS
    // ------------------------------------------------------------
    private Date parseDate(String dateStr, String feuille, Row row) {
        if (dateStr.isEmpty()) {
            throw new RuntimeException("Date vide ligne " + (row.getRowNum() + 1) + " feuille " + feuille);
        }
        try {
            return Date.valueOf(LocalDate.parse(dateStr, DATE_FORMAT));
        } catch (Exception e) {
            throw new RuntimeException(
                "Format de date invalide ligne " + (row.getRowNum() + 1) + " feuille " + feuille +
                " : " + dateStr
            );
        }
    }

    // ------------------------------------------------------------
    // VÉRIFICATION SI UNE LIGNE EST VIDE
    // ------------------------------------------------------------
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK
                && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------
    // MÉTHODES ROBUSTES DE LECTURE DES CELLULES
    // ------------------------------------------------------------
    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
            default:
                return "";
        }
    }

    private Double getDoubleCell(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    try {
                        return Double.parseDouble(cell.getStringCellValue().trim());
                    } catch (NumberFormatException ex) {
                        return 0.0;
                    }
                }
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue() ? 1.0 : 0.0;
            case BLANK:
            default:
                return 0.0;
        }
    }

    // ------------------------------------------------------------
    // (Optionnel) GÉNÉRATION DES MODÈLES EXCEL
    // ------------------------------------------------------------
    public byte[] generateModel(String type) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            switch (type) {
                case "caisse":
                    generateCaisseModel(workbook);
                    break;
                case "pesee":
                    generatePeseeModel(workbook);
                    break;
                case "inventaire":
                    generateInventaireModel(workbook);
                    break;
                case "paiement":
                    generatePaiementModel(workbook);
                    break;
                default:
                    throw new IllegalArgumentException("Type inconnu");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private void generateCaisseModel(Workbook workbook) {
        Sheet sheet1 = workbook.createSheet("Caisse");
        Row h1 = sheet1.createRow(0);
        h1.createCell(0).setCellValue("libelle");
        h1.createCell(1).setCellValue("solde_initial");
        Row ex1 = sheet1.createRow(1);
        ex1.createCell(0).setCellValue("Caisse principale");
        ex1.createCell(1).setCellValue(1000.0);

        Sheet sheet2 = workbook.createSheet("Mouvement");
        Row h2 = sheet2.createRow(0);
        h2.createCell(0).setCellValue("libelle_caisse");
        h2.createCell(1).setCellValue("date");
        h2.createCell(2).setCellValue("montant");
        h2.createCell(3).setCellValue("cause");
        Row ex2 = sheet2.createRow(1);
        ex2.createCell(0).setCellValue("Caisse principale");
        ex2.createCell(1).setCellValue("2026-01-01");
        ex2.createCell(2).setCellValue(200.0);
        ex2.createCell(3).setCellValue("Vente de lait");
    }

    private void generatePeseeModel(Workbook workbook) {
        Sheet sheet1 = workbook.createSheet("Bovin");
        Row h1 = sheet1.createRow(0);
        h1.createCell(0).setCellValue("ref");
        h1.createCell(1).setCellValue("race_nom");
        h1.createCell(2).setCellValue("date_achat");
        h1.createCell(3).setCellValue("date_vente");
        h1.createCell(4).setCellValue("prix_achat");
        h1.createCell(5).setCellValue("prix_vente");
        h1.createCell(6).setCellValue("poids_achat");
        h1.createCell(7).setCellValue("poids_vente");
        Row ex1 = sheet1.createRow(1);
        ex1.createCell(0).setCellValue(1);
        ex1.createCell(1).setCellValue("Holstein");
        ex1.createCell(2).setCellValue("2025-01-15");
        ex1.createCell(3).setCellValue("");
        ex1.createCell(4).setCellValue(1500.0);
        ex1.createCell(5).setCellValue(0.0);
        ex1.createCell(6).setCellValue(200.0);
        ex1.createCell(7).setCellValue(0.0);

        Sheet sheet2 = workbook.createSheet("Pesee");
        Row h2 = sheet2.createRow(0);
        h2.createCell(0).setCellValue("ref_bovin");
        h2.createCell(1).setCellValue("date_pese");
        h2.createCell(2).setCellValue("poids_apres");
        Row ex2 = sheet2.createRow(1);
        ex2.createCell(0).setCellValue(1);
        ex2.createCell(1).setCellValue("2025-06-01");
        ex2.createCell(2).setCellValue(250.0);
    }

    private void generateInventaireModel(Workbook workbook) {
        Sheet sheet1 = workbook.createSheet("Inventaire");
        Row h1 = sheet1.createRow(0);
        h1.createCell(0).setCellValue("ref");
        h1.createCell(1).setCellValue("date");
        h1.createCell(2).setCellValue("libelle");
        Row ex1 = sheet1.createRow(1);
        ex1.createCell(0).setCellValue(1);
        ex1.createCell(1).setCellValue("2026-01-01");
        ex1.createCell(2).setCellValue("Inventaire janvier");

        Sheet sheet2 = workbook.createSheet("Inventaire_detail");
        Row h2 = sheet2.createRow(0);
        h2.createCell(0).setCellValue("ref_inventaire");
        h2.createCell(1).setCellValue("materiel_libelle");
        h2.createCell(2).setCellValue("quantite_initiale");
        h2.createCell(3).setCellValue("quantite_finale");
        h2.createCell(4).setCellValue("observations");
        Row ex2 = sheet2.createRow(1);
        ex2.createCell(0).setCellValue("1");
        ex2.createCell(1).setCellValue("Aliment vache");
        ex2.createCell(2).setCellValue(100.0);
        ex2.createCell(3).setCellValue(85.0);
        ex2.createCell(4).setCellValue("Consommation");
    }

    private void generatePaiementModel(Workbook workbook) {
        Sheet sheet1 = workbook.createSheet("Employé");
        Row h1 = sheet1.createRow(0);
        h1.createCell(0).setCellValue("nom");
        h1.createCell(1).setCellValue("prenom");
        h1.createCell(2).setCellValue("date_naissance");
        h1.createCell(3).setCellValue("date_entree");
        Row ex1 = sheet1.createRow(1);
        ex1.createCell(0).setCellValue("Dupont");
        ex1.createCell(1).setCellValue("Jean");
        ex1.createCell(2).setCellValue("1990-01-01");
        ex1.createCell(3).setCellValue("2025-01-01");

        Sheet sheet2 = workbook.createSheet("Contrat");
        Row h2 = sheet2.createRow(0);
        h2.createCell(0).setCellValue("nom");
        h2.createCell(1).setCellValue("prenom");
        h2.createCell(2).setCellValue("date_debut");
        h2.createCell(3).setCellValue("date_fin");
        h2.createCell(4).setCellValue("salaire");
        Row ex2 = sheet2.createRow(1);
        ex2.createCell(0).setCellValue("Dupont");
        ex2.createCell(1).setCellValue("Jean");
        ex2.createCell(2).setCellValue("2025-01-01");
        ex2.createCell(3).setCellValue("2025-12-31");
        ex2.createCell(4).setCellValue(2000.0);

        Sheet sheet3 = workbook.createSheet("Paiement");
        Row h3 = sheet3.createRow(0);
        h3.createCell(0).setCellValue("nom");
        h3.createCell(1).setCellValue("prenom");
        h3.createCell(2).setCellValue("date_debut_contrat");
        h3.createCell(3).setCellValue("mois");
        h3.createCell(4).setCellValue("montant");
        h3.createCell(5).setCellValue("type_paiement");
        Row ex3 = sheet3.createRow(1);
        ex3.createCell(0).setCellValue("Dupont");
        ex3.createCell(1).setCellValue("Jean");
        ex3.createCell(2).setCellValue("2025-01-01");
        ex3.createCell(3).setCellValue("2025-01-01");
        ex3.createCell(4).setCellValue(2000.0);
        ex3.createCell(5).setCellValue("Salaire");
    }
}