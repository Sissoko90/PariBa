# 📱 Pariba - Mock API pour Développement Mobile

## 🚀 Démarrage Rapide

### Installation
```bash
npm install -g json-server
```

### Lancement du serveur
```bash
# Depuis le répertoire du projet
cd src/main/resources/db
json-server --watch db.json --routes routes.json --port 3000 --host 0.0.0.0
```

### URLs d'accès
- **API Base**: `http://localhost:3000`
- **Interface Web**: `http://localhost:3000` (pour visualiser les données)

## 📊 Collections Disponibles

### 🔐 **Authentification**
- `GET /persons` - Liste des utilisateurs
- `GET /users` - Comptes utilisateurs
- `GET /refreshTokens` - Tokens de rafraîchissement
- `GET /otpTokens` - Codes OTP

### 📱 **Appareils**
- `GET /deviceTokens` - Tokens d'appareils mobiles
- `POST /deviceTokens` - Enregistrer un appareil
- `PUT /deviceTokens/:id` - Mettre à jour un appareil
- `DELETE /deviceTokens/:id` - Supprimer un appareil

### 🏦 **Tontines**
- `GET /tontineGroups` - Groupes de tontines
- `GET /groupMemberships` - Membres des groupes
- `GET /tours` - Tours de tontines
- `GET /contributions` - Contributions
- `GET /payments` - Paiements

### 🔔 **Notifications**
- `GET /notifications` - Notifications
- `GET /notificationPreferences` - Préférences utilisateur
- `GET /notificationTemplates` - Templates de notifications

### 📄 **Autres**
- `GET /invitations` - Invitations
- `GET /exportJobs` - Tâches d'export
- `GET /subscriptions` - Abonnements
- `GET /auditLogs` - Logs d'audit

## 🔍 **Exemples d'Utilisation**

### Authentification
```bash
# Login (simulation)
curl "http://localhost:3000/users?username=abdaty11@gmail.com"

# Profil utilisateur
curl "http://localhost:3000/persons/p-1"
```

### Gestion des Appareils
```bash
# Mes appareils
curl "http://localhost:3000/deviceTokens?personId=p-1"

# Enregistrer un appareil
curl -X POST "http://localhost:3000/deviceTokens" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": "p-1",
    "token": "fcm-token-new-device",
    "platform": "ios",
    "deviceName": "iPhone 15",
    "appVersion": "1.0.0",
    "osVersion": "iOS 17.1",
    "active": true
  }'
```

### Tontines
```bash
# Mes groupes
curl "http://localhost:3000/tontineGroups?creatorPersonId=p-1"

# Détails d'un groupe
curl "http://localhost:3000/tontineGroups/g-1"

# Membres d'un groupe
curl "http://localhost:3000/groupMemberships?groupId=g-1"
```

### Notifications
```bash
# Mes notifications
curl "http://localhost:3000/notifications?personId=p-2"

# Marquer comme lu (simulation)
curl -X PATCH "http://localhost:3000/notifications/n-1" \
  -H "Content-Type: application/json" \
  -d '{"readFlag": true}'
```

## 🎯 **Filtres et Recherche**

### Filtres par champs
```bash
# Appareils actifs
curl "http://localhost:3000/deviceTokens?active=true"

# Notifications non lues
curl "http://localhost:3000/notifications?readFlag=false"

# Groupes par fréquence
curl "http://localhost:3000/tontineGroups?frequency=MONTHLY"
```

### Pagination
```bash
# Page 1, 10 éléments
curl "http://localhost:3000/persons?_page=1&_limit=10"

# Tri par date de création
curl "http://localhost:3000/notifications?_sort=createdAt&_order=desc"
```

### Relations
```bash
# Groupe avec ses membres
curl "http://localhost:3000/tontineGroups/g-1?_embed=groupMemberships"

# Personne avec ses appareils
curl "http://localhost:3000/persons/p-1?_embed=deviceTokens"
```

## 🔧 **Configuration CORS**

Pour le développement mobile, ajoutez ces headers :
```bash
json-server --watch db.json --routes routes.json --port 3000 --host 0.0.0.0 \
  --middlewares cors.js
```

Créez `cors.js` :
```javascript
module.exports = (req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
  next()
}
```

## 📱 **Intégration Mobile**

### React Native / Flutter
```javascript
const API_BASE = 'http://localhost:3000';

// Login
const login = async (username, password) => {
  const response = await fetch(`${API_BASE}/users?username=${username}`);
  return response.json();
};

// Mes groupes
const getMyGroups = async (personId) => {
  const response = await fetch(`${API_BASE}/tontineGroups?creatorPersonId=${personId}`);
  return response.json();
};
```

## 🎨 **Données de Test**

Le fichier contient des données réalistes pour :
- **4 utilisateurs** avec différents rôles (SUPERADMIN, ADMIN, USER)
- **2 groupes de tontines** (mensuelle et hebdomadaire)
- **3 appareils mobiles** (iPhone, Samsung, iPad)
- **Notifications** avec différents types et canaux
- **Refresh tokens** actifs
- **Historique complet** des transactions

Parfait pour tester toutes les fonctionnalités de votre app mobile ! 🚀
