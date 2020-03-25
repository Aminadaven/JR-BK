package com.aminadav_studio.jr.bk.Launch;

import static com.aminadav_studio.jr.bk.Framework.UIComponents.building;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.button;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.ibutton;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.image;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.label;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.mainFrame;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.setHeadless;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.setSize;
import static com.aminadav_studio.jr.bk.Framework.UIComponents.transTA;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.synth.SynthLookAndFeel;

import com.aminadav_studio.jr.bk.Framework.ImageButton;
import com.aminadav_studio.jr.bk.Framework.ImagePanel;
import com.aminadav_studio.jr.bk.Framework.JBuilding;
import com.aminadav_studio.jr.bk.Framework.RecPanel;
import com.aminadav_studio.jr.bk.Framework.SPanel;
import com.aminadav_studio.jr.bk.Interfaces.Builder;
import com.aminadav_studio.jr.bk.Interfaces.IHave;
import com.aminadav_studio.jr.bk.Interfaces.LMoney;
import com.aminadav_studio.jr.bk.Interfaces.LPop;
import com.aminadav_studio.jr.bk.Interfaces.LUnit;

public class UI
{
	JFrame frame = mainFrame("Ó¯„ È‰Â„È: ·¯ ÎÂÎ·‡", 600, 600,
		"images/Jew-color.png");// "images/Icon3.png"
	// Panels:
	SPanel mapP = new SPanel("images/Judea-Map.jpg"), // "images/Israel.jpg"
		navigate = new SPanel("images/static-back4.jpg"),
		status = new SPanel("images/static-back.jpg"), top = new SPanel(),
		holdsP = new SPanel("images/tech.jpg"),
		newsP = new SPanel("images/scroll-back.jpg");
	// JPanel bottom = new JPanel(); // ads panel
	// Layout
	CardLayout cl = new CardLayout();
	JPanel current = new JPanel(cl);
	// News
	JTextArea news = transTA(21, 34);
	// Top - Stats display
	JLabel money = label("Money: " + Main.judea.money),
		morale = label("Morale: " + Main.judea.getMorale()),
		pop = label("Pop: " + ((Judea) Main.judea).getPop());
	// Holds Panel
	Hold selected;
	JLabel hPop, hIncome;
	JBuilding bBar, bRange, bEco;
	ImageButton bHap;
	
