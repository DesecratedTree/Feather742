package com.feather.game.player.content;

import java.io.Serializable;

import com.feather.game.player.Player;
import com.feather.utils.Utils;

/**
 * 
 * @author Tyler
 * 
 */
public class MoneyPouch implements Serializable {

	/**
	 * Generated SerialUID
	 */
	private static final long serialVersionUID = -3847090682601697992L;

	private Player player;
	private boolean toggled, full, empty;
	private int money;

	public MoneyPouch(Player player) {
		this.player = player;
		long money = player.getMoneyPouch();
		this.money = (int) money;
	}

	public void refreshPouch(boolean unlock) {
		if (unlock)
			player.getPackets().sendRunScript(5557, 1);
		player.getPackets().sendRunScript(5560, money);
	}

	public boolean willOverflow(int amount) {
		return (getMoney() + amount) < 0;
	}

	/**
	 * Toggles the money pouch.
	 */
	public void toggleMoneyPouch() {
		toggled = !toggled;
	}

	/**
	 * Handle the money pouch.
	 * 
	 * @param banking
	 */
	public void handleMoneyPouch(boolean shopping, boolean banking, boolean withdraw, int removeAmount, int addAmount) {
		int amount = withdraw ? removeAmount : player.getInventory().getNumberOf(995);
		if (willOverflow(amount) && !withdraw && !shopping) {// Overflowed
			player.getPackets().sendGameMessage("You do not have enough room in your money pouch!");
			return;
		}
		if (shopping) {
			if (withdraw) {
				player.getPackets().sendGameMessage(amount == 1 ? "One coin has been removed from your money pouch." : Utils.formatNumber(amount) + " coins have been removed from your money pouch!");
				player.getPackets().sendRunScript(5561, 0, amount);
				removeMoney(amount);
				return;
			}
			player.getPackets().sendGameMessage(Utils.formatNumber(addAmount) + " coins have been added to your money pouch.");
			addMoney(addAmount);
		} else if (withdraw) {
			if (player.getInventory().getNumberOf(995) + amount < 0) {
				player.getPackets().sendGameMessage("Your inventory cannot hold any more of that item!");
				return;
			}
			player.getPackets().sendGameMessage(Utils.formatNumber(amount) + (amount == 1 ? " coin has " : " coins have") + " been removed from your money pouch!");
			player.getPackets().sendRunScript(5560, 0, amount);
			player.getInventory().addItem(995, amount);
			removeMoney(amount);
		} else {
			player.getPackets().sendGameMessage(Utils.formatNumber(amount) + (amount == 1 ? " coin has " : " coins have") + " been added to your money pouch!");
			player.getInventory().deleteItem(995, amount);
			addMoney(amount);
		}
		player.getPackets().sendRunScript(5561, withdraw ? 0 : 1, shopping && withdraw ? amount : shopping ? addAmount : amount);
		refreshPouch(false);
		if (this.getMoney() == Integer.MAX_VALUE) {
			this.setFull(true);
		} else {
			this.setFull(false);
		}
		if (this.getMoney() == 0) {
			this.setEmpty(true);
		} else {
			this.setEmpty(false);
		}
	}

	/**
	 * @return the toggled
	 */
	public boolean isToggled() {
		return toggled;
	}

	/**
	 * @param toggled
	 *            the toggled to set
	 */
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Get the current money.
	 * 
	 * @return money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * Set the players money.
	 * 
	 * @param money
	 */
	public void setMoney(int money) {
		this.money = money;
	}

	/**
	 * Add money
	 * 
	 * @param amount
	 */
	public void addMoney(int amount) {
		this.money += amount;
	}

	/**
	 * Removes money.
	 * 
	 * @param amount
	 */
	public void removeMoney(int amount) {
		this.money -= amount;
	}

	public void sendExamine() {
		player.getPackets().sendGameMessage(money == 1 ? "1 coin is currently held in your money pouch." : Utils.formatNumber(money) + " coins are currently held in your money pouch");
	}

	/**
	 * @return the full
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * @param full
	 *            the full to set
	 */
	public void setFull(boolean full) {
		this.full = full;
	}

	/**
	 * @return the empty
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * @param empty
	 *            the empty to set
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}
