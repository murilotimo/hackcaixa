# Simuladores de Empréstimo

Este projeto faz parte do Primeiro Desafio de Desenvolvimento - Hackathon VITEC 2023 para o Desafio perfil Back-end: API Simulador. Desenvolvemos APIs que atendem aos seguintes requisitos:

- Receber um envelope JSON, via chamada à API, contendo uma solicitação de simulação de empréstimo;
- Consultar um conjunto de informações parametrizadas em uma tabela de banco de dados SQL Server;
- Validar os dados de entrada da API com base nos parâmetros de produtos retornados no banco de dados;
- Filtrar qual produto se adequa aos parâmetros de entrada;
- Realizar os cálculos para os sistemas de amortização SAC e PRICE de acordo com os dados validados;
- Retornar um envelope JSON contendo o nome do produto validado e o resultado da simulação utilizando dois sistemas de amortização (SAC e Price);
- Gravar esse mesmo envelope JSON no Eventhub.

## Controle de Versão com Git

Gostaríamos de destacar que nosso projeto é gerenciado por um sistema de controle de versão Git. O Git nos permite rastrear todas as alterações feitas no código-fonte ao longo do tempo, registrando um histórico completo de desenvolvimento.

Alguns dos principais benefícios do uso do controle de versão Git incluem:

1. Histórico de Alterações: O Git mantém um registro detalhado de todas as alterações feitas no código-fonte. Isso permite que acompanhemos quem fez quais alterações e quando, facilitando a colaboração em equipe e a identificação de problemas ou erros.

2. Versionamento de Releases: Com o Git, podemos criar e gerenciar diferentes versões do nosso projeto. Podemos marcar versões específicas, criar branches para desenvolvimento de recursos específicos e mesclar alterações de forma controlada. Isso ajuda a organizar o trabalho e a manter um histórico claro de cada release.

3. Reversão de Alterações: Em caso de problemas ou erros, o Git permite reverter alterações indesejadas de forma rápida e eficiente. Podemos voltar a um estado anterior do código com facilidade, garantindo a estabilidade do projeto.

4. Branches para Desenvolvimento Paralelo: Com o Git, podemos criar branches independentes para desenvolver recursos ou correções separadamente do código principal. Isso possibilita a colaboração em equipe de forma paralela e segura, evitando conflitos entre as alterações.

5. Colaboração em Equipe: O Git facilita a colaboração entre os membros da equipe de desenvolvimento. Vários desenvolvedores podem trabalhar simultaneamente no mesmo projeto, mesclando suas alterações de forma controlada e resolvendo conflitos de forma eficiente.

6. Integração com Ferramentas de Desenvolvimento: O Git se integra facilmente com várias ferramentas de desenvolvimento, como sistemas de integração contínua, ambientes de desenvolvimento integrado (IDEs) e plataformas de hospedagem de código, facilitando o fluxo de trabalho e a automação de tarefas.

O uso do Git como sistema de controle de versão traz benefícios significativos em termos de rastreabilidade, colaboração e gerenciamento de alterações no nosso projeto. Estamos comprometidos em utilizar boas práticas de gerenciamento de versões para garantir um desenvolvimento ágil, organizado e confiável.

## Simulador em Python

O simulador em Python está localizado na pasta `srcPython`. 

### Pré-requisitos

Antes de executar o simulador em Python, verifique se você possui os seguintes requisitos instalados em seu ambiente:

- Python 3.11: A linguagem de programação Python é necessária para executar o simulador. Verifique se você tem o Python instalado executando o comando `python --version` no terminal.

### Executando o Simulador em Python

1. Navegue até a pasta `srcPython` do projeto.
2. Execute a instalção das dependências `pip install -r requirements.txt` 
3. Execute o comando `python simulator.py` para iniciar o simulador.
4. A API estará disponivel na porta 5000 
5. O simulador calculará as parcelas com base nas informações fornecidas e retornará o resultado em json.

## Simulador em Java

O simulador em Java está localizado na pasta `srcJava`. Este simulador foi construído como parte de um estudo e aprendizado da linguagem Java. Embora possa não ser tão sofisticado quanto o simulador em Python, ele demonstra os conceitos básicos de programação em Java para cálculos de empréstimos.

