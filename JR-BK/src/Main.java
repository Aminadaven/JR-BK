import static Framework.UI.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.synth.SynthLookAndFeel;

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
		judea = new Judea();
		rome = new Rome(rand(100));
		judea.addHold(new Hold("Jerusalem"));
		rome.addHold(new Hold("Rome"));
	}
	
	static int rand(int max) {
		return (int) (Math.random() * max);
	}
	
	public static void Battle(Hold def, Hold atk, int mu, int ru) {
		if (mu + ru <= 0)
			return;
		ui.news.append(atk.owner.getClass().getSimpleName() + " Has Attacked from "
			+ atk.name + " With " + mu + " Soldiers and " + ru
			+ " Rangers, The Enemey Hold: " + def.name);
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
			def.owner.morale -= 0.3;
			atk.owner.morale += 0.3;
		} else {
			def.owner.morale += 0.3;
			atk.owner.morale -= 0.3;
		}
		if (def.mu + def.ru <= 0) {
			// Morale changes due to the conquest
			def.owner.morale -= 0.2;
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

class UI
{
	JFrame frame = mainFrame("מרד יהודי: בר כוכבא", 600, 600, "images/Icon3.png");// new
																																								// JFrame("מרד
																																								// יהודי:
																																								// בר
																																								// כוכבא");
	SPanel mapP = new SPanel("images/Israel.jpg"),
		navigate = new SPanel("images/static-back4.jpg"),
		status = new SPanel("images/static-back.jpg"), top = new SPanel(),
		holdsP = new SPanel("images/tech.jpg"),
		newsP = new SPanel("images/scroll-back.jpg");
	// JPanel bottom = new JPanel(); // ads panel
	CardLayout cl = new CardLayout();
	JPanel current = new JPanel(cl);
	JTextArea news = transTA(21, 34);
	JLabel money = label("Money: " + Main.judea.money),
		morale = label("Morale: " + Main.judea.getMorale()),
		pop = label("Pop: " + ((Judea) Main.judea).getPop());
	
	public UI()
	{
		setHeadless(frame);
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				SynthLookAndFeel laf = new SynthLookAndFeel();
				laf.load(Main.class.getResourceAsStream("GameDesign.xml"), Main.class);
				UIManager.setLookAndFeel(laf);
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e1) {
			}
		}
		frame.setContentPane(new ImagePanel("images/global-background.jpg"));
		frame.setLayout(new BorderLayout());
		
		current.add(holdsP.get(), "Holds");
		current.add(mapP.get(), "Map");
		current.add(newsP.get(), "News");
		
		status.add(image("images/coin.png"));
		status.add(money);
		status.add(image("images/ambush.jpg"));
		status.add(morale);
		status.add(image("images/Jerusalem.jpg"));
		status.add(pop);
		
		setSize(status.get(), 590, 45);
		
		navigate.add(button("Exit", p ->
		{
			System.exit(0);
		}));
		navigate.add(button("Holds", p ->
		{
			cl.show(current, "Holds");
		}));
		navigate.add(button("Map", p ->
		{
			cl.show(current, "Map");
		}));
		navigate.add(button("News", p ->
		{
			cl.show(current, "News");
		}));
		navigate.add(button("End Turn", p ->
		{
			Main.judea.endTurn();
			news.setText("");
			((Rome) Main.rome).doTurn();
			updateStatus();
			cl.show(current, "News");
		}));
		setSize(navigate.get(), 590, 100);
		
		top.setLayout(new FlowLayout());
		top.addSPanel(status);
		top.addSPanel(navigate);
		setSize(top.get(), 590, 100);
		top.addTo(frame, BorderLayout.PAGE_START);
		
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.WEST, news, 105, SpringLayout.WEST,
			newsP.get());
		layout.putConstraint(SpringLayout.NORTH, news, 65, SpringLayout.NORTH,
			newsP.get());
		newsP.setLayout(layout);
		newsP.get().add(news);
		
		current.setSize(590, 450);
		
		frame.add(current, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	void updateStatus() {
		money.setText("Money: " + Main.judea.money);
		morale.setText("Morale: " + Main.judea.getMorale());
		pop.setText("Pop: " + ((Judea) Main.judea).getPop());
	}
}

// not working properly. ceased for now.
/*
 * @SuppressWarnings("serial")
 * class ImageButton extends JButton // really needs improve on drawing text
 * {
 * private Image background;
 * 
 * public ImageButton()
 * {
 * super();
 * try {
 * background = Framework.loadImage("images/static-background.jpg"); // default
 * // is
 * // static-background
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * 
 * public ImageButton(String imageLoc)
 * {
 * super();
 * try {
 * background = Framework.loadImage(imageLoc);
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * 
 * public ImageButton(String txt, String imageLoc)
 * {
 * super(txt);
 * try {
 * background = Framework.loadImage(imageLoc);
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * 
 * @Override
 * protected void paintComponent(Graphics g) {
 * super.paintComponent(g);
 * g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
 * // g.setColor(c);
 * g.drawString(getText(),
 * (getWidth() - g.getFontMetrics().stringWidth(getText())) / 2,
 * (getHeight() + g.getFontMetrics().getHeight()) / 2);
 * }
 * }
 */

@SuppressWarnings("serial")
class ImagePanel extends JPanel
{
	private Image background;
	
	public ImagePanel()
	{
		try {
			background = loadImage("images/static-back.jpg"); // default
																												// is
																												// static-background
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ImagePanel(String imageLoc)
	{
		try {
			background = loadImage(imageLoc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}

class SPanel
{
	private ImagePanel internal;
	private JScrollPane scroller;
	
	public SPanel()
	{
		internal = new ImagePanel();
		scroller = new JScrollPane(internal);
	}
	
	public SPanel(String imageLoc)
	{
		internal = new ImagePanel(imageLoc);
		scroller = new JScrollPane(internal);
	}
	
	public JPanel get() {
		return internal;
	}
	
	void addSPanel(SPanel pane) {
		internal.add(pane.get());
	}
	
	void add(Component comp) {
		internal.add(comp);
	}
	
	void setLayout(LayoutManager manager) {
		internal.setLayout(manager);
	}
	
	void addTo(Container parent, String place) {
		parent.add(scroller, place);
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
	
	// currently unused methods:
	/*
	 * public void removeHold(Hold hold) {
	 * holds.remove(hold);
	 * }
	 * 
	 * public void changeOwner(Hold hold, Player newOwner) {
	 * holds.remove(hold);
	 * newOwner.addHold(hold);
	 * }
	 */
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
}

final class Rome extends Player
{
	final int ATTACK_RATE;
	
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
					if (hold.barracks && buildAB >= Hold.BAR_COST)
						hold.buildBar();
					if (hold.range && buildAB >= Hold.RANGE_COST)
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
		newOwner.addHold(this);
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
	}
	
	void buildRange() {
		if (range || owner.money < RANGE_COST)
			return;
		owner.money -= RANGE_COST;
		range = true;
	}
	
	void buildEconomy() {
		if (owner.money < ecoCost())
			return;
		owner.money -= ecoCost();
		economy++;
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
		if (hold.owner.equals(owner) || mu < this.mu || ru < this.ru)
			return;
		Main.Battle(hold, this, mu, ru);
	}
}