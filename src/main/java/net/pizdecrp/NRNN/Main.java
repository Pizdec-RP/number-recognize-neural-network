import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Main {
	public JFrame frame;
	public Paint paint;
	public Paint afterrender;
	Map<Integer, Perceptron> pct = new HashMap<>();
	public static final int aftWidth = 12, aftHeight = 27, ampl = 3;
	
	public static double[] convertDoubles(List<Double> doubles) {
	    double[] ret = new double[doubles.size()];
	    Iterator<Double> iterator = doubles.iterator();
	    int i = 0;
	    while(iterator.hasNext()) {
	        ret[i] = iterator.next();
	        i++;
	    }
	    return ret;
	}
	
	public BufferedImage cropImg(BufferedImage img) {
		int minx = Paint.DEFAULT_SIZE.width,
			miny = Paint.DEFAULT_SIZE.height,
			maxx = 0,
			maxy = 0;
		for (int x = 0; x < Paint.DEFAULT_SIZE.width; x++) {
			for (int y = 0; y < Paint.DEFAULT_SIZE.height; y++) {
				if (img.getRGB(x, y) == Color.BLACK.getRGB()) {
					if (minx > x) {
						minx = x;
					} else if (miny > y) {
						miny = y;
					} else if (maxx < x) {
						maxx = x;
					} else if (maxy < y) {
						maxy = y;
					}
				}
			}
		}//     {"pct1":[]}
		/*paint.graphics.setColor(Color.RED);
		paint.graphics.drawRect(minx, miny, maxx-minx, maxy-miny);
		paint.graphics.setColor(Color.BLACK);
		paint.repaint();*/
		return img.getSubimage(minx, miny, maxx-minx, maxy-miny);
	}
	
	public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	    graphics2D.dispose();
	    return resizedImage;
	}
	
	public BufferedImage reDraw(BufferedImage img) {
		try {
			BufferedImage img1 = cropImg(img);
			//System.out.println("redrawed");
			img1 = resizeImage(img1, aftWidth, aftHeight);
			afterrender.image = resizeImage(img1, aftWidth*ampl, aftHeight*ampl);
			afterrender.repaint();
			return img1;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	
	
	public Main() throws IOException {	
		pct.put(0, new Perceptron(aftWidth, aftHeight, 0));
		pct.put(1, new Perceptron(aftWidth, aftHeight, 1));
		pct.put(2, new Perceptron(aftWidth, aftHeight, 2));
		pct.put(3, new Perceptron(aftWidth, aftHeight, 3));
		pct.put(4, new Perceptron(aftWidth, aftHeight, 4));
		pct.put(5, new Perceptron(aftWidth, aftHeight, 5));
		pct.put(6, new Perceptron(aftWidth, aftHeight, 6));
		pct.put(7, new Perceptron(aftWidth, aftHeight, 7));
		pct.put(8, new Perceptron(aftWidth, aftHeight, 8));
		pct.put(9, new Perceptron(aftWidth, aftHeight, 9));
		
		this.frame = new JFrame("pipiska!");
		this.frame.setSize(600 ,300);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(null);
		
		paint= new Paint();
		paint.setBounds(50, 0, Paint.DEFAULT_SIZE.width, Paint.DEFAULT_SIZE.height);
		this.frame.getContentPane().add(paint);
		
		final JLabel l1=new JLabel("0");
		l1.setBounds(540, 30, 100, 25);
		this.frame.add(l1);
		
		JTextField num=new JTextField("int");
		num.setBounds(410, 30, 100, 25);
		this.frame.add(num);
		
		final JLabel l2=new JLabel("0");
		l2.setBounds(540, 70, 100, 25);
		this.frame.add(l2);
		
		JButton Learn2=new JButton("learn");
		Learn2.setBounds(410, 70, 100, 25);
		Learn2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				learn(reDraw(paint.image), Integer.parseInt(num.getText()));
				l2.setText("lnd as "+Integer.parseInt(num.getText()));
				
			}
		});
		this.frame.add(Learn2);
		
		final JLabel l3=new JLabel("0");
		l3.setBounds(540, 120, 200, 25);
		this.frame.add(l3);
		
		JButton Test=new JButton("Classification");
		Test.setBounds(410, 120, 125, 25);
		Test.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				double o = clasify(reDraw(paint.image));
				l3.setText(""+o);
			}
		});
		this.frame.add(Test);
		
		JButton Cl=new JButton("Clear");
		Cl.setBounds(410, 200, 75, 25);
		Cl.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				paint.clear();
				l3.setText("");
			}
		});
		this.frame.add(Cl);
		
		afterrender = new Paint();
		afterrender.setBounds(500, 150, aftWidth*ampl, aftHeight*ampl);
		this.frame.getContentPane().add(afterrender);
		
		frame.setVisible(true);
	}
	
	
	public static void main(String args[]) throws IOException{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Main();
	}
	
	
	public void learn(BufferedImage bufferedImage, int i) {
		pct.get(i).learning(bufferedImage, i);
	}
	
	
	public int clasify(BufferedImage bufferedImage) {
		Entry<Integer, Perceptron> max = null;
		double maxl = 0;
		for (Entry<Integer, Perceptron> single : pct.entrySet()) {
			double t = single.getValue().output(bufferedImage);
			if (max == null) {
				max = single;
				maxl = t;
			} else if (maxl < t) {
				max = single;
				maxl = t;
			}
		}
		return max.getKey();
	}
	/*
	{
    "pct0":[],
    "pct1":[],
    "pct2":[],
    "pct3":[],
    "pct4":[],
    "pct5":[],
    "pct6":[],
    "pct7":[],
    "pct8":[],
    "pct9":[]
}
	 */
}




class Paint extends JPanel implements MouseListener, MouseMotionListener {
	public static final Dimension DEFAULT_SIZE = new Dimension(300, 300);
	public BufferedImage image;
	public Graphics graphics;
	private Point startPoint;
	public Paint() {
		setPreferredSize(DEFAULT_SIZE);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		image = new BufferedImage(
		DEFAULT_SIZE.width, DEFAULT_SIZE.height, BufferedImage.TYPE_3BYTE_BGR);
		graphics = image.getGraphics();
		clear();
	}

	public void clear(){
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
		graphics.setColor(Color.BLACK);
		repaint();
		
	}
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(image, 0, 0, this);
	}
	
	@Override public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		graphics.fillOval(p.x-10, p.y-10, 20, 20);
		repaint();
		startPoint = p;
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		graphics.fillOval(p.x-10, p.y-10, 20, 20);
		//graphics.drawLine(startPoint.x, startPoint.y, p.x, p.y);
		repaint();
		startPoint = p;
	}
	
	@Override public void mouseMoved(MouseEvent e) {}
	
	public void saveImage() {
		JFileChooser fileDialog = new JFileChooser();
		int state = fileDialog.showSaveDialog(this);
		if (state != JFileChooser.APPROVE_OPTION)
		return;
		
		File file = fileDialog.getSelectedFile();
		String fileName = file.getName();
		if (!fileName.endsWith(".png"))
		file = new File(file.getParent(), fileName + ".png");
		
		try { 
			ImageIO.write(image, "png", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


