INSERT INTO users_app (id, username, password, role)
SELECT 1,'admin', '$2a$10$6pGJZ38BkKt5sOv74l9UuOlXzR3VHjNtLhM7mJZqfC0gPzYr0wT1W', 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM users_app WHERE username = 'admin');