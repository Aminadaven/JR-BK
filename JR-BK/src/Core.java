import java.util.ArrayList;
import java.util.Iterator;

class Hold
{
	static final double PEO_TAX = 0.25, GROW_RATE = 0.003, MAX_HAP = 1.5,
		MU_UPKEEP = 1.75, RU_UPKEEP = 3.25;
	static final int MU_COST = 20, RU_COST = 45, BAR_COST = 5000,
		RANGE_COST = 15000, ECO_MULT = 3500, HAP_MULT = 25000;
	Player owner;
	int pop, mu, ru;
	double happiness;
	// buildings
	boolean barracks = false, range = false;
	int economy = 0;
	final String name;
	
	public String toString() {
		return name;
	}
	
	public Hold(String name)
	{
		this.name = name;
		pop = 1000;
		mu = 0;
		ru = 0;
		happiness = 0.5;
	}
	
	public void changeOwner(Player newOwner) {
		owner.holds.remove(this);
		if (newOwner instanceof Rome)
			((Rome) newOwner).toAdd.add(this);
		else
			newOwner.addHold((pHold) this);// pHold
	}
	
	void grow() {
		pop = (int) (pop * ((0.5 + happiness) * (1 + GROW_RATE)));
	}
	
	int calcIncome() {
		return (int) (pop * (economy + 1) * PEO_TAX * happiness
			- (mu * MU_UPKEEP + ru * RU_UPKEEP));
	}
	
	void recMU(int sum) {
		if (!barracks)
			return;
		if (pop < sum)
			return;
		if (owner.money < sum * MU_COST)
			return;
		pop -= sum;
		owner.money -= sum * MU_COST;
		mu += sum;
		/*
		 * Main.ui.news
		 * .append(owner.toString() + " Has Recruited " + sum + " Melee Units.\n");
		 */
	}
	
	void recRU(int sum) {
		if (!range)
			return;
		if (pop < sum)
			return;
		if (owner.money < sum * RU_COST)
			return;
		pop -= sum;
		owner.money -= sum * RU_COST;
		ru += sum;
		Main.ui.news
			.append(owner.toString() + " Has Recruited " + sum + " Ranged Units.\n");
	}
	
	int ecoCost() {
		return (economy + 1) * ECO_MULT;
	}
	
	int hapCost() {
		return (int) Math.round(HAP_MULT * happiness);
	}
	
	void buildBar() {
		if (barracks || owner.money < BAR_COST)
			return;
		owner.money -= BAR_COST;
		barracks = true;
		Main.ui.news.append(owner.toString() + " Has Built Barracks!\n");
	}
	
	void buildRange() {
		if (range || owner.money < RANGE_COST)
			return;
		owner.money -= RANGE_COST;
		range = true;
		Main.ui.news.append(owner.toString() + " Has Built Range!\n");
	}
	
	void buildEconomy() {
		if (owner.money < ecoCost())
			return;
		owner.money -= ecoCost();
		economy++;
		Main.ui.news.append(owner.toString() + " Has Built up his Economy to level:"
			+ economy + "\n");
	}
	
	void raiseHappiness() {
		if (happiness >= MAX_HAP || owner.money < hapCost())
			return;
		owner.money -= hapCost();
		happiness += 0.1;
	}
	
	void reinforce(Hold hold, int mu, int ru) {
		if (!hold.owner.equals(owner) || mu > this.mu || ru > this.ru)
			return;
		hold.mu += mu;
		this.mu -= mu;
		hold.ru += ru;
		this.ru -= ru;
	}
	
	void attack(Hold hold, int mu, int ru) {
		if (hold.owner.equals(owner) || mu > this.mu || ru > this.ru) {
			Main.ui.news.append(owner.toString() + " Can't Attack!\n");
			return;
		}
		if (mu + ru <= 0) {
			Main.ui.news.append(owner.toString() + " 0 - Soldiers!\n");
			return;
		}
		Main.ui.news.append(owner.toString() + " is Attacking!\n");
		Main.Battle(hold, this, mu, ru);
	}
}

final class Judea extends Player
{
	public Judea()
	{
		super(4000, 0.7, 6, 9);
	}
	
	public int getPop() {
		Iterator<Hold> it = holds.iterator();
		int pop = 0;
		while (it.hasNext()) {
			pop += it.next().pop;
		}
		return pop;
	}
}

