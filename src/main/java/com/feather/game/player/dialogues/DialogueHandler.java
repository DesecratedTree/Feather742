package com.feather.game.player.dialogues;

import java.util.HashMap;

import com.feather.game.player.dialogues.administrator.RottenPotato;
import com.feather.game.player.dialogues.alkharid.Camel;
import com.feather.game.player.dialogues.catherby.Hickton;
import com.feather.game.player.dialogues.draynor.Diango;
import com.feather.game.player.dialogues.draynor.Schism;
import com.feather.game.player.dialogues.falador.Emily;
import com.feather.game.player.dialogues.lumbridge.BorderGuard;
import com.feather.game.player.dialogues.lumbridge.Doomsayer;
import com.feather.game.player.dialogues.lumbridge.ExplorerJack;
import com.feather.game.player.dialogues.lumbridge.FatherAereck;
import com.feather.game.player.dialogues.lumbridge.Hans;
import com.feather.game.player.dialogues.lumbridge.LumbridgeCook;
import com.feather.game.player.dialogues.lumbridge.LumbridgeSage;
import com.feather.game.player.dialogues.lumbridge.Musicians;
import com.feather.game.player.dialogues.portsarim.Brian;
import com.feather.game.player.dialogues.portsarim.Gerrant;
import com.feather.game.player.dialogues.portsarim.Grum;
import com.feather.game.player.dialogues.portsarim.Wydin;
import com.feather.game.player.dialogues.tutorial.SirVant;
import com.feather.game.player.dialogues.varrock.Aubury;
import com.feather.game.player.dialogues.varrock.GrandExchangeTutor;
import com.feather.game.player.dialogues.varrock.Iffie;
import com.feather.game.player.dialogues.varrock.Lowe;
import com.feather.game.player.dialogues.varrock.Zaff;
import com.feather.utils.Logger;

public final class DialogueHandler {

