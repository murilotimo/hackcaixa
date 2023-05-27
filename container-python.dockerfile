# Use a imagem base do Python 3.11
FROM python:3.11

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Instala as dependências do projeto
RUN pip install flask pymssql

# Expõe a porta 5000 para acesso à API
EXPOSE 5000

# Define o comando padrão para executar a aplicação Flask
#CMD ["python", "app.py"]

#inicia o servidor flask em modo de desenvolvimento 
CMD ["flask", "run", "--host=0.0.0.0", "--port=5000", "--reload"]
