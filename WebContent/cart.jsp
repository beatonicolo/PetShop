<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="resources/css/stile.css" rel="stylesheet" type="text/css">
<title>Carrello</title>
</head>
<body>
	<%@ include file="resources/parts/header.jsp" %> 
	<%@ include file="resources/parts/menubar.jsp" %> 
	<div class="container">
		<h2 class="titolopagina">Carrello</h2>
		<table class="carrello">
			<c:if test="${empty listaCarrello}">
				<tr>
					<td><p>CARRELLO VUOTO</p></td>
				</tr>
			</c:if>
			<c:forEach items="${listaCarrello}" var="product">
				<tr>
					<td class="tdnome"><p>
							<c:out value="${product.nomeS}" />
						</p></td>
					<td><img src="${product.immagine}"
						onerror="this.onerror=null;this.src='resources/images/generic.jpg';" /></td>
					<td><p>
							<c:out value="${product.quantita}" />
						</p></td>
				</tr>

			</c:forEach>
			<tr>


				<td class="tdnome classe"><p>
						Totale:
						<fmt:formatNumber type="currency" currencySymbol="€"
							value="${totale}" />
					</p></td>

			</tr>
		</table>
	</div>
	<%@ include file="resources/parts/footer.jsp" %>
</body>
</html>