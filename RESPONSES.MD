## 1. Distinctions entre JPA, Hibernate et Spring Data JPA
   JPA (Jakarta Persistence / Java Persistence API) : Spécification. Il s'agit d'un ensemble d'interfaces, d'annotations (@Entity, @Table) et de règles standardisées définissant la gestion de la persistance objet-relationnelle en Java.

Hibernate : Implémentation. C'est le framework ORM (Object-Relational Mapping) concret qui réalise les interfaces de la spécification JPA et exécute les requêtes SQL sous-jacentes.

Spring Data JPA : Orchestration / Abstraction. C'est une couche matérielle au-dessus de JPA/Hibernate. Elle élimine le code boilerplate des DAO en générant automatiquement l'implémentation des repositories (Repository, CrudRepository, JpaRepository) à partir d'interfaces.

## 2. Annotation @Transactional sur les méthodes du Repository
   Methodes de mutation (save, update, delete, deleteAll) : Elles modifient l'état de la base de données. L'annotation @Transactional est obligatoire pour garantir l'atomicité de la transaction (ACID), forcer la synchronisation du contexte de persistance (flush) vers la base de données, et annuler les modifications (rollback) en cas d'erreur.

Méthodes de lecture (findById, findAll) : Elles sont en lecture seule. Elles ne nécessitent pas de transaction d'écriture explicite pour altérer des données, ce qui évite la gestion coûteuse de transactions d'écriture et de verrous inutiles au niveau du repository.

## 3. Différence entre persist(entity) et merge(entity)
   persist(entity) : Prends une entité à l'état transient (nouvellement créée, non gérée) et l'attache directement au contexte de persistance (EntityManager). L'objet passé devient managé (managed).

merge(entity) : Copie l'état d'une entité détachée (detached) ou distante dans une instance managée au sein du contexte de persistance courant.

Retour de merge : Renvoie une nouvelle instance managée de l'entité.

Objet passé en paramètre : L'objet d'origine reste détaché du contexte de persistance. Toute modification ultérieure sur cet objet initial ne sera pas répercutée en base de données.

## 4. Différence entre JPQL et SQL
   Nature de la différence : Le SQL manipule directement les tables, jointures et colonnes d'une base de données relationnelle. Le JPQL (Java Persistence Query Language) est un langage orienté objet.

Cible de FROM Student WHERE lastName = :theData : La requête porte sur la classe Java (Student) et son champ/attribut Java (lastName), et non sur la table ou la colonne physique en base de données.

## 5. Avantages des paramètres nommés (:theData) vs concaténation
   Sécurité contre les injections SQL/JPQL : Le paramètre nommé sépare strictement la structure de la requête des données utilisateur. L'injection de code malveillant devient impossible.

Performance (Cache de requêtes) : Le SGBD et le fournisseur JPA peuvent précompiler le plan d'exécution de la requête et le réutiliser pour différentes valeurs passées au paramètre.

Gestion automatique des types : Les types complexes (dates, chaînes contenant des guillemets/apostrophes) sont automatiquement échappés et convertis par le driver sans erreur de syntaxe.

## 6. Rôle de @Repository et traduction d'exceptions
   En plus de déclarer un bean géré par le conteneur Spring (spécialisation de @Component), @Repository active la traduction d'exceptions (Exception Translation).

Grâce au post-processeur PersistenceExceptionTranslationPostProcessor, Spring intercepte les exceptions natives de bas niveau liées à la persistance (ex: SQLException, HibernateException, PersistenceException) et les convertit en une hiérarchie d'exceptions unchecked unifiée propre à Spring : DataAccessException (par exemple DataIntegrityViolationException ou EntityNotFoundException).

## 7. Suppression avec entityManager.remove()
   Pourquoi entityManager.remove(new Student(5)) échoue : La méthode remove() exige que l'instance passée en paramètre soit à l'état managé (managed) par le contexte de persistance actuel. Une instance créée via new Student(5) est à l'état transient (non rattachée).

Procédure correcte :

Java
// 1. Charger l'entité pour la rendre managée
Student student = entityManager.find(Student.class, 5);
// Ou utiliser getReference(Student.class, 5) pour éviter un SELECT inutile si la suppression est directe

// 2. Supprimer l'instance managée
if (student != null) {
entityManager.remove(student);
}
## 8. Comportement des valeurs de spring.jpa.hibernate.ddl-auto
   none : Aucune modification du schéma. Comportement par défaut si aucune base embarquée n'est utilisée.

validate : Vérifie que le schéma de la base correspond aux entités Java. Lève une erreur au démarrage si le schéma diverge, sans modifier la base.

update : Met à jour le schéma existant en ajoutant les nouvelles tables/colonnes. Ne supprime jamais de tables ou de colonnes existantes.

create : Supprime toutes les tables existantes au démarrage, puis recrée le schéma à partir des entités Java.

create-drop : Crée le schéma au démarrage de l'application et le supprime intégralement lors de la fermeture de la SessionFactory (à l'arrêt de l'application).

Valeur pour la production : none (ou validate). En production, les évolutions de schéma doivent être gérées de manière traçable et sécurisée à l'aide d'outils de migration de schéma comme Flyway ou Liquibase.

Pourquoi jamais create en production : create détruit la structure de la base et efface l'intégralité des données existantes à chaque redémarrage de l'application, provoquant une perte irréversible des données de production.