# Use a imagem base do Python 3.11
FROM python:3.11

# Instala as dependências do projeto
RUN pip install flask pyodbc

# Expõe a porta 5000 para acesso à API
EXPOSE 5000

# Define o comando padrão para executar a aplicação Flask
CMD ["python", "app.py"]