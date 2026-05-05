# =========================
# ANALYSE COMPLETE DES VENTES
# =========================
ventes = []
while True:
    print("\n===== MENU =====")
    print("1. Ajouter une vente")
    print("2. Voir les ventes")
    print("3. Analyse complète")
    print("4. Quitter")

    choix = input("Choix : ")

    if choix == "1":
        client = input("Nom client : ")
        produit = input("Produit : ")
        montant = float(input("Montant : "))

        ventes.append({
            "client": client,
            "produit": produit,
            "montant": montant
        })

        print("Vente ajoutée")

    elif choix == "2":
        print("\n--- VENTES ---")
        for v in ventes:
            print(v["client"], "-", v["produit"], "-", v["montant"])

    elif choix == "3":

        if len(ventes) == 0:
            print("Aucune donnée")
        else:

            total = 0
            clients = {}
            produits = {}

            # analyse des données
            for v in ventes:
                total += v["montant"]

                # clients
                if v["client"] in clients:
                    clients[v["client"]] += v["montant"]
                else:
                    clients[v["client"]] = v["montant"]

                # produits
                if v["produit"] in produits:
                    produits[v["produit"]] += 1
                else:
                    produits[v["produit"]] = 1

            moyenne = total / len(ventes)

            # meilleur client
            meilleur_client = max(clients, key=clients.get)

            # produit le plus vendu
            meilleur_produit = max(produits, key=produits.get)

            # clients réguliers (>=2 achats)
            frequent_clients = []

            for c in clients:
                count = 0
                for v in ventes:
                    if v["client"] == c:
                        count += 1
                if count >= 2:
                    frequent_clients.append(c)

            print("\n===== ANALYSE COMPLETE =====")
            print("Total ventes :", total)
            print("Moyenne :", moyenne)

            print("\n--- CLIENT ---")
            print("Meilleur client :", meilleur_client)
            print("Montant :", clients[meilleur_client])

            print("\nClients réguliers :", frequent_clients)

            print("\n--- PRODUITS ---")
            print("Produit le plus vendu :", meilleur_produit)  
  
    elif choix == "4":
        print("Fin du programme")
        break

    else:
        print("Choix invalide")