### Pré-requisitos

Antes de executar o simulador em Java, verifique se você possui os seguintes requisitos instalados em seu ambiente:

- Java Development Kit (JDK): O JDK é necessário para compilar e executar o simulador em Java. Verifique se você tem o JDK instalado executando o comando `java -version` no terminal. Neste projeto, foi utilizada a versão jdk1.8.
- Maven: O Maven é uma ferramenta de automação de compilação e gerenciamento de dependências para projetos Java. Verifique se você tem o Maven instalado executando o comando `mvn --version` no terminal.


### Executando o Simulador em Java

1. Navegue até a pasta `srcJava/simulador` do projeto.
2. Compile o código-fonte Java executando o comando `mvn clean package`.
3. Execute o simulador usando o comando `java -jar target/simulador-0.0.1-SNAPSHOT.jar`.
4. A API estará disponivel na porta 8080 
5. O simulador calculará as parcelas com base nas informações fornecidas e retornará o resultado em json.

## Infraestrutura em Containers Docker

Este projeto também inclui a infraestrutura necessária para executar os simuladores em containers Docker. Os arquivos Dockerfile estão disponíveis na raiz do projeto, permitindo a criação das imagens Docker para cada simulador.

O uso de containers Docker traz uma série de benefícios para o desenvolvimento e implantação de aplicações:

- **Isolamento e portabilidade**: Os containers Docker encapsulam todos os componentes necessários para a execução de uma aplicação, incluindo suas dependências e configurações. Isso permite que a aplicação seja executada de forma isolada, garantindo consistência e facilitando a implantação em diferentes ambientes.

- **Escalabilidade e elasticidade**: Com o uso de containers Docker, é possível escalar verticalmente (adicionar mais recursos a um único container) ou horizontalmente (adicionar mais containers) de forma simples e rápida. Isso permite lidar com picos de tráfego e ajustar a capacidade conforme necessário, garantindo a disponibilidade da aplicação.

- **Gerenciamento simplificado**: O Docker fornece ferramentas para gerenciar e orquestrar containers em larga escala, como o Docker Compose e o Kubernetes. Isso facilita a implementação de arquiteturas baseadas em microsserviços, onde cada componente da aplicação pode ser executado em um container separado, permitindo maior flexibilidade, escalabilidade e manutenção.

- **Reprodutibilidade e controle de versão**: Com os arquivos Dockerfile, é possível definir de forma precisa as dependências e configurações da aplicação, garantindo que ela seja executada da mesma maneira em qualquer ambiente. Isso facilita o controle de versão e a reprodução consistente do ambiente de desenvolvimento para produção.

Ao utilizar containers Docker, é possível obter uma abordagem mais ágil e eficiente no desenvolvimento, implantação e manutenção de aplicações. A combinação do Docker com práticas DevOps permite uma maior integração entre desenvolvimento e operações, acelerando o ciclo de entrega de software e facilitando a colaboração entre equipes.

Neste projeto, o uso de containers Docker oferece a flexibilidade e a facilidade de implantação dos simuladores em Python e Java, juntamente com sua infraestrutura, proporcionando uma experiência de implantação de APIs simplificada e escalável.

Cabe ressaltar que a utilização de containers Docker é apenas uma das diversas abordagens para a execução de aplicações, e cada projeto deve avaliar a melhor solução de acordo com suas necessidades e requisitos específicos.

Os arquivos Dockerfile estão disponíveis na raiz do projeto 

- `container-python.dockerfile`
- `container-java.dockerfile`

Os Dockerfiles fornecem as instruções para criar as imagens Docker de cada simulador.


### Pré-requisitos

Antes de executar os simuladores em containers Docker, verifique se você possui os seguintes requisitos instalados em seu ambiente:

- Docker: O Docker é necessário para criar e executar os containers Docker. Verifique se você tem o Docker instalado executando o comando `docker --version` no terminal.
- Docker Compose: O Docker Compose é uma ferramenta complementar ao Docker que permite definir e executar aplicativos compostos por vários containers Docker. Verifique se você tem o Docker Compose instalado executando o comando `docker-compose --version` no terminal.


