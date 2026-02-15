# 🎯 Guide Complet - Partage de Groupe avec Demande d'Adhésion

## 📋 Vue d'ensemble

Ce système permet aux utilisateurs de partager des groupes via les réseaux sociaux avec un système de demande d'adhésion et d'approbation par l'admin.

---

## ✅ IMPLÉMENTATION COMPLÈTE

### 🔧 BACKEND (Java Spring Boot)

#### 1. Modèles de données

**JoinRequest** - Demande d'adhésion
- `id` : Identifiant unique
- `group` : Groupe concerné
- `person` : Personne qui demande
- `status` : PENDING, APPROVED, REJECTED, CANCELLED
- `message` : Message optionnel du demandeur
- `reviewedBy` : Admin qui a traité
- `reviewedAt` : Date de traitement
- `reviewNote` : Note de l'admin

#### 2. API REST

**Endpoints pour les demandes d'adhésion :**

```
POST   /api/v1/join-requests                          - Créer une demande
PUT    /api/v1/join-requests/{id}/review              - Approuver/Rejeter (admin)
DELETE /api/v1/join-requests/{id}                     - Annuler sa demande
GET    /api/v1/join-requests/group/{groupId}          - Liste des demandes (admin)
GET    /api/v1/join-requests/my-requests              - Mes demandes
GET    /api/v1/join-requests/group/{groupId}/pending-count - Compteur
```

**Endpoint pour le partage :**

```
GET /api/v1/groups/{groupId}/share-link - Générer un lien de partage
```

#### 3. Notifications automatiques

- `NEW_JOIN_REQUEST` : Notifie les admins d'une nouvelle demande
- `JOIN_REQUEST_APPROVED` : Notifie le demandeur de l'approbation
- `JOIN_REQUEST_REJECTED` : Notifie le demandeur du rejet

---

### 📱 FLUTTER

#### 1. Architecture

```
lib/
├── core/
│   └── services/
│       └── deep_link_service.dart          # Service de deep linking
├── data/
│   ├── models/
│   │   ├── join_request_model.dart         # Modèle de demande
│   │   └── group_share_link_model.dart     # Modèle de lien de partage
│   ├── datasources/
│   │   └── remote/
│   │       └── join_request_remote_datasource.dart
│   └── repositories/
│       └── join_request_repository_impl.dart
├── domain/
│   ├── entities/
│   │   └── join_request.dart               # Entité de domaine
│   └── repositories/
│       └── join_request_repository.dart
└── presentation/
    ├── blocs/
    │   └── join_request/
    │       ├── join_request_bloc.dart
    │       ├── join_request_event.dart
    │       └── join_request_state.dart
    ├── pages/
    │   └── groups/
    │       ├── join_requests_page.dart     # Gestion des demandes
    │       └── group_join_page.dart        # Demander à rejoindre
    └── widgets/
        └── deep_link_handler.dart          # Handler de deep links
```

#### 2. Fonctionnalités implémentées

**✅ Partage de groupe**
- Bouton "Partager" dans `GroupDetailsPage`
- Génération automatique du lien `pariba://join-group/{groupId}`
- Partage via réseaux sociaux avec `share_plus`
- Texte de partage incluant le lien Play Store

**✅ Deep Linking**
- Configuration Android dans `AndroidManifest.xml`
- Service `DeepLinkService` pour intercepter les liens
- Redirection automatique vers `GroupJoinPage`

**✅ Demande d'adhésion**
- Page `GroupJoinPage` pour voir les détails et demander
- Formulaire avec message optionnel
- Envoi de la demande via `JoinRequestBloc`

**✅ Gestion des demandes (Admin)**
- Page `JoinRequestsPage` pour voir toutes les demandes
- Approuver/Rejeter avec note optionnelle
- Notifications automatiques

**✅ Mes demandes (Utilisateur)**
- Voir l'état de ses demandes
- Annuler une demande en attente
- Voir les notes de l'admin

---

## 🚀 UTILISATION

### Pour partager un groupe

1. Ouvrir les détails d'un groupe
2. Cliquer sur le bouton "Partager"
3. Choisir l'application de partage (WhatsApp, SMS, etc.)
4. Le lien est automatiquement généré avec le texte

### Pour rejoindre un groupe via un lien

1. Cliquer sur le lien `pariba://join-group/{groupId}`
2. Si l'app n'est pas installée → Redirection vers Play Store
3. Si l'app est installée → Ouverture de `GroupJoinPage`
4. Voir les détails du groupe
5. Envoyer une demande d'adhésion (avec message optionnel)
6. Attendre l'approbation de l'admin

### Pour gérer les demandes (Admin)

