# 📚 RÉFÉRENCE COMPLÈTE DU BACKEND PARIBA

> Document de référence pour comprendre l'architecture, les endpoints et la logique métier du backend Spring Boot.

---

## 🏗️ ARCHITECTURE GLOBALE

```
src/main/java/com/example/pariba/
├── configs/           # Configuration (Security, CORS, DataSeeder)
├── constants/         # Constantes (Messages, UI)
├── controllers/       # 28 Controllers REST
│   └── admin/         # 13 Controllers Admin
├── dtos/              # 60 DTOs (Request/Response)
├── enums/             # 24 Enums
├── exceptions/        # Gestion d'erreurs
├── models/            # 31 Entités JPA
├── repositories/      # 29 Repositories JPA
├── schedulers/        # Tâches planifiées
├── security/          # JWT, Filters, UserDetails
├── services/          # 35 Interfaces + 33 Implémentations
├── storages/          # Gestion fichiers
└── utils/             # Utilitaires
```

---

## 📊 MODÈLE DE DONNÉES (ENTITÉS)

### Relations Principales

```
Person (Utilisateur)
    │
    ├── User (1:1) - Compte avec mot de passe
    │
    ├── GroupMembership (1:N) - Appartenance aux groupes
    │       │
    │       └── TontineGroup (N:1) - Groupe de tontine
    │               │
    │               ├── Tour (1:N) - Tours de rotation
    │               │       │
    │               │       └── Contribution (1:N) - Cotisations
    │               │               │
    │               │               └── Payment (1:1) - Paiement
    │               │
    │               ├── Invitation (1:N) - Invitations
    │               │
    │               └── JoinRequest (1:N) - Demandes d'adhésion
    │
    ├── Notification (1:N) - Notifications
    │
    └── Subscription (1:N) - Abonnements premium
```

### Entités Détaillées

| Entité | Description | Champs Clés |
|--------|-------------|-------------|
| `Person` | Profil utilisateur | id, prenom, nom, email, phone, photo, role, fcmToken |
| `User` | Compte authentification | id, username, password, person_id |
| `TontineGroup` | Groupe de tontine | id, nom, description, montant, frequency, rotationMode, totalTours, startDate, creator |
| `GroupMembership` | Adhésion groupe | group_id, person_id, role (ADMIN/MEMBER), joinedAt |
| `Tour` | Tour de rotation | id, group, indexInGroup, beneficiary, status, scheduledDate, totalDue, totalCollected |
| `Contribution` | Cotisation membre | id, group, member, tour, amountDue, status, dueDate, payment |
| `Payment` | Paiement | id, group, payer, amount, paymentType, status, externalRef, validatedBy, validatedAt |
| `Invitation` | Invitation groupe | id, group, targetPhone, targetEmail, linkCode, status, expiresAt |
| `JoinRequest` | Demande adhésion | id, group, requester, status, message |
| `Notification` | Notification | id, person, title, body, type, isRead, data |
| `Subscription` | Abonnement | id, person, plan, status, startDate, endDate |
| `SubscriptionPlan` | Plan d'abonnement | id, type, name, monthlyPrice, **maxGroups**, **canExportPdf**, **canExportExcel**, **hasMultiAccount** |

---

## 💎 PLANS D'ABONNEMENT (PREMIUM)

Les limites et fonctionnalités sont configurables depuis le **Dashboard Admin** lors de la création des plans.

### Champs du Plan (`SubscriptionPlan`)

| Champ | Type | Description | Valeur par défaut |
|-------|------|-------------|-------------------|
| `maxGroups` | Integer | Nombre max de tontines (0 = illimité) | 2 |
| `canExportPdf` | Boolean | Export PDF autorisé | false |
| `canExportExcel` | Boolean | Export Excel autorisé | false |
| `hasMultiAccount` | Boolean | Gestion multi-compte | false |

### Exemple de Configuration

**Plan Gratuit:**
- `maxGroups: 2` (limite à 2 tontines)
- `canExportPdf: false`
- `canExportExcel: false`
- `hasMultiAccount: false`

**Plan Premium:**
- `maxGroups: 0` (illimité)
- `canExportPdf: true`
- `canExportExcel: true`
- `hasMultiAccount: true`

### Vérification dans le Code

```java
// Vérifier la limite de tontines
int maxGroups = subscriptionService.getMaxGroupsForPerson(personId);
if (maxGroups > 0 && groupCount >= maxGroups) {
    throw new BadRequestException("Limite atteinte");
}

// Vérifier l'accès aux exports
boolean canPdf = subscriptionService.canExportPdf(personId);
boolean canExcel = subscriptionService.canExportExcel(personId);
boolean hasMulti = subscriptionService.hasMultiAccountAccess(personId);
```

