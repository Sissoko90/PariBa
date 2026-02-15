-- Migration pour ajouter le champ metadata_json à la table notifications
-- Ce champ stocke des informations supplémentaires comme le code d'invitation

ALTER TABLE notifications ADD COLUMN IF NOT EXISTS metadata_json TEXT;

-- Créer un index pour améliorer les performances de recherche
CREATE INDEX IF NOT EXISTS idx_notifications_metadata ON notifications USING gin ((metadata_json::jsonb));

COMMENT ON COLUMN notifications.metadata_json IS 'Métadonnées JSON pour stocker des informations supplémentaires (ex: code d''invitation)';
