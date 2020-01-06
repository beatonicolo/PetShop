/**
 *funzione che gestisc la presione dei pulsanti di aumento e diminuzione della quantità
 */
function Clicked(op, id) {
		document.getElementById("btnSel").value = op;
		document.getElementById("idSel").value = id;
		document.getElementById("catalogo").submit();
}

/**
 * funzione di prova
 */
function foo(op, id) {
		alert(op.concat(id));
}