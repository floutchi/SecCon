Pour lancer le FFE, veuillez éditer la configuration du lancement du FFE avec le chemin du certificat dans les options de la VM :

-Djavax.net.ssl.keyStore="**Chemin vers le certificat ffe.labo.swilabus.com.p12**"
-Djavax.net.ssl.keyStorePassword=labo2022
-Dhttps.protocols=TLSv1.2


Lancez le FFE, puis le SBE, sélectionnez votre interface réseau dans le FFE puis dans le SBE.

Lancez le client en cochant la case SSL/TLS et vous pourrez créer un compte, vous connecter, ect...
Modifiez votre fichier host pour que la connexion fonctionne avec l'adresse ffe.labo.swilabus.com et assurez vous que vous êtes connectés en Ethernet.


(La connexion en SSL/TLS s'initialise à la ligne 33 et 34 de la classe Server dans le FFE. La ligne 36 commentée est là au cas où la connexion TLS/SSL ne fonctionne pas).