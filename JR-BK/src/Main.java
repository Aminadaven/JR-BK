import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main
{
	private static final double DMG_TO_KILL = 0.1;// like every unit has 10 hp
	private final static ArrayList<Hold> jewishHolds = new ArrayList<>(),
		romanHolds = new ArrayList<>();
	static final Player judea = new Judea(jewishHolds),
		rome = new Rome(romanHolds, rand(100));
	
	public static void main(String[] args) {
		initHolds();
		createUI();
	}
	
	static void createUI() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		
		JFrame frame = new JFrame("המרד הגדול ברומאים - בטא");
		SPanel mapP = new SPanel("images/Israel.jpg"),
			navigate = new SPanel("images/static-back4.jpg"),
			status = new SPanel("images/static-back.jpg"), top = new SPanel(),
			holdsP = new SPanel("images/tech.jpg"),
			newsP = new SPanel("images/scroll-back.jpg");
		CardLayout cl = new CardLayout();
		JPanel current = new JPanel(cl);
		current.add(holdsP.get(), "Holds");
		current.add(mapP.get(), "Map");
		current.add(newsP.get(), "News");
		
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				frame.setShape(
					new Rectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight()));
			}
		});
		frame.setUndecorated(true);
		frame.setContentPane(new ImagePanel("images/global-background.jpg"));
		frame.setLayout(new BorderLayout());
		status.addImage("images/coin.png");
		status.addLabel("Money: " + judea.money);
		status.addImage("images/ambush.jpg");
		status.addLabel("Morale: " + judea.getMorale());
		status.addImage("images/Jerusalem.jpg");
		status.addLabel("Pop: " + ((Judea) judea).getPop());
		navigate.addButton("Exit", p ->
		{
			System.exit(0);
		});
		navigate.addButton("Holds", p ->
		{
			cl.show(current, "Holds");
		});
		navigate.addButton("Map", p ->
		{
			cl.show(current, "Map");
		});
		navigate.addButton("News", p ->
		{
			cl.show(current, "News");
		});
		navigate.setSize(590, 45);
		status.setSize(590, 45);
		top.setLayout(new FlowLayout());
		top.addSPanel(status);
		top.addSPanel(navigate);
		top.setSize(590, 100);
		top.addTo(frame, BorderLayout.PAGE_START);
		current.setSize(590, 450);
		// current.addTo(frame, BorderLayout.CENTER);
		frame.add(current, BorderLayout.CENTER);
		try {
			frame.setIconImage(Framework.loadImage("images/Icon3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static void initHolds() {
		jewishHolds.add(new Hold("Jerusalem"));
		romanHolds.add(new Hold("Rome"));
	}
	
	/*
	 * private static void initPlayers() {
	 * 
	 * }
	 */
	
	static int rand(int max) {
		return (int) (Math.random() * max);
	}
	
	public static void Battle(Hold def, Hold atk, int mu, int ru) {
		def.owner.attacked.add(def);
		int acMu = mu, acRu = ru;
		// Stage 1 - Ranged Units shoot on Melee Units,
		// Any Left Power will be put on enemy Range units
		//power calculations
		double defRangedPower = def.ru * def.owner.getMorale() * def.owner.RU_POWER,
			atkRangedPower = ru * atk.owner.getMorale() * atk.owner.RU_POWER,
			defExPower = Math.max((defRangedPower * DMG_TO_KILL) - mu, 0),
			atkExPower = Math.max((atkRangedPower * DMG_TO_KILL) - def.mu, 0);
		//loses calculations
		int atkMULoses = (int) Math.min(defRangedPower * DMG_TO_KILL, mu),
			atkRULoses = (int) Math.min(defExPower * DMG_TO_KILL, ru),
			defMULoses = (int) Math.min(atkRangedPower * DMG_TO_KILL, def.mu),
			defRULoses = (int) Math.min(atkExPower * DMG_TO_KILL, def.ru);
		//apply dmg
		acMu -= atkMULoses;
		acRu -= atkRULoses;
		def.mu -= defMULoses;
		def.ru -= defRULoses;
		/*
		 * def.loseMU(atkRangedPower);
		 * atk.loseRU(atkRangedPower);
		 * //
		 */
	}
}

class Framework
{
	static Image loadImage(String location) throws IOException {
		return ImageIO.read(Framework.class.getResource(location));
	}
	
	static void setSize(Component comp, int width, int height) {
		Dimension size = new Dimension(width, height);
		comp.setSize(size);
		comp.setPreferredSize(size);
		comp.setMinimumSize(size);
		comp.setMaximumSize(size);
	}
}

@SuppressWarnings("serial")
class ImageButton extends JButton // really needs improve on drawing text
{
	private Image background;
	
	public ImageButton()
	{
		super();
		try {
			background = Framework.loadImage("images/static-background.jpg"); // default
																																				// is
																																				// static-background
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ImageButton(String imageLoc)
	{
		super();
		try {
			background = Framework.loadImage(imageLoc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ImageButton(String txt, String imageLoc)
	{
		super(txt);
		try {
			background = Framework.loadImage(imageLoc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
		// g.setColor(c);
		g.drawString(getText(),
			(getWidth() - g.getFontMetrics().stringWidth(getText())) / 2,
			(getHeight() + g.getFontMetrics().getHeight()) / 2);
	}
}

@SuppressWarnings("serial")
class ImagePanel extends JPanel
{
	private Image background;
	
	public ImagePanel()
	{
		try {
			background = Framework.loadImage("images/static-back.jpg"); // default
																																	// is
																																	// static-background
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ImagePanel(String imageLoc)
	{
		try {
			background = Framework.loadImage(imageLoc);
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
	// private JScrollPane scroller;
	
	public SPanel()
	{
		internal = new ImagePanel();
		// scroller = new JScrollPane(internal);
	}
	
	public JPanel get() {
		return internal;
	}
	
	public SPanel(String imageLoc)
	{
		internal = new ImagePanel(imageLoc);
		// scroller = new JScrollPane(internal);
	}
	
	void addButton(String txt, ActionListener listener) {
		JButton btn = new JButton(txt);
		btn.addActionListener(listener);
		internal.add(btn);
	}
	
	void addImageButton(String txt, ActionListener listener, String location) {
		ImageButton btn = new ImageButton(txt, location);
		btn.addActionListener(listener);
		internal.add(btn);
	}
	
	void addLabel(String txt) {
		internal.add(new JLabel(txt));
	}
	
	void addImage(String location) {
		try {
			JLabel img = new JLabel();
			Framework.setSize(img, 20, 20);
			img.setIcon(new ImageIcon(Framework.loadImage(location)));
			// img.setm
			internal.add(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void addTF(int width, String txt) {
		internal.add(new JTextField(txt, width));
	}
	
	void addSPanel(SPanel pane) {
		internal.add(pane.get());
	}
	
	void setLayout(LayoutManager manager) {
		internal.setLayout(manager);
	}
	
	void setSize(int width, int height) {
		Dimension size = new Dimension(width, height);
		internal.setSize(size);
		internal.setPreferredSize(size);
		internal.setMinimumSize(size);
		internal.setMaximumSize(size);
	}
	
	void addTo(Container parent, String place) {
		parent.add(internal, place);
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
	
	public Player(int money, double morale, ArrayList<Hold> holds,
		double MU_Power, double RU_Power)
	{
		this.money = money;
		this.morale = morale;
		this.holds = holds;
		this.MU_POWER = MU_Power;
		this.RU_POWER = RU_Power;
	}
	
	public double getMorale() {
		return honor ? morale * (1 + HONOR_MORALE) : morale;
	}
	
	// abstract void doTurn();
	
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
	
	public Rome(ArrayList<Hold> holds, int ATTACK_RATE)
	{
		super(100000, 0.5, holds, 8, 7);
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
	public Judea(ArrayList<Hold> holds)
	{
		super(4000, 0.7, holds, 6, 9);
	}
	
	public int getPop() {
		Iterator<Hold> it = holds.iterator();
		int pop = 0;
		while (it.hasNext()) {
			pop += it.next().pop;
		}
		return pop;
	}
	
	// @Override
	/*
	 * void doTurn() {
	 * // TODO Auto-generated method stub
	 * 
	 * }//
	 */
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
	
	void grow() {
		pop = (int) (pop * ((0.5 + happiness) * GROW_RATE));
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