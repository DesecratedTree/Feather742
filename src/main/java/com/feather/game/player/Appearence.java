package com.feather.game.player;

import java.io.Serializable;
import java.util.Arrays;

import com.feather.cache.parser.ClientScriptMap;
import com.feather.cache.parser.ItemDefinitions;
import com.feather.cache.parser.ItemsEquipIds;
import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.World;
import com.feather.game.item.Item;
import com.feather.io.OutputStream;
import com.feather.utils.Utils;

public class Appearence implements Serializable {

	/**
	 * The serial UID
	 */
	private static final long serialVersionUID = 7655608569741626586L;

	/**
	 * The emote at which the player is rendered at
	 */
	private transient int renderEmote;
	/**
	 * The player's title
	 */
	private int title;
	/**
	 * The player's body looks.
	 */
	private int[] bodyStyle;
	/**
	 * The cosmetic items
	 */
	private Item[] cosmeticItems;
	/**
	 * The player's body color
	 */
	private byte[] bodyColors;
	/**
	 * If the player's gender is a male
	 */
	private boolean male;
	/**
	 * If the player's eyes glow red
	 */
	private transient boolean glowRed;
	/**
	 * The appearance block
	 */
	private transient byte[] appearanceBlock;
	/**
	 * The encyrpted appearance block
	 */
	private transient byte[] encyrptedAppearanceBlock;
	/**
	 * The NPC the player is transformed into
	 */
	private transient short asNPC;
	/**
	 * If we should skip the character block
	 */
	private transient boolean hidePlayer;
	/**
	 * If we should show the player's skill level rather then combat level
	 */
	private boolean showSkillLevel;
	/**
	 * The player being appearance rendered
	 */
	private transient Player player;

	/**
	 * Constructs a new {@code PlayerAppearance} object
	 */
	public Appearence() {
		male = true;
		renderEmote = -1;
		title = -1;
		resetAppearance();
	}

	/**
	 * Sets the glow red flag
	 * 
	 * @param glowRed
	 *            True or false
	 */
	public void setGlowRed(boolean glowRed) {
		this.glowRed = glowRed;
		loadAppearanceBlock();
	}

	/**
	 * Sets the player
	 * 
	 * @param player
	 *            The player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
		asNPC = -1;
		renderEmote = -1;
		if (bodyStyle == null || cosmeticItems == null)
			resetAppearance();
	}

	/**
	 * Sets the npc mask
	 * 
	 * @param id
	 *            The NPC to set
	 */
	public void asNPC(int id) {
		asNPC = (short) id;
		loadAppearanceBlock();
	}

	/**
	 * Hides and unhides the player
	 */
	public void switchHidden() {
		hidePlayer = !hidePlayer;
		loadAppearanceBlock();
	}

	/**
	 * If this player is hidden
	 * 
	 * @return True if hidden; false otherwise
	 */
	public boolean isHidden() {
		return hidePlayer;
	}

	/**
	 * If this player's eyes glow red
	 * 
	 * @return True if so; false otherwise
	 */
	public boolean isGlowRed() {
		return glowRed;
	}

	/**
	 * Loads this player's appearance to a buffer and is sent to the client
	 * within a packet containing information on how this player should be
	 * viewed as graphically
	 */
	public void loadAppearanceBlock() {
		OutputStream stream = new OutputStream();
		writeFlags(stream);
		/**
		 * If there is no title we skip the title block
		 */
		if (title != 0)
			writeTitle(stream);
		/**
		 * Writes the skull of this player
		 */
		writeSkull(stream);
		/**
		 * If there is no NPC we skip the NPC wrap block
		 */
		if (asNPC >= 0) {
			writeNPCData(stream);
			/**
			 * Instead we write the player's equipment
			 */
		} else {
			writeEquipment(stream);
			writeEquipmentAppearence(stream);
		}
		/**
		 * Writing the player's body, username, and landscape flags (pvp,
		 * non-pvp)
		 */
		writeBodyColors(stream);
		writeCharacter(stream);
		writeLandscapeFlags(stream);
		/**
		 * If there is an NPC to write then we will write it's cached data
		 */
		writeCachedNPCFlags(stream);
		/**
		 * Saving the appearance buffer
		 */
		byte[] appeareanceData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, appeareanceData, 0,
				appeareanceData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(appeareanceData);
		this.appearanceBlock = appeareanceData;
		encyrptedAppearanceBlock = md5Hash;
	}

