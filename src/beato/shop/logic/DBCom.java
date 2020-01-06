package beato.shop.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import beato.calc.util.Product;
import beato.shop.bean.Carrello;
import beato.shop.bean.Classe;




public class DBCom {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/petshop?useLegacyDatetimeCode=false&serverTimezone=CET";
	//  Database credentials
	static final String USER = "username";
	static final String PASS = "password";
	//queries
	static final String queryUnica = "SELECT p.id as id, p.nomes as nomes, p.prezzou as prezzou, c.nomec as nomec FROM  categoria c JOIN prodotto p ON c.id=p.idc ORDER BY id";
	static final String querySalvaCart= "INSERT INTO carrello (nomeUtente,stamp) VALUES (?,?)";
	static final String querySalvaPCart="INSERT INTO contiene (prodotto,carrello,quantita) VALUES (?,?,?)";
	//static final String queryGetLastCartId="SELECT LAST_INSERT_ID() FROM carrello;";
	static final String queryGetLastCartId="SELECT MAX(id) FROM carrello;";
	static final String queryGetCart= "SELECT * FROM carrello WHERE nomeUtente=? ORDER BY stamp DESC LIMIT 1";
	static final String queryGetProductCart= "SELECT * FROM contiene WHERE carrello = ?";
	static final String queryGetLastCartIdByUser="SELECT * FROM carrello WHERE nomeUtente=? ORDER BY stamp DESC LIMIT 1";
	static final String queryCheckCredenziali="SELECT * FROM utente WHERE username=? AND password=?";
	static final String queryCheckFreeUser="SELECT * FROM utente WHERE username=?";
	static final String querySalvaUser="INSERT INTO utente (username,password) VALUE (?,?)";
	static final String queryGetClassi="SELECT * FROM categoria ORDER BY id";
	static final String queryGetSpecie="SELECT * FROM prodotto ORDER BY id";
	static final String queryInsertClasse="INSERT INTO categoria (nomec) value (LOWER(?))";
	static final String queryModClasse="UPDATE categoria set nomec=? where id=?";
	static final String queryInsertSpecie="INSERT INTO prodotto (nomes,prezzou,idc) values (LOWER(?),?,?)";
	static final String queryModSpecie="UPDATE prodotto set nomes=?, prezzou=?, idc=? where id=?";
	static final String queryGetClasseId="SELECT id from categoria where nomec=?";

	public DBCom() {}

	/**
	 * recupera da DB tutti i prodotti presenti
	 * @return
	 */
	public List<List<Product>> getCatalogo(){
		Connection conn = null;
		List<List<Product>> listaCatalogo= new ArrayList<List<Product>>();
		List<Product> qres=new ArrayList<Product>();
		HashSet<String> categorie = new HashSet<String>();

		Statement stmt = null;
		ResultSet rs=null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(queryUnica);

			//recupero dati dal db e creo hashset + lista di tutti i prodotti
			while ( rs.next() ) {
				Product p = new Product();
				p.setId(rs.getInt("id"));
				String nomeSpecie=rs.getString("nomes");
				p.setNomeS(nomeSpecie.substring(0, 1).toUpperCase()+nomeSpecie.substring(1).toLowerCase());
				p.setPrezzoU(rs.getDouble("prezzou"));
				p.setNomeC(rs.getString("nomec").toUpperCase());
				p.setImmagine("resources/images/"+nomeSpecie+".jpg");
				qres.add(p);
				categorie.add(p.getNomeC());		
			}

			Iterator <String> it = categorie.iterator();

			while(it.hasNext()) {
				List<Product> listaSpecie = new ArrayList<Product>();
				String categoria = it.next();

				for (int i=0; i<qres.size();i++) {
					if(qres.get(i).getNomeC().equalsIgnoreCase(categoria))
						listaSpecie.add(qres.get(i));
				}
				listaCatalogo.add(listaSpecie);
			}
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(stmt!=null)
					stmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return listaCatalogo;		   
	}

	/**
	 * Metodo che dato un username ritorna il suo carrello più recente
	 * @param user
	 * @return
	 */
	public Carrello getCarrello(String user) {
		Connection conn = null;
		PreparedStatement prpstmt = null;
		ResultSet rs=null;

		Carrello cStorico= new Carrello();

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryGetCart);
			prpstmt.setString(1, user);
			rs=prpstmt.executeQuery();

