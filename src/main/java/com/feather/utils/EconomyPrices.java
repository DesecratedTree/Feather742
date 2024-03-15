package com.feather.utils;

import com.feather.cache.parser.ItemDefinitions;
import com.feather.game.item.Item;
import com.feather.game.player.content.ItemConstants;

public final class EconomyPrices {

	public static int getPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.isNoted())
			itemId = defs.getCertId();
		else if (defs.isLended())
			itemId = defs.getLendId();
		if (!ItemConstants.isTradeable(new Item(itemId, 1)))
			return 0;
		if (itemId == 995) // TODO after here
			return 1;
		return defs.getValue() * 3; // TODO get price from real item from saved
									// prices from ge
	}

	private EconomyPrices() {

	}
}
