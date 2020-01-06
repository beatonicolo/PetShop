package beato.shop.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import beato.calc.util.Product;

public class Carrello implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nomeUtente;
	private Timestamp stamp;
	private List<Product> listaCarrello;
	
	public Carrello() {}
	
	public Carrello(int id,String nomeUtente,Timestamp stamp) {
		this.id=id;
		this.nomeUtente=nomeUtente;
		this.stamp=stamp;
	}
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	
	public String getNomeUtente() {return nomeUtente;}
	public void setNomeUtente(String nomeUtente) {this.nomeUtente = nomeUtente;}
	
	public Timestamp getStamp() {return stamp;}
	public void setStamp(Timestamp stamp) {this.stamp = stamp;}

	public List<Product> getListaCarrello() {return listaCarrello;}
	public void setListaCarrello(List<Product> listaCarrello) {this.listaCarrello = listaCarrello;}
	
	

}
