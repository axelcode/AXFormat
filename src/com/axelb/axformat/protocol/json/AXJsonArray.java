/*
 * ==========================================================================
 * class name  : com.axelb.axformat.protocol.json.AXJsonArray
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
package com.axelb.axformat.protocol.json;

import java.util.Vector;

public class AXJsonArray extends AXJsonValue {
	private Vector<AXJsonValue> value;
	public AXJsonArray(Vector<AXJsonValue> value) {
		this.value = value;
		
		this.type = AXJsonValue.VALUE_ARRAY;
	}
	@Override
	public Object getValue() {
		
		return value;
	}
	public String toString() {
		String ret = "";
		
		for(int i = 0;i < value.size();i++) {
			ret = ret + (String)value.get(i).getValue() + ";";
		}
		
		return ret;
	}
	
	public int length() {
		return value.size();
	}
	public String val(int index) {
		return (String)value.get(index).getValue();
	}
	public String[] array() {
		String[] ret = new String[value.size()];
		
		for(int i = 0;i < value.size();i++)
			ret[i] = this.val(i);
		
		return ret;
	}
}
