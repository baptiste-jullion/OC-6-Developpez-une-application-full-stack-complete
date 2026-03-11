# MDD - Monde de Dév

MDD est un réseau social dédié aux développeurs, conçu pour faciliter le partage de connaissances, la mise en relation et la collaboration entre pairs. Ce projet est le Minimum Viable Product (MVP) de la plateforme.

## 🚀 Fonctionnalités du MVP

- **Authentification sécurisée** : Inscription et connexion via JWT.
- **Thématiques** : Abonnement à des sujets spécifiques (Java, JavaScript, DevOps, etc.).
- **Articles** : Rédaction d'articles liés à un thème et consultation du fil d'actualité en fonction des abonnements.
- **Commentaires** : Interaction sur les articles via un système de commentaires.
- **Profil** : Gestion des informations personnelles et de la liste des abonnements.
- **Design Responsive** : Interface conçue pour Desktop, Tablette et Mobile.

---

## 🛠 Stack Technique

### Backend
- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Security & JWT**
- **PostgreSQL 14.2**
- **MapStruct**
- **Lombok** 
- **Scalar**

### Frontend
- **Angular 20**
- **Angular Material**
- **Tailwind CSS**
- **Zod**

---

## 📦 Installation et Lancement

### Prérequis
- **Docker & Docker Compose** (optionnel, pour la base de données)
- **Java 21**
- **Node.js** (v20 ou v22 recommandé)
- **Angular CLI** (`npm install -g @angular/cli`)

### 1. Configuration de l'environnement
À la racine du projet, créez un fichier `.env` en vous basant sur le fichier `.env.example` :
```bash
cp .env.example .env
```
Remplissez les variables (identifiants de base de données et clé secrète JWT).

### 2. Lancement de la base de données (Docker)
```bash
docker compose up -d
```
Vous pouvez aussi utiliser une base de données locale

### 3. Lancement du Backend
```bash
cd back
./mvnw clean install
./mvnw spring-boot:run
```
L'API sera accessible sur `http://localhost:8080`.
La documentation Scalar est disponible sur : `http://localhost:8080/docs`.

### 4. Lancement du Frontend
```bash
cd front
npm install
ng serve
```
L'application sera accessible sur `http://localhost:4200`.

---

## 🏗 Architecture du Projet

Le projet suit une architecture monolithique modulaire respectant les principes **SOLID** :

- **`entity`** : Modèles de données JPA héritant d'une `BaseEntity` (UUID & Timestamps).
- **`dto`** : Objets de transfert de données pour sécuriser les échanges API.
- **`mapper`** : Interfaces MapStruct pour la conversion automatique Entity/DTO.
- **`repository`** : Interfaces Spring Data JPA.
- **`service`** : Logique métier.
- **`security`** : Filtre d'authentification JWT.

---

## 🧪 Tests

### Backend
Pour lancer les tests d'intégration et unitaires (JUnit 5 / MockMvc) :
```bash
cd back
./mvnw test
```

---

## 📝 Documentation API
Le projet génère automatiquement une documentation API via Scalar (basée sur OpenAPI) pour une meilleure expérience de développement. Une fois le backend lancé, vous pouvez accéder à la documentation complète et testez les endpoints directement sur :
👉 **[http://localhost:8080/docs](http://localhost:8080/docs)**