			Carrello c=new Carrello();
			if(rs.next()) {
				c.setId(rs.getInt("id"));
				c.setNomeUtente(rs.getString("nomeUtente"));
				c.setStamp(rs.getTimestamp("stamp"));

				java.util.Date date=new java.util.Date();
				Timestamp timestamp = new Timestamp( date.getTime());
				if ((timestamp.getTime()-c.getStamp().getTime()) < 60*60*1000)
					cStorico=c;
			}

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return cStorico;		   
	}

	/**
	 * metodo che dato un id di un carrello ne ritorna la lista dei prodotti presenti
	 * @param id
	 * @return
	 */
	public List<Product> getListaCarrello(int id){
		Connection conn = null;
		PreparedStatement prpstmt = null;
		ResultSet rs=null;

		List<Product> listaCarrello=new ArrayList<Product>();

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryGetProductCart);
			prpstmt.setInt(1, id);
			rs=prpstmt.executeQuery();

			while(rs.next()) {
				Product p=new Product();
				p.setId(rs.getInt("prodotto"));
				p.setQuantita(rs.getInt("quantita"));
				listaCarrello.add(p);
			}
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return listaCarrello;		   
	}

	/**
	 * metodo che si occupa di salvare a db il carrello(senza lista)
	 * @param user
	 */
	public void scriviCarrello(String user) {
		Connection conn = null;
		PreparedStatement prpstmt = null;

		java.util.Date date=new java.util.Date();
		Timestamp timestamp = new Timestamp( date.getTime());

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(querySalvaCart);
			prpstmt.setString(1, user);
			prpstmt.setTimestamp(2, timestamp);
			prpstmt.executeUpdate();

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}	   
	}

	/**
	 * Metodo che dato un carello e una listaCarrello salva a db (nella tabella contiene) il prodotti
	 * con relativa quantità
	 * @param idLastCarrello
	 * @param listaCarrello
	 */
	public void scriviProdottiContenuti(int idLastCarrello,List<Product>listaCarrello) {
		PreparedStatement prpstmt = null;
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);

			for(int i=0;i<listaCarrello.size();i++) {			
				prpstmt=conn.prepareStatement(querySalvaPCart);
				prpstmt.setInt(1, listaCarrello.get(i).getId());
				prpstmt.setInt(2, idLastCarrello);
				prpstmt.setInt(3, listaCarrello.get(i).getQuantita());
				prpstmt.executeUpdate();
				prpstmt.clearParameters();
			}
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}	   
	}

	/**
	 * Metodo che riturna l'id dell'ultimo carrello di un utente
	 * @param user username dell'utente
	 * @return id
	 */
	public int getLastCartUser(String user) {
		Connection conn = null;
		PreparedStatement prpstmt = null;
		ResultSet rs=null;

		int lastId=1;;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryGetLastCartIdByUser);
			prpstmt.setString(1, user);
			rs=prpstmt.executeQuery();

			if(rs.next()) 
				lastId=(int)rs.getLong(1);
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return lastId;		   
	}

	/**
	 * Metodo che verifica la correttezza delle credenziali
	 * @param username
	 * @param password
	 * @return true se sono corrette, false altrimenti
	 */
	public boolean checkCredenziali(String username, String password) {
		PreparedStatement prpstmt = null;
		Connection conn = null;
		ResultSet rs=null;
		boolean esito=false;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryCheckCredenziali);
			prpstmt.setString(1, username);
			prpstmt.setString(2, password);
			rs=prpstmt.executeQuery();

			if(rs.next()) 
				esito= true;
			else
				esito=  false;
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che verifica la disponibilità di un username
	 * @param nUsername
	 * @return
	 */
	public boolean checkFreeUser(String nUsername) {
		PreparedStatement prpstmt = null;
		ResultSet rs=null;
		Connection conn = null;
		boolean esito=false;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryCheckFreeUser);
			prpstmt.setString(1, nUsername);
			rs=prpstmt.executeQuery();

			if(rs.next()) 
				esito= false;
			else
				esito=  true;
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return esito;
	}

	/**
	 * Metodo che si occupa dell'inserimento a db di un nuovo utente
	 * @param username
	 * @param password
	 */
	public void saveUser(String username,String password) {
		PreparedStatement prpstmt = null;
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(querySalvaUser);
			prpstmt.setString(1, username);
			prpstmt.setString(2, password);
			prpstmt.executeUpdate();

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}	 
	}

	/**
	 * Metodo che recuper ala lista delle classi
	 * @return
	 */
	public List<Classe> getListaClassi(){
		List<Classe> qres=new ArrayList<Classe>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(queryGetClassi);

			//recupero dati dal db e creo hashset + lista di tutti i prodotti
			while ( rs.next() ) {
				Classe c=new Classe();
				c.setId(rs.getInt("id"));
				c.setNomeC(rs.getString("nomec"));
				qres.add(c);
			}
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(stmt!=null)
					stmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return qres;		   
	}

	/**
	 * Metodo che ritorna la lista di tutte le spercie
	 * @return
	 */
	public List<Product> getListaSpecie(){
		List<Product> qres=new ArrayList<Product>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(queryUnica);

			//recupero dati dal db e creo hashset + lista di tutti i prodotti
			while ( rs.next() ) {
				Product p = new Product();
				p.setId(rs.getInt("id"));
				String nomeSpecie=rs.getString("nomes");
				p.setNomeS(nomeSpecie.substring(0, 1).toUpperCase()+nomeSpecie.substring(1).toLowerCase());
				p.setPrezzoU(rs.getDouble("prezzou"));
				p.setNomeC(rs.getString("nomec").toUpperCase());
				p.setImmagine("resources/images/"+nomeSpecie+".jpg");
				qres.add(p);
			}
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(stmt!=null)
					stmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return qres;		   
	}

	/**
	 * Metodo che si occupa dell'inserimento di una classe
	 * @param nomeClasse
	 * @return
	 */
	public boolean insertClasse(String nomeClasse) {
		Connection conn = null;
		PreparedStatement prpstmt = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryInsertClasse);
			prpstmt.setString(1, nomeClasse);
			prpstmt.executeUpdate();

		}
		catch (SQLIntegrityConstraintViolationException cv) {return false;}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}

		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return true;
	}

	/**
	 * Metodo che si occupa di eseguire l'update di una classe
	 * @param idClasse
	 * @param nomeClasse
	 * @return
	 */
	public boolean modClasse(int idClasse, String nomeClasse) {
		Connection conn = null;
		PreparedStatement prpstmt = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryModClasse);
			prpstmt.setString(1, nomeClasse);
			prpstmt.setInt(2, idClasse);
			prpstmt.executeUpdate();

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (SQLIntegrityConstraintViolationException cv) {return false;}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return true;
	}

	/**
	 * Metodo che dato il nome di una classe ne ritorna l'id
	 * @param nomeC
	 * @return
	 */
	public int getClasseId(String nomeC) {
		Connection conn = null;
		PreparedStatement prpstmt = null;
		ResultSet rs=null;
		int idClasse= 0;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryGetClasseId);
			prpstmt.setString(1, nomeC);
			rs=prpstmt.executeQuery();

			if(rs.next()) 
				idClasse=(int)rs.getLong(1);
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return idClasse;		   
	}

	/**
	 * Metodo che si occupa dell'inserimento di una specie
	 * @param p
	 * @return
	 */
	public boolean insertSpecie(Product p) {
		Connection conn = null;
		PreparedStatement prpstmt = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryInsertSpecie);
			prpstmt.setString(1, p.getNomeS());
			prpstmt.setDouble(2, p.getPrezzoU());
			int idClasse=getClasseId(p.getNomeC());
			prpstmt.setInt(3,idClasse);
			prpstmt.executeUpdate();

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (SQLIntegrityConstraintViolationException cv) {return false;}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return true;
	}

	/**
	 * Metodo che si occupa dell'update di una specie
	 * @param p
	 * @return
	 */
	public boolean modSpecie(Product p) {
		Connection conn = null;
		PreparedStatement prpstmt = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmt=conn.prepareStatement(queryModSpecie);
			prpstmt.setString(1, p.getNomeS());
			prpstmt.setDouble(2, p.getPrezzoU());
			prpstmt.setInt(3, getClasseId(p.getNomeC()));
			prpstmt.setInt(4, p.getId());
			prpstmt.executeUpdate();

		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (SQLIntegrityConstraintViolationException cv) {return false;}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return true;
	}

	public String importaClassi (List<Classe> listaClassi) {
		Connection conn = null;
		PreparedStatement prpstmtIns = null;
		PreparedStatement prpstmtMod = null;

		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			prpstmtIns=conn.prepareStatement(queryInsertClasse);
			prpstmtMod=conn.prepareStatement(queryModClasse);
			
			conn.setAutoCommit(false);
			for (Classe c:listaClassi) {
				int res;
				if (c.getId()>0) {
					prpstmtMod.setString(1, c.getNomeC());
					prpstmtMod.setInt(1, c.getId());
					res=prpstmtMod.executeUpdate();
				}
				else {
					prpstmtIns.setString(1, c.getNomeC());
					res=prpstmtIns.executeUpdate();
				}

			}

		}
		catch (SQLIntegrityConstraintViolationException cv) {return false;}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}

		finally {
			try{
				if(prpstmt!=null)
					prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
				se.printStackTrace();}
		}
		return true;
	}
}
