<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link href="resources/css/stile.css" rel="stylesheet" type="text/css">

<title>PetShop</title>
</head>
<body>
	<%@ include file="resources/parts/header.jsp" %> 
	<div class="menuContainer">

		<form id="menuForm" action="PetShopServ" method="post">
			<div class="navigationField">
				<input type="submit" name="btnAdmin" class="menubtn"
					value="Pannello Admin" /> <input type="submit" name="btnCarrello"
					class="menubtn" value="Carrello" />
			</div>
			<c:choose>
				<c:when test="${userLogged=='yes'}">
					<div class="logoutField">
						<input type="submit" name="btnLogout" class="menubtn"
							value="Logout" />
					</div>
				</c:when>
				<c:otherwise>
					<div class=loginContainer>
						<div class="userField">
							Username: <input type="text" class="loginField" name="username" />
						</div>
						<div class="passField">
							Password: <input type="password" class="loginField" name="password" />
						</div>
						<div class="logisterField">
							<input type="submit" name="btnLogin" class="menubtn"
								value="Login" /> <input type="submit" name="btnRegister"
								class="menubtn" value="Registrati" />
						</div>
					</div>

				</c:otherwise>
			</c:choose>
			<div class="errormsg">
				<c:out value="${message}"></c:out>
			</div>
		</form>
	</div>
	<div class="container">
		<form id="catalogo" action="PetShopServ" method="post">
			<input type="hidden" name="btnSel" id="btnSel" value="" /> <input
				type="hidden" name="idSel" id="idSel" value="" />
			<table class="catalogo">
				<c:forEach items="${catalogo}" var="categoria">
					<c:forEach items="${categoria}" var="product" varStatus="stat">
						<c:if test="${stat.first}">
							<tr>
								<td class="tdnome classe"><p>${product.nomeC}</p></td>
							</tr>
						</c:if>
						<tr>
							<td></td>
							<td class="tdnome specie smallFont"><p>
									<c:out value="${product.nomeS}" />
								</p></td>
							<td><img src="${product.immagine}"
								onerror="this.onerror=null;this.src='resources/images/generic.jpg';" /></td>
							<td class="smallFont"><fmt:formatNumber type="currency"
									currencySymbol="€" value="${product.prezzoU}" /></td>
							<td><p>
									<c:out value="${product.quantita}" />
								</p></td>
							<td class="tdbuttons"><input type="button"
								class="quantityButton" name="btnQuantita"
								onclick="javascript:Clicked('+','${product.id}')" value="+" /></td>
							<td class="tdbuttons"><input type="button"
								class="quantityButton" name="btnQuantita"
								onclick="javascript:Clicked('-','${product.id}')" value="-"></td>
						</tr>
					</c:forEach>
				</c:forEach>
				<tr>
				</tr>
			</table>
		</form>
	</div>
	<%@ include file="resources/parts/footer.jsp" %>
	<script type="text/javascript" src="resources/js/catalogue.js"></script>
</body>
</html>