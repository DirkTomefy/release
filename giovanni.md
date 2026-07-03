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
- [x] page entree stock
    - [x] input
        - [x] materiel
        - [x] qte
        - [x] prix unitaire
        - [x] date
        - [x] mode de payement dynamique si entree

- [x] page sortie stock
    - [x] input
        - [x] materiel
        - [x] date
        - [x] qte
    - [ ] fetch pour envoyer
        - [ ] gerer exception

# truc a faire
dans une seule et meme page, on fait entree et sortie de stock.
c'est assez etrange car entree necessite qtt et pu alors que sortie juste qtt. donc on va faire un truc dynamique, si c'est entree, on affiche qtt et pu, si c'est sortie, on affiche juste qtt.
mais le probleme c'est que si c'est entree on ajoute un mode depayement, mais si c'est sortie, c'est pas necessaire. donc on va faire un truc dynamique, si c'est entree, on affiche qtt et pu et mode de payement, si c'est sortie, on affiche juste qtt.

## bref pour l'instant concentre toi sur l'entree

decision finale: j'ai separer formEntree et formSortie, car c'est plus simple a gerer vu que sortie n'a pas besoin de mode de payement et de prix unitaire
**j'ai fini !.**

## WAIT
### correction probleme de conception
type_materiel(id, libelle)
materiel(id, libellle, type_materiel, type_gestion)
caisse(id, libelle, montant_actuelle)
mvt_caisse(id, caisse, type_mouvement, montant, date)
mvt_stock_entree(id, materiel, prix_unitaire, qte, qte_restant, date_entree)
mvt_stock_entree_paiement(id, mvt_stock_entree, caisse, montant)
mvt_stock_sortie(id, materiel, qte, date_sortie)
mode_payement(id, libelle)
**j'ai fini !.**

## maintenent on passe a la sortie
(une sortie necessite une insertion dans mvt_stock_sortie et une update de mvt_stock_entree pour diminuer le qte_restant)
on commence par les modele, repo, service et controller tout simple
on fait le controlller
on fait les 2 services
on fait le service de transaction qui fait les 2 precedents en meme temps
|truc chiant| = gerer la diminution si fifo ou lifo
on fait un peu de front-end maintenant