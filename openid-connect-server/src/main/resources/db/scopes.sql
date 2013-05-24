--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT FALSE;

START TRANSACTION;

--
-- Insert scope information into the temporary tables.
-- 

INSERT INTO system_scope_TEMP (scope, description, icon, allow_dyn_reg, default_scope, structured) VALUES
  ('openid', 'log in using your identity', 'user', true, true, false),
  ('profile', 'basic profile information', 'list-alt', true, true, false),
  ('email', 'email address', 'envelope', true, true, false),
  ('address', 'physical address', 'home', true, true, false),
  ('phone', 'telephone number', 'bell', true, true, false),
  ('search', 'BB+ Document Search', 'bell', true, true, true),
  ('summary', 'BB+ Clinical Summary', 'bell', true, true, true);
  

--
-- Merge the temporary scopes safely into the database. This is a two-step process to keep scopes from being created on every startup with a persistent store.
--

MERGE INTO system_scope
	USING (SELECT scope, description, icon, allow_dyn_reg, default_scope, structured FROM system_scope_TEMP) AS vals(scope, description, icon, allow_dyn_reg, default_scope, structured)
	ON vals.scope = system_scope.scope
	WHEN NOT MATCHED THEN
	  INSERT (scope, description, icon, allow_dyn_reg, default_scope, structured) VALUES(vals.scope, vals.description, vals.icon, vals.allow_dyn_reg, vals.default_scope, structured);

COMMIT;

SET AUTOCOMMIT TRUE;