	public UI()
	{
		setListeners();
		setMainFrame();
		setMainPanel();
		setStatusPanel();
		setNavigatePanel();
		setTopPanel();
		setNewsPanel();
		setHoldsPanel();
		
		// Frame final settings, display.
		frame.add(current, BorderLayout.CENTER);
		frame.setVisible(true);
	}
	private void setMapPanel() {
	}
	private void setHoldsPanel() {
		// Holds Panel
		holdsP.setLayout(new GridLayout(2, 1));
		JPanel internal = new JPanel();
		internal.setOpaque(false);
		internal.setLayout(new GridLayout(1, 2));
		JComboBox<pHold> holds = new JComboBox<>();
		holds.setModel(new DefaultComboBoxModel<>());
		for (Hold h : Main.judea.holds)
			holds.addItem((pHold) h);
		selected = (pHold) holds.getItemAt(0);
		holds.addItemListener(p ->
		{
			selected = (pHold) p.getItem();
			updateHolds();
		});
		
		internal.add(label("Hold: "));
		internal.add(holds);
		hPop = label("Pop: " + selected.pop);
		internal.add(hPop);
		hIncome = label("Income: " + selected.calcIncome());
		internal.add(hIncome);
		
		bHap = ibutton("images/smiley.png", "images/smileys.png", p ->
		{
			selected.raiseHappiness();
		});
		setSize(bHap, 45, 45);
		internal.add(bHap);
		
		bEco = building("images/farm.png", "images/farmg.png",
			"Lvl: " + selected.economy + "\nCost: " + selected.ecoCost(),
			new Builder()
			{
				@Override
				public boolean canBuild() {
					return selected.ecoCost() >= selected.owner.money;
				}
				
				@Override
				public void build() {
					selected.buildEconomy();
				}
				
				@Override
				public int getCost() {
					return selected.ecoCost();
				}
			});
		setSize(bEco, 70, 70);
		internal.add(bEco);
		bBar = building("images/Barracks.png", "images/˛˛Barracksg.png",
			"Lvl: " + (selected.barracks ? "1" : "0") + "\nCost: " + Hold.BAR_COST,
			new Builder()
			{
				@Override
				public boolean canBuild() {
					return Hold.BAR_COST >= selected.owner.money;
				}
				
				@Override
				public void build() {
					selected.buildBar();
				}
				
				@Override
				public int getCost() {
					return (selected.barracks ? 0 : Hold.BAR_COST);
				}
			});
		setSize(bBar, 100, 100);
		internal.add(bBar);
		bRange = building("images/range.png", "images/rangeg.png",
			"Lvl: " + (selected.range ? "1" : "0") + "\nCost: " + Hold.RANGE_COST,
			new Builder()
			{
				@Override
				public boolean canBuild() {
					return Hold.RANGE_COST >= selected.owner.money;
				}
				
				@Override
				public void build() {
					selected.buildRange();
				}
				
				@Override
				public int getCost() {
					return (selected.range ? 0 : Hold.RANGE_COST);
				}
			});
		setSize(bRange, 100, 100);
		internal.add(bRange);
		setSize(internal, 300, 300);
		
		SPanel armyP = new SPanel("images/Army.jpg");
		armyP.setLayout(new GridLayout(2, 1));
		RecPanel mp, rp;
		mp = new RecPanel("images/Spearman.png", "images/Spearmang.png", "Spearman",
			"The Spearman is the main unit in the Jewish Army."
			+ "\nThey are the frontier warriors who will fight the Roman Leggionaries."
			+ "\nAnd, if they can reach Roman Archers, they will get 30% STR Bonus!",
			Hold.MU_COST, Main.judea.MU_POWER, new IHave() {
				@Override
				public void update(boolean have) {
				}
				@Override
				public boolean have() {
					return selected.barracks;
				}
		}, new LUnit() {
			@Override
			public void update(int sum) {
				selected.recMU(sum);
			}
			@Override
			public int units() {
				return selected.mu;
			}
		});
		setSize(mp, 200, 100);
		armyP.add(mp);
		rp = new RecPanel("images/Slinger.png", "images/Slingerg.png", "Slinger",
			"The Slingers are the ranged unit in the Jewish Army."
			+ "\nThey will help the Spearmen to fight the front from range,"
			+ "\n and then attack the enemey rangers."
			+ "\nBeware! if enemy Leggionares will reach them, they will get 30% STR Bonus!",
			Hold.RU_COST, Main.judea.RU_POWER, new IHave() {
				@Override
				public void update(boolean have) {
				}
				@Override
				public boolean have() {
					return selected.range;
				}
		}, new LUnit() {
			@Override
			public void update(int sum) {
				selected.recRU(sum);
			}
			@Override
			public int units() {
				return selected.ru;
			}
		});
		setSize(rp, 200, 100);
		armyP.add(rp);
		//setSize(armyP.get(), 150, 150);
		
		holdsP.add(internal);
		holdsP.add(armyP.get());
		// Lay out the panel.
		/*
		 * SpringUtilities.makeCompactGrid(holdsP.get(),
		 * 3, 2, //rows, cols
		 * 6, 6, //initX, initY
		 * 6, 6); //xPad, yPad
		 */
	}
	