---

## 🔐 ENUMS (STATUTS & TYPES)

### AppRole (Rôle Application)
```java
SUPERADMIN  // Super Administrateur système
ADMIN       // Administrateur
USER        // Utilisateur standard
```

### GroupRole (Rôle dans un Groupe)
```java
ADMIN   // Administrateur du groupe (créateur)
MEMBER  // Membre simple
```

### PaymentStatus
```java
PENDING     // En attente de validation
CONFIRMED   // Confirmé par l'admin
PROCESSING  // En cours de traitement
REJECTED    // Rejeté
SUCCESS     // Réussi
FAILED      // Échoué
```

### PaymentType
```java
ORANGE_MONEY    // Orange Money
MOOV_MONEY      // Moov Money
WAVE_MONEY      // Wave Money
CASH            // Espèces
BANK_TRANSFER   // Virement bancaire
```

### TourStatus
```java
PENDING      // En attente
SCHEDULED    // Planifié
IN_PROGRESS  // En cours
PAID_OUT     // Payé au bénéficiaire
COMPLETED    // Terminé
CLOSED       // Clôturé
```

### ContributionStatus
```java
PENDING  // En attente
DUE      // À payer
PARTIAL  // Partiellement payé
PAID     // Payé
LATE     // En retard
WAIVED   // Dispensé
```

### Frequency (Fréquence Tontine)
```java
DAILY       // Quotidien
WEEKLY      // Hebdomadaire
BIWEEKLY    // Bi-hebdomadaire
MONTHLY     // Mensuel
QUARTERLY   // Trimestriel
YEARLY      // Annuel
```

### RotationMode
```java
SEQUENTIAL   // Séquentiel
RANDOM       // Aléatoire
SHUFFLE      // Mélangé
CUSTOM       // Personnalisé
FIXED_ORDER  // Ordre fixe
```

### InvitationStatus
```java
PENDING   // En attente
ACCEPTED  // Acceptée
DECLINED  // Refusée
EXPIRED   // Expirée
```

### JoinRequestStatus
```java
PENDING    // En attente d'approbation
APPROVED   // Approuvée
REJECTED   // Rejetée
CANCELLED  // Annulée par le demandeur
```

---

## 🌐 ENDPOINTS API

> Base URL: `/api/v1` (configuré dans BaseRestController)

### 🔑 AUTH - Authentification (`/auth`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/auth/register` | Inscription | ❌ |
| POST | `/auth/login` | Connexion → JWT | ❌ |
| POST | `/auth/otp/send` | Envoyer OTP | ❌ |
| POST | `/auth/otp/verify` | Vérifier OTP | ❌ |
| POST | `/auth/password/forgot` | Mot de passe oublié | ❌ |
| POST | `/auth/password/reset` | Réinitialiser mot de passe | ❌ |
| POST | `/auth/password/change` | Changer mot de passe | ✅ |
| POST | `/auth/refresh` | Rafraîchir token | ❌ |
| POST | `/auth/logout` | Déconnexion | ❌ |
| GET | `/auth/validate` | Valider token | ❌ |

### 👤 PERSONS - Profils (`/persons`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/persons/me` | Mon profil | ✅ |
| GET | `/persons/{personId}` | Profil par ID | ✅ |
| PUT | `/persons/me` | Modifier mon profil | ✅ |
| POST | `/persons/me/photo` | Upload photo | ✅ |
| DELETE | `/persons/me/photo` | Supprimer photo | ✅ |
| DELETE | `/persons/me` | Supprimer compte | ✅ |
| GET | `/persons/me/statistics` | Mes statistiques | ✅ |

### 👥 GROUPS - Groupes de Tontine (`/groups`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| POST | `/groups` | Créer groupe | ✅ | USER |
| GET | `/groups/{groupId}` | Détails groupe | ✅ | MEMBER |
| GET | `/groups/my-groups` | Mes groupes | ✅ | USER |
| GET | `/groups/created-by-me` | Groupes créés | ✅ | USER |
| PUT | `/groups/{groupId}` | Modifier groupe | ✅ | ADMIN groupe |
| DELETE | `/groups/{groupId}` | Supprimer groupe | ✅ | ADMIN groupe |
| GET | `/groups/{groupId}/share-link` | Lien de partage | ✅ | ADMIN groupe |
| POST | `/groups/{groupId}/leave` | Quitter groupe | ✅ | MEMBER |

