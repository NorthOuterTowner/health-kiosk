"""
File: outputEncoding.py

Description:
    This script connects to a MySQL database, retrieves image file names stored in the `user` table,
    loads the corresponding image files from the uploads folder, extracts face encodings using the
    `face_recognition` library, and saves them as `.npy` files in the same folder.

Dependencies:
    - face_recognition
    - mysql-connector-python
    - python-dotenv
    - numpy
    
Environment Variables (.env file):
    DB_HOST      - MySQL host address
    DB_DATABASE  - Database name
    DB_USER      - Database username
    DB_PASSWORD  - Database password
    DB_CHARSET   - Character set

Output:
    - For each valid image: a corresponding .npy file containing the face encoding
      saved in the ../health-kiosk/health-kiosk-backend/uploads folder
"""
import face_recognition
import mysql.connector
import os
from mysql.connector import Error
from dotenv import load_dotenv
import numpy as np

load_dotenv()

image_base_path = "../health-kiosk-backend/uploads"

db_config = {
    'host': os.getenv('DB_HOST'),
    'database': os.getenv('DB_DATABASE'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'charset': os.getenv('DB_CHARSET')
}

connection = None
try:
    connection = mysql.connector.connect(**db_config)
    
    if connection.is_connected():
        cursor = connection.cursor()
        cursor.execute("SELECT `pic` FROM `user`")
        file_names = cursor.fetchall()
        
except Error as e:
    print(f"MySQL数据库连接或查询出错: {e}")

finally:
    if connection and connection.is_connected():
        cursor.close()
        connection.close()

for file_name_tuple in file_names:
    file_name = file_name_tuple[0]
    image_path = os.path.join(image_base_path, file_name)
    
    if not os.path.exists(image_path):
        continue
    
    try:
        db_image = face_recognition.load_image_file(image_path)
        db_face_encodings = face_recognition.face_encodings(db_image)
        
        db_face_encoding = db_face_encodings[0]

        encoding_save_path = os.path.join(image_base_path, file_name + ".npy")

        np.save(encoding_save_path, db_face_encoding)
            
    except Exception as e:
        print(f"处理图片 {file_name} 时出错: {e}")
        continue