class pHold extends Hold
{
	static NewsListener nListen;
	static PopListener pListen;
	static IncomeListener iListen;
	static MUListener muListen;
	static RUListener ruListen;
	static BarListener barListen;
	static RangeListener rngListen;
	static EcoListener ecoListen;
	static HappinesListener hapListen;
	static MoneyListener mListen;
	
	public pHold(String name)
	{
		super(name);
	}
	
	public pHold(Hold hold)
	{
		super(hold.name);
		this.owner = hold.owner;
		this.pop = hold.pop;
		this.mu = hold.mu;
		this.ru = hold.ru;
		this.barracks = hold.barracks;
		this.range = hold.range;
		this.economy = hold.economy;
		this.happiness = hold.happiness;
	}
	
	public void changeOwner(Player newOwner) {
		owner.holds.remove(this);
		if (newOwner instanceof Rome)
			((Rome) newOwner).toAdd.add((Hold) this);
		else
			newOwner.addHold(this);
	}
	
	void recMU(int sum) {
		super.recMU(sum);
		nListen
			.report(owner.toString() + " Has Recruited " + sum + " Melee Units.\n");
		muListen.update(mu);
		pListen.update(pop);
		iListen.update(calcIncome());
		mListen.update(owner.money);
	}
	
	void recRU(int sum) {
		super.recRU(sum);
		nListen
			.report(owner.toString() + " Has Recruited " + sum + " Ranged Units.\n");
		ruListen.update(ru);
		pListen.update(pop);
		iListen.update(calcIncome());
		mListen.update(owner.money);
	}
	
	void buildBar() {
		super.buildBar();
		nListen.report(owner.toString() + " Has Built Barracks!\n");
		barListen.update(barracks);
		mListen.update(owner.money);
	}
	
	void buildRange() {
		super.buildRange();
		nListen.report(owner.toString() + " Has Built Range!\n");
		rngListen.update(range);
		mListen.update(owner.money);
	}
	
	void buildEconomy() {
		super.buildEconomy();
		nListen.report(owner.toString() + " Has Built up his Economy to level:"
			+ economy + "\n");
		ecoListen.update(economy);
		iListen.update(calcIncome());
		mListen.update(owner.money);
	}
	
	void raiseHappiness() {
		super.raiseHappiness();
		nListen.report("");
		hapListen.update(happiness);
		iListen.update(calcIncome());
		mListen.update(owner.money);
	}
	
	void reinforce(Hold hold, int mu, int ru) {
		super.reinforce(hold, mu, ru);
		nListen.report("");
		muListen.update(mu);
		ruListen.update(ru);
		iListen.update(calcIncome());
	}
	
	void attack(Hold hold, int mu, int ru) {
		if (hold.owner.equals(owner) || mu > this.mu || ru > this.ru) {
			nListen.report(owner.toString() + " Can't Attack!\n");
			return;
		}
		if (mu + ru <= 0) {
			nListen.report(owner.toString() + " 0 - Soldiers!\n");
			return;
		}
		nListen.report(owner.toString() + " is Attacking!\n");
		Main.Battle(hold, this, mu, ru);
		muListen.update(mu);
		ruListen.update(ru);
		iListen.update(calcIncome());
	}
}

abstract class Player
{
	// basic stats
	int money;
	// advanced stats
	double morale;
	// units strength
	final double MU_POWER, RU_POWER, HONOR_COST = 0.07, HONOR_MORALE = 0.056;
	ArrayList<Hold> holds;
	// laws:
	// honor - costs 7% of income, but gives 5.6% more morale
	boolean honor = false;
	ArrayList<Hold> attacked = new ArrayList<Hold>();
	
	public Player(int money, double morale, double MU_Power, double RU_Power)
	{
		this.money = money;
		this.morale = morale;
		this.holds = new ArrayList<>();
		this.MU_POWER = MU_Power;
		this.RU_POWER = RU_Power;
	}
	
	public double getMorale() {
		return honor ? morale * (1 + HONOR_MORALE) : morale;
	}
	
	public void addHold(Hold hold) {
		hold.owner = this;
		holds.add(hold);
	}
	
	void endTurn() {
		Iterator<Hold> it = holds.iterator();
		int income = 0;
		Hold hold;
		while (it.hasNext()) {
			hold = it.next();
			income += hold.calcIncome();
			hold.grow();
		}
		if (honor)
			income = (int) (income * (1 - HONOR_COST));
		money += income;
		attacked.clear();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}

final class Rome extends Player
{
	final int ATTACK_RATE;
	ArrayList<Hold> toAdd = new ArrayList<>();
	
