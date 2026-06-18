# ============================================
# ÉTAPE 1 : Build de l'application
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

# Dossier de travail dans le conteneur
WORKDIR /app

# On copie d'abord le pom.xml seul
# (optimisation : si le code change mais pas les dépendances,
# Maven ne les retélécharge pas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# On copie le code source
COPY src ./src

# On compile et on crée le JAR
RUN mvn clean package -DskipTests

# ============================================
# ÉTAPE 2 : Image finale légère
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# On récupère uniquement le JAR depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Lancement de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]