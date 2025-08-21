import sys
import json
import face_recognition
import numpy as np
import os
import mysql.connector
from dotenv import load_dotenv

load_dotenv()
image_base_path = "../health-kiosk-backend/uploads"

# Create MySQL connection once the child process running.
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
file_names = cursor.fetchall()  # [(filename1,), (filename2,), ...]
cursor.close()
connection.close()

for line in sys.stdin:
    try:
        data = json.loads(line.strip())
        file_name = data["file_name"]
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
                for (db_file_name,) in file_names:
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

    except Exception as e:
        result = {"status": "error", "msg": str(e)}

    print(json.dumps(result), flush=True)
