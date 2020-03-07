import javax.swing.SwingUtilities;

public class Main
{
	private static final double DMG_TO_KILL = 0.1;// like every unit has 10 hp
	static Player judea, rome;
	static UI ui;
	
	public static void main(String[] args) {
		initHolds();
		SwingUtilities.invokeLater(() ->
		{
			ui = new UI();
		});
	}
	
	static void initHolds() {
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		pHold.nListen = txt ->
		{
			ui.news.append(txt);
		};
		judea = new Judea();
		// rome = new Rome(rand(100)); //for test purposes
		rome = new Rome(100);
		judea.addHold(new pHold("Jerusalem"));
		rome.addHold(new Hold("Rome"));
		
	}
	
	static int rand(int max) {
		return (int) (Math.random() * max);
	}
	
	public static void Battle(Hold def, Hold atk, int mu, int ru) {
		if (mu + ru <= 0)
			return;
		ui.news.append(
			atk.owner.toString() + " Has Attacked from " + atk.name + " With " + mu
				+ " Soldiers and " + ru + " Rangers, The Enemey Hold: " + def.name);
		def.owner.attacked.add(def);
		// clones for calc purpose
		int acMu = mu, acRu = ru, defMu = def.mu, defRu = def.ru;
		// Stage 1 - Ranged Units shoot on Melee Units,
		// Any Left Power will be put on enemy Range units
		// power calculations
		double defPower = def.ru * def.owner.getMorale() * def.owner.RU_POWER,
			atkPower = ru * atk.owner.getMorale() * atk.owner.RU_POWER,
			defExPower = Math.max((defPower * DMG_TO_KILL) - mu, 0),
			atkExPower = Math.max((atkPower * DMG_TO_KILL) - def.mu, 0);
		// loses calculations
		int atkMULoses = (int) Math.min(defPower * DMG_TO_KILL, mu),
			atkRULoses = (int) Math.min(defExPower * DMG_TO_KILL, ru),
			defMULoses = (int) Math.min(atkPower * DMG_TO_KILL, def.mu),
			defRULoses = (int) Math.min(atkExPower * DMG_TO_KILL, def.ru);
		// apply dmg
		acMu -= atkMULoses;
		acRu -= atkRULoses;
		def.mu -= defMULoses;
		def.ru -= defRULoses;
		// Stage 2 - Melee Units fight,
		// Any left power will put on enemy range units.
		defPower = def.mu * def.owner.getMorale() * def.owner.MU_POWER;
		atkPower = acMu * atk.owner.getMorale() * atk.owner.MU_POWER;
		defExPower = Math.max((defPower * DMG_TO_KILL) - acMu, 0);
		atkExPower = Math.max((atkPower * DMG_TO_KILL) - def.mu, 0);
		// loses calculations
		atkMULoses = (int) Math.min(defPower * DMG_TO_KILL, acMu);
		atkRULoses = (int) Math.min(defExPower * DMG_TO_KILL, acRu);
		defMULoses = (int) Math.min(atkPower * DMG_TO_KILL, def.mu);
		defRULoses = (int) Math.min(atkExPower * DMG_TO_KILL, def.ru);
		// apply dmg
		acMu -= atkMULoses;
		acRu -= atkRULoses;
		def.mu -= defMULoses;
		def.ru -= defRULoses;
		// apply changes in game
		// Morale changes due to the battle
		if ((mu - acMu) + (ru - acRu) < (def.mu - defMu) + (def.ru - defRu)) {
			def.owner.morale -= 0.1;
			atk.owner.morale += 0.2;
		} else {
			def.owner.morale += 0.1;
			atk.owner.morale -= 0.2;
		}
		if (def.mu + def.ru <= 0) {
			// Morale changes due to the conquest
			def.owner.morale -= 0.4;
			atk.owner.morale += 0.3;
			def.changeOwner(atk.owner);// conquest
			// any attacking unit left will pass to the new hold
			def.mu = acMu;
			def.ru = acRu;
			// the soldiers that attacked are removed from the attacking hold
			atk.mu -= mu;
			atk.ru -= ru;
		} else {
			atk.mu = atk.mu - mu + acMu;
			atk.ru = atk.ru - ru + acRu;
		}
	}
}