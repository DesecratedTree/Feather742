package com.feather.tools;

import java.io.File;
import java.io.IOException;

import com.feather.cache.Cache;
import com.feather.cache.parser.ItemDefinitions;
import com.feather.utils.Utils;

public class ItemCheck {

	public static final void main(String[] args) throws IOException {
		Cache.init();
		int total = 0;
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			if (ItemDefinitions.getItemDefinitions(itemId).isWearItem(true)
					&& !ItemDefinitions.getItemDefinitions(itemId).isNoted()) {
				File file = new File("bonuses/" + itemId + ".txt");
				if (!file.exists()) {
					System.out.println(file.getName());
					total++;
				}
			}
		}
		System.out.println("Total " + total);
	}
}
