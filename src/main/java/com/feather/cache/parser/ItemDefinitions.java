package com.feather.cache.parser;

import java.util.*;

import com.alex.utils.Constants;
import com.feather.cache.Cache;
import com.feather.game.item.Item;
import com.feather.game.player.Skills;
import com.feather.io.InputStream;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

public final class ItemDefinitions {

    private static final ItemDefinitions[] definitions = new ItemDefinitions[Utils.getItemDefinitionsSize()];

    // Constants for better maintainability
    private static final int DEFAULT_MODEL_ZOOM = 2000;
    private static final int DEFAULT_UNKNOWN_INT9 = 128;
    private static final int DEFAULT_VALUE = 1;
    private static final int DEFAULT_RENDER_ANIM_ID = 1426;
    private static final int SKILL_REQUIREMENT_BASE = 749;
    private static final int MAXED_SKILL_KEY = 277;
    private static final int SPECIAL_BAR_KEY = 686;
    private static final int ATTACK_SPEED_KEY = 14;
    private static final int WHIRLPOOL_HASH_SIZE = 64;

    // Core item properties
    private final int id;
    private boolean loaded;
    private int modelId;
    private String name;

    // Model and display properties
    private int modelZoom;
    private int modelRotationX;
    private int modelRotationY;
    private int modelOffsetX;
    private int modelOffsetY;

    // Item mechanics
    private int stackable;
    private int value;
    private boolean membersOnly;

    // Equipment models
    private int maleEquip1 = -1;
    private int femaleEquip1 = -1;
    private int maleEquip2 = -1;
    private int femaleEquip2 = -1;
    private int maleEquipModelId3 = -1;
    private int femaleEquipModelId3 = -1;

    // Options
    private String[] groundOptions;
    private String[] inventoryOptions;

    // Visual customization
    private int[] originalModelColors;
    private int[] modifiedModelColors;
    private short[] originalTextureColors;
    private short[] modifiedTextureColors;

    // Special item properties
    private boolean unnoted;
    private int certId = -1;
    private int certTemplateId = -1;
    private int lendId = -1;
    private int lendTemplateId = -1;
    private int bindTemplateId = -1;
    private int bindId = -1;
    private int teamId = -1;
    private int equipSlot = -1;
    private int equipType = -1;

    // Stacking information
    private int[] stackIds;
    private int[] stackAmounts;

    // State flags
    private boolean noted;
    private boolean lended;

    // Script data and requirements
    private Map<Integer, Object> clientScriptData;
    private Map<Integer, Integer> skillRequirements;

    // Unknown arrays for future compatibility
    private byte[] unknownArray1;
    private byte[] unknownArray3;
    private byte[] unknownArray6;
    private int[] unknownArray2;
    private int[] unknownArray4;
    private int[] unknownArray5;

    // Various unknown integers - maintaining all original fields
    private int unknownInt1, unknownInt2, unknownInt3, unknownInt4, unknownInt5, unknownInt6;
    private int unknownInt7, unknownInt8, unknownInt9 = DEFAULT_UNKNOWN_INT9;
    private int unknownInt10, unknownInt11, unknownInt12, unknownInt13, unknownInt14;
    private int unknownInt15, unknownInt16, unknownInt17, unknownInt18, unknownInt19;
    private int unknownInt20, unknownInt21, unknownInt22, unknownInt23;

    public ItemDefinitions(int id) {
        this.id = id;
        initializeDefaults();
        loadItemDefinitions();
    }

    /**
     * Gets or creates an item definition for the specified ID.
     */
    public static ItemDefinitions getItemDefinitions(int itemId) {
        if (itemId < 0 || itemId >= definitions.length) {
            //Logger.log("ItemDefinitions", "Invalid item ID: " + itemId + ", using default item 0");
            itemId = 0;
        }

        ItemDefinitions def = definitions[itemId];
        if (def == null) {
            definitions[itemId] = def = new ItemDefinitions(itemId);
        }
        return def;
    }

    /**
     * Clears all cached item definitions to free memory.
     */
    public static void clearItemDefinitions() {
        Arrays.fill(definitions, null);
        Logger.log("ItemDefinitions", "Cleared " + getCachedDefinitionsCount() + " item definitions from memory.");
    }

    /**
     * Gets the total number of cached definitions.
     */
    public static long getCachedDefinitionsCount() {
        return Arrays.stream(definitions).filter(Objects::nonNull).count();
    }