	private void setNewsPanel() {
		// News Panel
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.WEST, news, 105, SpringLayout.WEST,
			newsP.get());
		layout.putConstraint(SpringLayout.NORTH, news, 65, SpringLayout.NORTH,
			newsP.get());
		newsP.setLayout(layout);
		newsP.get().add(news);
	}
	
	private void setTopPanel() {
		// Top Panel
		top.setLayout(new FlowLayout());
		top.addSPanel(status);
		top.addSPanel(navigate);
		setSize(top.get(), 590, 100);
		top.addTo(frame, BorderLayout.PAGE_START);
	}
	
	private void setNavigatePanel() {
		// Navigate Bar (Panel)
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
			// ((Rome) Main.rome).rush(); //Test Method
			updateStatus();
			if (news.getText().length() > 0)
				cl.show(current, "News"); // only if there are updates
		}));
		// for testing purposes
		navigate.add(button("End Turn X 10", p ->
		{
			news.setText("");
			for (int i = 0; i < 10; i++) {
				Main.judea.endTurn();
				((Rome) Main.rome).doTurn();
				// ((Rome) Main.rome).rush();
				updateStatus();
			}
			cl.show(current, "News");
		}));
		setSize(navigate.get(), 590, 100);
	}
	
	private void setStatusPanel() {
		// Status Panel
		status.add(image("images/coin.png"));
		status.add(money);
		status.add(image("images/ambush.jpg"));
		status.add(morale);
		status.add(image("images/Jerusalem.jpg"));
		status.add(pop);
		
		setSize(status.get(), 590, 45);
	}
	
	private void setMainPanel() {
		// Layout Frames
		current.add(holdsP.get(), "Holds");
		current.add(mapP.get(), "Map");
		current.add(newsP.get(), "News");
		
		setSize(current, 590, 450);
	}
	
	private void setMainFrame() {
		// Main Frame Settings
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
	}
	
	private void setListeners() {
		pHold.nListen = txt ->
		{
			news.append(txt);
		};
		pHold.barListen = have ->
		{
			bBar.setLvl(have ? 1 : 0);
		};
		pHold.ecoListen = (lvl) ->
		{
			bEco.setLvl(lvl);
		};
		pHold.hapListen = (hap, cost) ->
		{
			bHap.setText("Happiness: " + (int) (hap * 100) + "\nCost: " + cost);
		};
		pHold.incListen = txt ->
		{
			hIncome.setText("Income: " + txt);
		};
		pHold.mListen = txt ->
		{
			money.setText("Money: " + txt);
		};
		LPop holdPop = new LPop() {
			@Override
			public void update(int num, int all) {
				hPop.setText("Pop: " + num);
				pop.setText("Pop: " + all);
			}
			
			@Override
			public int getHold() {
				return selected.pop;
			}
		};
		pHold.pListen = holdPop;
		pHold.rngListen = have ->
		{
			bRange.setLvl(have ? 1 : 0);
		};
		pHold.ruListen = new LUnit() {
			@Override
			public void update(int num) {
				selected.ru = num;
			}
			
			@Override
			public int units() {
				return selected.ru;
			}
		};
		pHold.muListen = new LUnit() {
			@Override
			public void update(int num) {
				selected.mu = num;
			}
			
			@Override
			public int units() {
				return selected.mu;
			}
		};
		RecPanel.mListen = new LMoney() {
			@Override
			public void update(int sum) {
				money.setText("Money: " + sum);
			}
			
			@Override
			public int get() {
				return selected.owner.money;
			}
		};
		RecPanel.pListen = holdPop;
	}
	
	void updateHolds() {
		pHold.barListen.update(selected.barracks);
		pHold.rngListen.update(selected.range);
		pHold.ecoListen.update(selected.economy);
		pHold.hapListen.update(selected.happiness, selected.hapCost());
		pHold.incListen.update(selected.calcIncome());
		pHold.muListen.update(selected.mu);
		pHold.ruListen.update(selected.ru);
		pHold.pListen.update(selected.pop, ((Judea) selected.owner).getPop());
		RecPanel.mListen.update(selected.owner.money);
		RecPanel.pListen.update(selected.pop, ((Judea) selected.owner).getPop());
	}
	
	void updateStatus() {
		money.setText("Money: " + Main.judea.money);
		morale.setText("Morale: " + Main.judea.getMorale());
		pop.setText("Pop: " + ((Judea) Main.judea).getPop());
	}
}