### 👥 MEMBERSHIPS - Membres (`/memberships`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| GET | `/memberships/group/{groupId}` | Membres du groupe | ✅ | MEMBER |
| GET | `/memberships/group/{groupId}/person/{personId}` | Membre spécifique | ✅ | MEMBER |
| GET | `/memberships/my-memberships` | Mes appartenances | ✅ | USER |
| PUT | `/memberships/role` | Modifier rôle | ✅ | ADMIN groupe |
| PUT | `/memberships/group/{groupId}/person/{personId}/promote` | Promouvoir en ADMIN | ✅ | ADMIN groupe |
| PUT | `/memberships/group/{groupId}/person/{personId}/demote` | Rétrograder | ✅ | ADMIN groupe |
| DELETE | `/memberships/group/{groupId}/member/{personId}` | Retirer membre | ✅ | ADMIN groupe |

### 📨 INVITATIONS (`/invitations`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| POST | `/invitations` | Inviter membre | ✅ | ADMIN groupe |
| POST | `/invitations/accept` | Accepter invitation | ✅ | USER |
| GET | `/invitations/group/{groupId}` | Invitations du groupe | ✅ | ADMIN groupe |

### 📝 JOIN REQUESTS - Demandes d'adhésion (`/api/v1/join-requests`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| POST | `/join-requests` | Créer demande | ✅ | USER |
| PUT | `/join-requests/{requestId}/review` | Approuver/Rejeter | ✅ | ADMIN groupe |
| DELETE | `/join-requests/{requestId}` | Annuler demande | ✅ | Demandeur |
| GET | `/join-requests/group/{groupId}` | Demandes du groupe | ✅ | ADMIN groupe |
| GET | `/join-requests/my-requests` | Mes demandes | ✅ | USER |
| GET | `/join-requests/group/{groupId}/pending-count` | Compter en attente | ✅ | ADMIN groupe |

### 🔄 TOURS - Tours de Rotation (`/tours`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| POST | `/tours/generate` | Générer tours | ✅ | ADMIN groupe |
| GET | `/tours/{id}` | Détails tour | ✅ | MEMBER |
| GET | `/tours/group/{groupId}` | Tours du groupe | ✅ | MEMBER |
| GET | `/tours/group/{groupId}/current` | Tour en cours | ✅ | MEMBER |
| GET | `/tours/group/{groupId}/next` | Prochain tour | ✅ | MEMBER |
| POST | `/tours/{id}/start` | Démarrer tour | ✅ | ADMIN groupe |
| POST | `/tours/{id}/complete` | Terminer tour | ✅ | ADMIN groupe |

### 💰 CONTRIBUTIONS (`/contributions`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/contributions/{id}` | Détails contribution | ✅ |
| GET | `/contributions/group/{groupId}` | Contributions groupe | ✅ |
| GET | `/contributions/tour/{tourId}` | Contributions tour | ✅ |
| GET | `/contributions/member/{personId}` | Contributions membre | ✅ |
| GET | `/contributions/group/{groupId}/pending` | En attente | ✅ |

### 💳 PAYMENTS - Paiements (`/payments`)

| Méthode | Endpoint | Description | Auth | Rôle |
|---------|----------|-------------|------|------|
| POST | `/payments` | Déclarer paiement | ✅ | USER |
| GET | `/payments/{id}` | Détails paiement | ✅ | USER |
| GET | `/payments/contribution/{contributionId}` | Paiements contribution | ✅ | USER |
| GET | `/payments/person/{personId}` | Paiements personne | ✅ | USER |
| GET | `/payments/group/{groupId}` | Paiements groupe | ✅ | USER |
| GET | `/payments/group/{groupId}/pending` | En attente (groupe) | ✅ | ADMIN groupe |
| GET | `/payments/me/pending` | Mes paiements en attente | ✅ | USER |
| POST | `/payments/{id}/verify` | Vérifier paiement | ✅ | ADMIN groupe |
| POST | `/payments/validate` | Valider (confirmer/rejeter) | ✅ | ADMIN groupe |
| GET | `/payments/history/group/{groupId}` | Historique groupe | ✅ | USER |

### 🔔 NOTIFICATIONS (`/notifications`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/notifications` | Mes notifications | ✅ |
| GET | `/notifications/unread` | Non lues | ✅ |
| PUT | `/notifications/{id}/read` | Marquer comme lue | ✅ |
| PUT | `/notifications/read-all` | Tout marquer lu | ✅ |
| POST | `/notifications/fcm-token` | Enregistrer token FCM | ✅ |
| DELETE | `/notifications/{id}` | Supprimer notification | ✅ |
| DELETE | `/notifications/delete-all` | Tout supprimer | ✅ |

