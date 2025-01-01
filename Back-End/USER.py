import pickle
from sqlite3 import connect, Cursor
import face_recognition as fr

class User:
    
    def __init__(self,id:int, name1:str, name2:str, path_img:str, face_encoding):
        self.id = id
        self.name1 = name1
        self.name2 = name2
        self.path_img = path_img
        self.face_encoding = pickle.loads(face_encoding)
        
    def __str__(self):
        return f"{self.name1} {self.name2}"
        
# def get_users():
#     cursor = get_cursor()
#     cursor.execute(
#         """
#         SELECT * from users_encodings;
#         """
#     )
    
#     table = cursor.fetchall()
#     users:list[User] = [
#         User(id, first_name, second_name, path_img, face_encoding) 
#             for (id, first_name, second_name, path_img, face_encoding) in table
#             ]
#     encodings = [
#         user.face_encoding for user in users
#     ]
#     return users, encodings

def update_user(id:int,face_encoding):
    cursor = get_cursor()
    cursor.execute(
        """
        UPDATE users_encodings
        SET encoding = ?
        WHERE id = ?;
        """,
        (face_encoding, id)
    )
    cursor.connection.commit()
 
def get_users():
    cursor = get_cursor()
    cursor.execute(
        """
        SELECT * from users_encodings;
        """
    )
    
    table = cursor.fetchall()

    users:list[User] = []
    for (id, first_name, second_name, path_img, face_encoding) in table :
        if face_encoding is None:
            received_image = fr.load_image_file(path_img)
            face_location = fr.face_locations(received_image)
            face_encoding = fr.face_encodings(received_image, face_location)[0]
            face_encoding = pickle.dumps(face_encoding)

            try:
                update_user(id, face_encoding)
            except Exception as e:
                print(f"Failed to update user {id}: {e}")
        users.append(User(id, first_name, second_name, path_img, face_encoding))

    encodings = [
        user.face_encoding for user in users
    ]
    return users, encodings

def get_cursor() -> Cursor:
    conn = connect("DB/ProjectFR.db")
    cursor = conn.cursor()    
    return cursor

        