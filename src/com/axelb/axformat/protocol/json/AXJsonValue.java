/*
 * ==========================================================================
 * class name  : com.axelb.axformat.protocol.json.AXJsonValue
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

public abstract class AXJsonValue {
	public static final int		TYPEVAL_VALUE	= 1;
	public static final int		TYPEVAL_ARRAY	= 2;
	public static final int		TYPEVAL_OBJECT	= 3;
	
	public static final int		VALUE_NULL		= -1;
	public static final int		VALUE_STRING	=  1;
	public static final int		VALUE_NUMBER	=  2;
	public static final int		VALUE_BOOLEAN	=  3;
	public static final int		VALUE_ARRAY		=  4;
	public static final int		VALUE_OBJECT	=  5;
	
	protected int type;
	
	public static AXJsonValue createValue(String value, int type) {
		switch(type) {
		case VALUE_STRING:
			return new AXJsonString(value);
			
		case VALUE_NUMBER:
			return new AXJsonNumber(value);
			
		case VALUE_BOOLEAN:
		case VALUE_NULL:

			return null;
			
		}
		
		return null;
	}
		
	public static AXJsonValue createArrayValue(Vector<AXJsonValue> value) {
		return new AXJsonArray(value);
	}
	
	public static AXJsonValue createObjectValue(Vector<AXJsonObject> value) {
		return new AXJsonBlock(value);
	}
	
	public AXJsonValue get(String key) {
		if(type != VALUE_OBJECT)
			return null;
		Vector<AXJsonObject> obj = (Vector<AXJsonObject>)this.getValue();
		
		for(int i = 0;i < obj.size();i++) {
			if(obj.get(i).getName().equals(key)) {
				
				return obj.get(i).getValue();
			}
			
		}
		
		return null;
	}
	public int getType() {
		return type;
	}
	public abstract Object getValue();
}
