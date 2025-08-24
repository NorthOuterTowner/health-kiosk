# 铁路司机用健康检测终端设计

## :rocket: 项目简介

本项目基于多种检测传感器与stm32单片机系统相结合，并基于Android系统开发用户交互软件实现健康检测终端设计；同时，在结合Android及嵌入式单片机系统终端的基础上，完成管理软件及数据库的设计，形成了一套软硬件结合的健康检测系统。

## :wrench: 需要进行的配置

## :computer: 前端使用说明

前端为管理员或用户进行信息查看所使用的UI界面。使用Vue.js框架，使用npm包管理器进行依赖管理，提供用户交互界面。

![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white) ![NPM](https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white)  ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white) ![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)

### 1.安装依赖
首先在官网中下载node.js并安装，之后安装相关的npm包。
```bash
cd health-kiosk-frontend
npm install
```

### 2.运行
在终端运行如下命令，之后打开浏览器访问`localhost:5173`即可。
```bash
cd health-kiosk-frontend
npm run dev
```

## :iphone: Android APP

Android APP为用户在体检过程中提供指引，并可视化的查看当前体检数据，使用Kotlin语言开发，使用Gradle进行依赖管理。

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Ktor](https://img.shields.io/badge/Ktor-000000?style=for-the-badge&logo=ktor&logoColor=white)

### 1.运行
使用数据线连接待运行设备，之后在Android Studio中打开项目，点击运行按钮即可。

## :computer: 后端使用说明
后端使用node.js和Express框架，连接MySQL数据库，并提供RESTful API接口供前端调用。

![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white) ![Express](https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white) ![NPM](https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) ![Apifox](https://img.shields.io/badge/Apifox-FF6A00?style=for-the-badge&logo=apifox&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens) ![dotenv](https://img.shields.io/badge/dotenv-000000?style=for-the-badge&logo=dotenv) 

### 1.运行
在scheduling目录打开cmd。
```bash 
cd health-kiosk-backend
npm install
node app.js
```
之后在浏览器打开`localhost:8080`即可。

### 2.改变后端端口
默认为8080端口，如果需要改动，在`app.js`中有如下语句决定启动端口。
```javaScript
const port = 8080;
```

## :bookmark_tabs:数据库说明
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

数据库在本地创建后使用 ***mysqldump*** 导出数据形成***sql***文件，输入本地MySQL服务器的凭据即可使用。

#### :file_folder: 文件说明

- schedule.sql为MySQL根据数据库结构自动导出的文件
- data为数据源xls格式和xlsx格式

---
####  :tent: 导出方式

使用 mysqldump 命令将数据库导出为 SQL 文件。
```bash
mysqldump -u [username] -p [database_name] > [database_name.sql]
```

*方括号内为变量，根据实际情况填写。[username] 是本地 MySQL 的用户名，[database_name] 是要导出的数据库名称，[database_name.sql] 是导出的 SQL 文件名。下同*
#### :sparkler: 使用方式
法1：使用 mysql 命令将 SQL 文件导入到本地 MySQL 服务器，即可在本地使用该数据库。
```bash
mysql -u [username] -p [database_name] < [database_name.sql]
```
法2：直接启动mysql命令行，将 SQL 文件导入到本地 MySQL 服务器
use [database_bane]
source [D:/Demo.sql]

## 人脸识别模块

本模块在后端中进行启动，用于 Android 用户人脸识别认证登录。使用dlib和face_recognition库进行人脸识别，使用Python语言开发。

![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white) ![Dlib](https://img.shields.io/badge/Dlib-6C8CD5?style=for-the-badge&logo=dlib&logoColor=white) ![face_recognition](https://img.shields.io/badge/face__recognition-FF6F61?style=for-the-badge&logo=python&logoColor=white)

### 1.安装依赖
```bash
conda create -n face_recognition python=3.9
conda activate face_recognition

conda install -c conda-forge dlib
pip install face_recognition
```
### 2.运行
```bash
cd face_recognition
conda activate face_recognition
```