<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="resources/css/stile.css" rel="stylesheet" type="text/css">
<title>Pannello Amministratore</title>
</head>
<body onload="ripristinoTab()">
	<%@ include file="resources/parts/header.jsp" %> 
	<%@ include file="resources/parts/menubar.jsp" %> 
	<div class="container">
		<h2 class="titolopagina">Pannello di amministrazione</h2>

		<p id="adminrerrormsg">
			<c:out value="${adminmsg}"></c:out>
		</p>

		<input type="hidden" id="lastOp" value="${lastOp}">
		<div class="tab">
			<button id="tabClasse" class="tablink" onclick="selezionaTab(event,'Classe')">Classe</button>
			<button id="tabSpecie" class="tablink" onclick="selezionaTab(event,'Specie')">Specie</button>
		</div>

		<div id="Classe" class="contenutoTab">
			<form id="modificaClasse" action="PetShopServ" method="post">
				<table id="tableFormClasse" class="tableFormClasse">
					<tr>
						<td>Id:</td>
						<td><input name="idClasse" id="idClasse" type="text"
							class="inputIdClasse" value=0 readonly></td>
						<td>Nome classe:</td>
						<td><input name="nnClasse" id="nomeClasse"
							class="inputNomeClasse" type="text"></td>
						<td><input type="submit" class="btnInserisciClasse"
							id="btnInserisciClasse" name="btnInserisciClasse"
							value="Inserisci"></td>
						<td><input type="submit" class="btnAnnulla" id="btnAnnulla"
							name="btnAnnulla" value="Annulla"></td>
					</tr>
					<tr>
						<div id="msgModClasse" class="msgClasse"></div>
					</tr>
				</table>
				<table id="tableClasse" class="tableClasse">
					<tr>
						<th>ID</th>
						<th>CLASSE</th>
					</tr>
					<c:forEach items="${classi}" var="classe">
						<tr class="clikable"
							onclick="rigaClasseSel(event,'${classe.id}','${classe.nomeC}')">
							<td><c:out value="${classe.id}"></c:out></td>
							<td><c:out value="${classe.nomeC}"></c:out></td>
						</tr>
					</c:forEach>
				</table>
			</form>
		</div>

		<div id="Specie" class="contenutoTab">
			<form id="modificaClasse" action="PetShopServ" method="post">
				<table class="tableFormSpecie">
					<tr>
						<td>Id:</td>
						<td><input name="idSpecie" value=0 id="idSpecie" type="text"
							readonly></td>
						<td colspan="2">Nome specie:</td>
						<td colspan="2"><input name="nnSpecie" id="nomeSpecie"
							type="text"></td>
						<td><input type="submit" id="btnInserisciSpecie" class="btnInserisciSpecie" name="btnInserisciSpecie" value="Inserisci"></td>
					</tr>
					<tr>
						<td>Classe:</td>
						<td><select id="selClasseSpecie" name="selClasseSpecie">
								<c:forEach items="${classi}" var="classeSpecie">
									<option value="${classeSpecie.nomeC}">${classeSpecie.nomeC}</option>
								</c:forEach>
						</select></td>
						<td colspan="2">Prezzo unitario:</td>
						<td colspan="2"><input name="puSpecie" id="puSpecie"
							type="text"></td>
						<td><input type="submit" class="btnAnnulla" name="btnAnnulla" value="Annulla"></td>
					</tr>
					<tr>
						<td>URL Immagine:</td>
						<td colspan="3"><input type="text" name="inputURLImmagine" id="inputURLImmagine"></td>
					</tr>
					<tr>
						<td colspan="6"><div id="msgModSpecie"></div></td>
					</tr>
				</table>
				<table id="tableSpecie" class="tableSpecie">
					<tr>
						<th>ID</th>
						<th>SPECIE</th>
						<th>PREZZO UNITARIO</th>
						<th>CLASSE</th>
					</tr>
					<c:forEach items="${listaSpecie}" var="specie">
						<tr class="clikable"
							onclick="rigaSpecieSel(event,'${specie.id}','${specie.nomeS}','${specie.prezzoU}','${specie.nomeC}')">
							<td><c:out value="${specie.id}"></c:out></td>
							<td><c:out value="${specie.nomeS}"></c:out></td>
							<td><c:out value="${specie.prezzoU}"></c:out></td>
							<td><c:out value="${specie.nomeC}"></c:out></td>
						</tr>
					</c:forEach>
				</table>
			</form>
		</div>
	</div>
	<%@ include file="resources/parts/footer.jsp" %>
	<script type="text/javascript" src="resources/js/adminpanel.js"></script>
</body>
</html>