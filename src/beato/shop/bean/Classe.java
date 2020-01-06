package beato.shop.bean;

import java.io.Serializable;

public class Classe implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nomeC;
	
	public Classe() {}

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}

	public String getNomeC() {return nomeC;}
	public void setNomeC(String nomeC) {this.nomeC = nomeC;}

	@Override
	public String toString() {return "Classe [id=" + id + ", nomeC=" + nomeC + "]";}
	
	
}
