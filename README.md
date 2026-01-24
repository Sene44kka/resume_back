# RESUME_BACK

## Описание
This is my resume

## Требования
- Java 25
- Spring 4.0.0

---

## FIRST RUN APPLICATION

### Create dataBase

- Создаем бд 
```CREATE DATABASE my_resume;```
- Создаем пользователя
```CREATE USER my_resume_admin WITH PASSWORD 'your_secure_password_here';```
- Даем пользователю все права на базу 
```GRANT ALL PRIVILEGES ON DATABASE my_resume TO my_resume_admin;```
- Даем права на схему public
```
GRANT ALL ON SCHEMA public TO my_resume_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO my_resume_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO my_resume_admin;
```