	public Rome(int ATTACK_RATE)
	{
		super(100000, 0.5, 8, 7);
		this.ATTACK_RATE = ATTACK_RATE;
	}
	
	// @Override
	void doTurn() {
		// budget split even among all holds, then invest in building & recruiting
		int rec, buildAB, buildECO, holdBudget = money / holds.size();
		Iterator<Hold> it = holds.iterator();
		Hold hold;
		while (it.hasNext()) {
			rec = 0;
			buildAB = 0;
			buildECO = 0;
			hold = it.next();
			// if the hold was attacked last turn, use all resources to reinforce it,
			// and if not possible, don't waste resources on him
			if (attacked.contains(hold)) {
				if (hold.range)
					hold.recRU(Math.min(hold.pop, holdBudget / Hold.RU_COST));
				else if (hold.barracks)
					hold.recMU(Math.min(hold.pop, holdBudget / Hold.MU_COST));
				else if (holdBudget >= Hold.BAR_COST) {
					hold.buildBar();
					hold.recMU(
						Math.min(hold.pop, (holdBudget - Hold.BAR_COST) / Hold.MU_COST));
				} else
					continue;
			} else {
				if (hold.barracks && hold.range) {
					buildECO = (int) (0.4 * holdBudget);
					rec = (int) (0.6 * holdBudget);
				} else if (hold.barracks || hold.range) {
					buildAB = (int) (0.2 * holdBudget);
					buildECO = (int) (0.35 * holdBudget);
					rec = (int) (0.45 * holdBudget);
				} else {
					buildAB = (int) (0.4 * holdBudget);
					buildECO = (int) (0.28 * holdBudget);
					rec = (int) (0.32 * holdBudget);
				}
				if (buildAB > 0) {
					if (!hold.barracks && buildAB >= Hold.BAR_COST)
						hold.buildBar();
					if (!hold.range && buildAB >= Hold.RANGE_COST)
						hold.buildRange();
				}
				if (Math.random() > 0.5) {
					if (buildECO >= hold.ecoCost())
						hold.buildEconomy();
				} else if (buildECO >= hold.hapCost())
					hold.raiseHappiness();
				if (Math.random() > 0.5)
					hold.recMU(Math.min(hold.pop, rec / Hold.MU_COST));
				else
					hold.recRU(Math.min(hold.pop, rec / Hold.RU_COST));
			}
			if (!attacked.contains(hold)) {
				if (!attacked.isEmpty()) {
					hold.reinforce(attacked.get(Main.rand(attacked.size())),
						Main.rand(hold.mu), Main.rand(hold.ru));
				}
				if (Main.rand(100) >= ATTACK_RATE)
					hold.attack(Main.judea.holds.get(Main.rand(Main.judea.holds.size())),
						Main.rand(hold.mu), Main.rand(hold.ru));
			}
		}
		endTurn();
	}
	
	// for testing only purposes
	void rush() {
		// budget split even among all holds, then invest in building & recruiting
		int rec, buildAB, holdBudget = money / holds.size();
		Main.ui.news.append(toString() + " holdBudget=" + holdBudget + "\n");
		Iterator<Hold> it = holds.iterator();
		Hold hold;
		while (it.hasNext()) {
			rec = 0;
			buildAB = 0;
			hold = it.next();
			// budget splitting
			if (hold.barracks && hold.range) {
				rec = holdBudget;
			} else if (hold.barracks || hold.range) {
				buildAB = (int) (0.4 * holdBudget);
				rec = (int) (0.6 * holdBudget);
			} else {
				buildAB = holdBudget;
			}
			Main.ui.news
				.append(toString() + " buildAB=" + buildAB + ", rec=" + rec + "\n");
			if (buildAB > 0) {
				if (!hold.barracks && buildAB >= Hold.BAR_COST) {
					hold.buildBar();
				}
				if (!hold.range && buildAB >= Hold.RANGE_COST)
					hold.buildRange();
			}
			if (Math.random() > 0.5)
				hold.recMU(Math.min(hold.pop, rec / Hold.MU_COST));
			else
				hold.recRU(Math.min(hold.pop, rec / Hold.RU_COST));
			if (Main.rand(100) <= ATTACK_RATE) {
				Main.ui.news.append(toString() + " inside attack mechanic \n");
				hold.attack(Main.judea.holds.get(Main.rand(Main.judea.holds.size())),
					Main.rand(hold.mu), Main.rand(hold.ru));
			}
		}
		it = toAdd.iterator();
		while (it.hasNext())
			addHold(it.next());
		endTurn();
	}
}