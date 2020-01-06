/**
 * 
 */
function selezionaTab(evt, tabSelezionata) {
	var i, contenutoTab, tablink;
	contenutoTab = document.getElementsByClassName("contenutoTab");
	for (i = 0; i < contenutoTab.length; i++) {
		contenutoTab[i].style.display = "none";
	}
	tablink = document.getElementsByClassName("tablink");
	for (i = 0; i < tablink.length; i++) {
		tablink[i].className = tablink[i].className.replace(" active",
		"");
	}
	document.getElementById(tabSelezionata).style.display = "block";
	evt.currentTarget.className += " active";
	//document.getElementById("lastOp").value=tabSelezionata;

}

function rigaClasseSel(evt, idSel, nomeCSel) {
	document.getElementById("idClasse").value = idSel;
	document.getElementById("nomeClasse").value = nomeCSel;
	document.getElementById("msgModClasse").innerHTML = "Sei entrato in modalità modifica.Premi annulla per annullare";
	var tabellaClassi = document.getElementById("tableClasse");
	for (var i = 0, row; row = tabellaClassi.rows[i]; i++) {
		row.className = row.className.replace(" active", "");
	}
	evt.currentTarget.className += " active";
	document.getElementById("msgModClasse").innerHTML = "Sei entrato in modalità modifica. Premi annulla per annullare";
	document.getElementById("btnInserisciClasse").value = "Modifica";
}

function rigaSpecieSel(evt, id, nomeS, prezzoU, nomeC) {
	document.getElementById("idSpecie").value = id;
	document.getElementById("nomeSpecie").value = nomeS;
	document.getElementById("puSpecie").value = prezzoU;

	var cbClasse = document.getElementById("selClasseSpecie");

	for (var i = 0; i < cbClasse.options.length; i++) {
		var selection = cbClasse.options[i].text.toUpperCase();
		;
		if (selection == nomeC) {
			cbClasse.options[i].selected = true;
		}
	}

	var tabellaSpecie = document.getElementById("tableSpecie");
	for (var i = 0, row; row = tabellaSpecie.rows[i]; i++) {
		row.className = row.className.replace(" active", "");
	}
	evt.currentTarget.className += " active";
	document.getElementById("msgModSpecie").innerHTML = "Sei entrato in modalità modifica. Premi annulla per annullare";
	document.getElementById("btnInserisciSpecie").value = "Modifica";
}

function ripristinoTab() {
	var lastOpType = document.getElementById("lastOp").value;
	document.getElementById("lastOp").value ="";
	if (lastOp!=null && lastOp!=""){
		var i, contenutoTab, tablink;
		contenutoTab = document.getElementsByClassName("contenutoTab");
		for (i = 0; i < contenutoTab.length; i++) {
			contenutoTab[i].style.display = "none";
		}
		tablink = document.getElementsByClassName("tablink");
		for (i = 0; i < tablink.length; i++) {
			tablink[i].className = tablink[i].className.replace(" active", "");
		}
		document.getElementById(lastOpType).style.display = "block";
		document.getElementById("tab"+lastOpType).className += " active";
	}
}