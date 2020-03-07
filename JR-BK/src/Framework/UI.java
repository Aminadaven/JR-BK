package Framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI
{
	public static final int DEF_WIDTH = 600, DEF_HEIGHT = 600, IMG_SIZE = 20;
	
	public static Image loadImage(String location) throws IOException {
		//return ImageIO.read(ClassLoader.getSystemResource("../" + location));
		return ImageIO.read(UI.class.getResourceAsStream("../" + location));
		//return ImageIO.read(new FileInputStream("../" + location));
	}
	
	public static BufferedImage loadBuffImage(String location)
		throws IOException {
		return ImageIO.read(UI.class.getResource("../" + location));
	}
	
	public static void setSize(Component comp, int width, int height) {
		Dimension size = new Dimension(width, height);
		comp.setSize(size);
		comp.setPreferredSize(size);
		comp.setMinimumSize(size);
		comp.setMaximumSize(size);
	}
	
	public static JFrame mainFrame() {
		JFrame frame = new JFrame();
		setSize(frame, DEF_WIDTH, DEF_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	
	public static JFrame mainFrame(String title) {
		JFrame frame = mainFrame();
		frame.setTitle(title);
		return frame;
	}
	
	public static JFrame mainFrame(String title, String icon) {
		JFrame frame = mainFrame(title);
		try {
			frame.setIconImage(loadImage(icon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frame;
	}
	
	public static JFrame mainFrame(String title, int width, int height) {
		JFrame frame = mainFrame(title);
		setSize(frame, width, height);
		return frame;
	}
	
	public static JFrame mainFrame(String title, int width, int height,
		String icon) {
		JFrame frame = mainFrame(title, width, height);
		try {
			frame.setIconImage(loadImage(icon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frame;
	}
	
	public static void setHeadless(JFrame frame) {
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				frame.setShape(
					new Rectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight()));
			}
		});
		frame.setUndecorated(true);
	}
	
	public static JButton button(String txt, ActionListener listener) {
		JButton btn = new JButton(txt);
		btn.addActionListener(listener);
		return btn;
	}
	
	public static ImageButton ibutton(String loc, String loc2,
		ActionListener listener) {
		ImageButton btn = new ImageButton(loc, loc2);
		btn.addActionListener(listener);
		return btn;
	}
	
	public static JBuilding building(String loc, String loc2,
		ActionListener listener) {
		JBuilding bld = new JBuilding(loc, loc2);
		bld.addActionListener(listener);
		return bld;
	}
	
	public static JLabel label(String txt) {
		return (new JLabel(txt));
	}
	
	public static JImage image(String location) {
		JImage img = new JImage(location);
		/*
		 * try {
		 * //JLabel img = new JLabel();
		 * //img.setIcon(new ImageIcon(UI.loadImage(location)));
		 * return img;
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * return null;
		 * }
		 */
		UI.setSize(img, IMG_SIZE, IMG_SIZE);
		return img;
	}
	
	public static JTextField rf(int width, String txt) {
		return (new JTextField(txt, width));
	}
	
	public static JTextArea transTA(int rows, int cols) {
		JTextArea ta = new JTextArea(rows, cols) {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		ta.setOpaque(false);
		ta.setBackground(new Color(255, 255, 255, 0));
		ta.setEditable(false);
		ta.setBorder(BorderFactory.createEmptyBorder());
		return ta;
	}
}

// Used for Buttons only! forbuildings, use JBuilding
@SuppressWarnings("serial")
class ImageButton extends JButton // really needs improve on drawing text
{
	public boolean onClick = false;
	private Image background, backgroundg;
	
	public ImageButton(String imageLoc, String image2loc)
	{
		super();
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				onClick = true;
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				onClick = false;
			}
		});
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0));
		try {
			background = UI.loadImage(imageLoc);
			backgroundg = UI.loadImage(image2loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Image img = onClick ? background : backgroundg;
		g.drawImage(img.getScaledInstance(getWidth(), getHeight(), 0), 0, 0,
			getWidth(), getHeight(), null);
		super.paintComponent(g);
	}
}

class JImage extends JComponent
{
	protected Image img, current;
	
	public JImage(String loc)
	{
		super();
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0));
		try {
			img = UI.loadImage(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		current = img;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(current.getScaledInstance(getWidth(), getHeight(), 0), 0, 0,
			getWidth(), getHeight(), null);
		super.paintComponent(g);
	}
}

class JBuilding extends JImage
{
	private Image img2;
	private ArrayList<ActionListener> listeners;
	
	public JBuilding(String loc, String loc2)
	{
		super(loc);
		try {
			img2 = UI.loadImage(loc2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		listeners = new ArrayList<>();
		current = img2;
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				img2 = img;
				for (ActionListener click : listeners)
					click.actionPerformed(null);
				((JBuilding) e.getSource()).repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				current = img;
				((JBuilding) e.getSource()).repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				current = img2;
				((JBuilding) e.getSource()).repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
	}
	
	public void addActionListener(ActionListener click) {
		listeners.add(click);
	}
	
	public void removeActionListener(ActionListener click) {
		listeners.remove(click);
	}
}