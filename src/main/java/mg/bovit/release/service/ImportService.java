package mg.bovit.release.service;

import mg.bovit.release.model.*;
import mg.bovit.release.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ImportService {

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
    // 1. IMPORT CAISSE + MOUVEMENTS (tolère feuille Caisse vide)
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importCaisseEtMouvements(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet caisseSheet = workbook.getSheet("Caisse");
            Sheet mouvementSheet = workbook.getSheet("Mouvement");

            // 1. Lire les caisses si la feuille existe et a des lignes
            Map<String, Caisse> caisses = new HashMap<>();
            if (caisseSheet != null && caisseSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : caisseSheet) {
                    if (row.getRowNum() == 0) continue;
                    String libelle = getStringCell(row, 0);
                    Double solde = getDoubleCell(row, 1);
                    Caisse c = new Caisse();
                    c.setLibelle(libelle);
                    c.setMontant_actuelle(solde);
                    caisses.put(libelle, caisseRepository.save(c));
                }
            }

            // 2. Lire les mouvements si la feuille existe et a des lignes
            if (mouvementSheet != null && mouvementSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : mouvementSheet) {
                    if (row.getRowNum() == 0) continue;
                    String libelleCaisse = getStringCell(row, 0);
                    Date date = Date.valueOf(LocalDate.parse(getStringCell(row, 1), DATE_FORMAT));
                    Double montant = getDoubleCell(row, 2);
                    String causeLibelle = getStringCell(row, 3);

                    // Chercher la caisse d'abord dans les nouvelles, sinon en base
                    Caisse caisse = caisses.get(libelleCaisse);
                    if (caisse == null) {
                        // Rechercher en base
                        caisse = caisseRepository.findByLibelle(libelleCaisse)
                                .orElseThrow(() -> new RuntimeException("Caisse introuvable : " + libelleCaisse));
                    }

                    // Récupérer ou créer la cause
                    CauseCaisse cause = causeCaisseRepository.findByLibelle(causeLibelle)
                            .orElseGet(() -> {
                                CauseCaisse newCause = new CauseCaisse();
                                newCause.setLibelle(causeLibelle);
                                return causeCaisseRepository.save(newCause);
                            });

                    // Vérifier le solde
                    double nouveauSolde = caisse.getMontant_actuelle() + montant;
                    if (nouveauSolde < 0) {
                        throw new RuntimeException(
                                String.format("Solde négatif pour la caisse '%s' après mouvement de %.2f (solde actuel : %.2f)",
                                        libelleCaisse, montant, caisse.getMontant_actuelle())
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
                }
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // 2. IMPORT BOVINS + PESÉES (tolère feuille Bovin vide)
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importBovinsEtPesees(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet bovinSheet = workbook.getSheet("Bovin");
            Sheet peseeSheet = workbook.getSheet("Pesee");

            Map<String, Bovin> bovins = new HashMap<>();
            if (bovinSheet != null && bovinSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : bovinSheet) {
                    if (row.getRowNum() == 0) continue;
                    Double refDouble = getDoubleCell(row, 0);
                    String ref = (refDouble == null) ? "" : String.valueOf(refDouble.longValue());
                    String raceNom = getStringCell(row, 1);
                    Date dateAchat = Date.valueOf(LocalDate.parse(getStringCell(row, 2), DATE_FORMAT));
                    Date dateVente = getStringCell(row, 3).isEmpty() ? null : Date.valueOf(LocalDate.parse(getStringCell(row, 3), DATE_FORMAT));
                    Double prixAchat = getDoubleCell(row, 4);
                    Double prixVente = getDoubleCell(row, 5);
                    Double poidsAchat = getDoubleCell(row, 6);
                    Double poidsVente = getDoubleCell(row, 7);

                    Race race = raceRepository.findByNom(raceNom)
                            .orElseGet(() -> {
                                Race newRace = new Race();
                                newRace.setNom(raceNom);
                                newRace.setDescriptions("");
                                return raceRepository.save(newRace);
                            });

                    Bovin bovin = new Bovin();
                    bovin.setRace(race);
                    bovin.setDate_achat(dateAchat);
                    bovin.setDate_vente(dateVente);
                    bovin.setPrix_achat(prixAchat);
                    bovin.setPrix_vente(prixVente);
                    bovin.setPoids_achat(poidsAchat);
                    bovin.setPoids_vente(poidsVente);
                    bovins.put(ref, bovinRepository.save(bovin));
                }
            }

            if (peseeSheet != null && peseeSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : peseeSheet) {
                    if (row.getRowNum() == 0) continue;
                    Double bovinRefDouble = getDoubleCell(row, 0);
                    String bovinRef = (bovinRefDouble == null) ? "" : String.valueOf(bovinRefDouble.longValue());
                    Date datePese = Date.valueOf(LocalDate.parse(getStringCell(row, 1), DATE_FORMAT));
                    Double poidsApres = getDoubleCell(row, 2);

                    // Chercher le bovin d'abord dans les nouveaux, sinon en base
                    Bovin bovin = bovins.get(bovinRef);
                    if (bovin == null) {
                        // On suppose que la réf est un ID existant en base
                        Long id = Long.parseLong(bovinRef);
                        bovin = bovinRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Bovin introuvable avec l'ID : " + bovinRef));
                    }

                    PeseBovin pesee = new PeseBovin();
                    pesee.setBovin(bovin);
                    pesee.setDate_pese(datePese);
                    pesee.setPoids_apres(poidsApres);
                    peseBovinRepository.save(pesee);
                }
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // 3. IMPORT INVENTAIRE + DÉTAILS (tolère feuille Inventaire vide)
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importInventaireEtDetails(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet inventaireSheet = workbook.getSheet("Inventaire");
            Sheet detailSheet = workbook.getSheet("Inventaire_detail");

            Map<String, Inventaire> inventaires = new HashMap<>();
            if (inventaireSheet != null && inventaireSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : inventaireSheet) {
                    if (row.getRowNum() == 0) continue;
                    Double refDouble = getDoubleCell(row, 0);
                    String ref = (refDouble == null) ? "" : String.valueOf(refDouble.longValue());
                    Date date = Date.valueOf(LocalDate.parse(getStringCell(row, 1), DATE_FORMAT));
                    String libelle = getStringCell(row, 2);

                    Inventaire inv = new Inventaire();
                    inv.setDateInventaire(date);
                    inv.setLibelle(libelle);
                    inventaires.put(ref, inventaireRepository.save(inv));
                }
            }

            if (detailSheet != null && detailSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : detailSheet) {
                    if (row.getRowNum() == 0) continue;
                    String refInventaire = getStringCell(row, 0);
                    String materielLibelle = getStringCell(row, 1);
                    Double quantiteInitiale = getDoubleCell(row, 2);
                    Double quantiteFinale = getDoubleCell(row, 3);
                    String observations = getStringCell(row, 4);

                    // Chercher l'inventaire d'abord dans les nouveaux, sinon en base
                    Inventaire inv = inventaires.get(refInventaire);
                    if (inv == null) {
                        // La référence est un ID existant en base
                        Long id = Long.parseLong(refInventaire);
                        inv = inventaireRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Inventaire introuvable avec l'ID : " + refInventaire));
                    }

                    // Matériel peut être créé s'il n'existe pas
                    Materiel materiel = materielRepository.findByLibelle(materielLibelle)
                            .orElseGet(() -> {
                                Materiel m = new Materiel();
                                m.setLibelle(materielLibelle);
                                // on ne définit pas le type ni le typeGestion, ils peuvent être nuls
                                return materielRepository.save(m);
                            });

                    InventaireDetail detail = new InventaireDetail();
                    detail.setInventaire(inv);
                    detail.setMateriel(materiel);
                    detail.setQuantiteInitiale(quantiteInitiale);
                    detail.setQuantiteFinale(quantiteFinale);
                    detail.setObservations(observations);
                    inventaireDetailRepository.save(detail);
                }
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
    }

    // ------------------------------------------------------------
    // 4. IMPORT PAIEMENTS + EMPLOYÉS + CONTRATS (tolère feuilles Employé et Contrat vides)
    // ------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void importPaiementsEmployesContrats(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet employeeSheet = workbook.getSheet("Employé");
            Sheet contratSheet = workbook.getSheet("Contrat");
            Sheet paiementSheet = workbook.getSheet("Paiement");

            Map<String, Employee> employees = new HashMap<>();
            if (employeeSheet != null && employeeSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : employeeSheet) {
                    if (row.getRowNum() == 0) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateNaissance = Date.valueOf(LocalDate.parse(getStringCell(row, 2), DATE_FORMAT));
                    Date dateEntree = Date.valueOf(LocalDate.parse(getStringCell(row, 3), DATE_FORMAT));

                    Employee e = new Employee();
                    e.setNom(nom);
                    e.setPrenom(prenom);
                    e.setDateNaissance(dateNaissance);
                    e.setDateEntree(dateEntree);
                    employees.put(nom + "_" + prenom, employeeRepository.save(e));
                }
            }

            Map<String, Contrat> contrats = new HashMap<>();
            if (contratSheet != null && contratSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : contratSheet) {
                    if (row.getRowNum() == 0) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateDebut = Date.valueOf(LocalDate.parse(getStringCell(row, 2), DATE_FORMAT));
                    Date dateFin = getStringCell(row, 3).isEmpty() ? null : Date.valueOf(LocalDate.parse(getStringCell(row, 3), DATE_FORMAT));
                    Double salaire = getDoubleCell(row, 4);

                    Employee emp = employees.get(nom + "_" + prenom);
                    if (emp == null) {
                        // Chercher l'employé en base
                        emp = employeeRepository.findByNomAndPrenom(nom, prenom)
                                .orElseThrow(() -> new RuntimeException("Employé introuvable : " + nom + " " + prenom));
                    }

                    Contrat c = new Contrat();
                    c.setEmployee(emp);
                    c.setDateDebut(dateDebut);
                    c.setDateFin(dateFin);
                    c.setSalaire(java.math.BigDecimal.valueOf(salaire));
                    contrats.put(nom + "_" + prenom + "_" + dateDebut.toString(), contratRepository.save(c));
                }
            }

            if (paiementSheet != null && paiementSheet.getPhysicalNumberOfRows() > 1) {
                for (Row row : paiementSheet) {
                    if (row.getRowNum() == 0) continue;
                    String nom = getStringCell(row, 0);
                    String prenom = getStringCell(row, 1);
                    Date dateDebutContrat = Date.valueOf(LocalDate.parse(getStringCell(row, 2), DATE_FORMAT));
                    Date mois = Date.valueOf(LocalDate.parse(getStringCell(row, 3), DATE_FORMAT));
                    Double montant = getDoubleCell(row, 4);
                    String typePaiementLibelle = getStringCell(row, 5);

                    String key = nom + "_" + prenom + "_" + dateDebutContrat.toString();
                    Contrat contrat = contrats.get(key);
                    if (contrat == null) {
                        // Chercher le contrat en base selon le couple (nom, prenom, dateDebut)
                        Employee emp = employeeRepository.findByNomAndPrenom(nom, prenom)
                                .orElseThrow(() -> new RuntimeException("Employé introuvable : " + nom + " " + prenom));
                        contrat = contratRepository.findByEmployeeAndDateDebut(emp, dateDebutContrat)
                                .orElseThrow(() -> new RuntimeException("Contrat introuvable pour " + nom + " " + prenom + " débutant le " + dateDebutContrat));
                    }

                    TypePayementEmployee type = typePayementEmployeeRepository.findByLibelle(typePaiementLibelle)
                            .orElseGet(() -> {
                                TypePayementEmployee t = new TypePayementEmployee();
                                t.setLibelle(typePaiementLibelle);
                                return typePayementEmployeeRepository.save(t);
                            });

                    PayementEmployee paiement = new PayementEmployee();
                    paiement.setEmployee(contrat.getEmployee());
                    paiement.setTypePayementEmployee(type);
                    paiement.setMois(mois);
                    paiement.setMontant(java.math.BigDecimal.valueOf(montant));
                    paiement.setRestePaye(java.math.BigDecimal.ZERO);
                    paiement.setDatePayement(new java.sql.Timestamp(System.currentTimeMillis()));
                    payementEmployeeRepository.save(paiement);
                }
            }
        } catch (IOException e) {
            throw new Exception("Erreur de lecture du fichier", e);
        }
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
                // Éviter ".0" pour les entiers
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
        ex1.createCell(0).setCellValue(1);      // ref (nombre)
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
        ex2.createCell(0).setCellValue(1);      // ref_bovin (nombre)
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
        ex1.createCell(0).setCellValue(1);      // ref (nombre)
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
        ex2.createCell(0).setCellValue("1");    // ref_inventaire (String)
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