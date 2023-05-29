package com.murilo.hackcaixa.simulador;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@SpringBootApplication
public class SimuladorApplication {
	private static Connection connection;
	
	private static void setJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
    }

	static {
        try {
            // Configuração da conexão com o banco de dados
            String jdbcUrl = "jdbc:sqlserver://dbhackathon.database.windows.net:1433;databaseName=hack";
            String username = "hack";
            String password = "Password23";

            // Criação da conexão
            connection = DriverManager.getConnection(jdbcUrl, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	/* Classe para representar uma parcela
	 *	- numero: numero da parcela	
	 *	- valorAmortizacao: valor da amortizacao da parcela
	 *	- valorJuros: valor dos juros da parcela
	 *	- valorPrestacao: valor da prestacao da parcela
	*/
    public static class Parcela {
        private int numero;
        private BigDecimal valorAmortizacao;
        private BigDecimal valorJuros;
        private BigDecimal valorPrestacao;

        public Parcela(int numero, BigDecimal valorAmortizacao, BigDecimal valorJuros, BigDecimal valorPrestacao) {
            this.numero = numero;
            this.valorAmortizacao = valorAmortizacao;
            this.valorJuros = valorJuros;
            this.valorPrestacao = valorPrestacao;
        }

        public int getNumero() {
            return numero;
        }

        public BigDecimal getValorAmortizacao() {
            return valorAmortizacao;
        }

        public BigDecimal getValorJuros() {
            return valorJuros;
        }

        public BigDecimal getValorPrestacao() {
            return valorPrestacao;
        }
    }

	/* 
	Classe para representar uma produto
	*/
	private static class Produto {
        private int coProduto;
        private String noProduto;
        private BigDecimal pcTaxaJuros;
        private int nuMinimoMeses;
        private int nuMaximoMeses;
        private double vrMinimo;
        private double vrMaximo;

        // Getters e Setters

        public int getCoProduto() {
            return coProduto;
        }

        public void setCoProduto(int coProduto) {
            this.coProduto = coProduto;
        }

        public String getNoProduto() {
            return noProduto;
        }

        public void setNoProduto(String noProduto) {
            this.noProduto = noProduto;
        }

        public BigDecimal getPcTaxaJuros() {
            return pcTaxaJuros;
        }

        public void setPcTaxaJuros(BigDecimal pcTaxaJuros) {
            this.pcTaxaJuros = pcTaxaJuros;
        }

        public int getNuMinimoMeses() {
            return nuMinimoMeses;
        }

        public void setNuMinimoMeses(int nuMinimoMeses) {
            this.nuMinimoMeses = nuMinimoMeses;
        }

        public int getNuMaximoMeses() {
            return nuMaximoMeses;
        }

        public void setNuMaximoMeses(int nuMaximoMeses) {
            this.nuMaximoMeses = nuMaximoMeses;
        }

        public double getVrMinimo() {
            return vrMinimo;
        }

        public void setVrMinimo(double vrMinimo) {
            this.vrMinimo = vrMinimo;
        }

        public double getVrMaximo() {
            return vrMaximo;
        }

        public void setVrMaximo(double vrMaximo) {
            this.vrMaximo = vrMaximo;
        }
    }

	/**
	 * Calcula a simulação de amortização pelo sistema de amortização PRICE.
	 * 
	 * @param valorDesejado O valor desejado do empréstimo.
	 * @param prazo O prazo em meses para pagamento.
	 * @param taxaJuros A taxa de juros mensal.
	 * @return Uma lista de objetos Parcela contendo as informações das parcelas.
	 */
	public static List<Parcela> calcularAmortizacaoPRICE(BigDecimal valorDesejado, int prazo, BigDecimal taxaJuros) {
        List<Parcela> parcelas = new ArrayList<>();

        BigDecimal taxaJurosMensal = taxaJuros;
        BigDecimal coeficiente = taxaJurosMensal.multiply(BigDecimal.ONE.add(taxaJurosMensal).pow(prazo))
                .divide(BigDecimal.ONE.add(taxaJurosMensal).pow(prazo).subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);
        BigDecimal prestacao = valorDesejado.multiply(coeficiente);

        BigDecimal saldoDevedor = valorDesejado;

        for (int mes = 1; mes <= prazo; mes++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal);
            BigDecimal amortizacao = prestacao.subtract(juros);
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            prestacao = prestacao.setScale(2, RoundingMode.HALF_UP);
            amortizacao = amortizacao.setScale(2, RoundingMode.HALF_UP);
            juros = juros.setScale(2, RoundingMode.HALF_UP);
            saldoDevedor = saldoDevedor.setScale(2, RoundingMode.HALF_UP);

            Parcela parcela = new Parcela(mes, amortizacao, juros, prestacao);
            parcelas.add(parcela);
        }

        return parcelas;
    }

	/**
	 * Calcula a simulação de amortização pelo sistema de amortização SAC.
	 * @param valorDesejado O valor desejado do empréstimo.
	 * @param prazo O prazo em meses para pagamento.
	 * @param taxaJuros A taxa de juros mensal.
	 * @return Uma lista de objetos Parcela contendo as informações das parcelas.
	 * @throws IllegalArgumentException Caso o prazo seja menor ou igual a zero. 
	 */
    public static List<Parcela> calcularAmortizacaoSAC(BigDecimal valorDesejado, int prazo, BigDecimal taxaJuros) {
        List<Parcela> prestacoes = new ArrayList<>();

        BigDecimal amortizacao = valorDesejado.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);
        BigDecimal saldoDevedor = valorDesejado;

        for (int mes = 1; mes <= prazo; mes++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJuros);
            BigDecimal prestacao = amortizacao.add(juros);
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            prestacao = prestacao.setScale(2, RoundingMode.HALF_UP);
            amortizacao = amortizacao.setScale(2, RoundingMode.HALF_UP);
            juros = juros.setScale(2, RoundingMode.HALF_UP);

            Parcela parcela = new Parcela(mes, amortizacao, juros, prestacao);
            prestacoes.add(parcela);
        }

        return prestacoes;
    }

	@GetMapping("/")
	private String ola() throws JsonProcessingException{
		
        try {
            String sql = 
				"SELECT\n" +
					"CO_PRODUTO,\n" +
					"NO_PRODUTO,\n" +
					"PC_TAXA_JUROS,\n" +
					"NU_MINIMO_MESES,\n" +
					"NU_MAXIMO_MESES,\n" +
					"VR_MINIMO,\n" +
					"VR_MAXIMO\n" +
				"FROM\n" +
					"hack.dbo.PRODUTO;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

			List<Map<String, Object>> result = new ArrayList<>();
			
			while (resultSet.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("CO_PRODUTO", resultSet.getInt("CO_PRODUTO"));
				row.put("NO_PRODUTO", resultSet.getString("NO_PRODUTO"));
				row.put("PC_TAXA_JUROS", resultSet.getDouble("PC_TAXA_JUROS"));
				row.put("NU_MINIMO_MESES", resultSet.getInt("NU_MINIMO_MESES"));
				row.put("NU_MAXIMO_MESES", resultSet.getInt("NU_MAXIMO_MESES"));
				row.put("VR_MINIMO", resultSet.getDouble("VR_MINIMO"));
				row.put("VR_MAXIMO", resultSet.getDouble("VR_MAXIMO"));
				result.add(row);
			}

			// Serializar para JSON
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(result);

			// Fechamento dos recursos
			resultSet.close();
			statement.close();
			
			return json;

        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "Olá, mundo!";
	}

    // Classe para representar o JSON de entrada
    public static class SimulacaoInput {
        @JsonProperty("valorDesejado")
        private BigDecimal valorDesejado;
        @JsonProperty("prazo")
        private int prazo;

        public BigDecimal getValorDesejado() {
            return valorDesejado;
        }

        public void setValorDesejado(BigDecimal valorDesejado) {
            this.valorDesejado = valorDesejado;
        }

        public int getPrazo() {
            return prazo;
        }

        public void setPrazo(int prazo) {
            this.prazo = prazo;
        }
    }


    public static Produto getProdutoByPrazoAndValorDesejado(int prazo, BigDecimal valorDesejado) {
        try {
            String query = "SELECT CO_PRODUTO, NO_PRODUTO, PC_TAXA_JUROS, NU_MINIMO_MESES, NU_MAXIMO_MESES, VR_MINIMO, VR_MAXIMO " +
                    "FROM hack.dbo.PRODUTO " +
                    "WHERE NU_MINIMO_MESES <= ? " +
                    "AND (NU_MAXIMO_MESES >= ? OR NU_MAXIMO_MESES IS NULL) " +
                    "AND VR_MINIMO <= ? " +
                    "AND (VR_MAXIMO >= ? OR VR_MAXIMO IS NULL)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, prazo);
                statement.setInt(2, prazo);
                statement.setBigDecimal(3, valorDesejado);
                statement.setBigDecimal(4, valorDesejado);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Produto produto = new Produto();
                        produto.setCoProduto(resultSet.getInt("CO_PRODUTO"));
                        produto.setNoProduto(resultSet.getString("NO_PRODUTO"));
                        produto.setPcTaxaJuros(resultSet.getBigDecimal("PC_TAXA_JUROS"));
                        produto.setNuMinimoMeses(resultSet.getInt("NU_MINIMO_MESES"));
                        produto.setNuMaximoMeses(resultSet.getInt("NU_MAXIMO_MESES"));
                        produto.setVrMinimo(resultSet.getDouble("VR_MINIMO"));
                        produto.setVrMaximo(resultSet.getDouble("VR_MAXIMO"));
                        return produto;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

	public Map<String, Object> geraSugestao(BigDecimal valorDesejado, int prazo) {
        List<String> sugestoes = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT min(VR_MINIMO) FROM hack.dbo.PRODUTO");
			resultSet.next(); // Mover o cursor para a primeira linha
            BigDecimal valorMinimo = resultSet.getBigDecimal(1);

            if (valorDesejado.compareTo(valorMinimo) < 0) {
                sugestoes.add("O valor mínimo de empréstimo é de R$ " + valorMinimo);
            }

            String sqlProdutoPorValor = String.format("SELECT CO_PRODUTO, NO_PRODUTO, PC_TAXA_JUROS, " +
                    "NU_MINIMO_MESES, NU_MAXIMO_MESES, VR_MINIMO, VR_MAXIMO FROM hack.dbo.PRODUTO " +
                    "WHERE VR_MINIMO <= %s AND (VR_MAXIMO >= %s OR VR_MAXIMO IS NULL)",
                    valorDesejado, valorDesejado);

            resultSet = statement.executeQuery(sqlProdutoPorValor);
            while (resultSet.next()) {
				BigDecimal taxaJuros = resultSet.getBigDecimal("PC_TAXA_JUROS").multiply(BigDecimal.valueOf(100));
                taxaJuros = taxaJuros.setScale(2, RoundingMode.HALF_UP);

                String mensagem = String.format("Para o valor de R$ %s você pode contratar %s com taxa de juros " +
                        "de %s%% ao mês e prazo de %s a %s meses.",
                        valorDesejado, resultSet.getString("NO_PRODUTO"), taxaJuros,
                        resultSet.getInt("NU_MINIMO_MESES"), resultSet.getInt("NU_MAXIMO_MESES"));
                sugestoes.add(mensagem);
            }

            if (sugestoes.isEmpty()) {
                Map<String, Object> envelopeRetorno = new HashMap<>();
                envelopeRetorno.put("error", "Nenhum produto encontrado para os valores e prazos informados.");
                envelopeRetorno.put("sugestoes", sugestoes);
                return envelopeRetorno;
            } else {
				Map<String, Object> envelopeRetorno = new HashMap<>();
				envelopeRetorno.put("sugestoes", sugestoes);
				return envelopeRetorno;
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostMapping(value = "/simulacao", produces="application/json")
    private String simulacao(@RequestBody SimulacaoInput simulacaoInput) {
        BigDecimal valorDesejado = simulacaoInput.getValorDesejado();
        int prazo = simulacaoInput.getPrazo();
		

		Map<String, Object> envelopeRetorno = new HashMap<>();

		// Verificar se todos os campos foram informados e retorna um json com o erro
		if (valorDesejado == null || prazo <= 0) {
			envelopeRetorno.put("Erro", "É necessário informar o prazo e o valor desejado");

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				String json = objectMapper.writeValueAsString(envelopeRetorno);
				return json;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			// Validar valorDesejado e prazo são numeros positivos
			if (valorDesejado.compareTo(BigDecimal.ZERO) <= 0 || prazo <= 0) {
				envelopeRetorno.put("Erro", "O valor desejado e o prazo devem ser números positivos");

				try {
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(envelopeRetorno);
					return json;
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}		

		Produto produto = getProdutoByPrazoAndValorDesejado(prazo, valorDesejado);

		// Validar se existe produto para o prazo e valor desejado
		if (produto == null) {

			Map<String, Object> SugestoesRetorno = geraSugestao(valorDesejado, prazo);

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				String json = objectMapper.writeValueAsString(SugestoesRetorno);
				return json;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			BigDecimal taxaJuros = produto.getPcTaxaJuros(); // Exemplo de taxa de juros fixa

			// Realizar cálculo de amortização PRICE
			List<Parcela> parcelasPRICE = calcularAmortizacaoPRICE(valorDesejado, prazo, taxaJuros);

			// Realizar cálculo de amortização SAC
			List<Parcela> parcelasSAC = calcularAmortizacaoSAC(valorDesejado, prazo, taxaJuros);

			// Construir resultado de retorno
			envelopeRetorno.put("codigoProduto",produto.getCoProduto()); // Código do produto (exemplo)
			envelopeRetorno.put("descricaoProduto", produto.getNoProduto()); // Descrição do produto (exemplo)
			envelopeRetorno.put("taxaJuros", taxaJuros); // Taxa de juros (exemplo)

			Map<String, Object> resultadoSAC = new HashMap<>();
			resultadoSAC.put("tipo", "SAC");
			resultadoSAC.put("parcelas", parcelasSAC);

			Map<String, Object> resultadoPRICE = new HashMap<>();
			resultadoPRICE.put("tipo", "PRICE");
			resultadoPRICE.put("parcelas", parcelasPRICE);

			List<Map<String, Object>> resultadoSimulacao = new ArrayList<>();
			resultadoSimulacao.add(resultadoSAC);
			resultadoSimulacao.add(resultadoPRICE);

			envelopeRetorno.put("resultadoSimulacao", resultadoSimulacao);

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				String json = objectMapper.writeValueAsString(envelopeRetorno);
				return json;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
        return "";
    }

	public static void main(String[] args) {
		SpringApplication.run(SimuladorApplication.class, args);
	}

}