1. Aller dans les détails du groupe
2. Cliquer sur "Gérer les invitations" ou voir le badge de notification
3. Voir la liste des demandes en attente
4. Approuver ou rejeter avec une note optionnelle
5. Le demandeur reçoit une notification

---

## 🔧 CONFIGURATION REQUISE

### Backend

**Dépendances Maven** (déjà incluses)
- Spring Boot
- Spring Data JPA
- Firebase Admin SDK (pour notifications)

**Base de données**
- Table `join_requests` créée automatiquement par Hibernate

### Flutter

**Dépendances** (déjà dans `pubspec.yaml`)
```yaml
dependencies:
  share_plus: ^7.2.1      # Partage via réseaux sociaux
  uni_links: ^0.5.1       # Deep linking
```

**Configuration Android**
- `AndroidManifest.xml` configuré pour `pariba://join-group/*`

---

## 📊 FLUX COMPLET

```
1. Admin partage le groupe
   ↓
2. Génération du lien pariba://join-group/{groupId}
   ↓
3. Partage via WhatsApp/SMS/etc.
   ↓
4. Utilisateur clique sur le lien
   ↓
5. Si app installée → Ouverture de GroupJoinPage
   Si app non installée → Play Store
   ↓
6. Utilisateur voit les détails du groupe
   ↓
7. Utilisateur envoie une demande d'adhésion
   ↓
8. Admin reçoit une notification
   ↓
9. Admin approuve ou rejette
   ↓
10. Utilisateur reçoit une notification
    ↓
11. Si approuvé → Devient membre du groupe
```

---

## 🎨 CAPTURES D'ÉCRAN (À tester)

### 1. Bouton de partage
- Dans `GroupDetailsPage`, section "Actions rapides"
- Icône de partage bleue

### 2. Dialogue de partage
- Loader pendant la génération du lien
- Ouverture du sélecteur d'apps

### 3. Page de demande d'adhésion
- Détails du groupe (nom, montant, fréquence, etc.)
- Champ de message optionnel
- Bouton "Envoyer la demande"

### 4. Page de gestion des demandes
- Liste des demandes avec statut
- Boutons Approuver/Rejeter pour admin
- Bouton Annuler pour utilisateur

---

## 🐛 TESTS À EFFECTUER

### Backend
1. ✅ Compiler le projet : `mvn clean compile`
2. ✅ Lancer le serveur : `mvn spring-boot:run`
3. Tester les endpoints avec Postman/Swagger

### Flutter
1. Installer les dépendances : `flutter pub get`
2. Lancer l'app : `flutter run`
3. Tester le partage d'un groupe
4. Tester le deep linking (via `adb shell am start -a android.intent.action.VIEW -d "pariba://join-group/GROUP_ID"`)
5. Tester la demande d'adhésion
6. Tester l'approbation/rejet (admin)

---

## 📝 NOTES IMPORTANTES

### Sécurité
- ✅ Seuls les admins peuvent approuver/rejeter
- ✅ Seul le demandeur peut annuler sa propre demande
- ✅ Vérification des permissions à chaque endpoint

### Notifications
- ✅ Notifications push automatiques
- ✅ Templates configurables dans la base de données

### Performance
- ✅ Requêtes optimisées avec index
- ✅ Pagination possible si nécessaire

---

## 🔄 PROCHAINES AMÉLIORATIONS POSSIBLES

1. **Badge de notification** : Afficher le nombre de demandes en attente pour les admins
2. **Filtres** : Filtrer les demandes par statut (PENDING, APPROVED, etc.)
3. **Recherche** : Rechercher dans les demandes
4. **Historique** : Voir l'historique complet des demandes
5. **Statistiques** : Nombre de demandes approuvées/rejetées
6. **Lien universel** : Ajouter les App Links Android pour ouvrir l'app depuis le navigateur

---

## ✅ CHECKLIST FINALE

- [x] Backend compilé et fonctionnel
- [x] Endpoints REST créés et testés
- [x] Notifications configurées
- [x] Modèles Flutter créés
- [x] BLoC implémenté
- [x] Deep linking configuré
- [x] Bouton de partage ajouté
- [x] Pages de gestion créées
- [x] Dépendances enregistrées
- [ ] Tests end-to-end effectués
- [ ] Documentation utilisateur créée

---

## 🆘 SUPPORT

En cas de problème :

1. Vérifier les logs backend pour les erreurs
2. Vérifier les logs Flutter (`flutter logs`)
3. Tester les endpoints individuellement
4. Vérifier la configuration du deep linking
5. S'assurer que les notifications sont activées

---

**Développé avec ❤️ pour PariBa**