	/**
	 * Returns the size of this player
	 * 
	 * @return The size
	 */
	public int getSize() {
		if (asNPC >= 0)
			return NPCDefinitions.getNPCDefinitions(asNPC).size;
		return 1;
	}

	/**
	 * Sets the render emote of this player
	 * 
	 * @param id
	 *            The id of the render emote to set
	 */
	public void setRenderEmote(int id) {
		this.renderEmote = id;
		loadAppearanceBlock();
	}

	/**
	 * Returns the render emote of this player, or if the player has an NPC
	 * appearance then we return the NPC's render
	 * 
	 * @return The render emote
	 */
	public int getRenderEmote() {
		if (renderEmote >= 0)
			return renderEmote;
		if (asNPC >= 0)
			return NPCDefinitions.getNPCDefinitions(asNPC).renderEmote;
		return player.getEquipment().getWeaponRenderEmote();
	}

	/**
	 * Resets the appearance flags
	 */
	public void resetAppearance() {
		bodyStyle = new int[7];
		bodyColors = new byte[10];
		if (cosmeticItems == null)
			cosmeticItems = new Item[14];
		setMale();
	}

	/**
	 * Writes the NPC flags if there is an NPC buffer to write
	 * 
	 * @param stream
	 *            The stream to write data on
	 */
	private void writeCachedNPCFlags(OutputStream stream) {
		stream.writeByte(asNPC >= 0 ? 1 : 0);
		if (asNPC >= 0) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(asNPC);
			/**
			 * Unknown NPC variables are written to the client ensuring the NPC
			 * we are appearing as
			 */
			stream.writeShort(defs.anInt876);
			stream.writeShort(defs.anInt842);
			stream.writeShort(defs.anInt884);
			stream.writeShort(defs.anInt875);
			stream.writeByte(defs.anInt875);
		}
	}

	/**
	 * Writing the landscape flags, (non PVP or PVP)
	 * 
	 * @param stream
	 *            The stream to write data on
	 */
	private void writeLandscapeFlags(OutputStream stream) {
		boolean pvpArea = World.isPvpArea(player);
		stream.writeByte(pvpArea ? player.getSkills().getCombatLevel() : player
				.getSkills().getCombatLevelWithSummoning());
		stream.writeByte(pvpArea ? player.getSkills()
				.getCombatLevelWithSummoning() : 0);
		stream.writeByte(-1);
	}

	/**
	 * Writes the body colors of the player
	 * 
	 * @param stream
	 *            The stream to write data on
	 */
	private void writeBodyColors(OutputStream stream) {
		for (int index = 0; index < bodyColors.length; index++)
			stream.writeByte(bodyColors[index]);
	}

	/**
	 * Writes the character render and display name to the stream
	 * 
	 * @param stream
	 *            The stream to write data on
	 */
	private void writeCharacter(OutputStream stream) {
		stream.writeShort(getRenderEmote());
		stream.writeString(player.getDisplayName());
		if (showSkillLevel)
			stream.writeShort(player.getSkills().getTotalLevel());
	}

	/**
	 * Writes the player's equipment appearance
	 * 
	 * @param stream
	 *            The stream to write data on
	 */
	private void writeEquipmentAppearence(OutputStream stream) {
		/**
		 * Writes the chest data
		 */
		Item item = player.getEquipment().getItems().get(Equipment.SLOT_CHEST);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_CHEST] != null)
				item = cosmeticItems[Equipment.SLOT_CHEST];
		}
		stream.writeShort(item == null ? 0x100 + bodyStyle[2] : 32768 + item
				.getEquipId());
		/**
		 * Writes the shield data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_SHIELD);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_SHIELD] != null)
				item = cosmeticItems[Equipment.SLOT_SHIELD];
		}
		if (item == null)
			stream.writeByte(0);
		else
			stream.writeShort(32768 + item.getEquipId());
		/**
		 * Writes ANOTHER set of chest data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_CHEST);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_CHEST] != null)
				item = cosmeticItems[Equipment.SLOT_CHEST];
		}
		if (item == null || !Equipment.hideArms(item))
			stream.writeShort(0x100 + bodyStyle[3]);
		else
			stream.writeByte(0);
		/**
		 * Writes the leg data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_LEGS);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_LEGS] != null)
				item = cosmeticItems[Equipment.SLOT_LEGS];
		}
		stream.writeShort(glowRed ? 32768 + ItemsEquipIds.getEquipId(2908)
				: item == null ? 0x100 + bodyStyle[5] : 32768 + item
						.getEquipId());
		/**
		 * Writes the hat, mask, and helmet data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_HAT);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_HAT] != null)
				item = cosmeticItems[Equipment.SLOT_HAT];
		}
		if (!glowRed && (item == null || !Equipment.hideHair(item)))
			stream.writeShort(0x100 + bodyStyle[0]);
		else
			stream.writeByte(0);
		/**
		 * Writes the glove data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_HANDS);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_HANDS] != null)
				item = cosmeticItems[Equipment.SLOT_HANDS];
		}
		stream.writeShort(glowRed ? 32768 + ItemsEquipIds.getEquipId(2912)
				: item == null ? 0x100 + bodyStyle[4] : 32768 + item
						.getEquipId());
		/**
		 * Writes the boot data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_FEET);
		if (item != null) {
			if (cosmeticItems[Equipment.SLOT_FEET] != null)
				item = cosmeticItems[Equipment.SLOT_FEET];
		}
		stream.writeShort(glowRed ? 32768 + ItemsEquipIds.getEquipId(2904)
				: item == null ? 0x100 + bodyStyle[6] : 32768 + item
						.getEquipId());
		/**
		 * Writes a new set of chest data
		 */
		item = player.getEquipment().getItems()
				.get(male ? Equipment.SLOT_HAT : Equipment.SLOT_CHEST);
		if (item != null) {
			if (cosmeticItems[male ? Equipment.SLOT_HAT : Equipment.SLOT_CHEST] != null)
				item = cosmeticItems[male ? Equipment.SLOT_HAT
						: Equipment.SLOT_CHEST];
		}
		if (item == null || (male && Equipment.showBear(item)))
			stream.writeShort(0x100 + bodyStyle[1]);
		else
			stream.writeByte(0);
		/**
		 * Writes the aura data
		 */
		item = player.getEquipment().getItems().get(Equipment.SLOT_AURA);
		// you can't have a cosmetic aura lmao
		if (item == null)
			stream.writeByte(0);
		else
			stream.writeShort(32768 + item.getEquipId());
		int pos = stream.getOffset();
		stream.writeShort(0);
		int flag = 0;
		int slotFlag = -1;
		/**
		 * Writes extra equipment data
		 */
		for (int slotId = 0; slotId < player.getEquipment().getItems()
				.getSize(); slotId++) {
			if (Equipment.DISABLED_SLOTS[slotId] != 0)
				continue;
			slotFlag++;
			/**
			 * Extra hat data
			 */
			if (slotId == Equipment.SLOT_HAT) {
				if (!needsHatModelUpdate())
					continue;
				/**
				 * Indicate that we are editing hat flags
				 */
				flag |= 1 << slotFlag;
				/**
				 * Write the data to the stream, (this includes colors and
				 * textures)
				 */
				writeHatModelData(stream, player.getEquipment().getHatId());
			} else if (slotId == Equipment.SLOT_CAPE) {
				/**
				 * Extra cape data
				 */
				if (!needsCapeModelUpdate())
					continue;
				/**
				 * Indicating that we are editing the cape flags
				 */
				flag |= 1 << slotFlag;
				/**
				 * Write the data to the stream, (this includes colors and
				 * textures)
				 */
				writeCapeModelData(stream, player.getEquipment().getCapeId());
			} else if (slotId == Equipment.SLOT_AURA) {
				/**
				 * Extra aura data
				 */
				if (!needsAuraModelUpdate())
					continue;
				/**
				 * Indicated that we are editing the cape flags
				 */
				flag |= 1 << slotFlag;
				/**
				 * Write the data to the stream, (this includes colors and
				 * textures)
				 */
				writeAuraModelData(stream, player.getEquipment().getAuraId());
			} else if (slotId == Equipment.SLOT_WEAPON) {
				/**
				 * Extra aura data
				 */
				if (!needsWeaponModelUpdate())
					continue;
				/**
				 * Indicated that we are editing the cape flags
				 */
				flag |= 1 << slotFlag;
				/**
				 * Write the data to the stream, (this includes colors and
				 * textures)
				 */
				writeWeaponModelData(stream, player.getEquipment()
						.getWeaponId());
			}
		}
		/**
		 * Write the slot flag
		 */
		int pos2 = stream.getOffset();
		stream.setOffset(pos);
		stream.writeShort(flag);
		stream.setOffset(pos2);
	}

	/**
	 * If the cape needs a model update
	 * 
	 * @return True if so; false otherwise
	 */
	private boolean needsCapeModelUpdate() {
		int capeId = player.getEquipment().getCapeId();
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(capeId);
		if (capeId != 20767 && capeId != 20769 && capeId != 20771
				&& capeId != 20708)
			return false;
//		if (capeId == 20708
//				&& Arrays
//						.equals(player.getClanCape(), defs.originalModelColors)
//				&& Arrays.equals(player.getClanCapeTexture(),
//						defs.originalTextureColorsInt()))
//			return false;
		else if ((capeId == 20767
				&& Arrays.equals(player.getMaxedCapeCustomized(),
						defs.originalModelColors) || ((capeId == 20769 || capeId == 20771) && Arrays
				.equals(player.getCompletionistCapeCustomized(),
						defs.originalModelColors))))
			return false;
		return true;
	}

	/**
	 * If the aura needs a model update
	 * 
	 * @return True if so; false otherwise
	 */
	private boolean needsAuraModelUpdate() {
		int auraId = player.getEquipment().getAuraId();
		if (auraId == -1 || !player.getAuraManager().isActivated())
			return false;
		ItemDefinitions auraDefs = ItemDefinitions.getItemDefinitions(auraId);
		if (auraDefs.getMaleWornModelId1() == -1
				|| auraDefs.getFemaleWornModelId1() == -1)
			return false;
		return true;
	}

	/**
	 * If the helmet needs a model update
	 * 
	 * @return True if so; false otherwise
	 */
	private boolean needsHatModelUpdate() {
		int hatId = player.getEquipment().getHatId();
		if (hatId != 20768 && hatId != 20770 && hatId != 20772)
			return false;
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(hatId - 1);
		if ((hatId == 20768
				&& Arrays.equals(player.getMaxedCapeCustomized(),
						defs.originalModelColors) || ((hatId == 20770 || hatId == 20772) && Arrays
				.equals(player.getCompletionistCapeCustomized(),
						defs.originalModelColors))))
			return false;
		return true;
	}

	/**
	 * If the equiped weapon needs a model update
	 * 
	 * @return True if so; false otherwise
	 */
	private boolean needsWeaponModelUpdate() {
		int weapon = player.getEquipment().getWeaponId();
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(weapon);
		if (weapon != 20709)
			return false;
//		if (weapon == 20708
//				&& Arrays
//						.equals(player.getClanCape(), defs.originalModelColors)
//				&& Arrays.equals(player.getClanCapeTexture(),
//						defs.originalTextureColorsInt()))
//			return false;
		return true;
	}

	/**
	 * Writes the weapon model data to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 * @param weapon
	 *            The weapon to authiticate
	 */
	private void writeWeaponModelData(OutputStream stream, int weapon) {
		int flag = 0;
		/**
		 * Flag indicated whether we should encode textures or models, 0x4 and
		 * 0x8 is both.
		 */
		flag |= 0x4;
		flag |= 0x8;
		stream.writeByte(flag);
		/**
		 * The slot flags
		 */
		int slotFlag = 0 | 1 << 4 | 2 << 8 | 3 << 12;
		stream.writeShort(slotFlag);
		/**
		 * Encoding the colors
		 */
//		for (int i = 0; i < 4; i++) {
//			stream.writeShort(player.getClanCape()[i]);
//		}
		slotFlag = 0 | 1 << 4;
		stream.writeByte(slotFlag);
		/**
		 * Encoding the textures
		 */
//		for (int i = 0; i < 2; i++) {
//			stream.writeShort(player.getClanCapeTexture()[i]);
//		}
	}

	/**
	 * Writes the aura model data to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 * @param auraId
	 *            The aura to authiticate
	 */
	private void writeAuraModelData(OutputStream stream, int auraId) {
		ItemDefinitions auraDefs = ItemDefinitions.getItemDefinitions(auraId);
		stream.writeByte(0x1);
		int modelId = player.getAuraManager().getAuraModelId();
		stream.writeBigSmart(modelId);
		stream.writeBigSmart(modelId);
		if (auraDefs.getMaleWornModelId2() != -1
				|| auraDefs.getFemaleWornModelId2() != -1) {
			int modelId2 = player.getAuraManager().getAuraModelId2();
			stream.writeBigSmart(modelId2);
			stream.writeBigSmart(modelId2);
		}
	}

	/**
	 * Writes the helmet model data to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 * @param auraId
	 *            The helmet to authiticate
	 */
	private void writeHatModelData(OutputStream stream, int hatId) {
		/**
		 * Modify the color data
		 */
		stream.writeByte(0x4);
		int[] hat = hatId == 20768 ? player.getMaxedCapeCustomized() : player
				.getCompletionistCapeCustomized();
		int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
		stream.writeShort(slots);
		for (int i = 0; i < 4; i++)
			stream.writeShort(hat[i]);
	}

	/**
	 * Writes the cape model data to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 * @param auraId
	 *            The cape to authiticate
	 */
	private void writeCapeModelData(OutputStream stream, int itemId) {
		switch (itemId) {
		case 20708:
			int flag = 0;
			/**
			 * Flag indicated whether we should encode textures or models, 0x4
			 * and 0x8 is both.
			 */
			flag |= 0x4;
			flag |= 0x8;
			stream.writeByte(flag);
			/**
			 * The slot flags
			 */
			int slotFlag = 0 | 1 << 4 | 2 << 8 | 3 << 12;
			stream.writeShort(slotFlag);
			/**
			 * Encoding the colors
			 */
//			for (int i = 0; i < 4; i++) {
//				stream.writeShort(player.getClanCape()[i]);
//			}
			slotFlag = 0 | 1 << 4;
			stream.writeByte(slotFlag);
			/**
			 * Encoding the textures
			 */
//			for (int i = 0; i < 2; i++) {
//				stream.writeShort(player.getClanCapeTexture()[i]);
//			}
			break;
		case 20767:
		case 20769:
		case 20771:
			stream.writeByte(0x4);
			int[] cape = itemId == 20767 ? player.getMaxedCapeCustomized()
					: player.getCompletionistCapeCustomized();
			int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
			stream.writeShort(slots);
			/**
			 * Encoding the colors
			 */
			for (int i = 0; i < 4; i++)
				stream.writeShort(cape[i]);
			break;
		}
	}

	/**
	 * Writes the player's equipment to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 */
	private void writeEquipment(OutputStream stream) {
		for (int index = 0; index < 4; index++) {
			Item item = player.getEquipment().getItems().get(index);
			if (item != null) {
				if (cosmeticItems[index] != null)
					item = cosmeticItems[index];
			}
			if (glowRed) {
				if (index == 0) {
					stream.writeShort(32768 + ItemsEquipIds.getEquipId(2910));
					continue;
				}
				if (index == 1) {
					stream.writeShort(32768 + ItemsEquipIds.getEquipId(14641));
					continue;
				}
			}
			if (item == null)
				stream.writeByte(0);
			else
				stream.writeShort(32768 + item.getEquipId());
		}
	}

	/**
	 * Writes the player's NPC data to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 */
	private void writeNPCData(OutputStream stream) {
		stream.writeShort(-1);
		stream.writeShort(asNPC);
		stream.writeByte(0);
	}

	/**
	 * Writes the player's skull to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 */
	private void writeSkull(OutputStream stream) {
		stream.writeByte(player.hasSkull() ? player.getSkullId() : -1);
		stream.writeByte(player.getPrayer().getPrayerHeadIcon());
		stream.writeByte(hidePlayer ? 1 : 0);
	}

	/**
	 * Writes the player's title to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 */
	private void writeTitle(OutputStream stream) {
		String titleName = ClientScriptMap.getMap(male ? 1093 : 3872)
				.getStringValue(title);
		stream.writeGJString(titleName);
	}

	/**
	 * Writes the player's default flags to the stream
	 * 
	 * @param stream
	 *            The stream to write data to
	 */
	private void writeFlags(OutputStream stream) {
		int flag = 0;
		if (!male)
			/**
			 * Female flag
			 */
			flag |= 0x1;
		if (asNPC >= 0 && NPCDefinitions.getNPCDefinitions(asNPC).aBoolean3190)
			/**
			 * Is NPC flag
			 */
			flag |= 0x2;
		if (showSkillLevel)
			flag |= 0x4;
		if (title != 0)
			/**
			 * Has title flag
			 */
			flag |= title >= 32 && title <= 37 ? 0x80 : 0x40; // after/before
		stream.writeByte(flag);
	}

	/**
	 * Sets the player to a male
	 */
	public void setMale() {
		bodyStyle[0] = 3;
		bodyStyle[1] = 14;
		bodyStyle[2] = 18;
		bodyStyle[3] = 26;
		bodyStyle[4] = 34;
		bodyStyle[5] = 38;
		bodyStyle[6] = 42;

		bodyColors[2] = 16;
		bodyColors[1] = 16;
		bodyColors[0] = 3;
		male = true;
	}

	/**
	 * Sets the player to a female
	 */
	public void female() {
		bodyStyle[0] = 48;
		bodyStyle[1] = 57;
		bodyStyle[2] = 57;
		bodyStyle[3] = 65;
		bodyStyle[4] = 68;
		bodyStyle[5] = 77;
		bodyStyle[6] = 80;
		bodyColors[2] = 16;
		bodyColors[1] = 16;
		bodyColors[0] = 3;
		male = false;
	}

	/**
	 * Returns the loaded appearance block
	 * 
	 * @return The appearance block
	 */
	public byte[] getAppearanceBlock() {
		return appearanceBlock;
	}

	/**
	 * Returns the loaded encrypted appearance block
	 * 
	 * @return The encrypted appearance block
	 */
	public byte[] getEncryptedAppearanceBlock() {
		return encyrptedAppearanceBlock;
	}

	/**
	 * If the player is a male
	 * 
	 * @return True if so; false otherwise
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * Sets the player's body style
	 * 
	 * @param i
	 *            The slot
	 * @param i2
	 *            The style
	 */
	public void setBodyStyle(int i, int i2) {
		bodyStyle[i] = i2;
	}

	/**
	 * Sets the player's body color
	 * 
	 * @param i
	 *            The slot
	 * @param i2
	 *            The color
	 */
	public void setBodyColor(int i, int i2) {
		bodyColors[i] = (byte) i2;
	}

	/**
	 * Sets the player's gender
	 * 
	 * @param male
	 *            If the player is male
	 */
	public void setMale(boolean male) {
		this.male = male;
	}

	/**
	 * Sets the hair style
	 * 
	 * @param i
	 *            The hair style to set
	 */
	public void setHairStyle(int i) {
		bodyStyle[0] = i;
	}

	/**
	 * Sets the player's top style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setTopStyle(int i) {
		bodyStyle[2] = i;
	}

	/**
	 * Returns the player's top style
	 * 
	 * @return The style to set
	 */
	public int getTopStyle() {
		return bodyStyle[2];
	}

	/**
	 * Sets the player's arm style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setArmsStyle(int i) {
		bodyStyle[3] = i;
	}

	/**
	 * Sets the player's wrist style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setWristsStyle(int i) {
		bodyStyle[4] = i;
	}

	/**
	 * Sets the player's leg style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setLegsStyle(int i) {
		bodyStyle[5] = i;
	}

	/**
	 * Sets the player's leg style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setShoeStyle(int i) {
		bodyStyle[6] = i;
	}

	/**
	 * Sets the player's hair style
	 * 
	 * @param i
	 *            The style to set
	 */
	public int getHairStyle() {
		return bodyStyle[0];
	}

	/**
	 * Sets the player's beard style
	 * 
	 * @param i
	 *            The style to set
	 */
	public void setBeardStyle(int i) {
		bodyStyle[1] = i;
	}

	/**
	 * Returns the player's beard style
	 * 
	 * @return The beard style
	 */
	public int getBeardStyle() {
		return bodyStyle[1];
	}

	/**
	 * Sets the player's facial hair style
	 * 
	 * @param i
	 *            The facial hair style to set
	 */
	public void setFacialHair(int i) {
		bodyStyle[1] = i;
	}

	/**
	 * Returns the player's facial hair style
	 * 
	 * @return The facial hair style
	 */
	public int getFacialHair() {
		return bodyStyle[1];
	}

	/**
	 * Sets the player's body color
	 * 
	 * @param color
	 *            The color to set
	 */
	public void setSkinColor(int color) {
		bodyColors[4] = (byte) color;
	}

	/**
	 * Returns the player's skin colors
	 * 
	 * @return The skin colors to set
	 */
	public int getSkinColor() {
		return bodyColors[4];
	}

	/**
	 * Sets the player's hair color
	 * 
	 * @param color
	 *            The color to set
	 */
	public void setHairColor(int color) {
		bodyColors[0] = (byte) color;
	}

	/**
	 * Sets the player's top color
	 * 
	 * @param color
	 *            The color to set
	 */
	public void setTopColor(int color) {
		bodyColors[1] = (byte) color;
	}

	/**
	 * Sets the player's leg color
	 * 
	 * @param color
	 *            The color to set
	 */
	public void setLegsColor(int color) {
		bodyColors[2] = (byte) color;
	}

	/**
	 * Returns the player's hair color
	 * 
	 * @return The hair color
	 */
	public int getHairColor() {
		return bodyColors[0];
	}

	/**
	 * Sets a specified slot as cosmetic
	 * 
	 * @param item
	 *            The cosmetic item
	 * @param slot
	 *            The slot to set
	 */
	public void setCosmetic(Item item, int slot) {
		cosmeticItems[slot] = item;
	}

	/**
	 * Returns the cosmetic item corresponding to the specified slot
	 * 
	 * @param slot
	 *            The slot to get
	 * @return The cosmetic item
	 */
	public Item getCosmeticItem(int slot) {
		return cosmeticItems[slot];
	}

	/**
	 * Clears the cosmetic data
	 */
	public void resetCosmetics() {
		cosmeticItems = new Item[14];
		loadAppearanceBlock();
	}

	/**
	 * Sets the player's title
	 * 
	 * @param title
	 *            The title to set
	 */
	public void setTitle(int title) {
		this.title = title;
		loadAppearanceBlock();
	}

	public void setLooks(short[] look) {
		for (byte i = 0; i < this.bodyStyle.length; i = (byte) (i + 1))
			if (look[i] != -1)
				this.bodyStyle[i] = look[i];
	}

	public void copyColors(short[] colors) {
		for (byte i = 0; i < this.bodyColors.length; i = (byte) (i + 1))
			if (colors[i] != -1)
				this.bodyColors[i] = (byte) colors[i];
	}

	public void print() {
		for (int i = 0; i < bodyStyle.length; i++) {
			System.out.println("look[" + i + " ] = " + bodyStyle[i] + ";");
		}
		for (int i = 0; i < bodyColors.length; i++) {
			System.out.println("colour[" + i + " ] = " + bodyColors[i] + ";");
		}
	}

	/**
	 * Toggles showing skills levels.
	 */
	public void switchShowingSkill() {
		showSkillLevel = !showSkillLevel;
		loadAppearanceBlock();
	}

	/**
	 * If we are showing the skill level as apposed to the combat level
	 * 
	 * @return True if so; false otherwise
	 */
	public boolean isShowSkillLevel() {
		return showSkillLevel;
	}

	/**
	 * Sets if we should show the skill level
	 * 
	 * @param showSkillLevel
	 *            If we should show the skill level
	 */
	public void setShowSkillLevel(boolean showSkillLevel) {
		this.showSkillLevel = showSkillLevel;
	}

	/**
	 * Retruns the title
	 * 
	 * @return The title
	 */
	public int getTitle() {
		return title;
	}
}