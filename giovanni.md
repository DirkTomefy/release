# TRUC QUE JE DOIS FAIRE
-> truc de stock

- [x] creer modele
    - [x] type_materiel
    - [x] materiel
    - [x] mvt_stock
- [x] creer repo
    - [x] type_materiel
    - [x] materiel
    - [x] mvt_stock
- [ ] page
    - [ ] input
        - [x] materiel
        - [x] qte
        - [x] prix unitaire
        - [ ] type mvmt
        - [ ] mode de payement dynamique si entree

# truc a faire
dans une seule et meme page, on fait entree et sortie de stock.
c'est assez etrange car entree necessite qtt et pu alors que sortie juste qtt. donc on va faire un truc dynamique, si c'est entree, on affiche qtt et pu, si c'est sortie, on affiche juste qtt.
mais le probleme c'est que si c'est entree on ajoute un mode depayement, mais si c'est sortie, c'est pas necessaire. donc on va faire un truc dynamique, si c'est entree, on affiche qtt et pu et mode de payement, si c'est sortie, on affiche juste qtt.

bref pour l'instant concentre toi sur l'entree

## WAIT
### correction probleme de conception
type_materiel(id, libelle)
materiel(id, libellle, type_materiel, type_gestion)
caisse(id, libelle, montant_actuelle)
mvt_stock_entree(id, materiel, prix_unitaire, qte, qte_restant, date_entree)
mvt_stock_entree_paiement(id, mvt_stock_entree, caisse, montant)
mvt_stock_sortie(id, materiel, qte, date_sortie)
mode_payement(id, libelle)