'''
Name: outputEncoding.py

Description: Through running this python file, 
    you could transform all pictures saved in MySQL 
    and save numpy file into 'uploads' floder. 
'''

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