### Executando os Simuladores em Containers Docker

1. Certifique-se de que o Docker esteja em execução.
2. Navegue até a raiz do projeto.
3. Execute o comando `docker-compose up` para iniciar os simuladores e seus respectivos serviços.
4. O Simulador escrito em Python estará disponível na porta 8001
5. O Simulador escrito em Java estará disponivel na porta 8002

## Instuções de utilização das APIS

Deve ser realizada uma chamada via Post para o endpoint http://localhost:8001/simulacao ou http://localhost:8002/simulacao contendo no body as informações

```json
{"valorDesejado": 500, "prazo": 20}
```

O simulador irá calcular as parcelas com base nas informações fornecidas e retornará o resultado em formato JSON, mostrando o valor das parcelas para cada tipo de simulação (SAC e PRICE). O resultado será semelhante ao seguinte:

```json
{
    "codigoProduto": 1,
    "descricaoProduto": "Produto 1",
    "resultadoSimulacao": [
        {
            "parcelas": [
                {
                    "numero": 1,
                    "valorAmortizacao": "25.00",
                    "valorJuros": "8.95",
                    "valorPrestacao": "33.95"
                },
                ...
                {
                    "numero": 20,
                    "valorAmortizacao": "25.00",
                    "valorJuros": "0.45",
                    "valorPrestacao": "25.45"
                }
            ],
            "tipo": "SAC"
        },
        {
            "parcelas": [
                {
                    "numero": 1,
                    "valorAmortizacao": "21.01",
                    "valorJuros": "8.95",
                    "valorPrestacao": "29.96"
                },
                ...
                {
                    "numero": 20,
                    "valorAmortizacao": "29.43",
                    "valorJuros": "0.53",
                    "valorPrestacao": "29.96"
                }
            ],
            "tipo": "PRICE"
        }
    ],
    "taxaJuros": "0.017900000"
}
```

## Notas Sobre a Segurança das Aplicações

Gostaríamos de ressaltar a importância da segurança das nossas aplicações. No momento, as chaves e senhas de acesso ao banco de dados estão sendo armazenadas no código-fonte por uma questão de simplificação. No entanto, reconhecemos que essa abordagem não é a mais adequada em termos de segurança.

Para garantir a proteção adequada das informações sensíveis, é altamente recomendável seguir as práticas de segurança recomendadas durante o deployment em ambiente de produção. Abaixo estão alguns requisitos e sugestões que devem ser considerados:

1. Utilização de Cofres de Senhas (Secret Management): Recomendamos fortemente o uso de cofres de senhas para armazenar as chaves e senhas de acesso. Cofres de senhas, como o Vault da HashiCorp ou o AWS Secrets Manager, permitem o armazenamento seguro e o gerenciamento centralizado de segredos sensíveis.

2. Utilização de Variáveis de Ambiente: Outra prática recomendada é o uso de variáveis de ambiente para fornecer informações sensíveis às aplicações durante o deployment. Dessa forma, as informações confidenciais não são expostas no código-fonte e podem ser configuradas separadamente em cada ambiente de execução.

3. Segurança de Rede: Garantir que as aplicações estejam protegidas por firewalls e que apenas as portas necessárias estejam abertas. A Correta configuração de regras de acesso restritas para minimizar as superfícies de ataque.

4. Auditoria e Monitoramento: É importante Implementar mecanismos de auditoria e monitoramento em para detectar e responder a atividades suspeitas. Com o registro de logs detalhados e  alertas para detectar tentativas de acesso não autorizado ou comportamento malicioso.

5. Atualizações: Manter nosso ambiente atualizado com as últimas correções de segurança e aatualizaçao regular para mitigar riscos.


Lembramos que a segurança das aplicações é uma responsabilidade compartilhada entre a equipe de desenvolvimento, operações e segurança da informação. Estamos comprometidos em aprimorar constantemente nossas práticas de segurança e buscar soluções mais robustas para proteger as informações sensíveis dos nossos sistemas.