    /**
     * Initializes default values for all properties.
     */
    private void initializeDefaults() {
        name = "null";
        modelZoom = DEFAULT_MODEL_ZOOM;
        value = DEFAULT_VALUE;

        // Initialize default options
        groundOptions = new String[]{null, null, "take", null, null};
        inventoryOptions = new String[]{null, null, null, null, "drop"};
    }

    /**
     * Loads item definition data from the cache.
     */
    private void loadItemDefinitions() {
        try {
            byte[] data = Cache.getStore().getIndexes()[Constants.ITEM_DEFINITIONS_INDEX]
                    .getFile(getArchiveId(), getFileId());

            if (data == null) {
                //Logger.log("ItemDefinitions", "No data found for item " + id);
                return;
            }

            readOpcodeValues(new InputStream(data));

            // Apply transformations based on templates
            applyTransformations();

            loaded = true;
            //Logger.log("ItemDefinitions", "Successfully loaded item definition for ID: " + id);

        } catch (Exception e) {
            Logger.log("ItemDefinitions", "Failed to load item definition for ID: " + id);
        }
    }

    /**
     * Applies note, lend, or bind transformations based on template IDs.
     */
    private void applyTransformations() {
        if (certTemplateId != -1) {
            transformToNote();
        }
        if (lendTemplateId != -1) {
            transformToLend();
        }
        if (bindTemplateId != -1) {
            transformToBind();
        }
    }

    private void transformToNote() {
        ItemDefinitions realItem = getItemDefinitions(certId);
        copyBasicProperties(realItem);
        stackable = 1;
        noted = true;
    }

    private void transformToLend() {
        ItemDefinitions realItem = getItemDefinitions(lendId);
        copyEquipmentProperties(realItem);
        addDiscardOption();
        lended = true;
    }

    private void transformToBind() {
        ItemDefinitions realItem = getItemDefinitions(bindId);
        copyEquipmentProperties(realItem);
        addDiscardOption();
    }

    private void copyBasicProperties(ItemDefinitions source) {
        this.membersOnly = source.membersOnly;
        this.value = source.value;
        this.name = source.name;
    }

    private void copyEquipmentProperties(ItemDefinitions source) {
        copyBasicProperties(source);

        this.originalModelColors = source.originalModelColors;
        this.maleEquipModelId3 = source.maleEquipModelId3;
        this.femaleEquipModelId3 = source.femaleEquipModelId3;
        this.teamId = source.teamId;
        this.value = 0;
        this.groundOptions = source.groundOptions;
        this.maleEquip1 = source.maleEquip1;
        this.maleEquip2 = source.maleEquip2;
        this.femaleEquip1 = source.femaleEquip1;
        this.femaleEquip2 = source.femaleEquip2;
        this.clientScriptData = source.clientScriptData;
        this.equipSlot = source.equipSlot;
        this.equipType = source.equipType;

        // Copy inventory options but preserve original structure
        this.inventoryOptions = new String[5];
        if (source.inventoryOptions != null) {
            System.arraycopy(source.inventoryOptions, 0, this.inventoryOptions, 0,
                    Math.min(4, source.inventoryOptions.length));
        }
    }

    private void addDiscardOption() {
        if (inventoryOptions == null) {
            inventoryOptions = new String[5];
        }
        inventoryOptions[4] = "Discard";
    }

    // File location methods
    private int getArchiveId() {
        return id >>> 8;
    }

    private int getFileId() {
        return id & 0xFF;
    }

    /**
     * Reads and parses opcode values from the input stream.
     */
    private void readOpcodeValues(InputStream stream) {
        int opcode;
        while ((opcode = stream.readUnsignedByte()) != 0) {
            readValues(stream, opcode);
        }
    }

