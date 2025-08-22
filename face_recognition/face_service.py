"""
File: face_encoding_service.py

Description:
    This script provides face encoding and comparison services. 
    It connects to a MySQL database to fetch stored user image file names, 
    extracts face encodings from images using the `face_recognition` library,
    saves them as `.npy` files, and compares a given image against stored encodings.  

Usage:
    The script is designed to run as a child process.  
    It listens to stdin for JSON input lines with the following structure:
    {
        "action": "encode" | "compare",
        "file_name": "<image_file_name>"
    }

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
    - For "encode": saves a .npy file containing the face encoding
    - For "compare": returns a JSON result with match info
"""
import sys
import json
import face_recognition
import numpy as np
import os
import mysql.connector
from dotenv import load_dotenv

load_dotenv()
image_base_path = "../health-kiosk-backend/uploads"

db_config = {
    'host': os.getenv('DB_HOST'),
    'database': os.getenv('DB_DATABASE'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'charset': os.getenv('DB_CHARSET')
}

connection = mysql.connector.connect(**db_config)
cursor = connection.cursor()

cursor.execute("SELECT `pic` FROM `user`")
file_names = [row[0] for row in cursor.fetchall()]

cursor.close()
connection.close()

def encode_and_save(file_name):
    file_path = os.path.join(image_base_path, file_name)
    if not os.path.exists(file_path):
        return {"status": "error", "msg": f"{file_name} 不存在"}

    file_names.append(file_name)
    img = face_recognition.load_image_file(file_path)
    encodings = face_recognition.face_encodings(img)
    if len(encodings) == 0:
        return {"status": "error", "msg": "未检测到人脸"}

    encoding = encodings[0]
    npy_path = os.path.join(image_base_path, file_name + ".npy")
    np.save(npy_path, encoding)

    return {"status": "ok", "msg": f"编码已保存到 {npy_path}"}

def compare_face(file_name): 
    file_path = os.path.join(image_base_path, file_name)
    if not os.path.exists(file_path):
        result = {"status": "error", "msg": f"{file_name} 不存在"}
    else:
        given_image = face_recognition.load_image_file(file_path)
        given_encodings = face_recognition.face_encodings(given_image)

        if len(given_encodings) == 0:
            result = {"status": "error", "msg": "未检测到人脸"}
        else:
            given_encoding = given_encodings[0]

            match_found = None
            for db_file_name in file_names:
                npy_path = os.path.join(image_base_path, db_file_name + ".npy")
                if not os.path.exists(npy_path):
                    continue

                db_encoding = np.load(npy_path)
                matches = face_recognition.compare_faces([db_encoding], given_encoding)
                if matches[0]:
                    match_found = db_file_name
                    break

            if match_found:
                result = {"status": "ok", "match": match_found}
            else:
                result = {"status": "ok","match": None}
    return result


for line in sys.stdin:
    try:
        data = json.loads(line.strip())
        action = data.get("action")
        file_name = data.get("file_name")

        if action == "encode":
            result = encode_and_save(file_name)
        elif action == "compare":
            result = compare_face(file_name)
            print(json.dumps(result), flush=True)
        else:
            result = {"status": "error", "msg": "未知操作"}
    except Exception as e:
        result = {"status": "error", "msg": str(e)}
