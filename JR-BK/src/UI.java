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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.synth.SynthLookAndFeel;

@SuppressWarnings("serial")
class ImagePanel extends JPanel
{
	private Image background;
	
	public ImagePanel()
	{
		try {
			background = loadImage("images/static-back.jpg"); // default
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
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
		g.drawImage(background.getScaledInstance(getWidth(), getHeight(), 0), 0, 0,
			getWidth(), getHeight(), null);
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

class UI
{
	JFrame frame = mainFrame("îøã éäåãé: áø ëåëáà", 600, 600, "images/Icon3.png");
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
	Hold selected;
	
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
			// ((Rome) Main.rome).doTurn();
			((Rome) Main.rome).rush();
			updateStatus();
			cl.show(current, "News");
		}));
		// for testing purposes
		navigate.add(button("End Turn X 10", p ->
		{
			news.setText("");
			for (int i = 0; i < 10; i++) {
				Main.judea.endTurn();
				// ((Rome) Main.rome).doTurn();
				((Rome) Main.rome).rush();
				updateStatus();
			}
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
		
		setSize(current, 590, 450);
		
		JComboBox<Hold> holds = new JComboBox<>();
		DefaultComboBoxModel<Hold> model = new DefaultComboBoxModel<>();
		holds.setModel(model);
		holds.addItemListener(p->{
			selected =(Hold) p.getItem();
			updateHolds();
		});
		
		for(Hold h:Main.judea.holds) model.addElement((Hold) h);
		selected =(Hold) model.getElementAt(0);
		
		JComponent bBar = building("images/Barracks.png", "images/þþBarracksg.png", p ->
		{
			selected.buildBar();
		});
		JComponent bRange = building("images/range.jpg", "images/Sling-man.jpg", p ->
		{
			selected.buildRange();
		});
		JComponent bEco = building("images/farm2.jpg", "images/Warehouse.png", p ->
		{
			selected.buildEconomy();
		});
		JComponent bHap = building("images/smiley.png", "images/Win.jpg", p ->
		{
			selected.raiseHappiness();
		});
		setSize(bBar, 70, 70);
		setSize(bRange, 70, 70);
		setSize(bEco, 70, 70);
		setSize(bHap, 70, 70);
		holdsP.add(holds);
		holdsP.add(bBar);
		holdsP.add(bRange);
		holdsP.add(bEco);
		holdsP.add(bHap);
		
		frame.add(current, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	private void updateHolds() {
		
	}

	void updateStatus() {
		money.setText("Money: " + Main.judea.money);
		morale.setText("Morale: " + Main.judea.getMorale());
		pop.setText("Pop: " + ((Judea) Main.judea).getPop());
	}
}
