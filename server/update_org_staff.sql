ALTER TABLE org_staff ADD COLUMN role VARCHAR(20) DEFAULT 'operator';
UPDATE org_staff SET role = 'admin' WHERE id = 1;