    /**
     * Processes individual opcodes from the data stream - complete implementation.
     */
    private void readValues(InputStream stream, int opcode) {
        try {
            switch (opcode) {
                case 1 -> modelId = stream.readBigSmart();
                case 2 -> name = stream.readString();
                case 4 -> modelZoom = stream.readUnsignedShort();
                case 5 -> modelRotationX = stream.readUnsignedShort();
                case 6 -> modelRotationY = stream.readUnsignedShort();
                case 7 -> modelOffsetX = readSignedOffset(stream);
                case 8 -> modelOffsetY = readSignedOffset(stream);
                case 11 -> stackable = 1;
                case 12 -> value = stream.readInt();
                case 13 -> equipSlot = stream.readUnsignedByte();
                case 14 -> equipType = stream.readUnsignedByte();
                case 16 -> membersOnly = true;
                case 18 -> {
                    int multiStackSize = stream.readUnsignedShort(); // Read but don't store
                }
                case 23 -> maleEquip1 = stream.readBigSmart();
                case 24 -> maleEquip2 = stream.readBigSmart();
                case 25 -> femaleEquip1 = stream.readBigSmart();
                case 26 -> femaleEquip2 = stream.readBigSmart();
                case 27 -> stream.readUnsignedByte(); // Read and discard
                case 40 -> readModelColors(stream);
                case 41 -> readTextureColors(stream);
                case 42 -> readUnknownArray1(stream);
                case 44 -> readUnknownArray3(stream);
                case 45 -> readUnknownArray6(stream);
                case 65 -> unnoted = true;
                case 78 -> maleEquipModelId3 = stream.readBigSmart();
                case 79 -> femaleEquipModelId3 = stream.readBigSmart();
                case 90 -> unknownInt1 = stream.readBigSmart();
                case 91 -> unknownInt2 = stream.readBigSmart();
                case 92 -> unknownInt3 = stream.readBigSmart();
                case 93 -> unknownInt4 = stream.readBigSmart();
                case 95 -> unknownInt5 = stream.readUnsignedShort();
                case 96 -> unknownInt6 = stream.readUnsignedByte();
                case 97 -> certId = stream.readUnsignedShort();
                case 98 -> certTemplateId = stream.readUnsignedShort();
                case 110 -> unknownInt7 = stream.readUnsignedShort();
                case 111 -> unknownInt8 = stream.readUnsignedShort();
                case 112 -> unknownInt9 = stream.readUnsignedShort();
                case 113 -> unknownInt10 = stream.readByte();
                case 114 -> unknownInt11 = stream.readByte() * 5;
                case 115 -> teamId = stream.readUnsignedByte();
                case 121 -> lendId = stream.readUnsignedShort();
                case 122 -> lendTemplateId = stream.readUnsignedShort();
                case 125 -> {
                    unknownInt12 = stream.readByte();
                    unknownInt13 = stream.readByte();
                    unknownInt14 = stream.readByte();
                }
                case 126 -> {
                    unknownInt15 = stream.readByte();
                    unknownInt16 = stream.readByte();
                    unknownInt17 = stream.readByte();
                }
                case 127 -> {
                    unknownInt18 = stream.readUnsignedByte();
                    unknownInt19 = stream.readUnsignedShort();
                }
                case 128, 129 -> {
                    unknownInt20 = stream.readUnsignedByte();
                    unknownInt21 = stream.readUnsignedShort();
                }
                case 130 -> {
                    unknownInt22 = stream.readUnsignedByte();
                    unknownInt23 = stream.readUnsignedShort();
                }
                case 132 -> readUnknownArray2(stream);
                case 134 -> {
                    int unknownValue = stream.readUnsignedByte(); // Read and discard
                }
                case 139 -> bindId = stream.readUnsignedShort();
                case 140 -> bindTemplateId = stream.readUnsignedShort();
                case 142 -> {
                    // Handle both opcode 142 cases
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[0] = stream.readUnsignedShort();
                }
                case 143 -> {
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[1] = stream.readUnsignedShort();
                }
                case 144 -> {
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[2] = stream.readUnsignedShort();
                }
                case 145 -> {
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[3] = stream.readUnsignedShort();
                }
                case 146 -> {
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[4] = stream.readUnsignedShort();
                }
                case 147 -> {
                    if (unknownArray4 == null) {
                        unknownArray4 = new int[6];
                        Arrays.fill(unknownArray4, -1);
                    }
                    unknownArray4[5] = stream.readUnsignedShort();
                }
                case 150 -> {
                    if (unknownArray5 == null) {
                        unknownArray5 = new int[5];
                        Arrays.fill(unknownArray5, -1);
                    }
                    unknownArray5[0] = stream.readUnsignedShort();
                }
                case 151 -> {
                    if (unknownArray5 == null) {
                        unknownArray5 = new int[5];
                        Arrays.fill(unknownArray5, -1);
                    }
                    unknownArray5[1] = stream.readUnsignedShort();
                }
                case 152 -> {
                    if (unknownArray5 == null) {
                        unknownArray5 = new int[5];
                        Arrays.fill(unknownArray5, -1);
                    }
                    unknownArray5[2] = stream.readUnsignedShort();
                }
                case 153 -> {
                    if (unknownArray5 == null) {
                        unknownArray5 = new int[5];
                        Arrays.fill(unknownArray5, -1);
                    }
                    unknownArray5[3] = stream.readUnsignedShort();
                }
                case 154 -> {
                    if (unknownArray5 == null) {
                        unknownArray5 = new int[5];
                        Arrays.fill(unknownArray5, -1);
                    }
                    unknownArray5[4] = stream.readUnsignedShort();
                }
                case 249 -> readClientScriptData(stream);
                default -> {
                    if (opcode >= 30 && opcode < 35) {
                        groundOptions[opcode - 30] = stream.readString();
                    } else if (opcode >= 35 && opcode < 40) {
                        inventoryOptions[opcode - 35] = stream.readString();
                    } else if (opcode >= 100 && opcode < 110) {
                        readStackingData(stream, opcode - 100);
                    } else {
                        throw new RuntimeException("MISSING OPCODE " + opcode + " FOR ITEM " + id);
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("ItemDefinitions", "Error reading opcode " + opcode + " for item " + id);

            throw new RuntimeException("Error reading opcode " + opcode + " for item " + id);
        }
    }

    private int readSignedOffset(InputStream stream) {
        int value = stream.readUnsignedShort();
        return value > 32767 ? value - 65536 : value;
    }

    private void readModelColors(InputStream stream) {
        int length = stream.readUnsignedByte();
        originalModelColors = new int[length];
        modifiedModelColors = new int[length];
        for (int i = 0; i < length; i++) {
            originalModelColors[i] = stream.readUnsignedShort();
            modifiedModelColors[i] = stream.readUnsignedShort();
        }
    }

    private void readTextureColors(InputStream stream) {
        int length = stream.readUnsignedByte();
        originalTextureColors = new short[length];
        modifiedTextureColors = new short[length];
        for (int i = 0; i < length; i++) {
            originalTextureColors[i] = (short) stream.readUnsignedShort();
            modifiedTextureColors[i] = (short) stream.readUnsignedShort();
        }
    }

    private void readUnknownArray1(InputStream stream) {
        int length = stream.readUnsignedByte();
        unknownArray1 = new byte[length];
        for (int i = 0; i < length; i++) {
            unknownArray1[i] = (byte) stream.readByte();
        }
    }

    private void readUnknownArray2(InputStream stream) {
        int length = stream.readUnsignedByte();
        unknownArray2 = new int[length];
        for (int i = 0; i < length; i++) {
            unknownArray2[i] = stream.readUnsignedShort();
        }
    }

    private void readUnknownArray3(InputStream stream) {
        int length = stream.readUnsignedShort();
        int arraySize = 0;
        for (int modifier = length; modifier > 0; modifier >>>= 1) {
            arraySize++;
        }
        unknownArray3 = new byte[arraySize];
        byte offset = 0;
        for (int i = 0; i < arraySize; i++) {
            if ((length & (1 << i)) > 0) {
                unknownArray3[i] = offset;
                offset++;
            } else {
                unknownArray3[i] = -1;
            }
        }
    }

    private void readUnknownArray6(InputStream stream) {
        int value = stream.readUnsignedShort();
        int arraySize = 0;
        for (int temp = value; temp > 0; temp >>>= 1) {
            arraySize++;
        }
        unknownArray6 = new byte[arraySize];
        byte counter = 0;
        for (int i = 0; i < arraySize; i++) {
            if ((value & (1 << i)) > 0) {
                unknownArray6[i] = counter;
                counter++;
            } else {
                unknownArray6[i] = (byte) -1;
            }
        }
    }

    private void readStackingData(InputStream stream, int index) {
        if (stackIds == null) {
            stackIds = new int[10];
            stackAmounts = new int[10];
        }
        stackIds[index] = stream.readUnsignedShort();
        stackAmounts[index] = stream.readUnsignedShort();
    }

    private void readClientScriptData(InputStream stream) {
        int length = stream.readUnsignedByte();
        if (clientScriptData == null) {
            clientScriptData = new HashMap<>(length);
        }

        for (int i = 0; i < length; i++) {
            boolean isString = stream.readUnsignedByte() == 1;
            int key = stream.read24BitInt();
            Object value = isString ? stream.readString() : stream.readInt();
            clientScriptData.put(key, value);
        }
    }

    // Combat and Equipment Stats
    public CombatStats getCombatStats() {
        return new CombatStats();
    }

    public class CombatStats {
        public int getStabAttack() { return getStatValue(0); }
        public int getSlashAttack() { return getStatValue(1); }
        public int getCrushAttack() { return getStatValue(2); }
        public int getMagicAttack() { return getStatValue(3); }
        public int getRangeAttack() { return getStatValue(4); }
        public int getStabDef() { return getStatValue(5); }
        public int getSlashDef() { return getStatValue(6); }
        public int getCrushDef() { return getStatValue(7); }
        public int getMagicDef() { return getStatValue(8); }
        public int getRangeDef() { return getStatValue(9); }
        public int getSummoningDef() { return getStatValue(417); }
        public int getAbsorbMeleeBonus() { return getStatValue(967); }
        public int getAbsorbMageBonus() { return getStatValue(969); }
        public int getAbsorbRangeBonus() { return getStatValue(968); }
        public int getStrengthBonus() { return getStatValue(641) / 10; }
        public int getRangedStrBonus() { return getStatValue(643) / 10; }
        public int getMagicDamage() { return getStatValue(685); }
        public int getPrayerBonus() { return getStatValue(11); }
        public int getAttackSpeed() {
            int speed = getStatValue(ATTACK_SPEED_KEY);
            return speed > 0 ? speed - 1 : 3;
        }

        private int getStatValue(int key) {
            if (clientScriptData == null) return key == ATTACK_SPEED_KEY ? 4 : 0;
            Object value = clientScriptData.get(key);
            return value instanceof Integer ? (Integer) value : (key == ATTACK_SPEED_KEY ? 4 : 0);
        }
    }

    // Maintain original method names for backward compatibility
    public int getStabAttack() { return getCombatStats().getStabAttack(); }
    public int getSlashAttack() { return getCombatStats().getSlashAttack(); }
    public int getCrushAttack() { return getCombatStats().getCrushAttack(); }
    public int getMagicAttack() { return getCombatStats().getMagicAttack(); }
    public int getRangeAttack() { return getCombatStats().getRangeAttack(); }
    public int getStabDef() { return getCombatStats().getStabDef(); }
    public int getSlashDef() { return getCombatStats().getSlashDef(); }
    public int getCrushDef() { return getCombatStats().getCrushDef(); }
    public int getMagicDef() { return getCombatStats().getMagicDef(); }
    public int getRangeDef() { return getCombatStats().getRangeDef(); }
    public int getSummoningDef() { return getCombatStats().getSummoningDef(); }
    public int getAbsorbMeleeBonus() { return getCombatStats().getAbsorbMeleeBonus(); }
    public int getAbsorbMageBonus() { return getCombatStats().getAbsorbMageBonus(); }
    public int getAbsorbRangeBonus() { return getCombatStats().getAbsorbRangeBonus(); }
    public int getStrengthBonus() { return getCombatStats().getStrengthBonus(); }
    public int getRangedStrBonus() { return getCombatStats().getRangedStrBonus(); }
    public int getMagicDamage() { return getCombatStats().getMagicDamage(); }
    public int getPrayerBonus() { return getCombatStats().getPrayerBonus(); }
    public int getAttackSpeed() { return getCombatStats().getAttackSpeed(); }

    // Skill Requirements
    public Map<Integer, Integer> getWearingSkillRequirements() {
        return getSkillRequirements();
    }

    public Map<Integer, Integer> getSkillRequirements() {
        if (clientScriptData == null) return Collections.emptyMap();

        if (skillRequirements == null) {
            skillRequirements = new HashMap<>();

            // Read standard skill requirements
            for (int i = 0; i < 10; i++) {
                Integer skill = (Integer) clientScriptData.get(SKILL_REQUIREMENT_BASE + (i * 2));
                if (skill != null) {
                    Integer level = (Integer) clientScriptData.get(SKILL_REQUIREMENT_BASE + 1 + (i * 2));
                    if (level != null) {
                        skillRequirements.put(skill, level);
                    }
                }
            }

            // Handle special maxed skill requirement
            Integer maxedSkill = (Integer) clientScriptData.get(MAXED_SKILL_KEY);
            if (maxedSkill != null) {
                skillRequirements.put(maxedSkill, id == 19709 ? 120 : 99);
            }

            // Add hardcoded special cases
            addSpecialSkillRequirements();
        }

        return Collections.unmodifiableMap(skillRequirements);
    }

    private void addSpecialSkillRequirements() {
        switch (id) {
            case 7462 -> skillRequirements.put(Skills.DEFENCE, 40);
            case 19784, 22401, 19780 -> { // Korasi items
                skillRequirements.put(Skills.ATTACK, 78);
                skillRequirements.put(Skills.STRENGTH, 78);
            }
            case 20822, 20823, 20824, 20825, 20826 ->
                    skillRequirements.put(Skills.DEFENCE, 99);
        }

        if ("Dragon defender".equals(name)) {
            skillRequirements.put(Skills.ATTACK, 60);
            skillRequirements.put(Skills.DEFENCE, 60);
        }
    }

    // Utility Methods
    public boolean containsOption(String option) {
        return hasOption(option);
    }

    public boolean containsOption(int index, String option) {
        return hasOption(index, option);
    }

    public boolean hasOption(String option) {
        return inventoryOptions != null &&
                Arrays.stream(inventoryOptions).anyMatch(option::equals);
    }

    public boolean hasOption(int index, String option) {
        return inventoryOptions != null &&
                index >= 0 && index < inventoryOptions.length &&
                option.equals(inventoryOptions[index]);
    }

    public boolean isDestroyItem() {
        return hasOption("destroy");
    }

    public boolean isWearItem(boolean male) {
        if (equipSlot < 0) return false; // Assuming Equipment.SLOT_RING is 0 or similar
        if (male) {
            return getMaleWornModelId1() != -1;
        } else {
            return getFemaleWornModelId1() != -1;
        }
    }

    public boolean hasSpecialBar() {
        if (clientScriptData == null) return false;
        Object specialBar = clientScriptData.get(SPECIAL_BAR_KEY);
        return specialBar instanceof Integer && (Integer) specialBar == 1;
    }

    public int getRenderAnimId() {
        if (clientScriptData == null) return DEFAULT_RENDER_ANIM_ID;
        Object animId = clientScriptData.get(644);
        return animId instanceof Integer ? (Integer) animId : DEFAULT_RENDER_ANIM_ID;
    }

    public int getQuestId() {
        if (clientScriptData == null) return -1;
        Object questId = clientScriptData.get(861);
        return questId instanceof Integer ? (Integer) questId : -1;
    }

    public List<Item> getCreateItemRequirements() {
        if (clientScriptData == null) return Collections.emptyList();

        List<Item> items = new ArrayList<>();
        int requiredId = -1;
        int requiredAmount = -1;

        for (Map.Entry<Integer, Object> entry : clientScriptData.entrySet()) {
            int key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String || key < 538 || key > 770) continue;

            if (key % 2 == 0) {
                requiredId = (Integer) value;
            } else {
                requiredAmount = (Integer) value;
            }

            if (requiredId != -1 && requiredAmount != -1) {
                items.add(new Item(requiredId, requiredAmount));
                requiredId = -1;
                requiredAmount = -1;
            }
        }

        return items;
    }

    // Equipment type detection using string matching (maintaining original logic)
    public String getEquipType(Item item) {
        String lowercaseName = item.getDefinitions().name.toLowerCase();

        if (containsAny(lowercaseName, "sword", "dagger", "scimitar", "whip", "spear",
                "mace", "battleaxe", "staff", "hatchet", "pickaxe")) {
            return "wielded in the right hand";
        }
        if (containsAny(lowercaseName, "plate", "body", "robe top", "top", "jacket",
                "tabard", "shirt", "apron", "chest")) {
            return "worn on the torso";
        }
        if (containsAny(lowercaseName, "gloves", "gauntlets", "vambraces")) {
            return "worn on the hands";
        }
        if (lowercaseName.contains("boots")) {
            return "worn on the feet";
        }
        if (containsAny(lowercaseName, "necklace", "amulet")) {
            return "worn on the neck";
        }
        if (containsAny(lowercaseName, "skirt", "kilt", "leggings", "chaps", "pants", "shorts", "legs")) {
            return "worn on the legs";
        }
        if (containsAny(lowercaseName, "helm", "cap", "hood", "coif", "fez", "mask",
                "paint", "visor", "cavalier", "hat")) {
            return "worn on the head";
        }
        if (containsAny(lowercaseName, "shield", "book")) {
            return "held in the left hand";
        }
        if (containsAny(lowercaseName, "2h", "maul", "claws")) {
            return "wielded in both hands";
        }
        if (containsAny(lowercaseName, "cape", "ava's", "cloak", "Cape")) {
            return "worn on the back";
        }
        return "an item";
    }

    public String getItemType(Item item) {
        String lowercaseName = item.getDefinitions().name.toLowerCase();

        if (containsAny(lowercaseName, "sword", "dagger", "scimitar", "maul", "whip",
                "claws", "spear", "mace", "cane", "hasta", "brackish blade", "battleaxe")) {
            return "a melee weapon";
        }
        if (containsAny(lowercaseName, "Staff", "wand")) {
            return "a weapon for mages";
        }
        if (containsAny(lowercaseName, "body", "legs", "robe", "priest", "helm")) {
            return "a piece of apparel";
        }
        if (lowercaseName.contains("shield")) {
            return "a shield";
        }
        if (lowercaseName.contains("hatchet")) {
            return "a hatchet";
        }
        if (containsAny(lowercaseName, "arrow", "bolt", "ball")) {
            return "ammunition for a ranged weapon";
        }
        if (containsAny(lowercaseName, "chinchompa", "dart", "knife", "javelin", "holy water", "bow")) {
            return "a ranged weapon";
        }
        return "an item";
    }

    private boolean containsAny(String text, String... keywords) {
        return Arrays.stream(keywords).anyMatch(text::contains);
    }

    // Equipment type detection using enums (modern approach)
    public enum EquipmentType {
        WEAPON("melee weapon", "wielded"),
        MAGIC_WEAPON("weapon for mages", "wielded"),
        RANGED_WEAPON("ranged weapon", "wielded"),
        AMMUNITION("ammunition", "held"),
        ARMOR("piece of apparel", "worn"),
        SHIELD("shield", "held"),
        TOOL("tool", "wielded"),
        ACCESSORY("accessory", "worn");

        private final String description;
        private final String wearType;

        EquipmentType(String description, String wearType) {
            this.description = description;
            this.wearType = wearType;
        }

        public String getDescription() { return description; }
        public String getWearType() { return wearType; }
    }

    public EquipmentType getEquipmentType() {
        String lowercaseName = name.toLowerCase();

        // Weapon detection
        if (containsAny(lowercaseName, "sword", "dagger", "scimitar", "whip", "spear",
                "mace", "battleaxe", "maul", "claws", "cane", "hasta")) {
            return EquipmentType.WEAPON;
        }

        if (containsAny(lowercaseName, "staff", "wand")) {
            return EquipmentType.MAGIC_WEAPON;
        }

        if (containsAny(lowercaseName, "bow", "chinchompa", "dart", "knife", "javelin")) {
            return EquipmentType.RANGED_WEAPON;
        }

        if (containsAny(lowercaseName, "arrow", "bolt", "ball")) {
            return EquipmentType.AMMUNITION;
        }

        if (containsAny(lowercaseName, "shield", "book", "defender")) {
            return EquipmentType.SHIELD;
        }

        if (containsAny(lowercaseName, "hatchet", "pickaxe")) {
            return EquipmentType.TOOL;
        }

        if (containsAny(lowercaseName, "body", "legs", "helm", "gloves", "boots",
                "cape", "amulet", "necklace", "ring")) {
            return EquipmentType.ARMOR;
        }

        return EquipmentType.ACCESSORY;
    }

    public void toNote() {
        transformToNote();
    }

    public void toLend() {
        transformToLend();
    }

    public void toBind() {
        transformToBind();
    }

    public void setDefaultOptions() {
        groundOptions = new String[]{null, null, "take", null, null};
        inventoryOptions = new String[]{null, null, null, null, "drop"};
    }

    public void setDefaultsVariableValues() {
        initializeDefaults();
    }

    // Getters for all fields (maintaining original method names)
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isLoaded() { return loaded; }
    public boolean isStackable() { return stackable == 1; }
    public boolean isNoted() { return noted; }
    public boolean isLended() { return lended; }
    public boolean isMembersOnly() { return membersOnly; }
    public boolean isWearItem() { return equipSlot != -1; }
    public boolean isOverSized() { return modelZoom > 5000; }

    public int getValue() { return value; }
    public int getEquipSlot() { return equipSlot; }
    public int getEquipType() { return equipType; }
    public int getModelZoom() { return modelZoom; }
    public int getModelOffsetX() { return modelOffsetX; }
    public int getModelOffsetY() { return modelOffsetY; }
    public int getCertId() { return certId; }
    public int getLendId() { return lendId; }

    public int getMaleWornModelId1() { return maleEquip1; }
    public int getMaleWornModelId2() { return maleEquip2; }
    public int getFemaleWornModelId1() { return femaleEquip1; }
    public int getFemaleWornModelId2() { return femaleEquip2; }

    public String[] getInventoryOptions() { return inventoryOptions == null ? null : inventoryOptions.clone(); }
    public String[] getGroundOptions() { return groundOptions == null ? null : groundOptions.clone(); }
    public Map<Integer, Object> getClientScriptData() {
        return clientScriptData == null ? null : Collections.unmodifiableMap(clientScriptData);
    }

    // Additional getters for all fields that were public in original
    public int getModelId() { return modelId; }
    public int getModelRotationX() { return modelRotationX; }
    public int getModelRotationY() { return modelRotationY; }
    public int getStackable() { return stackable; }
    public int getMaleEquip1() { return maleEquip1; }
    public int getFemaleEquip1() { return femaleEquip1; }
    public int getMaleEquip2() { return maleEquip2; }
    public int getFemaleEquip2() { return femaleEquip2; }
    public int[] getOriginalModelColors() { return originalModelColors == null ? null : originalModelColors.clone(); }
    public int[] getModifiedModelColors() { return modifiedModelColors == null ? null : modifiedModelColors.clone(); }
    public short[] getOriginalTextureColors() { return originalTextureColors == null ? null : originalTextureColors.clone(); }
    public short[] getModifiedTextureColors() { return modifiedTextureColors == null ? null : modifiedTextureColors.clone(); }
    public byte[] getUnknownArray1() { return unknownArray1 == null ? null : unknownArray1.clone(); }
    public byte[] getUnknownArray3() { return unknownArray3 == null ? null : unknownArray3.clone(); }
    public int[] getUnknownArray2() { return unknownArray2 == null ? null : unknownArray2.clone(); }
    public boolean isUnnoted() { return unnoted; }
    public int getMaleEquipModelId3() { return maleEquipModelId3; }
    public int getFemaleEquipModelId3() { return femaleEquipModelId3; }
    public int getUnknownInt1() { return unknownInt1; }
    public int getUnknownInt2() { return unknownInt2; }
    public int getUnknownInt3() { return unknownInt3; }
    public int getUnknownInt4() { return unknownInt4; }
    public int getUnknownInt5() { return unknownInt5; }
    public int getUnknownInt6() { return unknownInt6; }
    public int getCertTemplateId() { return certTemplateId; }
    public int[] getStackIds() { return stackIds == null ? null : stackIds.clone(); }
    public int[] getStackAmounts() { return stackAmounts == null ? null : stackAmounts.clone(); }
    public int getUnknownInt7() { return unknownInt7; }
    public int getUnknownInt8() { return unknownInt8; }
    public int getUnknownInt9() { return unknownInt9; }
    public int getUnknownInt10() { return unknownInt10; }
    public int getUnknownInt11() { return unknownInt11; }
    public int getTeamId() { return teamId; }
    public int getLendTemplateId() { return lendTemplateId; }
    public int getUnknownInt12() { return unknownInt12; }
    public int getUnknownInt13() { return unknownInt13; }
    public int getUnknownInt14() { return unknownInt14; }
    public int getUnknownInt15() { return unknownInt15; }
    public int getUnknownInt16() { return unknownInt16; }
    public int getUnknownInt17() { return unknownInt17; }
    public int getUnknownInt18() { return unknownInt18; }
    public int getUnknownInt19() { return unknownInt19; }
    public int getUnknownInt20() { return unknownInt20; }
    public int getUnknownInt21() { return unknownInt21; }
    public int getUnknownInt22() { return unknownInt22; }
    public int getUnknownInt23() { return unknownInt23; }
    public int[] getUnknownArray5() { return unknownArray5 == null ? null : unknownArray5.clone(); }
    public int[] getUnknownArray4() { return unknownArray4 == null ? null : unknownArray4.clone(); }
    public byte[] getUnknownArray6() { return unknownArray6 == null ? null : unknownArray6.clone(); }

    @Override
    public String toString() {
        return String.format("ItemDefinition{id=%d, name='%s', loaded=%s}", id, name, loaded);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDefinitions that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}