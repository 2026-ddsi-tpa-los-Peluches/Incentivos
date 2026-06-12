import psycopg2

conn = psycopg2.connect(
    host="dpg-d8j1tgflk1mc738nrl4g-a.oregon-postgres.render.com",
    port=5432,
    dbname="incentivos_ygr8",
    user="incentivosdb_tfw4_user",
    password="bJSEz81VSXvvEj4WTBe7utCiXBBq8owY",
    sslmode="require",
)
cur = conn.cursor()

# Reales a preservar:
#   insignias 24, 35 ; misiones 26, 37 ; donador real "1".
# Basura de test: donador "donador-1" y todo lo demas (insignias 'Nueva'/'Ins',
# misiones con insignia_id='ins-id').
steps = [
    ("insignias_de_donador_insignias (test)",
     "DELETE FROM insignias_de_donador_insignias WHERE donador_id = 'donador-1'"),
    ("insignias_de_donador (test)",
     "DELETE FROM insignias_de_donador WHERE donador_id = 'donador-1'"),
    ("misiones_de_donador_misiones (test)",
     "DELETE FROM misiones_de_donador_misiones WHERE donador_id = 'donador-1'"),
    ("misiones_de_donador (test)",
     "DELETE FROM misiones_de_donador WHERE donador_id = 'donador-1'"),
    ("insignias (test)",
     "DELETE FROM insignias WHERE id NOT IN (24, 35)"),
    ("misiones (test)",
     "DELETE FROM misiones WHERE id NOT IN (26, 37)"),
]

try:
    for label, sql in steps:
        cur.execute(sql)
        print(f"  - {label}: {cur.rowcount} filas borradas")
    conn.commit()
    print("\nOK: commit realizado.")
except Exception as e:
    conn.rollback()
    print(f"\nERROR -> rollback. {e}")
finally:
    cur.close()
    conn.close()
