package com.feather.game.player.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.feather.cache.parser.ItemDefinitions;
import com.feather.game.item.Item;
import com.feather.game.player.Player;
import com.feather.utils.ItemExamines;
import com.feather.utils.ItemSetsKeyGenerator;

/**
* Handles shop
* @author Displee (Maffia-RPG)
*
*/

public class Shop {

    public static final int MAIN_STOCK_ITEMS_KEY = ItemSetsKeyGenerator.generateKey();

    private static final int MAX_SHOP_ITEMS = 40;
    public static final int COINS = 995;

    private String name;
    private Item[] mainStock;
    private int[] defaultQuantity;
    public int quantity = 1;
    private Item[] generalStock;
    private int money;
    private CopyOnWriteArrayList<Player> viewingPlayers;
    public boolean isGeneralStore;

    public Shop(String name, int money, Item[] mainStock, boolean isGeneralStore) {
        viewingPlayers = new CopyOnWriteArrayList<Player>();
        this.name = name;
        this.money = money;
        this.mainStock = mainStock;
        this.isGeneralStore = isGeneralStore;
        defaultQuantity = new int[mainStock.length];
        for (int i = 0; i < defaultQuantity.length; i++)
            defaultQuantity[i] = mainStock[i].getAmount();
        if (isGeneralStore && mainStock.length < MAX_SHOP_ITEMS)
            generalStock = new Item[MAX_SHOP_ITEMS - mainStock.length];
    }

    public void addPlayer(final Player player) {
        viewingPlayers.add(player);
        player.getTemporaryAttributtes().put("Shop", this);
        player.setCloseInterfacesEvent(new Runnable() {
            @Override
            public void run() {
                viewingPlayers.remove(player);
                player.getTemporaryAttributtes().remove("Shop");
            }
        });
        player.getPackets().sendConfig(118, MAIN_STOCK_ITEMS_KEY);
        sendStore(player);
        player.getInterfaceManager().sendInterface(1265); // opens shop
        player.getPackets().sendConfig(1496, 555);// unkown
        player.getPackets().sendConfig(532, money);// unlocks money amount in inv
        player.getPackets().sendConfig(1078, 16384);
        player.getPackets().sendIComponentSettings(1265, 20, 0, getStoreSize() * 6, 1150); // unlocks stock slots
        player.getPackets().sendIComponentSettings(1265, 26, 0, getStoreSize() * 6, 82903066); // unlocks drag item to inv
        sendInventory(player);
        quantity = 1;
        player.getPackets().sendIComponentText(1265, 85, name);
        player.getPackets().sendIComponentText(1265, 212, "+10");
        player.getPackets().sendIComponentText(1265, 221, "-10");
    }

    public void sendInventory(Player player) {
        player.getInterfaceManager().sendInventoryInterface(1266);
        player.getPackets().sendItems(93, player.getInventory().getItems());
        player.getPackets().sendUnlockIComponentOptionSlots(1266, 0, 0, 28, 0,
                1, 2, 3, 4, 5);
        player.getPackets().sendInterSetItemsOptionsScript(1266, 0, 93, 4, 7,
                "Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Examine");
    }

    public void buy(Player player, int clickSlot, int quantity) {
        if (clickSlot >= getStoreSize())
            return;
        Item item = clickSlot >= mainStock.length ? generalStock[clickSlot
                - mainStock.length] : mainStock[clickSlot];
        if (item == null)
            return;
        if (item.getAmount() == 0) {
            player.getPackets().sendGameMessage(
                    "There is no stock of that item at the moment.");
            return;
        }
        int dq = clickSlot >= mainStock.length ? 0 : defaultQuantity[clickSlot];
        int price = getBuyPrice(item, dq);
        int moneyPouchAmount = player.getPouch().getMoney();
        int amountCoins = player.getPouch().getMoney() >= price ? player.getPouch().getMoney() : player.getInventory().getItems().getNumberOf(money);
        int maxQuantity = amountCoins / price;
        int buyQ = item.getAmount() > quantity ? quantity : item.getAmount();

        boolean enoughCoins = maxQuantity >= buyQ;

        if (!enoughCoins) {
            player.getPackets().sendGameMessage("You don't have enough coins.");
            buyQ = maxQuantity;
        } else if (quantity > buyQ) {
            player.getPackets().sendGameMessage("The shop has run out of stock.");
            if (item.getDefinitions().isStackable()) {
                if (player.getInventory().getFreeSlots() < 1) {
                    player.getPackets().sendGameMessage("Not enough space in your inventory.");
                    return;
                }
            }
        } else {
            int freeSlots = player.getInventory().getFreeSlots();
            if (!item.getDefinitions().isNoted() && buyQ > freeSlots) {
                buyQ = freeSlots;
                player.getPackets().sendGameMessage("Not enough space in your inventory.");
            }
        }
        if (buyQ != 0) {
			int totalPrice = price * buyQ;
			if (moneyPouchAmount >= totalPrice) {
				player.getPouch().handleMoneyPouch(true, false, true, totalPrice, 0);
				player.getPouch().refreshPouch(false);
				player.getInventory().addItem(item.getId(), buyQ);
				item.setAmount(item.getAmount() - buyQ);
				if (item.getAmount() <= 0 && clickSlot >= mainStock.length)
					generalStock[clickSlot - mainStock.length] = null;
			} else if (moneyPouchAmount < totalPrice) {
				player.getInventory().deleteItem(money, totalPrice);
				player.getInventory().addItem(item.getId(), buyQ);
				item.setAmount(item.getAmount() - buyQ);
				if (item.getAmount() <= 0 && clickSlot >= mainStock.length)
					generalStock[clickSlot - mainStock.length] = null;
			}
			refreshShop();
			sendInventory(player);
		}
    }

