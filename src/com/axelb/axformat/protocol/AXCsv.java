/*
 * ==========================================================================
 * class name  : com.axelb.axformat.protocol.AXCsv
 * 
 * Begin       : 24/04/2018
 * Last Update : 24/04/2018
 *
 * Author      : Alessandro Baldini - alex.baldini72@gmail.com
 * License     : GNU-GPL v2 (http://www.gnu.org/licenses/)
 * ==========================================================================
 * 
 * AXFormat
 * Copyright (C) 2018 Alessandro Baldini
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking AXFormat(C) statically or dynamically with other 
 * modules is making a combined work based on AXFormat(C). 
 * Thus, the terms and conditions of the GNU General Public License cover 
 * the whole combination.
 *
 * In addition, as a special exception, the copyright holders 
 * of AXFormat(C) give you permission to combine 
 * AXFormat(C) program with free software programs or libraries 
 * that are released under the GNU LGPL. 
 * You may copy and distribute such a system following the terms of the GNU GPL 
 * for AXFormat(C) and the licenses of the other code concerned, 
 * provided that you include the source code of that other code 
 * when and as the GNU GPL requires distribution of source code.
 *
 * Note that people who make modified versions of AXFormat(C) 
 * are not obligated to grant this special exception for their modified versions; 
 * it is their choice whether to do so. The GNU General Public License 
 * gives permission to release a modified version without this exception; 
 * this exception also makes it possible to release a modified version 
 * which carries forward this exception.
 * 
 */
package com.axelb.axformat.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class AXCsv {
	private final static int	CHAR_SPECIAL	= 92;
	
	/** 
	 * Il flusso di dati CSV
	 */
	private byte[] stream;
	/**
	 * Il separatore di campi. Di default il separatore è rappresentato dal carattere ;
	 */
	private String separator;
	/**
	 * Il Vector che conterrà tutti i campi estratti dal flusso
	 */
	private Vector<String> fieldValue;
	
	public AXCsv(String value, String separator) {
		this.stream = value.getBytes();
		this.separator = separator;
		
		fieldValue = new Vector<String>();
		 
	}
	public AXCsv(String value) {
		this(value,";");
	}
	
	public AXCsv(InputStream is) {
		this(is, ";");
	}
	public AXCsv(InputStream is, String separator) {
		byte[] temp = new byte[1];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			while(is.read(temp) != -1) {
				baos.write(temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stream = baos.toByteArray();
		this.separator = separator;
		
		fieldValue = new Vector<String>();
		
	}
	public AXCsv(ByteArrayOutputStream baos) {
		this(baos, ";");
	}
	public AXCsv(ByteArrayOutputStream baos, String separator) {
		stream = baos.toByteArray();
		this.separator = separator;
		
		fieldValue = new Vector<String>();
		
	}
	
	/**
	 * Imposta un nuovo separatore
	 * 
	 * @param value Il/i carattere/i usato/i come separatore per i campi contenuti nel flusso
	 */
	public void setSeparator(String value) {
		this.separator = value;
	}
	
	/**
	 * Procede al parser del flusso di input
	 */
	public void parser() {
		
		byte[] sep = separator.getBytes();
		
		int start = 0;
		int lenField = 0;
		int pointerStream;
		
		StringBuffer value = new StringBuffer();
		
		for(int i = 0;i < stream.length;i++) {
			if(stream[i] == sep[0]) {
				// Verifica se è il separatore confrontando tutti i bytes
				int counter = 1;
				for(int x = 1;x < sep.length;x++) {
					if(stream[i+x] == sep[x])
						counter++;
				}
				
				if(counter == sep.length) {
					// Spezza il campo
					fieldValue.add(value.toString() + new String(stream,start,lenField));
					
					value.setLength(0);
					
					start = i + counter;
					i = i + counter-1;
					lenField = 0;
				} else {
					lenField++;
				}
			} else if(stream[i] == CHAR_SPECIAL) {
				// Il carattere successivo viene conteggiato come facente parte della stringa
				// Non spezza il campo
				
				value.append(new String(stream,start,lenField));
				
				start = i+1;
				i++;
				lenField = 1;
			} else {
				lenField++;
			}
			
			
		}
		
		// Se ha letto almeno un carattere aggiunge il campo
		if(lenField > 0)
			fieldValue.add(value.toString() + new String(stream,start,lenField));
		
		
	}
	
	/**
	 * Metodo che permette di conoscere il numero totale dei campi estratti dal flusso
	 * 
	 * @return Il numero totale dei campi estratti
	 */
	public int getSize() {
		if(fieldValue == null)
			return 0;
		
		return fieldValue.size();
	}
	
	/**
	 * Metodo che ritorna un array di stringhe contenenti i valori estratti dal flusso
	 * 
	 * @return Un array di String contenente tutti i valori estratti dal flusso
	 */
	public String[] getArray() {
		if(fieldValue == null)
			return null;
		
		return (String[])fieldValue.toArray();
	}
	
	/**
	 * Metodo che restituisce il valore in formato String, presente alla posizione
	 * puntata dall'indice passato come parametro.
	 * 
	 * @param index L'indice della cella richiesta dall'utente
	 * @return Il valore presente nell'array all'indice richiesto dall'utente.
	 * Se l'indice non è presente viene restituito null
	 * 
	 */
	public String getValue(int index) {
		if(fieldValue == null)
			return null;
		
		String ret = null;
		try {
			ret = fieldValue.get(index);
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}

		return ret;
	}
	public static void main(String[] args) {
		String test = "uno;due\\;tre;quanttro;;";
		
		AXCsv csv = new AXCsv(test);
		csv.setSeparator(";");
		csv.parser();
		
		System.out.println(csv.getSize());
		//String[] buff = test.split(";");
		
		//System.out.println(buff.length);
	}
}
