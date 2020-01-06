package beato.shop.logic;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beato.calc.util.Product;

public class ClientRest {
	
	//URL calcolatriceRest
	private static final String REST_SERVICE_URL = "http://localhost:8080/CalcolatriceRest/rest/CalcolatriceService/calcolaTotale";
	private Client client = ClientBuilder.newClient();
	
	private Gson gson;
	
	public ClientRest() {
		gson = new GsonBuilder().create();
	}

	/**
	 * meodo che restituisce il totale chiamando il servizio rest deputato al calcolo
	 * @param listaCarrello
	 * @return
	 */
	public double getTotale(List<Product> listaCarrello) {

		String carrellojson= gson.toJson(listaCarrello);
		String risultato=client.target(REST_SERVICE_URL).request(MediaType.APPLICATION_JSON).post(Entity.entity(carrellojson, MediaType.APPLICATION_JSON),String.class);
		double totale= gson.fromJson(risultato, double.class);
		return totale;
	}
}
