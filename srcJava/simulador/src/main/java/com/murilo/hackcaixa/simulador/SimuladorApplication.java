package com.murilo.hackcaixa.simulador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@SpringBootApplication
public class SimuladorApplication {

	@GetMapping("/")
	private String ola() throws JsonProcessingException{
		String url = "jdbc:sqlserver://dbhackathon.database.windows.net:1433;databaseName=hack";
        String username = "hack";
        String password = "Password23";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
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
			
			return json;

        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "Olá, mundo!";
	}

	public static void main(String[] args) {
		SpringApplication.run(SimuladorApplication.class, args);
	}

}
