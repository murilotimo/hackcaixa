from flask import Flask, request, jsonify
import pymssql
from decimal import Decimal, ROUND_HALF_UP
import asyncio
import logging
from azure.eventhub import EventData
from azure.eventhub.aio import EventHubProducerClient
import json

app = Flask(__name__)

# Configurar o nível de log desejado (por exemplo, DEBUG, INFO, WARNING, ERROR)
app.logger.setLevel(logging.DEBUG)

# Configurar a conexão com o banco de dados SQL Server
server = 'dbhackathon.database.windows.net'
port = 1433
database = 'hack'
username = 'hack'
password = 'Password23'

# Configurar a conexão com o Eventhub
#EVENT_HUB_CONNECTION_STR = "Endpoint=sb://eventhack.servicebus.windows.net/;SharedAccessKeyName=hack;SharedAccessKey=HeHeVaVqyVkntO2FnjQcs2Ilh/4MUDo4y+AEhKp8z+g=;EntityPath=simulacoes"
EVENT_HUB_CONNECTION_STR = "Endpoint=sb://meuhackcaixa.servicebus.windows.net/;SharedAccessKeyName=minha-politica;SharedAccessKey=kpOB1ZLepFAnb3evFzjspxCXX+voi6Inn+AEhHZBSlA=;EntityPath=testehackcaixa"


#EVENT_HUB_NAME = "eventhack"

# Configurar a conexão com o banco de dados MySQL
conn = pymssql.connect(
    host=server,
    port=port,
    user=username,
    password=password,
    database=database
)

def decimal_default(obj):
    if isinstance(obj, Decimal):
        return float(obj)
    raise TypeError("Object of type 'Decimal' is not JSON serializable")

# Grava os dados no Eventhub
async def gravar_eventhub(mensagem):
    app.logger.info('Iniciando a gravação dos dados no Event Hub')

    producer = EventHubProducerClient.from_connection_string(
        conn_str=EVENT_HUB_CONNECTION_STR
    )

    async with producer:
        # Create a batch.
        event_data_batch = await producer.create_batch()

        # Add events to the batch.
        event_data_batch.add(EventData(mensagem))

        # Send the batch of events to the event hub.
        await producer.send_batch(event_data_batch)

    app.logger.info('Mensagens enviadas com sucesso!')
    
    
# Gera sugestões baseadas nos parametros informados pelos usuários que não encontraram produtos adequados
def gera_sugestao(valor_desejado, prazo):
    sugestoes = []

    cursor = conn.cursor()
    cursor.execute('SELECT min(VR_MINIMO) from hack.dbo.PRODUTO')
    valor_minimo = cursor.fetchone()[0]

    if valor_desejado < valor_minimo:
        sugestoes.append(f'O valor mínimo de empréstimo é de 200 reais R$ {valor_minimo}')

    sql_produto_por_valor = f'''
        SELECT
        CO_PRODUTO,
        NO_PRODUTO,
        PC_TAXA_JUROS,
        NU_MINIMO_MESES,
        NU_MAXIMO_MESES,
        VR_MINIMO,
        VR_MAXIMO
    FROM
        hack.dbo.PRODUTO
    WHERE
            VR_MINIMO <= {valor_desejado} 
        AND (VR_MAXIMO >= {valor_desejado} OR VR_MAXIMO IS NULL);
    '''

    cursor.execute(sql_produto_por_valor)
    produtos_por_valor = cursor.fetchall()

    for produto in produtos_por_valor:
        msg = f'Para o valor de R$ {valor_desejado} você pode contratar {produto[1]} com taxa de juros de { round( produto[2]*100 , 2) }% ao mês e prazo de {produto[3]} a {produto[4]} meses.'
        sugestoes.append(msg)

    envelope_retorno = {'error': 'Nenhum produto encontrado para os valores e prazos informados.',
                        'sugestoes': sugestoes}
    return jsonify(envelope_retorno)

# calcula a simulacao das parcelas para o sistema de amortização SAC
def calcular_amortizacao_sac(valor_desejado, prazo, taxa_juros):
    # Implementando a lógica de cálculo do SAC
    amortizacao = Decimal(valor_desejado) / prazo
    saldo_devedor = Decimal(valor_desejado)
    taxa_juros = Decimal(taxa_juros)
    prestacoes = []

    for mes in range(1, prazo + 1):
        juros = saldo_devedor * taxa_juros
        prestacao = amortizacao + juros
        saldo_devedor -= amortizacao

        prestacao = prestacao.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)
        amortizacao = amortizacao.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)
        juros = juros.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)

        prestacoes.append({
            'numero': mes,
            'valorAmortizacao': amortizacao,
            'valorJuros': juros,
            'valorPrestacao': prestacao,
        })

    return prestacoes

