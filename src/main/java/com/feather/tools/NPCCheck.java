package com.feather.tools;

import java.io.IOException;

import com.feather.cache.Cache;
import com.feather.cache.parser.NPCDefinitions;
import com.feather.utils.Utils;

public class NPCCheck {
	
	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			if (def.name.contains("Elemental")) {
				System.out.println(id+" - "+def.name);
			}
		}
	}

}
