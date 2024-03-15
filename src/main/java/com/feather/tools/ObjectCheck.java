package com.feather.tools;

import java.io.IOException;

import com.feather.cache.Cache;
import com.feather.cache.parser.ObjectDefinitions;
import com.feather.utils.Utils;

public class ObjectCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
			ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(i);
			if ( def.containsOption("Steal-from")) {
				System.out.println(def.id+" - "+def.name);
			}
		}
	}

}
