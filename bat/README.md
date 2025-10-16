# Batch Files
This folder contains batch files that can quickly launch front-end and back-end applications. Due to different computer configurations, batch files may not have the same effect on other computers, so they have been added to the git ignore file ( `.gitignore` ). If the user needs this function, they can write a batch file suitable for the local machine in the following format.

## Front-end
Create a file named `frontend.bat` with the following content:
```bat
@echo off
cd /d [Your front-end project directory in absolute path]
npm run dev
```

## Back-end
Create a file named `backend.bat` with the following content:
```bat
@echo off
cd /d [Your back-end project directory in absolute path]
call conda activate face_recognition
node app.js
```
