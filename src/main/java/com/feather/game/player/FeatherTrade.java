package com.feather.game.player;
//package com.feather.game.player;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
//import com.feather.Settings;
//import com.feather.game.World;
//import com.feather.game.item.Item;
//import com.feather.game.item.ItemsContainer;
//import com.feather.game.player.content.ItemConstants;
//import com.feather.utils.ItemExamines;
//
//public class FeatherTrade { // CHANGE BACK TO TRADE
//
//	private Player player, target;
//	private ItemsContainer<Item> items;
//	private boolean tradeModified;
//	private boolean accepted;
//
//	private boolean borrowing = false;
//	private int borrowedFrom = -1;
//	private boolean lending = false;
//	private int lentItem = -1;
//	private int lentTo = -1;
//
//	public static final int[] LENDABLE_ITEMS = { 4151, 4675, 10280, 10282,
//			10284, 11235, 6739, 1377, 3204, 1305, 1434, 4587, 7158, 15259,
//			1215, 1249, 11694, 11696, 11698, 11700, 4153, 19143, 19149, 19146,
//			851, 855, 859, 1359, 1373, 1303, 1275, 1333, 1347, 1319, 9185,
//			11730, 6724, 853, 857, 861, 15486, 11716, 13107, 13115, 13109,
//			2633, 2635, 2637, 2639, 2641, 2643, 11277, 10394, 1053, 1055, 1057,
//			2645, 2647, 2649, 2631, 10396, 1038, 1040, 1042, 1044, 1046, 1048,
//			1050, 10392, 10398, 13101/* , 10460 */, 6585, 3749, 6733, 3751,
//			6737, 11718, 11720, 11722, 11724, 11726, 11728, 11335, 11732, 2513,
//			14479, 1149, 4087, 4585, 1187, 24365, 1135, 1099, 1065, 2499, 2493,
//			2487, 2501, 2495, 2489, 2503, 2497, 2491, 3755, 21790, 21793,
//			21787, 2673, 2669, 2671, 2675, 3480, 2665, 2661, 2663, 3479, 2667,
//			2657, 2653, 2655, 3478, 2659, 19428, 19431, 19434, 19437, 19440,
//			19413, 19416, 19419, 19422, 19425, 19398, 19401, 19404, 19407,
//			19409, 3122, 6809, 10564, 10589, 2579, 4089, 4091, 4093, 4095,
//			4097, 4099, 4101, 4103, 4105, 4107, 4109, 4111, 4113, 4115, 4117,
//			6568, 2577, 2581, 6731, 6137, 6139, 6141, 6147, 6153, 6131, 6133,
//			6135, 6143, 6149, 3385, 3387, 3389, 3391, 3393, 10330, 10332,
//			10334, 10336, 10338, 10340, 10342, 10344, 10346, 10348, 10350,
//			10352, 3753, 6735 };
//
//	public static final int[] LENT_IDS = { 13444, 13406, 13541, 13542, 13543,
//			13405, 13470, 13472, 13478, 13475, 13479, 13477, 13430, 15261,
//			13465, 13370, 13450, 13451, 13452, 13453, 13445, 19145, 19151,
//			19148, 13523, 13525, 13527, 13469, 13471, 13474, 13480, 13476,
//			13473, 13778, 13530, 16461, 13529, 13524, 13526, 13528, 15502,
//			13454, 13795, 13799, 13796, 13811, 13812, 13813, 13814, 13815,
//			13816, 13763, 13790, 13538, 13539, 13540, 13817, 13818, 13819,
//			13810, 13791, 13531, 13532, 13533, 13534, 13535, 13536, 13537,
//			13789, 13792, 13794/* , 10786 */, 13442, 13407, 13427, 13408,
//			13429, 13455, 13456, 13457, 13458, 13459, 13460, 13449, 13462,
//			13481, 14481, 13495, 13488, 13490, 13506, 24367, 13483, 13491,
//			13497, 13484, 13492, 13498, 13485, 13493, 13499, 13486, 13494,
//			13500, 13410, 21792, 21795, 21789, 13833, 13830, 13831, 13834,
//			13832, 13828, 13825, 13826, 13827, 13829, 13823, 13820, 13821,
//			13822, 13824, 19430, 19433, 19436, 19439, 19422, 19415, 19418,
//			19421, 19424, 19400, 19403, 19406, 19409, 19412, 13788, 13786,
//			13785, 13784, 13557, 13508, 13509, 13510, 13511, 13512, 13513,
//			13514, 13515, 13516, 13517, 13518, 13519, 13520, 13521, 13522,
//			13443, 13556, 13558, 13426, 13417, 13418, 13419, 13422, 13425,
//			13414, 13415, 13416, 13420, 13423, 13501, 13502, 13503, 13504,
//			13505, 13544, 13545, 13546, 13547, 13548, 13549, 13550, 13551,
//			13552, 13553, 13554, 13555, 13409, 13428 };
//
//	Item[] lendItems = new Item[] { new Item(-1, -1) };
//
//	public Trade(Player player) {
//		this.player = player;
//		items = new ItemsContainer<Item>(28, false);
//	}
//
//	public int getLentItem() {
//		return lentItem;
//	}
//
//	public boolean isLending() {
//		boolean l = false;
//		if (lentItem != -1) {
//			l = true;
//		}
//		return l;
//	}
//
//	public void handleCollectButton(Player p) {
//		Player target = World.getPlayerByIndex(p.getTrade().lentTo);
//		if (p.getTrade().isLending()) {
//			p.getPackets().sendGameMessage(
//					"Attempting to claim your item from "
//							+ target.getDisplayName() + "...");
//			target.getPackets()
//					.sendGameMessage(
//							p.getDisplayName()
//									+ " has claimed an item you borrowed and it has been returned to them.");
//			target.getTrade().removeAllBorrowedItems(target);
//			player.getPackets().sendIComponentText(109, 59,
//					"<col=00ff00>Claim!");
//			p.getTrade().lentTo = -1;
//			p.getTrade().lending = false;
//			p.getInventory().addItem(p.getTrade().lentItem, 1);/*
//																 * cheap fix by
//																 * jo
//																 */
//			p.getTrade().lentItem = -1;/* cheap fix by jo */
//			player.getTrade().lending = false;/* cheap fix by jo */
//			Player oldTarget = target;/* cheap fix by jo */
//			oldTarget.getTrade().borrowing = false;/* cheap fix by jo */
//			p.closeInterfaces();
//		} else if (p.getTrade().lentItem != -1) {
//			// p.getInventory().addItem(p.getTrade().lentItem, 1);/*cheap fix by
//			// jo*/
//			// p.getTrade().lentItem = -1;/*cheap fix by jo*/
//			for (int i = 540; i < 541; i++) {
//				for (int j = p.getTrade().lentItem; j < p.getTrade().lentItem + 1; j++) {
//					Item[] default_ = new Item[] { new Item(
//							p.getTrade().lentItem, 1) };
//					player.getPackets().sendItems(i, default_);
//				}
//			}
//		}
//	}
//
//	/*
//	 * called to both players
//	 */
//	public void openTrade(Player target) {
//		synchronized (this) {
//			synchronized (target.getTrade()) {
//				this.target = target;
//				Item item = new Item(-1);
//				player.getTrade().lendItems[0] = item;
//				target.getTrade().lendItems[0] = item;
//				player.getPackets().sendItems(541, false,
//						player.getTrade().lendItems);
//				target.getPackets().sendItems(541, true,
//						player.getTrade().lendItems);
//				player.getPackets().sendIComponentText(335, 17,
//						"Trading With: " + target.getDisplayName());
//				player.getPackets().sendGlobalString(203,
//						target.getDisplayName());
//				sendInterItems();
//				sendOptions();
//				sendTradeModified();
//				refreshFreeInventorySlots();
//				refreshTradeWealth();
//				refreshStageMessage(true);
//				player.getInterfaceManager().sendInterface(335);
//				player.getInterfaceManager().sendInventoryInterface(336);
//				player.setCloseInterfacesEvent(new Runnable() {
//					@Override
//					public void run() {
//						closeTrade(CloseTradeStage.CANCEL);
//					}
//				});
//			}
//		}
//	}
//
//	public void addLendItem(int slot) {
//		Item item = player.getInventory().getItem(slot);
//		if (!ItemConstants.isTradeable(item)) {
//			player.getPackets().sendGameMessage("You can't lend this item.");
//			return;
//		}
//		int itemId = -1;
//		for (int i = 0; i < LENDABLE_ITEMS.length; i++) {
//			if (item.getId() == LENDABLE_ITEMS[i]) {
//				itemId = LENDABLE_ITEMS[i];
//			}
//		}
//		if (itemId == -1) {
//			player.getPackets().sendGameMessage("You can't lend this item.");
//			return;
//		}
//		// System.out.println("Lending " + item.getId() + ", name: " +
//		// ItemDefinitions.getItemDefinitions(item.getId()).getName());
//		if (player.getTrade().lendItems[0].getId() != -1) {
//			player.getInventory().addItem(lendItems[0].getId(), 1);
//		}
//		player.getTrade().lendItems[0] = item;
//		player.getPackets().sendIComponentSettings(335, 62, -1, -1, 6);
//		player.getPackets().sendItems(541, false, player.getTrade().lendItems);
//		target.getPackets().sendItems(541, true, player.getTrade().lendItems);
//		player.getInventory().deleteItem(item);
//	}
//
//	public void changeHours() {
//		player.getTemporaryAttributtes().put("loanHrs", true);
//		player.getPackets().sendRunScript(109,
//				"How long do you wish to lend this item for?");
//		player.getTemporaryAttributtes().put("ChangeLoanHours", Boolean.TRUE);
//	}
//
//	public void updateHours(int hrs) {
//		if (hrs > 24 || hrs < 1) {
//			hrs = 0;
//		}
//		String h = (hrs == 1) ? " Hour" : " Hours";
//		if (hrs != 0) {
//			player.getPackets().sendIComponentText(335, 62, hrs + h);
//			target.getPackets().sendIComponentText(335, 58, hrs + h);
//		} else {
//			player.getPackets().sendIComponentText(335, 62,
//					"<col=ff0000>Until</col><br><col=ff0000>logout");
//			target.getPackets().sendIComponentText(335, 58,
//					"<col=ff0000>Until</col><br><col=ff0000>logout");
//		}
//
//	}
//
//	public void removeItem(final int slot, int amount) {
//		synchronized (this) {
//			if (!isTrading())
//				return;
//			synchronized (target.getTrade()) {
//				Item item = items.get(slot);
//				if (item == null)
//					return;
//				Item[] itemsBefore = items.getItemsCopy();
//				int maxAmount = items.getNumberOf(item);
//				if (amount < maxAmount)
//					item = new Item(item.getId(), amount);
//				else
//					item = new Item(item.getId(), maxAmount);
//				items.remove(slot, item);
//				player.getInventory().addItem(item);
//				refreshItems(itemsBefore);
//				cancelAccepted();
//				setTradeModified(true);
//			}
//		}
//	}
//
//	public void sendFlash(int slot) {
//		player.getPackets().sendInterFlashScript(335, 33, 4, 7, slot);
//		target.getPackets().sendInterFlashScript(335, 36, 4, 7, slot);
//	}
//
//	public void cancelAccepted() {
//		boolean canceled = false;
//		if (accepted) {
//			accepted = false;
//			canceled = true;
//		}
//		if (target.getTrade().accepted) {
//			target.getTrade().accepted = false;
//			canceled = true;
//		}
//		if (canceled)
//			refreshBothStageMessage(canceled);
//	}
//
//	public boolean untradeable(String name) {
//		boolean isUntradeable = false;
//		for (String untradeable : Settings.UNTRADEABLE_ITEMS) {
//			if (name.startsWith(untradeable))
//				isUntradeable = true;
//			else if (name.endsWith(untradeable))
//				isUntradeable = true;
//			else if (name.equals(untradeable))
//				isUntradeable = true;
//			if (isUntradeable)
//				break;
//		}
//		return isUntradeable;
//	}
//
//	public void addItem(int slot, int amount) {
//		synchronized (this) {
//			if (!isTrading())
//				return;
//			synchronized (target.getTrade()) {
//				Item item = player.getInventory().getItem(slot);
//				boolean isUntradeable = untradeable(item.getName()
//						.toLowerCase());
//				if (item.getId() != 995 && item.getId() >= 18829
//						&& item.getId() <= 11835
//						&& !ItemConstants.isTradeable(item)) {
//					player.getPackets().sendGameMessage(
//							"That item isn't tradeable.");
//					return;
//				}
//				if (isUntradeable == true) {
//					player.getPackets().sendGameMessage(
//							"That item isn't tradeable.");
//					return;
//				}
//				Item[] itemsBefore = items.getItemsCopy();
//				int maxAmount = player.getInventory().getItems()
//						.getNumberOf(item);
//				if (amount < maxAmount)
//					item = new Item(item.getId(), amount);
//				else
//					item = new Item(item.getId(), maxAmount);
//				items.add(item);
//				player.getInventory().deleteItem(slot, item);
//				refreshItems(itemsBefore);
//				cancelAccepted();
//			}
//		}
//	}
//
//	public void refreshItems(Item[] itemsBefore) {
//		int[] changedSlots = new int[itemsBefore.length];
//		int count = 0;
//		for (int index = 0; index < itemsBefore.length; index++) {
//			Item item = items.getItems()[index];
//			if (itemsBefore[index] != item) {
//				if (itemsBefore[index] != null
//						&& (item == null
//								|| item.getId() != itemsBefore[index].getId() || item
//								.getAmount() < itemsBefore[index].getAmount()))
//					sendFlash(index);
//				changedSlots[count++] = index;
//			}
//		}
//		int[] finalChangedSlots = new int[count];
//		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
//		refresh(finalChangedSlots);
//		refreshFreeInventorySlots();
//		refreshTradeWealth();
//	}
//
//	public void sendOptions() {
//		player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7,
//				"Offer", "Offer-5", "Offer-10", "Offer-All", "Offer-X",
//				"Value<col=FF9040>", "Lend");
//		player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278);
//		player.getPackets().sendInterSetItemsOptionsScript(335, 32, 90, 4, 7,
//				"Remove", "Remove-5", "Remove-10", "Remove-All", "Remove-X",
//				"Value");
//		player.getPackets().sendIComponentSettings(335, 32, 0, 27, 1150);
//		player.getPackets().sendInterSetItemsOptionsScript(335, 35, 90, true,
//				4, 7, "Value");
//		player.getPackets().sendIComponentSettings(335, 35, 0, 27, 1026);
//
//	}
//
//	public boolean isTrading() {
//		return target != null;
//	}
//
//	public void setTradeModified(boolean modified) {
//		if (modified == tradeModified)
//			return;
//		tradeModified = modified;
//		sendTradeModified();
//	}
//
//	public void sendInterItems() {
//		player.getPackets().sendItems(90, items);
//		target.getPackets().sendItems(90, true, items);
//	}
//
//	public void refresh(int... slots) {
//		player.getPackets().sendUpdateItems(90, items, slots);
//		target.getPackets().sendUpdateItems(90, true, items.getItems(), slots);
//	}
//
//	public void accept(boolean firstStage) {
//		synchronized (this) {
//			if (!isTrading())
//				return;
//			synchronized (target.getTrade()) {
//				if (target.getTrade().accepted) {
//					if (firstStage) {
//						if (nextStage())
//							target.getTrade().nextStage();
//					} else {
//						player.setCloseInterfacesEvent(null);
//						player.closeInterfaces();
//						closeTrade(CloseTradeStage.DONE);
//					}
//					return;
//				}
//				accepted = true;
//				refreshBothStageMessage(firstStage);
//			}
//		}
//	}
//
//	public void sendValue(int slot, boolean traders) {
//		if (!isTrading())
//			return;
//		Item item = traders ? target.getTrade().items.get(slot) : items
//				.get(slot);
//		if (item == null)
//			return;
//		if (!ItemConstants.isTradeable(item) && item.getId() != 995) {
//			player.getPackets().sendGameMessage("That item isn't tradeable.");
//			return;
//		}
//		int price = item.getDefinitions().getGEPrice();
//		player.getPackets().sendGameMessage(
//				item.getDefinitions().getName() + ": market price is " + price
//						+ " coins.");
//	}
//
//	public void sendValue(int slot) {
//		Item item = player.getInventory().getItem(slot);
//		if (item == null)
//			return;
//		if (!ItemConstants.isTradeable(item)) {
//			player.getPackets().sendGameMessage("That item isn't tradeable.");
//			return;
//		}
//		int price = item.getDefinitions().getGEPrice();
//		player.getPackets().sendGameMessage(
//				item.getDefinitions().getName() + ": market price is " + price
//						+ " coins.");
//	}
//
//	public void sendExamine(int slot, boolean traders) {
//		if (!isTrading())
//			return;
//		Item item = traders ? target.getTrade().items.get(slot) : items
//				.get(slot);
//		if (item == null)
//			return;
//		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
//	}
//
//	public boolean nextStage() {
//		if (!isTrading())
//			return false;
//		if (player.getInventory().getItems().getUsedSlots()
//				+ target.getTrade().items.getUsedSlots() > 28) {
//			player.setCloseInterfacesEvent(null);
//			player.closeInterfaces();
//			closeTrade(CloseTradeStage.NO_SPACE);
//			return false;
//		}
//		accepted = false;
//		player.getInterfaceManager().sendInterface(334);
//		player.getInterfaceManager().closeInventoryInterface();
//		player.getPackets().sendHideIComponent(334, 55,
//				!(tradeModified || target.getTrade().tradeModified));
//		refreshBothStageMessage(false);
//		return true;
//	}
//
//	public void refreshBothStageMessage(boolean firstStage) {
//		refreshStageMessage(firstStage);
//		target.getTrade().refreshStageMessage(firstStage);
//	}
//
//	public void refreshStageMessage(boolean firstStage) {
//		player.getPackets().sendIComponentText(firstStage ? 335 : 334,
//				firstStage ? 39 : 34, getAcceptMessage(firstStage));
//	}
//
//	public String getAcceptMessage(boolean firstStage) {
//		if (accepted)
//			return "Waiting for other player...";
//		if (target.getTrade().accepted)
//			return "Other player has accepted.";
//		return firstStage ? "" : "Are you sure you want to make this trade?";
//	}
//
//	public void sendTradeModified() {
//		player.getPackets().sendConfig(1042, tradeModified ? 1 : 0);
//		target.getPackets().sendConfig(1043, tradeModified ? 1 : 0);
//	}
//
//	public void refreshTradeWealth() {
//		int wealth = tradeWealth();
//		player.getPackets().sendGlobalConfig(729, wealth);
//		target.getPackets().sendGlobalConfig(697, wealth);
//	}
//
//	public void refreshFreeInventorySlots() {
//		int freeSlots = player.getInventory().getFreeSlots();
//		target.getPackets().sendIComponentText(
//				335,
//				23,
//				"has " + (freeSlots == 0 ? "no" : freeSlots) + " free"
//						+ "<br>inventory slots");
//	}
//
//	public int tradeWealth() {
//		int wealth = 0;
//		for (Item item : items.getItems()) {
//			if (item == null)
//				continue;
//			wealth += item.getDefinitions().getGEPrice() * item.getAmount();
//		}
//		return wealth;
//	}
//
//	private static enum CloseTradeStage {
//		CANCEL, NO_SPACE, DONE
//	}
//
//	public void returnItem() {
//		Player t = World.getPlayerByIndex(player.getTrade().borrowedFrom);
//		if (player.getTrade().borrowing) {
//			player.getTrade().removeAllBorrowedItems(player);
//			t.getPackets().sendGameMessage(
//					"An item you had lent out has been returned to you.");
//			t.getTrade().lending = false;
//			player.getTrade().borrowing = false;
//		}
//		if (player.getTrade().isLending()) {
//			Player lT = World.getPlayerByIndex(t.getTrade().borrowedFrom);
//			t.getPackets()
//					.sendGameMessage(
//							lT.getDisplayName()
//									+ " has logged out, and their item has been returned to them.");
//			t.getTrade().removeAllBorrowedItems(t);
//			t.getTrade().borrowing = false;
//			player.getTrade().lending = false;
//		}
//	}
//
//	public void removeAllBorrowedItems(Player p) {
//		for (int i = 0; i < LENT_IDS.length; i++) {
//			p.getBank().removeItem(LENT_IDS[i]);
//			p.getInventory().deleteItem(LENT_IDS[i], 1);
//		}
//		p.getAppearence().generateAppearenceData();
//	}
//
//	public void closeTrade(CloseTradeStage stage) {
//		synchronized (this) {
//			synchronized (target.getTrade()) {
//				Player oldTarget = target;
//				target = null;
//				tradeModified = false;
//				accepted = false;
//				if (CloseTradeStage.DONE != stage) {
//					player.getInventory().getItems().addAll(items);
//					player.getInventory().init();
//					items.clear();
//				} else {
//					write(oldTarget);
//					if (player.getTrade().lendItems[0].getId() != -1) {
//						if (oldTarget.getTrade().borrowing) {
//							oldTarget
//									.getPackets()
//									.sendGameMessage(
//											"You're already borrowing an item, please return it before borrowing another.");
//							player.getPackets()
//									.sendGameMessage(
//											oldTarget.getDisplayName()
//													+ " is already borrowing an item, they cannot borrow another.");
//							player.getInventory().addItem(
//									player.getTrade().lendItems[0].getId(), 1);
//							oldTarget.getInventory().addItem(
//									oldTarget.getTrade().lendItems[0].getId(),
//									1);
//							decline(oldTarget, player);
//							return;
//						}
//						if (player.getTrade().lending) {
//							player.getPackets()
//									.sendGameMessage(
//											"You're already lending an item, please claim it before lending another.");
//							oldTarget.getPackets().sendGameMessage(
//									player.getDisplayName()
//											+ " is already lending an item.");
//							player.getInventory().addItem(
//									player.getTrade().lendItems[0].getId(), 1);
//							oldTarget.getInventory().addItem(
//									oldTarget.getTrade().lendItems[0].getId(),
//									1);
//							decline(player, target);
//							return;
//						}
//						int item = 0;
//						for (int i = 0; i < LENDABLE_ITEMS.length; i++) {
//							if (LENDABLE_ITEMS[i] == player.getTrade().lendItems[0]
//									.getId()) {
//								item = LENT_IDS[i];
//							}
//						}
//						oldTarget.getInventory().addItem(item, 1);
//						oldTarget.getTrade().borrowing = true;
//						oldTarget.getTrade().borrowedFrom = player.getIndex();
//						player.getTrade().lending = true;
//						player.getTrade().lentTo = oldTarget.getIndex();
//						player.getTrade().lentItem = lendItems[0].getId();
//					}
//					if (oldTarget.getTrade().lendItems[0].getId() != -1) {
//						if (player.getTrade().borrowing) {
//							player.getPackets()
//									.sendGameMessage(
//											"You're already borrowing an item, please return it before borrowing another.");
//							oldTarget
//									.getPackets()
//									.sendGameMessage(
//											player.getDisplayName()
//													+ " is already borrowing an item, they cannot borrow another.");
//							player.getInventory().addItem(
//									player.getTrade().lendItems[0].getId(), 1);
//							oldTarget.getInventory().addItem(
//									oldTarget.getTrade().lendItems[0].getId(),
//									1);
//							decline(target, player);
//							return;
//						}
//						if (oldTarget.getTrade().lending) {
//							oldTarget
//									.getPackets()
//									.sendGameMessage(
//											"You're already lending an item, please claim it before lending another.");
//							player.getPackets().sendGameMessage(
//									oldTarget.getDisplayName()
//											+ " is already lending an item.");
//							player.getInventory().addItem(
//									player.getTrade().lendItems[0].getId(), 1);
//							oldTarget.getInventory().addItem(
//									oldTarget.getTrade().lendItems[0].getId(),
//									1);
//							decline(player, oldTarget);
//							return;
//						}
//						int item = 0;
//						for (int i = 0; i < LENDABLE_ITEMS.length; i++) {
//							if (LENDABLE_ITEMS[i] == oldTarget.getTrade().lendItems[0]
//									.getId()) {
//								item = LENT_IDS[i];
//							}
//						}
//						player.getInventory().addItem(item, 1);
//						player.getTrade().borrowing = true;
//						player.getTrade().borrowedFrom = oldTarget.getIndex();
//						oldTarget.getTrade().lending = true;
//						oldTarget.getTrade().lentTo = player.getIndex();
//						oldTarget.getTrade().lentItem = lendItems[0].getId();
//
//						player.getPackets().sendGameMessage("Accepted trade.");
//					}
//					oldTarget.getTrade().lendItems[0].setId(-1);
//					player.getTrade().lendItems[0].setId(-1);
//					player.getInventory().getItems()
//							.addAll(oldTarget.getTrade().items);
//					player.getInventory().init();
//					oldTarget.getTrade().items.clear();
//				}
//				if (oldTarget.getTrade().isTrading()) {
//					oldTarget.setCloseInterfacesEvent(null);
//					oldTarget.closeInterfaces();
//					oldTarget.getTrade().closeTrade(stage);
//					if (CloseTradeStage.CANCEL == stage) {
//						oldTarget.getPackets().sendGameMessage(
//								"<col=ff0000>Other player declined trade!");
//						System.out.println(player.getTrade().lendItems[0]
//								.getId()
//								+ ", "
//								+ oldTarget.getTrade().lendItems[0].getId());
//						if (player.getTrade().lendItems[0].getId() != -1) {
//							player.getInventory().addItem(
//									player.getTrade().lendItems[0].getId(), 1);
//						}
//						if (oldTarget.getTrade().lendItems[0].getId() != -1) {
//							oldTarget.getInventory().addItem(
//									oldTarget.getTrade().lendItems[0].getId(),
//									1);
//						}
//						oldTarget.getTrade().lendItems[0].setId(-1);
//						player.getTrade().lendItems[0].setId(-1);
//					} else if (CloseTradeStage.NO_SPACE == stage) {
//						player.getPackets()
//								.sendGameMessage(
//										"You don't have enough space in your inventory for this trade.");
//						oldTarget
//								.getPackets()
//								.sendGameMessage(
//										"Other player doesn't have enough space in their inventory for this trade.");
//					}
//				}
//			}
//		}
//	}
//
//	public void decline(Player p, Player p2) {
//		p.setCloseInterfacesEvent(null);
//		p2.setCloseInterfacesEvent(null);
//		p.getInventory().getItems().addAll(player.getTrade().items);
//		p2.getInventory().getItems().addAll(p2.getTrade().items);
//		p.closeInterfaces();
//		p2.closeInterfaces();
//		p.getInventory().init();
//		p2.getInventory().init();
//		p.getTrade().items.clear();
//		p2.getTrade().items.clear();
//		p2.getTrade().lendItems[0].setId(-1);
//		p.getTrade().lendItems[0].setId(-1);
//	}
//
//	public Trade write(Player other) {
//		BufferedWriter writer;
//		try {
//			writer = new BufferedWriter(new FileWriter(TRADE_LOG, true));
//			SimpleDateFormat formatter = new SimpleDateFormat(
//					"'['dd-MM-yyyy']['hh:mm:ss a']'");
//			Calendar calendar = Calendar.getInstance();
//			writer.write(player.getDisplayName() + " is trading with "
//					+ other.getDisplayName() + " at "
//					+ formatter.format(calendar.getTime()));
//			writer.newLine();
//			for (Item item : other.getTrade().items.getItems()) {
//				if (item == null)
//					continue;
//				writer.write(other.getDisplayName() + " - Item ID: "
//						+ item.getId() + " Item Amount: " + item.getAmount());
//			}
//			for (Item item : player.getTrade().items.getItems()) {
//				if (item == null)
//					continue;
//				writer.write(player.getDisplayName() + " - Item ID: "
//						+ item.getId() + " Item Amount: " + item.getAmount());
//			}
//			writer.newLine();
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return this;
//	}
//
//	private String TRADE_LOG = "./data/logs/trade.txt";
//
//	public void addItem(Item item) {
//		int amount = item.getAmount();
//		synchronized (this) {
//			if (!isTrading())
//				return;
//			synchronized (target.getTrade()) {
//				if (!ItemConstants.isTradeable(item)) {
//					player.getPackets().sendGameMessage(
//							"That item isn't tradeable.");
//					return;
//				}
//				if (item.getDefinitions().isDestroyItem()) {
//					player.getPackets().sendGameMessage(
//							"That item isn't tradeable.");
//					return;
//				}
//				Item[] itemsBefore = items.getItemsCopy();
//				int maxAmount = player.getInventory().getItems()
//						.getNumberOf(item.getId());
//				if (amount < maxAmount) {
//					item = new Item(item.getId(), amount);
//				} else {
//					item = new Item(item.getId(), maxAmount);
//				}
//				items.add(item);
//				refreshItems(itemsBefore);
//				cancelAccepted();
//			}
//		}
//	}
//
//}
