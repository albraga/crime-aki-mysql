
package jbridge;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.http.client.fluent.Request;
import org.json.JSONException;
import org.json.JSONObject;

class CakiThread implements Runnable {

	CakiThread (final String proxy){ 
		Thread tCaki = new Thread() {
			public void run() {	
				Connection con = null;
				Gson gson = new Gson();
				String URL = "jdbc:mysql://localhost:3306/crimeakidb?useTimezone=true&serverTimezone=UTC&user=userql&password=userqlpw";
	  		        try {
						Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
						con = DriverManager.getConnection(URL);
						if (con!= null){
							System.out.println("Conectado ao banco de dados local...");
							PreparedStatement pstc = con.prepareStatement("CREATE TABLE IF NOT EXISTS crimeakidb.ocorrencias ( id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, data DATE, lat VARCHAR(55), lon VARCHAR(55), rua VARCHAR(55), bairro VARCHAR(35), cidade VARCHAR(35), estado VARCHAR(35), cep VARCHAR(25), infracao VARCHAR(255), data_infracao DATE, hora_infracao VARCHAR(55), autor VARCHAR(55), autor_sexo VARCHAR(25), autor_idade VARCHAR(3), autor_cor VARCHAR(25), outros_autores VARCHAR(255), quantidade_autores INT, vitima VARCHAR(55), vitima_sexo VARCHAR(55), vitima_idade VARCHAR(3), vitima_cor VARCHAR(25), outras_vitimas VARCHAR(255), quantidade_vitimas INT, historico VARCHAR(255), autoridade VARCHAR(55), autoridade_identificacao VARCHAR(55), outras_autoridades VARCHAR(255) );");
							pstc.execute();
							String result;
							if(proxy.length() > 1) {
								result = Request.Get("https://parseapp-e8b30.firebaseio.com/ocorrencias.json").viaProxy(proxy.toString()).execute().returnContent().toString();
							} else {
								result = Request.Get("https://parseapp-e8b30.firebaseio.com/ocorrencias.json").execute().returnContent().toString();
							}
							if(result.length() > 9) {
							JSONObject objson = new JSONObject(result);
							for(int x =0; x < objson.names().length(); x++) {
								Ocorrencia ocorrencia = gson.fromJson(objson.get(objson.names().getString(x)).toString(), Ocorrencia.class);
								String sql = "INSERT INTO crimeakidb.ocorrencias(data, lat, lon, rua, bairro, cidade, estado, cep, infracao, data_infracao, hora_infracao, autor, autor_sexo, autor_idade, autor_cor, outros_autores, quantidade_autores, vitima, vitima_sexo, vitima_idade, vitima_cor, outras_vitimas, quantidade_vitimas, historico, autoridade, autoridade_identificacao, outras_autoridades) VALUES(STR_TO_DATE(\'" + ocorrencia.getData() + "\','%d/%m/%Y'), ?, ?, ?, ?, ?, ?, ?, ?, STR_TO_DATE(\'" + ocorrencia.getData_infracao() + "\','%d/%m/%Y'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
								System.out.println(sql);
								PreparedStatement pst = con.prepareStatement(sql);
								pst.setString(1, ocorrencia.getLat());
								pst.setString(2, ocorrencia.getLon());
								pst.setString(3, ocorrencia.getRua());
								pst.setString(4, ocorrencia.getBairro());
								pst.setString(5, ocorrencia.getCidade());
								pst.setString(6, ocorrencia.getEstado());
								pst.setString(7, ocorrencia.getCep());
								pst.setString(8, ocorrencia.getInfracao());
								pst.setString(9, ocorrencia.getHora_infracao());
								pst.setString(10, ocorrencia.getAutor());
								pst.setString(11, ocorrencia.getAutor_sexo());
								pst.setString(12, ocorrencia.getAutor_idade());
								pst.setString(13, ocorrencia.getAutor_cor());
								pst.setString(14, ocorrencia.getOutros_autores());
								pst.setInt(15, ocorrencia.getQuantidade_autores());
								pst.setString(16, ocorrencia.getVitima());
								pst.setString(17, ocorrencia.getVitima_sexo());
								pst.setString(18, ocorrencia.getVitima_idade());
								pst.setString(19, ocorrencia.getVitima_cor());
								pst.setString(20, ocorrencia.getOutras_vitimas());
								pst.setInt(21, ocorrencia.getQuantidade_vitimas());
								pst.setString(22, ocorrencia.getHistorico());
								pst.setString(23, ocorrencia.getAutoridade());
								pst.setString(24, ocorrencia.getAutoridade_identificacao());
								pst.setString(25, ocorrencia.getOutras_autoridades());
								pst.execute();
							 }
							if(proxy.length() > 1) {
							 Request.Delete("https://parseapp-e8b30.firebaseio.com/ocorrencias.json").viaProxy(proxy.toString()).execute();
							} else {
								Request.Delete("https://parseapp-e8b30.firebaseio.com/ocorrencias.json").execute();
							}

						}else{
							System.out.println("Não foram encontrados novos registros na nuvem");
						 }
						} else {
							System.out.println("Não foi possível conectar-se ao banco de dados local");
						}
					  }  catch (JsonSyntaxException | IOException | ClassNotFoundException | SQLException | JSONException | InstantiationException | IllegalAccessException  e) {
         System.out.println("erro: " + e);
      }
			}
			
		};
			tCaki.start();
	}

	@Override
	public void run() {
		
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}


public class Jbridge {
    
    public static void main(String[] args) {
		
		new CakiThread(args[0]);

   }
	
}
