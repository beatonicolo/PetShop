package beato.shop.logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beato.calc.util.Product;
import beato.shop.bean.Carrello;
import beato.shop.bean.Classe;

public class ShopServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String PAGE_CATALOGO="/catalogue.jsp";
	private static final String PAGE_CARRELLO="/cart.jsp";
	private static final String PAGE_REGISTRAZIONE="/register.jsp";
	private static final String PAGE_PANNELLO_ADMIN="/adminpanel.jsp";


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<List<Product>> catalogo = new ArrayList<List<Product>>();
		catalogo=new DBCom().getCatalogo();
		request.getSession().setMaxInactiveInterval(300);
		request.getSession().setAttribute("catalogo", catalogo); // Will be available as ${products} in JSP
		request.getRequestDispatcher("/catalogue.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//submit lanciata da login
		if(request.getParameter("btnLogin")!=null)
			loginAction(request,response);

		//pressione pulsante annulla
		else if (request.getParameter("btnAnnulla")!=null) 
			request.getRequestDispatcher(PAGE_PANNELLO_ADMIN).forward(request, response);

		//pressione inserimento classe
		else if (request.getParameter("btnInserisciClasse")!=null) {
			inserimentoClasseAction(request,response);
		}

		//pressione inserimento specie
		else if (request.getParameter("btnInserisciSpecie")!=null)
			inserimentoSpecieAction(request,response);

		//presione bottone pannello admin
		else if (request.getParameter("btnAdmin")!=null)
			pannelloAdminNavigation(request,response);

		//pressione pulsante indietro
		else if (request.getParameter("btnIndietro")!=null)
			request.getRequestDispatcher(PAGE_CATALOGO).forward(request, response);

		//procedura di logout
		else if(request.getParameter("btnLogout")!=null) {
			request.getSession().invalidate();
			doGet(request,response);
		}

		//presisone pulsante per inizare procedura registrazione
		else if (request.getParameter("btnRegister")!=null) {
			request.getSession().setAttribute("message", "");
			request.getRequestDispatcher(PAGE_REGISTRAZIONE).forward(request, response);
		}

		//pressione pulsante per eseguire registrazione
		else if (request.getParameter("btnConferma")!=null) 
			confermaRegistrazioneAction(request,response);

		//submit lanciata da bottone carrello
		else if(request.getParameter("btnCarrello")!=null) 
			carrelloNavigation(request,response);

		else if (request.getParameter("btnSel")!=null && (!request.getParameter("btnSel").equalsIgnoreCase("")))
			modQuantitaAction(request,response);
	}

	/**
	 * una volta premuto il pusante carrello se è stato eseguito il login viene generata la lista dei
	 * dei prodotti selezionati viene fatta la forward su cart.jsp
	 * 
	 * @param request
	 * @param response
	 * @param user
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generateCart(HttpServletRequest request, HttpServletResponse response, String user) throws ServletException, IOException { 

		List<Product> listaCarrello = new ArrayList<Product>();
		HttpSession currentSession=request.getSession();
		List<List<Product>> catalogo;
		try {catalogo=(List<List<Product>>)currentSession.getAttribute("catalogo");}
		catch (ClassCastException e) {catalogo=new DBCom().getCatalogo();}
		double totale=0.0;

		if(catalogo!=null) {
			for (int i=0;i<catalogo.size();i++) {
				for (int n=0; n<catalogo.get(i).size();n++) {
					Product p=catalogo.get(i).get(n);
					if(p.getQuantita()>0) {
						listaCarrello.add(p);
					}	
				}
			}
			salvaCarrello(user,listaCarrello);
			totale = new ClientRest().getTotale(listaCarrello);
		}
		else {
			catalogo=null;
			listaCarrello=null;
		}

		currentSession.setAttribute("listaCarrello", listaCarrello);
		currentSession.setAttribute("totale", totale);

		request.getRequestDispatcher(PAGE_CARRELLO).forward(request, response);   
	}

	/**
	 * metodo che si occupa di fare il merge tra il catalogo e il carrello recuperato da db
	 * @param user
	 * @return
	 */
	private List<List<Product>> ripristinaCarrello(String user) {

		DBCom db = new DBCom();

		List<List<Product>> listaCatalogo=db.getCatalogo();
		Carrello c = db.getCarrello(user);
		if (c!=null) {
			List<Product> listaProdottiStorico=db.getListaCarrello(c.getId());

			for(int i=0; i<listaProdottiStorico.size();i++) {//errore nel for
				for (int n=0; n<listaCatalogo.size();n++) {
					for(int x=0;x<listaCatalogo.get(n).size();x++) {
						if(listaProdottiStorico.get(i).getId()==listaCatalogo.get(n).get(x).getId())
							listaCatalogo.get(n).get(x).setQuantita(listaProdottiStorico.get(i).getQuantita());
					}
				} 
			}
		}
		return listaCatalogo;
	}

	/**
	 * metodo che si occupa della coordinazione delle operazioni di salvataggio carrello
	 * @param user
	 * @param listaCarrello
	 */
	private void salvaCarrello(String user,List<Product> listaCarrello) {

		DBCom db = new DBCom();

		db.scriviCarrello(user);
		int idLastCarrello=db.getLastCartUser(user);
		db.scriviProdottiContenuti(idLastCarrello,listaCarrello);
	}

	/**
	 * metodo che implementa la sequenza di login
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void loginAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		String user=request.getParameter("username");
		String password=request.getParameter("password");
		boolean esito = db.checkCredenziali(user,password);
		if (user==null||user.equalsIgnoreCase("")||password.equalsIgnoreCase("")||!esito) {//campo username vuoto

			if(!esito)
				currentSession.setAttribute("message", "LOGIN ERROR:Attenzione credenziali errate");
			else
				currentSession.setAttribute("message", "LOGIN ERROR:Attenzione inserire username e password");

			List<List<Product>> catalogo = db.getCatalogo();
			currentSession.setAttribute("catalogo", catalogo);
		}
		else {	/* ------user compilato------
	   				recupero catalogo storico
	   				scrivo user nella sessione
	   				scrivo catalogo nella sessione
	   				forward
		 */
			List<List<Product>> catalogo=ripristinaCarrello(user);

			//imposto guardia per utente loggato
			currentSession.setAttribute("userLogged", "yes");
			currentSession.setAttribute("message", "Benvenuto "+user+" !");
			currentSession.setAttribute("username", user);
			currentSession.setAttribute("catalogo", catalogo);
		}

		request.getRequestDispatcher(PAGE_CATALOGO).forward(request, response);

	}

	/**
	 * Metodo che implementa la sequenza di inserimento o modifica di una classe
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void inserimentoClasseAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		String nomeClasse=request.getParameter("nnClasse");
		int idClasse=Integer.valueOf(request.getParameter("idClasse"));
		String msg="";
		if(nomeClasse==null || nomeClasse.equalsIgnoreCase("")) {
			msg="campi non compilati corretamente";   
		}
		else {
			if (idClasse==0) {
				if(db.insertClasse(nomeClasse))
					msg="inserimento classe avvenuto con successo";
				else
					msg="nome classe già in uso";
			}
			else {
				if(db.modClasse(idClasse,nomeClasse))
					msg="modifica classe avvenuta con successo";
				else
					msg="nome classe già in uso";
			}
		}
		currentSession.setAttribute("classi", db.getListaClassi());
		currentSession.setAttribute("listaSpecie", db.getListaSpecie());
		request.setAttribute("lastOp","Classe");
		request.setAttribute("adminmsg", msg);
		request.getRequestDispatcher(PAGE_PANNELLO_ADMIN).forward(request, response);
	}

	/**
	 * metodo che implementa la sequenza di inserimento o modifica di una specie
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void inserimentoSpecieAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		int idSpecie=Integer.valueOf(request.getParameter("idSpecie"));
		String nomeSpecie=request.getParameter("nnSpecie");
		String urlImmagine=request.getParameter("inputURLImmagine");
		double prezzoU;
		boolean successD=false;
		String appendMsg="";
		try {
			prezzoU=Double.valueOf(request.getParameter("puSpecie"));}
		catch (NumberFormatException e) {
			prezzoU=0.0;}
		double prezzoUR= Math.round(prezzoU * 100.0) / 100.0;
		String nomeClasse=request.getParameter("selClasseSpecie");
		String msg="";

		if (nomeSpecie.equalsIgnoreCase("")||prezzoU==0.0) {
			msg="campi non compilati correttamente";
		}
		else {
			Product p = new Product();
			p.setId(idSpecie);
			p.setNomeS(nomeSpecie);
			p.setPrezzoU(prezzoUR);
			p.setNomeC(nomeClasse);
		
			if(p.getId()>0) {
				if(db.modSpecie(p)) {
					msg="modifica specie avvenuta con successo";
					if(urlImmagine!=null && !(urlImmagine.equalsIgnoreCase("")))
						msg+=saveImage(urlImmagine,nomeSpecie);
				}
				else
					msg="nome specie già in uso";
			}
			else {
				if(db.insertSpecie(p)) {
					msg="inserimento specie avvenuto con successo";
					if(urlImmagine!=null && !(urlImmagine.equalsIgnoreCase("")))
						msg+=saveImage(urlImmagine,nomeSpecie);
					}
				else
					msg="nome specie già in uso";
			}
		}
		currentSession.setAttribute("classi", db.getListaClassi());
		currentSession.setAttribute("listaSpecie", db.getListaSpecie());
		currentSession.setAttribute("catalogo", db.getCatalogo());
		request.setAttribute("adminmsg", msg);
		request.setAttribute("lastOp","Specie");
		request.getRequestDispatcher(PAGE_PANNELLO_ADMIN).forward(request, response);
	}

	/**
	 * metodo che implemente l'accesso alla sezione pannello admin
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void pannelloAdminNavigation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		String user= (String) currentSession.getAttribute("username");
		String target=PAGE_CATALOGO;
		if (user!=null) {
			List<Classe> listaClassi=db.getListaClassi();
			List<Product> listaSpecie=db.getListaSpecie();
			currentSession.setAttribute("classi", listaClassi);
			currentSession.setAttribute("listaSpecie", listaSpecie);
			target=PAGE_PANNELLO_ADMIN;
		}
		else {
			currentSession.setAttribute("message", "Attenzione effettuare l'accesso"+
					" per accedere al pannello ammistratore");
			List<List<Product>> catalogo = db.getCatalogo();
			currentSession.setAttribute("catalogo", catalogo);
		}
		request.getRequestDispatcher(target).forward(request, response);
	}

	/**
	 * metodo che implementa la registrazione di un nuovo utente
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void confermaRegistrazioneAction (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		String nUsername = (String) request.getParameter("nusername");
		String nPassword = (String) request.getParameter("npassword");
		String cnPassword = (String) request.getParameter("cnpassword");
		String target=PAGE_REGISTRAZIONE;
		String msg="";
		boolean freeUser=false;
		boolean campiCompilati=nUsername!=null && !(nUsername.equalsIgnoreCase("")) && nPassword!=null && !(nPassword.equalsIgnoreCase("")) && cnPassword!=null && !(cnPassword.equalsIgnoreCase(""));

		//controllo disponibilità username
		if (campiCompilati) {
			freeUser=db.checkFreeUser(nUsername);	
			if (freeUser) {
				if(nPassword.compareTo(cnPassword)==0) {
					//registro nel db nuovo user
					db.saveUser(nUsername,nPassword);
					msg="utente registrato, esegui il login";
					target=PAGE_CATALOGO;
				}
				else
					msg="le due password non coincidono";
			}
			else 
				msg="username già in uso";
		}
		else 
			msg="completare correttamente i campi";

		currentSession.setAttribute("message",msg);
		request.getRequestDispatcher(target).forward(request, response);
	}

	/**
	 * metodo che implementa l'accesso al carrello
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void carrelloNavigation (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		String user= (String) currentSession.getAttribute("username");
		if (user!=null)
			generateCart(request,response,user);
		else {
			currentSession.setAttribute("message", "CARRELLO:Attenzione inserire username");
			List<List<Product>> catalogo = db.getCatalogo();
			currentSession.setAttribute("catalogo", catalogo);
			request.getRequestDispatcher(PAGE_CATALOGO).forward(request, response);
		}
	}

	/**
	 * metodo che implementa la modifica della quantita
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void modQuantitaAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession currentSession=request.getSession();
		DBCom db = new DBCom();
		List<List<Product>> catalogo;
		try {catalogo=(List<List<Product>>)currentSession.getAttribute("catalogo");}
		catch (ClassCastException e) {catalogo=new DBCom().getCatalogo();}
		char btn=(request.getParameter("btnSel")).charAt(0);
		int id=Integer.valueOf(request.getParameter("idSel"));

		if(catalogo!=null) {
			for (int i=0;i<catalogo.size();i++) {
				for (int n=0; n<catalogo.get(i).size();n++) {
					if (catalogo.get(i).get(n).getId()==id)
						if(btn=='+')
							catalogo.get(i).get(n).incremetaQ();
						else
							catalogo.get(i).get(n).decrementaQ();
				}
			}
		}
		else {
			catalogo=db.getCatalogo();
		}
		currentSession.setAttribute("message", "");
		currentSession.setAttribute("catalogo", catalogo);
		request.getRequestDispatcher(PAGE_CATALOGO).forward(request, response);
	}

	private String saveImage(String imageUrl,String nomeS){
		try {
			URL url = new URL(imageUrl);
			BufferedImage img = ImageIO.read(url);
			File file = new File("C://Users/Fabrizio Tonus/eclipse-workspace/PetShop/WebContent/resources/images/"+nomeS+".jpg");
			ImageIO.write(img, "jpg", file);
			return "";
			}
		catch (MalformedURLException e) {
			return " malformed url exception";
		}
		catch (IllegalArgumentException e) {
			return  " illegal argurment exception";
		}
		catch (IIOException e) {
			return " image io exception:causa: "+e.getCause();
		}
		catch (IOException e) {
			return " io exception:causa: "+e.getCause();
		}
		/*ho deciso per non intercettare singolarmente i tipi delle eccezioni in qunato erano 
		 * tanti e con lo stessa procedura di gestione*/
		/*catch (Exception e) {
			return false;
		}*/
	}
}
