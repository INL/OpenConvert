package nl.openconvert.converters;

import java.sql.Connection;
import java.util.*;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.DoSomethingWithFile;
import nl.openconvert.util.Util;
import nl.openconvert.util.XML;

import org.w3c.dom.*;
/**
 * extracts all top-level tables from all word documents in a directory.
 * Prints tab-separated output.
 * @author does
 *
 */
public class WordTableConverter implements DoSomethingWithFile
{ 
	public Connection connection = null;
	List<Table> allTables = new ArrayList<Table>();
	static class Row
	{
		List<Cell> cells = new ArrayList<Cell>();
	}
	
	static class Cell
	{
		String text;
	}
	
	static class Table
	{
		List<Row> rows = new ArrayList<Row>();
	}
	
	public List<Table> extractTables(Document d)
	{
		Element r = d.getDocumentElement();
		List<Table> tables = new ArrayList<Table>();
		List<Element> tableElements = XML.getElementsByTagname(r,"table", false);
		for (Element t: tableElements)
		{
			Table table = new Table();
			tables.add(table);
			for (Element re : XML.getElementsByTagname(t,"tr", false))
			{
				Row row = new Row();
				table.rows.add(row);
				for (Element ce : XML.getElementsByTagname(re,"th", false))
				{
					Cell cell = new Cell();
					cell.text = ce.getTextContent().trim().replaceAll("\\s+", " ");
					// System.err.println("<"  + cell.text + ">");
					row.cells.add(cell);
				}
				for (Element ce : XML.getElementsByTagname(re,"td", false))
				{
					Cell cell = new Cell();
					cell.text = ce.getTextContent().trim().replaceAll("\\s+", " ");
					// System.err.println("<"  + cell.text + ">");
					row.cells.add(cell);
				}
			}
		}
		return tables;
	}

	public void dumpAll()
	{
		for (Table t: allTables)
		{
			for (Row r: t.rows)
			{
				List<String> cells = new ArrayList<String>();
				for (Cell c: r.cells)
				{
					cells.add(c.text);
				}
				System.out.println(Util.join(cells, "\t"));
			}
		}
	}
	
	public void dumpToConnection(Table t, Connection connection)
	{
		Row row0 = t.rows.get(0);
		List<String> fieldNames = new ArrayList<String>();
		for (Cell c: row0.cells)
		{
			String fieldName = makeFieldName(c.text);
			fieldNames.add(fieldName + " text ");
		}
		String createQuery = "create table table0 ( ";
		
	}
	
	private static String makeFieldName(String text) 
	{
		// TODO Auto-generated method stub
		text = text.replaceAll("[^a-zA-Z0-9_]", "_");
		return text;
	}

	@Override
	public void handleFile(String fileName)
	{
		// TODO Auto-generated method stub
		System.err.println(fileName);
		if (!fileName.endsWith(".doc"))
			return;
		try
		{
			Document d = Doc2HTML.Word2HtmlDocument(fileName);
			allTables.addAll(extractTables(d));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		WordTableConverter wtc = new WordTableConverter();
		DirectoryHandling.traverseDirectory(wtc, args[0]);
		wtc.dumpAll();
	}
}
