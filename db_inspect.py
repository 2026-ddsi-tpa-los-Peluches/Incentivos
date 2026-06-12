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

def dump(title, sql):
    print("\n==== " + title + " ====")
    cur.execute(sql)
    rows = cur.fetchall()
    cols = [d[0] for d in cur.description]
    print(" | ".join(cols))
    for r in rows:
        print(" | ".join("" if v is None else str(v) for v in r))
    print(f"-- total: {len(rows)}")

dump("INSIGNIAS", "SELECT id, nombre, descripcion FROM insignias ORDER BY id")
dump("MISIONES", "SELECT id, nombre, insignia_id, categoria_inicio, categoria_fin, tipo_mision FROM misiones ORDER BY id")
dump("INSIGNIAS_DE_DONADOR", "SELECT donador_id FROM insignias_de_donador ORDER BY donador_id")
dump("INSIGNIAS_DE_DONADOR_INSIGNIAS", "SELECT donador_id, insignia_id FROM insignias_de_donador_insignias ORDER BY donador_id")
dump("MISIONES_DE_DONADOR", "SELECT donador_id, mision_actual_id FROM misiones_de_donador ORDER BY donador_id")
dump("MISIONES_DE_DONADOR_MISIONES", "SELECT donador_id, mision_id FROM misiones_de_donador_misiones ORDER BY donador_id")

cur.close()
conn.close()