	private static final HashMap<Object, Class<Dialogue>> handledDialogues = new HashMap<Object, Class<Dialogue>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Dialogue> value1 = (Class<Dialogue>) Class
					.forName(LevelUp.class.getCanonicalName());
			handledDialogues.put("LevelUp", value1);
			Class<Dialogue> value2 = (Class<Dialogue>) Class
					.forName(ZarosAltar.class.getCanonicalName());
			handledDialogues.put("ZarosAltar", value2);
			Class<Dialogue> value3 = (Class<Dialogue>) Class
					.forName(ClimbNoEmoteStairs.class.getCanonicalName());
			handledDialogues.put("ClimbNoEmoteStairs", value3);
			Class<Dialogue> value4 = (Class<Dialogue>) Class
					.forName(Banker.class.getCanonicalName());
			handledDialogues.put("Banker", value4);
			Class<Dialogue> value5 = (Class<Dialogue>) Class
					.forName(DestroyItemOption.class.getCanonicalName());
			handledDialogues.put("DestroyItemOption", value5);
			Class<Dialogue> value6 = (Class<Dialogue>) Class
					.forName(FremennikShipmaster.class.getCanonicalName());
			handledDialogues.put("FremennikShipmaster", value6);
			Class<Dialogue> value8 = (Class<Dialogue>) Class
					.forName(NexEntrance.class.getCanonicalName());
			handledDialogues.put("NexEntrance", value8);
			Class<Dialogue> value9 = (Class<Dialogue>) Class
					.forName(MagicPortal.class.getCanonicalName());
			handledDialogues.put("MagicPortal", value9);
			Class<Dialogue> value10 = (Class<Dialogue>) Class
					.forName(LunarAltar.class.getCanonicalName());
			handledDialogues.put("LunarAltar", value10);
			Class<Dialogue> value11 = (Class<Dialogue>) Class
					.forName(AncientAltar.class.getCanonicalName());
			handledDialogues.put("AncientAltar", value11);
			// TODO 12 and 13
			Class<Dialogue> value12 = (Class<Dialogue>) Class
					.forName(FletchingD.class.getCanonicalName());
			handledDialogues.put("FletchingD", value12);
			Class<Dialogue> value14 = (Class<Dialogue>) Class
					.forName(RuneScapeGuide.class.getCanonicalName());
			handledDialogues.put("RuneScapeGuide", value14);
			Class<Dialogue> value15 = (Class<Dialogue>) Class
					.forName(SurvivalExpert.class.getCanonicalName());
			handledDialogues.put("SurvivalExpert", value15);
			Class<Dialogue> value16 = (Class<Dialogue>) Class
					.forName(SimpleMessage.class.getCanonicalName());
			handledDialogues.put("SimpleMessage", value16);
			Class<Dialogue> value17 = (Class<Dialogue>) Class
					.forName(ItemMessage.class.getCanonicalName());
			handledDialogues.put("ItemMessage", value17);
			Class<Dialogue> value18 = (Class<Dialogue>) Class
					.forName(ClimbEmoteStairs.class.getCanonicalName());
			handledDialogues.put("ClimbEmoteStairs", value18);
			Class<Dialogue> value19 = (Class<Dialogue>) Class
					.forName(QuestGuide.class.getCanonicalName());
			handledDialogues.put("QuestGuide", value19);
			Class<Dialogue> value20 = (Class<Dialogue>) Class
					.forName(GemCuttingD.class.getCanonicalName());
			handledDialogues.put("GemCuttingD", value20);
			Class<Dialogue> value21 = (Class<Dialogue>) Class
					.forName(CookingD.class.getCanonicalName());
			handledDialogues.put("CookingD", value21);
			Class<Dialogue> value22 = (Class<Dialogue>) Class
					.forName(HerbloreD.class.getCanonicalName());
			handledDialogues.put("HerbloreD", value22);
			Class<Dialogue> value23 = (Class<Dialogue>) Class
					.forName(BarrowsD.class.getCanonicalName());
			handledDialogues.put("BarrowsD", value23);
			Class<Dialogue> value24 = (Class<Dialogue>) Class
					.forName(SmeltingD.class.getCanonicalName());
			handledDialogues.put("SmeltingD", value24);
			Class<Dialogue> value25 = (Class<Dialogue>) Class
					.forName(LeatherCraftingD.class.getCanonicalName());
			handledDialogues.put("LeatherCraftingD", value25);
			Class<Dialogue> value26 = (Class<Dialogue>) Class
					.forName(EnchantedGemDialouge.class.getCanonicalName());
			handledDialogues.put("EnchantedGemDialouge", value26);
			Class<Dialogue> value27 = (Class<Dialogue>) Class
					.forName(ForfeitDialouge.class.getCanonicalName());
			handledDialogues.put("ForfeitDialouge", value27);
			Class<Dialogue> value28 = (Class<Dialogue>) Class
					.forName(Transportation.class.getCanonicalName());
			handledDialogues.put("Transportation", value28);
			Class<Dialogue> value29 = (Class<Dialogue>) Class
					.forName(WildernessDitch.class.getCanonicalName());
			handledDialogues.put("WildernessDitch", value29);
			Class<Dialogue> value30 = (Class<Dialogue>) Class
					.forName(SimpleNPCMessage.class.getCanonicalName());
			handledDialogues.put("SimpleNPCMessage", value30);
			Class<Dialogue> value31 = (Class<Dialogue>) Class
					.forName(Transportation.class.getCanonicalName());
			handledDialogues.put("Transportation", value31);
			Class<Dialogue> value32 = (Class<Dialogue>) Class
					.forName(DTSpectateReq.class.getCanonicalName());
			handledDialogues.put("DTSpectateReq", value32);
			Class<Dialogue> value33 = (Class<Dialogue>) Class
					.forName(StrangeFace.class.getCanonicalName());
			handledDialogues.put("StrangeFace", value33);
			Class<Dialogue> value34 = (Class<Dialogue>) Class
					.forName(AncientEffigiesD.class.getCanonicalName());
			handledDialogues.put("AncientEffigiesD", value34);
			Class<Dialogue> value35 = (Class<Dialogue>) Class
					.forName(DTClaimRewards.class.getCanonicalName());
			handledDialogues.put("DTClaimRewards", value35);
			Class<Dialogue> value36 = (Class<Dialogue>) Class
					.forName(SetSkills.class.getCanonicalName());
			handledDialogues.put("SetSkills", value36);
			Class<Dialogue> value37 = (Class<Dialogue>) Class
					.forName(DismissD.class.getCanonicalName());
			handledDialogues.put("DismissD", value37);
			Class<Dialogue> value38 = (Class<Dialogue>) Class
					.forName(MrEx.class.getCanonicalName());
			handledDialogues.put("MrEx", value38);
			Class<Dialogue> value39 = (Class<Dialogue>) Class
					.forName(MakeOverMage.class.getCanonicalName());
			handledDialogues.put("MakeOverMage", value39);
			Class<Dialogue> value40 = (Class<Dialogue>) Class
					.forName(KaramjaTrip.class.getCanonicalName());
			handledDialogues.put("KaramjaTrip", value40);
			Class<Dialogue> value42 = (Class<Dialogue>) Class
					.forName(DagonHai.class.getCanonicalName());
			handledDialogues.put("DagonHai", value42);
			handledDialogues.put("clan_wars_view", (Class<Dialogue>) Class.forName(ClanWarsViewing.class.getCanonicalName()));
			handledDialogues.put("DiceBag", (Class<Dialogue>) Class.forName(DiceBag.class.getCanonicalName()));
			handledDialogues.put("PartyPete", (Class<Dialogue>) Class.forName(PartyPete.class.getCanonicalName()));
			handledDialogues.put("PartyRoomLever", (Class<Dialogue>) Class.forName(PartyRoomLever.class.getCanonicalName()));
			handledDialogues.put("DrogoDwarf", (Class<Dialogue>) Class.forName(DrogoDwarf.class.getCanonicalName()));
			handledDialogues.put("GeneralStore", (Class<Dialogue>) Class.forName(GeneralStore.class.getCanonicalName()));
			handledDialogues.put("Nurmof", (Class<Dialogue>) Class.forName(Nurmof.class.getCanonicalName()));
			handledDialogues.put("BootDwarf", (Class<Dialogue>) Class.forName(BootDwarf.class.getCanonicalName()));
			handledDialogues.put("MiningGuildDwarf", (Class<Dialogue>) Class.forName(MiningGuildDwarf.class.getCanonicalName()));
			handledDialogues.put("Man", (Class<Dialogue>) Class.forName(Man.class.getCanonicalName()));
			handledDialogues.put("Guildmaster", (Class<Dialogue>) Class.forName(Guildmaster.class.getCanonicalName()));
			handledDialogues.put("Scavvo", (Class<Dialogue>) Class.forName(Scavvo.class.getCanonicalName()));	
			handledDialogues.put("Valaine", (Class<Dialogue>) Class.forName(Valaine.class.getCanonicalName()));	
			handledDialogues.put("Hura", (Class<Dialogue>) Class.forName(Hura.class.getCanonicalName()));
			handledDialogues.put("TzHaarMejJal", (Class<Dialogue>) Class.forName(TzHaarMejJal.class.getCanonicalName()));
			handledDialogues.put("TzHaarMejKah", (Class<Dialogue>) Class.forName(TzHaarMejKah.class.getCanonicalName()));
			handledDialogues.put("LanderD", (Class<Dialogue>) Class.forName(LanderDialouge.class.getCanonicalName()));
			handledDialogues.put("MasterOfFear", (Class<Dialogue>) Class.forName(MasterOfFear.class.getCanonicalName()));
			handledDialogues.put("TokHaarHok", (Class<Dialogue>) Class.forName(TokHaarHok.class.getCanonicalName()));
			handledDialogues.put("NomadThrone", (Class<Dialogue>) Class.forName(NomadThrone.class.getCanonicalName()));
			handledDialogues.put("SimplePlayerMessage", (Class<Dialogue>) Class.forName(SimplePlayerMessage.class.getCanonicalName()));
			handledDialogues.put("BonfireD", (Class<Dialogue>) Class.forName(BonfireD.class.getCanonicalName()));
			handledDialogues.put("MasterChef", (Class<Dialogue>) Class.forName(MasterChef.class.getCanonicalName()));
			handledDialogues.put("FightKilnDialogue", (Class<Dialogue>) Class.forName(FightKilnDialogue.class.getCanonicalName()));
			handledDialogues.put("RewardChest", (Class<Dialogue>) Class.forName(RewardChest.class.getCanonicalName()));
			handledDialogues.put("WizardFinix", (Class<Dialogue>) Class.forName(WizardFinix.class.getCanonicalName()));	
			handledDialogues.put("RunespanPortalD", (Class<Dialogue>) Class.forName(RunespanPortalD.class.getCanonicalName()));
			handledDialogues.put("SorceressGardenNPCs", (Class<Dialogue>) Class.forName(SorceressGardenNPCs.class.getCanonicalName()));
			handledDialogues.put("Marv", (Class<Dialogue>) Class.forName(Marv.class.getCanonicalName()));
			handledDialogues.put("FlamingSkull", (Class<Dialogue>) Class.forName(FlamingSkull.class.getCanonicalName()));
			handledDialogues.put("Hairdresser", (Class<Dialogue>) Class.forName(Hairdresser.class.getCanonicalName()));
			handledDialogues.put("Thessalia", (Class<Dialogue>) Class.forName(Thessalia.class.getCanonicalName()));
			handledDialogues.put("GrilleGoatsD", (Class<Dialogue>) Class.forName(GrilleGoatsDialogue.class.getCanonicalName()));
			Class<Dialogue> value43 = (Class<Dialogue>) Class
					.forName(SirVant.class.getCanonicalName());
			handledDialogues.put("SirVant", value43);
			

