-- Migration pour corriger les emails vides dans la table persons
-- Les emails vides causent des problèmes de contrainte unique

-- Mettre à NULL tous les emails vides
UPDATE persons 
SET email = NULL 
WHERE email = '' OR email IS NULL OR TRIM(email) = '';

-- Note: Cette migration permet d'avoir plusieurs enregistrements avec email NULL
-- sans violer la contrainte unique, car NULL n'est pas considéré comme une valeur unique en SQL
