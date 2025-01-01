import face_recognition as fr
from flask import Flask, request
from flask_cors import CORS
from sqlite3 import connect, Cursor
from USER import *
import numpy as np
from datetime import datetime
from USER import User


app = Flask(__name__)
CORS(app)


users, face_encodings = get_users()

def find_user(face_encoding) -> User | None:
    results = fr.compare_faces(face_encodings, face_encoding, tolerance=0.4)
    found_user_index = np.argmax(results)
    
    if results[found_user_index]:
        return users[found_user_index]
    
     


@app.get("/FRapi/history")
def get_history() -> list:
    
    cursor = get_cursor()
    cursor.execute(
        """
        SELECT * FROM history;
        """
    )
    return cursor.fetchall()

@app.get("/FRapi/users")
def get_users() -> list:
    
    cursor = get_cursor()
    cursor.execute(
        """
        SELECT id, `1st_name`,`2nd_name` FROM users_encodings;
        """
    )

    users = cursor.fetchall()
    cursor.close()
    return users

@app.route("/upload", methods=['POST'])
def upload():
    file = request.data
    with open("temp/uploaded_image.png", "wb") as f:
        f.write(file)
    
    # Check images
    received_image = fr.load_image_file("temp/uploaded_image.png")
    face_location = fr.face_locations(received_image)
    if face_location:
        face_encoding = fr.face_encodings(received_image, face_location)[0]
        
        found_user = find_user(face_encoding)
        if found_user:
            print(found_user)
            cursor = get_cursor()
            cursor.execute(
                """
                INSERT INTO history (date_time, id_user, firstName, lastName)
                VALUES (?, ?, ?, ?);
                """
                ,(datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f"), found_user.id, found_user.name1, found_user.name2)
            )
            cursor.connection.commit()
            cursor.close()
            return {"message":"Face detected successfully", "user":{"id":found_user.id,"firstName":found_user.name1,"secondName":found_user.name2}}, 200
    
        return {"message":"Face No existant in the DataBase"}, 400
    
    return {"message":"No face detected",}, 400
    

app.run(debug=True,)