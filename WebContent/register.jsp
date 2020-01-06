<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link href="resources/css/stile.css" rel="stylesheet" type="text/css">

<title>Registrazione</title>
</head>
<body>
	<%@ include file="resources/parts/header.jsp" %>  
	<%@ include file="resources/parts/menubar.jsp" %> 
	<div class="container">
		<h2 class="titolopagina">Registrati ora</h2>
		<form id="registrazione" action="PetShopServ" method="post">
			<table class="tableRegistrazione">

				<tr>
					<td>Username:</td>
					<td><input type="text" class="nusername" name="nusername" /></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" class="npassword" name="npassword" /></td>
				</tr>
				<tr>
					<td>Conferma Password:</td>
					<td><input type="password" class="cnpassword" name="cnpassword" /></td>
				</tr>
				<tr>
					<td><input type="submit" name="btnConferma" value="Conferma" />
					</td>
					<td><p class="errormsg">
							<c:out value="${message}"></c:out>
						</p></td>
				</tr>
			</table>
		</form>
	</div>
	<%@ include file="resources/parts/footer.jsp" %> 
</body>
</html>