    public void restoreItems() {
        boolean needRefresh = false;
        for (int i = 0; i < mainStock.length; i++) {
            if (mainStock[i].getAmount() < defaultQuantity[i]) {
                mainStock[i].setAmount(mainStock[i].getAmount() + 1);
                needRefresh = true;
            } else if (mainStock[i].getAmount() > defaultQuantity[i]) {
                mainStock[i].setAmount(mainStock[i].getAmount() + -1);
                needRefresh = true;
            }
        }
        if (generalStock != null) {
            for (int i = 0; i < generalStock.length; i++) {
                Item item = generalStock[i];
                if (item == null)
                    continue;
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0)
                    generalStock[i] = null;
                needRefresh = true;
            }
        }
        if (needRefresh)
            refreshShop();
    }

    private boolean addItem(int itemId, int quantity) {
        for (Item item : mainStock) {
            if (item.getId() == itemId) {
                item.setAmount(item.getAmount() + quantity);
                refreshShop();
                return true;
            }
        }
        if (generalStock != null) {
            for (Item item : generalStock) {
                if (item == null)
                    continue;
                if (item.getId() == itemId) {
                    item.setAmount(item.getAmount() + quantity);
                    refreshShop();
                    return true;
                }
            }
            for (int i = 0; i < generalStock.length; i++) {
                if (generalStock[i] == null) {
                    generalStock[i] = new Item(itemId, quantity);
                    refreshShop();
                    return true;
                }
            }
        }
        return false;
    }

    public void sell(Player player, int slotId, int quantity) {
        if (player.getInventory().getItemsContainerSize() < slotId)
            return;
        Item item = player.getInventory().getItem(slotId);
        if (item == null)
            return;
        int originalId = item.getId();
        if (item.getDefinitions().isNoted())
            item = new Item(item.getDefinitions().getCertId(), item.getAmount());
        if (item.getDefinitions().isDestroyItem()
                || ItemConstants.getItemDefaultCharges(item.getId()) != -1
                || !ItemConstants.isTradeable(item) || item.getId() == money) {
            player.getPackets().sendGameMessage("You can't sell this item.");
            return;
        }
        int dq = getDefaultQuantity(item.getId());
        if (dq == -1 && generalStock == null) {
            player.getPackets().sendGameMessage(
                    "You can't sell this item to this shop.");
            return;
        }
        double price = getSellPrice(item, dq);
        int numberOff = player.getInventory().getItems().getNumberOf(originalId);
        if (quantity > numberOff)
            quantity = numberOff;
        if (!addItem(item.getId(), quantity)) {
            player.getPackets().sendGameMessage("Shop is currently full.");
            return;
        }
        player.getInventory().deleteItem(originalId, quantity);
        if (player.getPouch().willOverflow((int) (price * quantity))) {
			player.getInventory().addItem(money, (int) (price * quantity));
		} else {
			player.getPouch().handleMoneyPouch(true, false, false, 0, (int) (price * quantity));
		}
    }

    public void sendValue(Player player, int slotId) {
        if (player.getInventory().getItemsContainerSize() < slotId)
            return;
        Item item = player.getInventory().getItem(slotId);
        if (item == null)
            return;
        if (item.getDefinitions().isNoted())
            item = new Item(item.getDefinitions().getCertId(), item.getAmount());
        if (item.getDefinitions().isNoted() || !ItemConstants.isTradeable(item)
                || item.getId() == money) {
            player.getPackets().sendGameMessage("You can't sell this item.");
            return;
        }
        int dq = getDefaultQuantity(item.getId());
        if (dq == -1 && generalStock == null) {
            player.getPackets().sendGameMessage(
                    "You can't sell this item to this shop.");
            return;
        }
        double price = getSellPrice(item, dq);
        if (price < 1) {
        	price = 1;
        }
        player.getPackets().sendGameMessage(
                item.getDefinitions().getName()
                        + ": Shop will buy for: "
                        + (int) price
                        + " "
                        + ItemDefinitions.getItemDefinitions(money).getName()
                                .toLowerCase()
                        + ". Right-click the item to sell.");
    }

    public int getDefaultQuantity(int itemId) {
        for (int i = 0; i < mainStock.length; i++)
            if (mainStock[i].getId() == itemId)
                return defaultQuantity[i];
        return -1;
    }

    public void sendInfo(Player player, int clickSlot) {
        if (player.isBuying) {
            if (clickSlot >= getStoreSize())
                return;
            Item item = clickSlot >= mainStock.length ? generalStock[clickSlot
                - mainStock.length] : mainStock[clickSlot];
            if (item == null)
                return;
            player.getTemporaryAttributtes().put("BuySelectedSlot", clickSlot);
            quantity = 1;
            player.getPackets().sendConfig(2561, MAIN_STOCK_ITEMS_KEY);
            player.getPackets().sendConfig(2562, item.getId());
            player.getPackets().sendConfig(2563, clickSlot);
            player.getPackets().sendConfig(2564, quantity);
            player.getPackets().sendGlobalString(362, ItemExamines.getExamine(item));
            player.getPackets().sendIComponentText(1265, 43, "This is " + item.getDefinitions().getItemType(item) + ".");
            if (isGeneralStore) {
                player.getPackets().sendHideIComponent(1265, 52, false);// generalstore icon
            }
            if (item.getDefinitions().isWearItem()) {
            	player.getPackets().sendGlobalConfig(1876, 0);
                player.getPackets().sendIComponentText(1265, 44, "It is " + item.getDefinitions().getEquipType(item) + ".");
            } else if (!(item.getDefinitions().isWearItem())) {
            	player.getPackets().sendGlobalConfig(1876, -1);
            	player.getPackets().sendIComponentText(1265, 44, "It is an item.");
            }
        } else {
            Item item = player.getInventory().getItem(clickSlot);
            if (item == null)
                return;
            player.getTemporaryAttributtes().put("SellSelectedSlot", clickSlot);
            quantity = 1;
            player.getPackets().sendConfig(2561, MAIN_STOCK_ITEMS_KEY);
            player.getPackets().sendConfig(2562, item.getId());
            player.getPackets().sendConfig(2563, clickSlot);
            player.getPackets().sendConfig(2564, quantity);
            player.getPackets().sendGlobalString(362, ItemExamines.getExamine(item));
            if (isGeneralStore) {
                player.getPackets().sendHideIComponent(1265, 52, false);// generalstore icon
            }
            player.getPackets().sendIComponentText(1265, 43, "This is " + item.getDefinitions().getItemType(item) + ".");
            if (item.getDefinitions().isWearItem()) {
            	player.getPackets().sendGlobalConfig(1876, 0);
                player.getPackets().sendIComponentText(1265, 44, "It is " + item.getDefinitions().getEquipType(item) + ".");
            } else if (!(item.getDefinitions().isWearItem())) {
            	player.getPackets().sendGlobalConfig(1876, -1);
            	player.getPackets().sendIComponentText(1265, 44, "It is an item.");
            }
        }
    }

    public int getBuyPrice(Item item, int dq) {
        int price = item.getDefinitions().getValue();
        if (price == 0) {
            price = 1;
        }
        return price;
    }

    public double getSellPrice(Item item, int dq) {
    	double price = item.getDefinitions().getValue() / 3.33333333333;
    	if (price == 0) {
    		price = 1;
    	}
        return price;
    }

    public void refreshShop() {
        for (Player player : viewingPlayers) {
            sendStore(player);
            player.getPackets().sendIComponentSettings(1265, 20, 0, getStoreSize() * 6, 1150);
        }
    }

    public int getStoreSize() {
        return mainStock.length
                + (generalStock != null ? generalStock.length : 0);
    }

    public void sendStore(Player player) {
        Item[] stock = new Item[mainStock.length
                + (generalStock != null ? generalStock.length : 0)];
        System.arraycopy(mainStock, 0, stock, 0, mainStock.length);
        if (generalStock != null)
            System.arraycopy(generalStock, 0, stock, mainStock.length, generalStock.length);
        player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
    }
    
}