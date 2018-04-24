/*
 * ==========================================================================
 * class name  : com.axelb.axformat.protocol.AXJson
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

import com.axelb.axformat.exception.jsonexception.AXJsonFormatNotCorrectException;
import com.axelb.axformat.exception.jsonexception.AXJsonRequestMalformedException;
import com.axelb.axformat.protocol.json.AXJsonArray;
import com.axelb.axformat.protocol.json.AXJsonObject;
import com.axelb.axformat.protocol.json.AXJsonValue;

public class AXJson {
	/**
	 * Il numero di versione relativo al rilascio corrente
	 */
	public static final int		AX_VERSION_MAJOR	= 0;
	/**
	 * Il numero di versione relativo al rilascio corrente
	 */
	public static final int		AX_VERSION_MINOR	= 1;
	
	/**
	 * Classificatori.  
	 * 
	 * Ogni costante identifica un carattere speciale utilizzato nella 
	 * costruzione e gestione del file Json.
	 */
	private final static int	CHAR_GRAFFA_APERTA	= 123;	// {
	private final static int 	CHAR_GRAFFA_CHIUSA	= 125;	// }
	private final static int	CHAR_APICI			= 34;	// "
	private final static int	CHAR_SEPARATOR		= 58;	// :
	private final static int	CHAR_VIRGOLA		= 44;	// ,
	private final static int	CHAR_QUADRA_APERTA	= 91;	// [
	private final static int	CHAR_QUADRA_CHIUSA	= 93;	// ]
	private final static int	CHAR_BLANK			= 32;	// SPAZIO
	
	/**
	 * L'array di byte contenente il file Json da parsare
	 */
	private byte[] stream;
	/**
	 * Puntatore al file Json
	 */
	private int pointerStream;
	
	private Vector<AXJsonObject> json;
	private int totObject;
	
	public AXJson(String value) {
		this.stream = value.getBytes();
	}
	public AXJson(InputStream is) {
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
	}
	public AXJson(ByteArrayOutputStream baos) {
		stream = baos.toByteArray();
	}
	/**
	 * Restituisce il flusso Json sotto forma di stringa
	 * 
	 * @return Una stringa contenente il flusso Json
	 */
	public String toString() {
		return new String(stream);
	}
	
	/**
	 * Metodo che permette di ottenere un oggetto di tipo AXJsonValue a seconda
	 * della chiave passata come parametro
	 * 
	 * @param key E' la chiave che definisce quale oggetto deve essere restituito.
	 * La chiave può assumere una forma del tipo:
	 * <nomeoggetto>><nomeoggetto>>...
	 * 
	 * @return L'oggetto che corrisponde alla chiave cercata. Se l'oggetto non è presente
	 * nell'alberatura, viene restituito null
	 * 
	 */
	public AXJsonValue getField(String key) {
		String[] buff = key.split(">");
		AXJsonValue j = this.get(buff[0]);
		if(j == null)
			return null;
		
		// Cicla finchè non raggiunge la chiave cercata
		for(int i = 1;i < buff.length;i++) {
			j = j.get(buff[i]);
			
			if(j == null)
				return null;
		}
		
		return j;
	}
	/**
	 * Metodo che permette di ottenere un array di stringhe a seconda
	 * della chiave passata come parametro
	 * 
	 * @param key E' la chiave che definisce quale oggetto deve essere restituito.
	 * La chiave può assumere una forma del tipo:
	 * <nomeoggetto>><nomeoggetto>>...
	 * 
	 * @return Un array di String contenente i valori dell'array puntato
	 * dalla corrispondente alla chiave cercata. Se l'oggetto non è presente
	 * nell'alberatura, viene restituito null
	 * 
	 */
	public String[] getArray(String key) {
		String[] buff = key.split(">");
		AXJsonValue j = this.get(buff[0]);
		if(j == null)
			return null;
		
		// Cicla finchè non raggiunge la chiave cercata
		for(int i = 1;i < buff.length;i++) {
			j = j.get(buff[i]);
			
			if(j == null)
				return null;
		}
		
		// Si presuppone, a questo punto, che j sia un array. Se non lo è
		// viene sollevata un eccezione
		if(j.getType() != AXJsonValue.VALUE_ARRAY) {
			return null;
		}
		
		AXJsonArray arr = (AXJsonArray)j;
		
		return arr.array();
	}
	/**
	 * Metodo che permette di ottenere il valore dell'oggetto AXJsonValue avente
	 * chiave uguale a quella passata come parametro.
	 * 
	 * Qualsiasi sia il tipo del valore cercato, verrà restituita una stringa che lo descrive.
	 * Nei casi in cui il tipo di oggetto sia:
	 * <ul>
	 * <li>STRING</li>
	 * <li>NUMBER</li>
	 * <li>BOOLEAN</li>
	 * <li>NULL</li>
	 * </ul>
	 * 
	 * Viene restituito il valore sotto forma di Stringa
	 * 
	 * Nel caso in cui il tipo sia <b>ARRAY</b>:
	 * Viene restituita una stringa contenente tutti i valori presenti nell'array
	 * separati da ;
	 * 
	 * Nel caso in cui il tipo sia <b>OBJECT</b>:
	 * Viene restituito l'indirizzo dell'oggetto sotto forma di stringa
	 * 
	 * @param key E' la chiave che definisce quale valore deve essere restituito.
	 * La chiave può assumere una forma del tipo:
	 * <nomeoggetto>><nomeoggetto>>...
	 * 
	 * @return Il valore corrispondente alla chiave cercata. Se l'oggetto non è presente
	 * nell'alberatura, viene restituito null
	 * 
	 */
	public String getValue(String key) {
		String[] buff = key.split(">");
		AXJsonValue j = this.get(buff[0]);
		if(j == null)
			return null;
		
		for(int i = 1;i < buff.length;i++) {
			j = j.get(buff[i]);
			
			if(j == null)
				return null;
		}
		
		return j.toString();
			
	}
	
	private AXJsonValue get(String key) {
		/* Metodo che cerca e restituisce il valore della chiave cercata
		 * 
		 * Se non lo trova restituisce null
		 */
		
		for(int i = 0;i < json.size();i++) {
			if(json.get(i).getName().equals(key)) {
				AXJsonValue v = json.get(i).getValue();
				
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Avvia la decodifica del file passato in input.
	 * Crea la struttura in memoria che potrà essere successivamente interrogata
	 * per ottenere le informazioni
	 * 
	 * @throws AXJsonFormatNotCorrectException Se durante la lettura vengono
	 * riscontrati degli errori viene sollevata un eccezione di tipo 
	 * AXJsonFormatNotCorrectException con l'indice ove è stato riscontrato l'errore
	 * 
	 */
	public void parser() throws AXJsonFormatNotCorrectException {
		// Imposto il puntatore all'inizio dello stream
		pointerStream = 0;
		
		/* 
		 * Si posiziona all'inizio di un documento JSON valido
		 * Ogni documento JSON deve iniziare con una graffa aperta { e terminare con una graffa chiusa }
		 * 
		 */
		while(pointerStream < stream.length) {
			if(stream[pointerStream] == CHAR_BLANK) {
				// Ok prosegue la ricerca. Questo è l'unico classificato accettato
			} else if(stream[pointerStream] == CHAR_GRAFFA_APERTA) {
				json = readBlock();
				
				
				break;
			} else {
				throw new AXJsonFormatNotCorrectException(pointerStream);
			}
			
			pointerStream++;
		}
		
		
	}
	
	/**
	 * Un blocco è tutto il contenuto presente tra due parentesi graffe
	 * 
	 * { ... blocco ... }
	 * 
	 * Un blocco può contenere zero o più oggetti JsonObject.
	 * 
	 * @return Restituisce un <b>Vector</b> contenente zero o più oggetti di tipo JsonObject
	 * @throws AXJsonFormatNotCorrectException Se il file non rispetta il corretto formato JSON viene restituita un eccezione
	 * 
	 */
	private Vector<AXJsonObject> readBlock() throws AXJsonFormatNotCorrectException {
		Vector<AXJsonObject> block = new Vector<AXJsonObject>();
		
		pointerStream++;	// Posiziona il puntatore oltre la graffa
		
		AXJsonObject jobj = readObject();
		while(jobj != null) {
			block.add(jobj);
			
			jobj = readObject();
		}

		// Al ritorno verifica che sia presente la graffa di chiusura
		if(pointerStream >= stream.length || stream[pointerStream] != CHAR_GRAFFA_CHIUSA) {
			throw new AXJsonFormatNotCorrectException(pointerStream);
		}
		
		pointerStream++;
		return block;
	}
	private AXJsonObject readObject() throws AXJsonFormatNotCorrectException {
		boolean apici = false;	// Indica se gli apici sono aperti o no
		
		AXJsonObject jobj = null;
		
		/* STEP 1 : legge la chiave */
		String key = readKey();
		if(key == null) {
			// Se la chiave è null, l'oggetto è vuoto
			return null;
		}
		
		/* STEP 2 : legge il valore */
		AXJsonValue value = readValue();
		//if(value == null) {
		//	return null;
		//}
		
		jobj = new AXJsonObject(key, value);
		totObject++;
		return jobj;
	}
	private Vector<AXJsonValue> readArray() throws AXJsonFormatNotCorrectException {
		/* Questo metodo recupera tutti i valori specificati come array.
		 * 
		 * Viene restituito un Vector di JsonValue. Potrebbe essere anche un array vuoto
		 */
		Vector<AXJsonValue> arrValue = new Vector<AXJsonValue>();
		
		pointerStream++;
		
		AXJsonValue v = readValue();	
		while(v != null) {
			//System.out.println("ARRAY: "+v.getValue()+" - "+stream[pointerStream]+" - "+pointerStream);
			
			arrValue.add(v);
			
			// Se il pointerStream punta al classificatore CHAR_QUADRA_CHIUSA, esce
			if(stream[pointerStream] == CHAR_QUADRA_CHIUSA) {
				
				//pointerStream++;
				break;
			}
			v = readValue();
		}
		
		return arrValue;
	}
	private AXJsonValue readValue() throws AXJsonFormatNotCorrectException {
		/* Questo metodo si fa carico di recuperare il valore da associare alla chiave
		 * Il valore è un oggetto di tipo JsonValue in quanto può assumere diverse forme
		 * 
		 * A livello di parser possono verificarsi tre opzioni
		 * 1 - Viene individuato un valore valido (String, Number, Boolean)
		 * 2 - Viene individuato un array. Questo è dato dal classificatore CHAR_QUADRA_APERTA
		 * 3 - Viene individuato un blocco. Questo è dato dal classificatore CHAR_QUADRA_APERTA
		 * 
		 * Nell'ultimo caso viene richiamato ricorsivamente il metodo readBlock
		 * 
		 * Il ritorno al chiamante avviene in uno di questi due casi
		 * 1 - Viene rilevato un errore nel parser
		 * 2 - Viene rilevato il classificatore CHAR_VIRGOLA
		 */
		int apici = 0;	// Indica se gli apici sono aperti o no
		// 0: chiusi (posizione di start)
		// 1: aperti
		// 2: chiusi (viene memorizzata la chiave)
		// > 2: ERROR
		
		int typeValue = 0;		// E' la tipologia di valore che può assumere
								// Le tre macrocategorie sono definite in JsonValue
		
		String value = null;	// Nel caso in cui il valore non sia un object od un array, questa variabile contiene il valore
		int start = 0;
		int length = 0;
		
		while(pointerStream < stream.length) {
			if(stream[pointerStream] == CHAR_APICI) {
				apici++;
				
				if(apici == 1) {
					if(typeValue != 0) {
						// Se apre gli apici quando il tipo è già stato definito va in errore
						throw new AXJsonFormatNotCorrectException(pointerStream);
					}
					typeValue = AXJsonValue.VALUE_STRING;
					
					start = pointerStream+1;
					length = 0;
				} else if(apici == 2) {
					value = new String(stream,start,length);
				} else {
					/* WARNING: DEVO GESTIRE I CARATTERI ESCAPE \" */
					throw new AXJsonFormatNotCorrectException(pointerStream);
				}
			} else if(stream[pointerStream] == CHAR_VIRGOLA) {
				
				if(typeValue == AXJsonValue.VALUE_NUMBER) {
					value = new String(stream,start,length);
				}
				
				pointerStream++;
				break;
			} else if(stream[pointerStream] == CHAR_QUADRA_APERTA) {
				Vector<AXJsonValue> vArray = readArray();

				// Verifica sulla parentesi quadra chiusa
				pointerStream++;
				AXJsonValue j = AXJsonValue.createArrayValue(vArray);
				return j;
				
			} else if(stream[pointerStream] == CHAR_QUADRA_CHIUSA) {
				break;
			} else if(stream[pointerStream] == CHAR_GRAFFA_APERTA) {
				/* Chiama ricorsivamente un altro blocco */
				Vector<AXJsonObject> vBlock = readBlock();
				
				AXJsonValue j = AXJsonValue.createObjectValue(vBlock);
				
				return j;
			} else if(stream[pointerStream] == CHAR_GRAFFA_CHIUSA) {
				if(typeValue == 0) {
					throw new AXJsonFormatNotCorrectException(pointerStream);
				} 
				
				break;
			} else if(stream[pointerStream] == CHAR_BLANK) {
				// Tutto ok
				if(typeValue == AXJsonValue.VALUE_STRING)
					length++;
			} else {
				if(typeValue == 0) {
					if(stream[pointerStream] >= 48 || stream[pointerStream] <= 57) {
						typeValue = AXJsonValue.VALUE_NUMBER;
						
						start = pointerStream;
						
					}
				}
				length++;
			}
			
			pointerStream++;
		}
		
		if(value == null)
			return null;
		AXJsonValue jv = AXJsonValue.createValue(value, typeValue);
		
		return jv;
	}
	private String readKey() throws AXJsonFormatNotCorrectException {
		/* Questo metodo si fa carico di recuperare la chiave
		 * Ogni chiave è una stringa compresa tra apici
		 * Se la chiave viene recuperata correttamente il metodo restituisce il nome altrimenti null
		 * 
		 * Il ritorno al chiamante avviene in uno di questi due casi:
		 * 1 - Viene rilevato un errore nel parser
		 * 2 - Viene rilevato il classificatore CHAR_SEPARATOR
		 */
		int apici = 0;	// Indica se gli apici sono aperti o no
						// 0: chiusi (posizione di start)
						// 1: aperti
						// 2: chiusi (viene memorizzata la chiave)
						// > 2: ERROR
		
		String key = null;
		int start = 0;
		int length = 0;
		
		while(pointerStream < stream.length) {
			if(stream[pointerStream] == CHAR_APICI) {
				apici++;
				
				if(apici == 1) {
					start = pointerStream+1;
					length = 0;
				} else if(apici == 2) {
					key = new String(stream,start,length);
				} else {
					System.out.println("ERR1");
					throw new AXJsonFormatNotCorrectException(pointerStream);
				}
			} else if(stream[pointerStream] == CHAR_SEPARATOR) {
				if(apici == 2) {
					pointerStream++;
					
					break;
				} else {
					System.out.println("ERR2");
					throw new AXJsonFormatNotCorrectException(pointerStream);
				}
			} else if(stream[pointerStream] == CHAR_BLANK) {
				if(apici == 1) {
					System.out.println("ERR3");
					throw new AXJsonFormatNotCorrectException(pointerStream);
				}
			} else if(stream[pointerStream] == CHAR_GRAFFA_CHIUSA) {
				return null;	
			} else {
				if(apici == 1) {
					length++;
				} else {
					// Se il classificatore è una virgola, è la prima che si incontra e non è il primo oggetto in memoria, si prosegue
					if(stream[pointerStream] == CHAR_VIRGOLA && totObject > 0) {
						;
					} else {
						System.out.println("ERR4: "+stream[pointerStream]);
						throw new AXJsonFormatNotCorrectException(pointerStream);
					}
				}
			}
			
			pointerStream++;
		}
		
		return key;
	}
	public static void main(String[] args) {
		/*
		String s = "{\"codice_errore\":1,\"message\":\"OK\"}";
		String s2 = "{\"pageInfo\":{\"pageName\":\"abc\"},\"codice_errore\":1,\"message\":\"OK\"}";
		String s3 = "{\"codice_errore\":0,\"messaggio_errore\":\"\",\"lista_allegati\":[\"KID1.pdf\",\"KID2.pdf\"]}";
		String s4 = "{\"codice_errore\":0,\"messaggio_errore\":\"\",\"lista_allegati\":[\"KID1.pdf\",\"KID2.pdf\"],\"pageinfo\":\"magica\"}";
		String s5 = "{\"codice_errore\":0,\"messaggio_errore\":\"\",\"lista_allegati\":[\"KIDDL756PCADL633V001.pdf\",\"KIDDL764PCADL629V001.pdf\",\"KIDDL764ADL629V001.pdf\",\"KIDDL764TCADL629V001.pdf\"]}";
		*/

		
		String s = "{"; 
		s = s + "\"author\": \"Zach Carter <zach@carter.name> (http://zaa.ch)\",";
		s = s + "\"name\": \"jsonlint\",";
		s = s + "\"description\": \"Validate JSON\",";
		s = s + "\"keywords\": [";
		s = s + "\"json\",";
		s = s + "\"validation\",";
		s = s + "\"lint\",";
		s = s + "\"jsonlint\"";
		s = s + "],";
		s = s + "\"version\": \"1.6.3\",";
		s = s + "\"preferGlobal\": true,";
		s = s + "\"repository\": {";
		s = s + "\"type\": \"git\",";
		s = s + "\"url\": \"git://github.com/zaach/jsonlint.git\"";
		s = s + "},";
		s = s + "\"bugs\": {";
		s = s + "\"url\": \"http://github.com/zaach/jsonlint/issues\"";
		s = s + "},";
		s = s + "\"main\": \"lib/jsonlint.js\",";
		
		s = s + "\"bin\": {";
		s = s + "    \"jsonlint\": \"lib/cli.js\"";
		s = s + "  },";
		s = s + "  \"engines\": {";
		s = s + "    \"node\": \">= 0.6\"";
		s = s + "  },";
		s = s + "  \"dependencies\": {";
		s = s + "    \"nomnom\": \"^1.5.x\",";
		s = s + "    \"JSV\": \"^4.0.x\"";
		s = s + "  },";
		s = s + "  \"devDependencies\": {";
		s = s + "    \"test\": \"*\",";
		s = s + "    \"jison\": \"*\",";
		s = s + "    \"uglify-js\": \"*\"";
		s = s + "  },";
		s = s + "  \"scripts\": {";
		s = s + "    \"test\": \"node test/all-tests.js\"";
		s = s + "  },";
		s = s + "  \"homepage\": \"http://zaach.github.com/jsonlint/\",";
		s = s + " \"optionalDependencies\": {}";
		
		s = s + "}";
		
		//String s = "{\"repository\": {\"type\": \"git\",\"topo\":{\"lupo\":\"manuela\"},\"url\": \"git://github.com/zaach/jsonlint.git\"},\"name\":\"mamma\"}";
		//String s1 = "{\"name\":{\"pippo\":2},\"lista\":[\"uno\",\"due\"]}";
		//String s = "{\"codice_errore\":0,\"messaggio_errore\":\"\",\"lista_allegati\":[\"KIDDL756PCADL633V001.pdf\",\"KIDDL764PCADL629V001.pdf\",\"KIDDL764ADL629V001.pdf\",\"KIDDL764TCADL629V001.pdf\"]}";
		
		System.out.println(s);
		AXJson json = new AXJson(s);
		
		
		try {
			json.parser();
		} catch (AXJsonFormatNotCorrectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * ACCEDERE AI CONTENUTI
		 */
		
		//System.out.println("VERIFICA: ");
		//System.out.println("repository: "+json.get("name").getValue());
		/*
		System.out.println("VAL codice_errore: "+json.get("codice_errore").getValue());
		System.out.println("VAL chiave: "+json.get("chiave"));
		System.out.println("VAL lista_allegati: "+json.get("lista_allegati"));
		//System.out.println("VAL pageinfo: "+json.get("pageinfo").getValue());
		
		JsonValue v = json.get("lista_allegati");
		JsonArray varr = (JsonArray)v;
		
		System.out.println("TYPE: "+v.getType());
		System.out.println("ELEMENT: "+varr.length());
		System.out.println("NOME 2: "+varr.val(2));
		*/
		
		System.out.println("DEBUG");
		String temp = json.getValue("lista_allegati");
		System.out.println("TEMP: "+temp);
		
		AXJsonValue ax = json.getField("keywords");
		System.out.println("TIPO OGGETTO: "+ax.getType());
		String[] buffer = json.getArray("namef");
		System.out.println(buffer[1]);
		
		System.out.println("FINE TEST 3");
		/*
		AXJsonValue v = json.get("lista_allegati");
		AXJsonArray varr = (AXJsonArray)v;
		System.out.println("TYPE: "+v.getType());
		System.out.println("ELEMENT: "+varr.length());
		System.out.println("NOME 2: "+varr.val(2));
		*/
	}
}
