package net.pizdecrp.NRNN;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Main { 
	public JFrame frame;
	public Paint paint;
	public Paint afterrender;
	public JLabel l1;
	Map<Integer, Perceptron> pct = new HashMap<>();
	public static final int aftWidth = 40, aftHeight = 72, ampl = 1;
	
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
		}
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
		this.frame.setSize(800 ,340);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(null);
		
		paint= new Paint(this);
		paint.setBounds(50, 0, Paint.DEFAULT_SIZE.width, Paint.DEFAULT_SIZE.height);
		this.frame.getContentPane().add(paint);
		
	    l1=new JLabel("0", SwingConstants.CENTER);
		l1.setBounds(600, 30, 100, 175);
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
				diagram(paint.image);
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
				int o = clasify(reDraw(paint.image));
				l3.setText("это цифра "+o);
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
		
		afterrender = new Paint(this);
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
	
	public void diagram(BufferedImage bufferedImage) {
		Map<Integer,Double> arr = new HashMap<>();
		for (Entry<Integer, Perceptron> single : pct.entrySet()) {
			arr.put(single.getKey(),single.getValue().output(reDraw(bufferedImage)));
		}
		Comparator<Entry<Integer, Double>> valueComparator = 
			    (e1, e2) -> e1.getValue().compareTo(e2.getValue());

		Map<Integer, Double> sortedMap = 
		    arr.entrySet().stream().
		    sorted(valueComparator).
		    collect(
		    		Collectors.toMap(
		    		Entry::getKey,
		    		Entry::getValue,
		            (e1, e2) -> e1,
		            LinkedHashMap::new
		    		)
		    );
		String s = "<html>";
		for (Entry<Integer, Double> d : sortedMap.entrySet()) {
			s += d.getKey()+" - " + String.format("%.8f",d.getValue()) + "<br/>";
		}
		s += "</html>";
		l1.setText(s);
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



