import pymysql
from sshtunnel import SSHTunnelForwarder

names = []
with open("familynames-usa-top1000.txt", 'r') as fp:
    names = fp.readlines()
    names = [names[i]+" "+names[499+i] for i in range(73)]

with SSHTunnelForwarder(
    ('193.42.40.110', 22),
    ssh_username = 'root',
    ssh_password = '52hsiehitzu',
    remote_bind_address = ('localhost', 3306) 
) as server:
    server.start()
    db = pymysql.connect(
        host = '127.0.0.1',
        port = server.local_bind_port,
        user = 'root',
        password = '52hsiehitzu',
        database = 'MedBigDataAI'
    )
    cur = db.cursor()
    for i in range(73):
        query = "INSERT INTO patient_info (id, name, age, sex) VALUES ("+str(i+1)+", \'" + names[i] +"\' ,"
        query += "(SELECT age FROM heart WHERE id = "+str(i+1)+" LIMIT 1),"
        query += "(SELECT sex FROM heart WHERE id = "+str(i+1)+" LIMIT 1));"
        cur.execute(query)
        db.commit()
    db.close