package beato.shop.test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import beato.calc.util.Product;
import beato.shop.bean.Classe;
import beato.shop.logic.DBCom;

public class TestPOI {

	private static final String WORKBOOK_PATH="WebContent/resources/xlsx/Workbook.xlsx";
	private static final String WORKBOOKWRITE_PATH="WebContent/resources/xlsx/WriteWorkbook.xlsx";
	private static final String WORKBOOKREAD_PATH="WebContent/resources/xlsx/ReadWorkbook.xlsx";

	public TestPOI() {}

	public static void main(String[] args) {
		TestPOI tester = new TestPOI();
		//tester.createWorkbook();
		//tester.openWorkbook();
		tester.writeClassiSheet();
		tester.writeSpecieSheet();
		//tester.readClassiSheet();
	}

	/**
	 * Metodo che crea un workbook e uno sheet
	 */
	private void createWorkbook() {
		//create a blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//create file system using specific name
		try {
			FileOutputStream out = new FileOutputStream(new File(WORKBOOK_PATH));
			workbook.write(out);
			out.close();
			System.out.println("firstworkbook.xlsx written successfully");
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}


	/**
	 * Metodo che apre un workbook esistente
	 */
	private void openWorkbook() {

		//open file and then create input stream
		File file = new File(WORKBOOK_PATH);
		try {
			FileInputStream fis = new FileInputStream(file);
			//get workbook instance for xlsx file from inputstream
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			if (file.isFile()&&file.exists())
				System.out.println("firstworkbook.xlsx file opened successfully!");
			else
				System.out.println("error opening firstworkbook.xlsx !");
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	private void writeClassiSheet() {
		try {
			File file = new File(WORKBOOK_PATH);
			FileInputStream fis = new FileInputStream(file);
			//get workbook instance for xlsx file from inputstream
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			//creo sheet
			XSSFSheet spreadsheet = workbook.getSheet("Classi");
			//creo object row
			XSSFRow row;

			DBCom db = new DBCom();
			List<Classe> listaClassi = db.getListaClassi();
			Map<Integer,Object[]> mapClassi= new HashMap<Integer,Object[]>();

			for (Classe c:listaClassi)
				mapClassi.put(c.getId(),new Object[] {String.valueOf(c.getId()),c.getNomeC()});

			Set<Integer> keyId =mapClassi.keySet();
			int rowId=1;

			for (int key:keyId) {
				row=spreadsheet.createRow(rowId++);
				Object[] classe = mapClassi.get(key);

				int cellId=0;

				for(Object obj: classe) {
					Cell cell = row.createCell(cellId++);
					cell.setCellValue((String) obj);
				}
			}


			//salvo workbook us file system
			FileOutputStream out = new FileOutputStream (new File(WORKBOOK_PATH));
			workbook.write(out);
			out.close();
			System.out.println("classiSheet written successfully on: "+WORKBOOK_PATH);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	private void writeSpecieSheet() {

		try {

			File file = new File(WORKBOOK_PATH);
			FileInputStream fis = new FileInputStream(file);
			//get workbook instance for xlsx file from inputstream
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			//creo sheet
			XSSFSheet spreadsheet = workbook.getSheet("Specie");
			//creo object row
			XSSFRow row;

			DBCom db = new DBCom();
			List<Product> listaSpecie = db.getListaSpecie();
			Map<Integer,Object[]> mapSpecie= new HashMap<Integer,Object[]>();

			for (Product s:listaSpecie)
				mapSpecie.put(s.getId(),new Object[] {String.valueOf(s.getId()),s.getNomeS(),String.valueOf(s.getPrezzoU()),s.getNomeC()});

			Set<Integer> keyId =mapSpecie.keySet();
			int rowId=1;

			for (int key:keyId) {
				row=spreadsheet.createRow(rowId++);
				Object[] classe = mapSpecie.get(key);

				int cellId=0;

				for(Object obj: classe) {
					Cell cell = row.createCell(cellId++);
					cell.setCellValue((String) obj);
				}
			}


			//salvo workbook us file system
			FileOutputStream out = new FileOutputStream (new File(WORKBOOK_PATH));
			workbook.write(out);
			out.close();
			System.out.println("specieSheet written successfully on: "+WORKBOOK_PATH);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}

	private void readClassiSheet() {

		XSSFRow row;

		try {
			//apro file workbook e sheet
			FileInputStream fis= new FileInputStream(new File(WORKBOOK_PATH));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet spreadsheet = workbook.getSheet("Classi");
			Iterator<Row> rowIt = spreadsheet.iterator();
			List<Classe> listaClassi=new ArrayList<Classe>();
			DBCom db = new DBCom();

			int rc=2;
			if(rowIt.hasNext())
				rowIt.next();
			//itero sulle righe dello sheet
			while (rowIt.hasNext()) {
				System.out.println("rc: "+rc++);
				row= (XSSFRow) rowIt.next();
				Iterator<Cell> cellIt = row.cellIterator();
				Classe c= new Classe();
				c.setId((int)cellIt.next().getNumericCellValue());
				c.setNomeC(cellIt.next().getStringCellValue());
				listaClassi.add(c);
			}

			//stampo la lista di classi lette
			for(Classe c:listaClassi) {
				if(db.insertClasse(c.getNomeC()))
					System.out.println("classe salvata a db: "+c);
				else
					System.out.println("classe già presente a db: "+c);
			}

		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}
}