# calcula a simulacao das parcelas para o sistema de amortização PRICE
def calcular_amortizacao_price(valor_desejado, prazo, taxa_juros):
    valor_desejado = Decimal(valor_desejado)
    prazo = int(prazo)
    parcelas = []

    taxa_juros_mensal = taxa_juros
    coeficiente = (taxa_juros_mensal * (1 + taxa_juros_mensal) ** prazo) / ((1 + taxa_juros_mensal) ** prazo - 1)
    prestacao = valor_desejado * coeficiente

    saldo_devedor = valor_desejado

    for mes in range(1, prazo + 1):
        juros = saldo_devedor * taxa_juros_mensal
        amortizacao = prestacao - juros
        saldo_devedor -= amortizacao

        prestacao = prestacao.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)
        amortizacao = amortizacao.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)
        juros = juros.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)
        saldo_devedor = saldo_devedor.quantize(Decimal('0.00'), rounding=ROUND_HALF_UP)

        parcelas.append({
            'numero': mes,
            'valorAmortizacao': amortizacao,
            'valorJuros': juros,
            'valorPrestacao': prestacao,
        })

    return parcelas

@app.route('/')
def hello_world():
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
    rows = cursor.fetchall()
   
    return jsonify(rows)

# Definir a rota para receber a solicitação de simulação de empréstimo
@app.route('/simulacao', methods=['POST'])
def simulacao_emprestimo():
    app.logger.info('Esta é uma mensagem de log de nível INFO')
    envelope_json = request.get_json()

    # Verificar se todos os campos obrigatórios estão presentes
    if 'prazo' not in envelope_json or 'valorDesejado' not in envelope_json:
        return jsonify({'error': 'É necessário informar o prazo e o valor desejado'})

    prazo = envelope_json['prazo']
    valor_desejado = envelope_json['valorDesejado']

    # Validar o tipo de dados dos campos
    if not isinstance(prazo, int) or not isinstance(valor_desejado, (int, float)):
        return jsonify({'error': 'Os valores de prazo e valor desejado devem ser números'})

    # Verificar se os valores estão dentro das faixas permitidas
    if prazo <= 0 or valor_desejado <= 0:
        return jsonify({'error': 'Valores devem ser maiores que zero'})

    # Consultar informações parametrizadas no banco de dados
    cursor = conn.cursor()
    sql_selecionar_produto = f'''
    SELECT
        CO_PRODUTO,
        NO_PRODUTO,
        PC_TAXA_JUROS,
        NU_MINIMO_MESES,
        NU_MAXIMO_MESES,
        VR_MINIMO,
        VR_MAXIMO
    FROM
        hack.dbo.PRODUTO
    WHERE
            NU_MINIMO_MESES <= {prazo} 
        AND (NU_MAXIMO_MESES >= {prazo} OR NU_MAXIMO_MESES IS NULL)
        AND VR_MINIMO <= {valor_desejado}
        AND (VR_MAXIMO >= {valor_desejado} OR VR_MAXIMO IS NULL);
    '''

    cursor.execute(sql_selecionar_produto)
    produtos = cursor.fetchall()

    # Verificar se algum produto foi encontrado para os parâmetros informados
    if not produtos:
        return gera_sugestao(valor_desejado,prazo)
    # Verificar se mais de um produto foi encontrado para os parâmetros informados
    elif len(produtos) > 1:
        return jsonify({'error': 'Mais de um produto encontrado para os valores e prazos informados.'})
    # Se apenas um produto foi encontrado, continuar com a simulação
    else:       
        # Realizar os cálculos para os sistemas de amortização SAC e PRICE
        resultado_sac = calcular_amortizacao_sac(valor_desejado,prazo,produtos[0][2])
        resultado_price = calcular_amortizacao_price(valor_desejado,prazo,produtos[0][2])

        # Retornar o envelope JSON com o nome do produto e os resultados da simulação
        envelope_retorno = {
            'codigoProduto': produtos[0][0],
            'descricaoProduto': produtos[0][1],
            'taxaJuros': produtos[0][2],
            'resultadoSimulacao': [
                {
                    'tipo': 'SAC',
                    'parcelas': resultado_sac
                },
                {
                    'tipo': 'PRICE',
                    'parcelas': resultado_price
                }
            ]
        }

        # Gravar o envelope JSON no Eventhub
        asyncio.run(gravar_eventhub( jsonify(envelope_retorno).get_data(as_text=True) ))

        return jsonify(envelope_retorno)


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)