			Class<Dialogue> value44 = (Class<Dialogue>) Class
					.forName(LumbridgeCook.class.getCanonicalName());
			handledDialogues.put("LumbridgeCook", value44);
			Class<Dialogue> value45 = (Class<Dialogue>) Class
					.forName(Musicians.class.getCanonicalName());
			handledDialogues.put("Musicians", value45);
			Class<Dialogue> value46 = (Class<Dialogue>) Class
					.forName(Schism.class.getCanonicalName());
			handledDialogues.put("Schism", value46);
			Class<Dialogue> value47 = (Class<Dialogue>) Class
					.forName(BorderGuard.class.getCanonicalName());
			handledDialogues.put("BorderGuard", value47);
			Class<Dialogue> value48 = (Class<Dialogue>) Class
					.forName(Diango.class.getCanonicalName());
			handledDialogues.put("Diango", value48);
			Class<Dialogue> value49 = (Class<Dialogue>) Class
					.forName(ExplorerJack.class.getCanonicalName());
			handledDialogues.put("ExplorerJack", value49);
			Class<Dialogue> value50 = (Class<Dialogue>) Class
					.forName(SpiritTreeDialogue.class.getCanonicalName());
			handledDialogues.put("SpiritTreeDialogue", value50);
			Class<Dialogue> value51 = (Class<Dialogue>) Class
					.forName(MainSpiritTreeDialogue.class.getCanonicalName());
			handledDialogues.put("MainSpiritTreeDialogue", value51);
			Class<Dialogue> value52 = (Class<Dialogue>) Class
					.forName(LumbridgeSage.class.getCanonicalName());
			handledDialogues.put("LumbridgeSage", value52);
			Class<Dialogue> value53 = (Class<Dialogue>) Class
					.forName(Hans.class.getCanonicalName());
			handledDialogues.put("Hans", value53);
			Class<Dialogue> value54 = (Class<Dialogue>) Class
					.forName(Doomsayer.class.getCanonicalName());
			handledDialogues.put("Doomsayer", value54);
			Class<Dialogue> value55 = (Class<Dialogue>) Class
					.forName(GrandExchangeTutor.class.getCanonicalName());
			handledDialogues.put("GrandExchangeTutor", value55);
			Class<Dialogue> value56 = (Class<Dialogue>) Class
					.forName(FatherAereck.class.getCanonicalName());
			handledDialogues.put("FatherAereck", value56);
			Class<Dialogue> value57 = (Class<Dialogue>) Class
					.forName(RottenPotato.class.getCanonicalName());
			handledDialogues.put("RottenPotato", value57);
			Class<Dialogue> value58 = (Class<Dialogue>) Class
					.forName(Emily.class.getCanonicalName());
			handledDialogues.put("Emily", value58);
			Class<Dialogue> value59 = (Class<Dialogue>) Class
					.forName(LendReturn.class.getCanonicalName());
			handledDialogues.put("LendReturn", value59);
			Class<Dialogue> value60 = (Class<Dialogue>) Class
					.forName(Hickton.class.getCanonicalName());
			handledDialogues.put("Hickton", value60);
			Class<Dialogue> value61 = (Class<Dialogue>) Class
					.forName(DiscardLend.class.getCanonicalName());
			handledDialogues.put("DiscardLend", value61);
			Class<Dialogue> value62 = (Class<Dialogue>) Class
					.forName(Camel.class.getCanonicalName());
			handledDialogues.put("Camel", value62);
			Class<Dialogue> value63 = (Class<Dialogue>) Class
					.forName(Brian.class.getCanonicalName());
			handledDialogues.put("Brian", value63);
			Class<Dialogue> value64 = (Class<Dialogue>) Class
					.forName(Grum.class.getCanonicalName());
			handledDialogues.put("Grum", value64);
			Class<Dialogue> value65 = (Class<Dialogue>) Class
					.forName(Gerrant.class.getCanonicalName());
			handledDialogues.put("Gerrant", value65);
			Class<Dialogue> value66 = (Class<Dialogue>) Class
					.forName(Wydin.class.getCanonicalName());
			handledDialogues.put("Wydin", value66);
			Class<Dialogue> value67 = (Class<Dialogue>) Class
					.forName(Aubury.class.getCanonicalName());
			handledDialogues.put("Aubury", value67);
			Class<Dialogue> value68 = (Class<Dialogue>) Class
					.forName(Iffie.class.getCanonicalName());
			handledDialogues.put("Iffie", value68);
			Class<Dialogue> value69 = (Class<Dialogue>) Class
					.forName(Lowe.class.getCanonicalName());
			handledDialogues.put("Lowe", value69);
			Class<Dialogue> value70 = (Class<Dialogue>) Class
					.forName(Zaff.class.getCanonicalName());
			handledDialogues.put("Zaff", value70);
			
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledDialogues.clear();
		init();
	}

	public static final Dialogue getDialogue(Object key) {
		if (key instanceof Dialogue)
			return (Dialogue) key;
		Class<Dialogue> classD = handledDialogues.get(key);
		if (classD == null)
			return null;
		try {
			return classD.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	private DialogueHandler() {

	}
}
