package com.feather.game.npc.combat;

import java.util.HashMap;

import com.feather.game.Entity;
import com.feather.game.npc.NPC;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();

	@SuppressWarnings("rawtypes")
	public static final void init() {
		try {
			Class[] classes = Utils.getClasses("com.rs.game.npc.combat.impl");
			for (Class c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				Object o = c.newInstance();
				if (!(o instanceof CombatScript))
					continue;
				CombatScript script = (CombatScript) o;
				for (Object key : script.getKeys())
					cachedCombatScripts.put(key, script);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static int specialAttack(final NPC npc, final Entity target) {
		CombatScript script = cachedCombatScripts.get(npc.getId());
		if (script == null) {
			script = cachedCombatScripts.get(npc.getDefinitions().name);
			if (script == null)
				script = DEFAULT_SCRIPT;
		}
		return script.attack(npc, target);
	}
}
