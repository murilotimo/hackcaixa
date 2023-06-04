# %%
# Vamos escrever um programa para testar o tempo de resposta das 2 APIs.
import requests
import time
import json
import pymssql
import random
from decimal import Decimal


# Captura os produtos disponíveis no banco de dados SQL Server
# Configurar a conexão com o banco de dados SQL Server
server = 'dbhackathon.database.windows.net'
port = 1433
database = 'hack'
username = 'hack'
password = 'Password23'

conn = pymssql.connect(
    host=server,
    port=port,
    user=username,
    password=password,
    database=database
)

sql = '''
SELECT
	CO_PRODUTO,
	NO_PRODUTO,
	PC_TAXA_JUROS,
	NU_MINIMO_MESES,
	NU_MAXIMO_MESES,
	VR_MINIMO,
	VR_MAXIMO
FROM
	hack.dbo.PRODUTO;
    '''
cursor = conn.cursor()
cursor.execute(sql)
produtos = cursor.fetchall()

massa_teste = []
# %%
# Vamos criar uma massa de teste com 1000 solicitações de empréstimo para cada produto
for produto in produtos:
    vlr_inicio = int(produto[5])
    vlr_fim = 50000000 if produto[6] is None else int(produto[6])
    pz_fim = 99 if produto[4] is None else int(produto[4])

    for i in range(1000):
        massa_teste.append({
            "valorDesejado": random.randint(vlr_inicio, vlr_fim)
            ,"prazo": random.randint(produto[3], pz_fim)
        })
print(f'Quantidade de solicitações de empréstimo: {len(massa_teste)}')

# %%
# Testar a API em Java
url = 'http://localhost:8002/simulador'
headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
tempo_resposta = []
for solicitacao in massa_teste:
    start = time.time()
    response = requests.post(url, data=json.dumps(solicitacao), headers=headers)
    end = time.time()
    tempo_resposta.append(end - start)
print(f'Tempo médio de resposta da API em Java: {sum(tempo_resposta)/len(tempo_resposta)} segundos')
print(f'Tempo mínimo de resposta da API em Java: {min(tempo_resposta)} segundos')
print(f'Tempo máximo de resposta da API em Java: {max(tempo_resposta)} segundos')
print(f'Tempo Total de resposta da API em Java: {sum(tempo_resposta)} segundos')

# %%
# Testar a API em Python
url = 'http://localhost:8001/simulador'
headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
tempo_resposta = []
for solicitacao in massa_teste:
    start = time.time()
    response = requests.post(url, data=json.dumps(solicitacao), headers=headers)
    end = time.time()
    tempo_resposta.append(end - start)
print(f'Tempo médio de resposta da API em Python: {sum(tempo_resposta)/len(tempo_resposta)} segundos')
print(f'Tempo mínimo de resposta da API em Python: {min(tempo_resposta)} segundos')
print(f'Tempo máximo de resposta da API em Python: {max(tempo_resposta)} segundos')
print(f'Tempo Total de resposta da API em Python: {sum(tempo_resposta)} segundos')