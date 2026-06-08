-- wstawienie admina z gotowym hashem dla hasla "admin"
INSERT INTO users (username, password, email, enabled) 
VALUES ('admin', '$2a$10$nPRdFn26AAoFCOeDAifTZuBPBiiUEsaobhGrL2zD3pRacX.i7Oth6', 'admin@bookstore.com', TRUE);

-- powiazanie admina z rola role_admin (id = 2)
INSERT INTO user_roles (user_id, role_id) 
VALUES ((SELECT id FROM users WHERE username = 'admin'), 2);
