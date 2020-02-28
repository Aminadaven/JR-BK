package Framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI
{
	public static final int DEF_WIDTH = 600, DEF_HEIGHT = 600, IMG_SIZE = 20;
	
	public static Image loadImage(String location) throws IOException {
		return ImageIO.read(UI.class.getResource("../" + location));
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
	
	public static JLabel label(String txt) {
		return (new JLabel(txt));
	}
	
	public static JLabel image(String location) {
		try {
			JLabel img = new JLabel();
			UI.setSize(img, IMG_SIZE, IMG_SIZE);
			img.setIcon(new ImageIcon(UI.loadImage(location)));
			return img;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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