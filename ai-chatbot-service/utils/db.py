import mysql.connector

def get_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",             # remplace par ton utilisateur MySQL
        password="MYSQL",        # remplace par ton mot de passe MySQL
        database="backoffice_db" # remplace si nécessaire
    )
def get_product_name_by_id(product_id: int) -> str:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT libelle FROM produit WHERE code_produit = %s", (product_id,))
    row = cursor.fetchone()
    cursor.close()
    conn.close()
    return row[0] if row else None