import sqlite3 as sql


conn = sql.connect("DB/ProjectFR.db")
cursor = conn.cursor()
cursor.execute("SELECT * from users_encodings;")

table = cursor.fetchall()
print(table)