### ⭐ SUBSCRIPTIONS - Abonnements (`/subscriptions`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/subscriptions/me` | Mon abonnement | ✅ |
| POST | `/subscriptions/subscribe/{planId}` | S'abonner | ✅ |
| POST | `/subscriptions/cancel` | Annuler | ✅ |
| GET | `/subscriptions/feature/{feature}` | Vérifier accès feature | ✅ |

### 📢 ADVERTISEMENTS - Publicités (`/advertisements`)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/advertisements` | Publicités actives | ❌/✅ |
| GET | `/advertisements/{adId}` | Publicité par ID | ❌ |
| POST | `/advertisements/{adId}/impression` | Enregistrer impression | ✅ |
| POST | `/advertisements/{adId}/click` | Enregistrer clic | ✅ |

---

## 🔒 LOGIQUE DE SÉCURITÉ

### Authentification JWT
1. L'utilisateur se connecte avec `/auth/login`
2. Le backend retourne un `accessToken` (JWT) et un `refreshToken`
3. Le client envoie le JWT dans le header `Authorization: Bearer <token>`
4. Le JWT contient: `sub` (personId), `email`, `role`, `iat`, `exp`

### Vérification des Rôles
- **AppRole** (SUPERADMIN, ADMIN, USER): Vérifié par `@PreAuthorize("hasRole('USER')")`
- **GroupRole** (ADMIN, MEMBER): Vérifié dans les services via `membershipRepository`

### Flux de Vérification Admin Groupe
```java
// Dans le service
GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId);
if (membership == null || membership.getRole() != GroupRole.ADMIN) {
    throw new ForbiddenException("Vous devez être admin du groupe");
}
```

---

## 🔄 FLUX MÉTIER PRINCIPAUX

### 1. Création d'un Groupe
```
POST /groups
    │
    ├── Créer TontineGroup
    ├── Créer GroupMembership (créateur = ADMIN)
    └── Retourner GroupResponse
```

### 2. Rejoindre un Groupe (via Invitation)
```
POST /invitations (admin envoie)
    │
    └── Créer Invitation (status=PENDING)

POST /invitations/accept (utilisateur accepte)
    │
    ├── Vérifier linkCode
    ├── Créer GroupMembership (role=MEMBER)
    └── Mettre Invitation.status = ACCEPTED
```

### 3. Rejoindre un Groupe (via JoinRequest)
```
POST /join-requests (utilisateur demande)
    │
    └── Créer JoinRequest (status=PENDING)

PUT /join-requests/{id}/review (admin approuve)
    │
    ├── Si APPROVED: Créer GroupMembership
    └── Mettre JoinRequest.status = APPROVED/REJECTED
```

### 4. Cycle d'un Tour
```
POST /tours/generate (admin)
    │
    └── Créer N Tours avec bénéficiaires

POST /tours/{id}/start (admin)
    │
    ├── Tour.status = IN_PROGRESS
    └── Créer Contributions pour chaque membre

POST /payments (membre paie)
    │
    └── Créer Payment (status=PENDING)

POST /payments/validate (admin valide)
    │
    ├── Payment.status = CONFIRMED
    └── Contribution.status = PAID

POST /tours/{id}/complete (admin)
    │
    ├── Tour.status = COMPLETED
    └── Créer Payout pour bénéficiaire
```

### 5. Flux de Paiement
```
1. Membre déclare paiement (POST /payments)
   → Payment créé avec status=PENDING

2. Admin voit les paiements en attente (GET /payments/group/{id}/pending)

3. Admin valide le paiement (POST /payments/validate)
   → Si confirmé: Payment.status=CONFIRMED, Contribution.status=PAID
   → Si rejeté: Payment.status=REJECTED
```

---

## 📁 CONFIGURATION

### application.yml
```yaml
server:
  port: 8085
  address: 0.0.0.0

spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:8889/pariba
    username: root
    password: root

security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24h

firebase:
  enabled: true
  config:
    file: firebase-adminsdk.json
```

---

## 📝 NOTES IMPORTANTES

1. **Base URL**: Tous les endpoints sont préfixés par `/api/v1` (sauf `/auth` qui est à la racine)

2. **JoinRequestController**: Utilise `/api/v1/join-requests` (chemin complet dans le controller)

3. **Validation Admin Groupe**: Toujours vérifiée dans le service, pas dans le controller

4. **Tokens FCM**: Stockés dans `Person.fcmToken` pour les notifications push

5. **Paiements**: Toujours créés en `PENDING`, validés par l'admin du groupe

6. **Contributions**: Créées automatiquement quand un tour démarre

---

*Document généré pour référence lors des modifications du projet PariBa*
