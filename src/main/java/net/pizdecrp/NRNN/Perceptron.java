package net.pizdecrp.NRNN;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Perceptron {
	int w,h;
	double[] weight;
	double alpha = 1;
	double loop = 100;
	
	public int currentInt;
	
	public Perceptron(int w,int h, int ci) throws FileNotFoundException{
		this.w = w;
		this.h = h;
		this.currentInt = ci;
		this.weight = new double[w*h+1];
		double[] temp = read();
		if (temp.length > 0) {
			this.weight = temp;
			System.out.println("readed");
		} else {
			for (int i = 0; i < weight.length; i++) {
				weight[i] = 0;
			}
		}
	}
	
	public void write() {
		try {
			JsonReader reader = new JsonReader(new FileReader("data.json"));
			JsonElement data = JsonParser.parseReader(reader);
			JsonArray jsonArray = new JsonArray();
			for (double d : weight) {
				jsonArray.add(d);
			}
			data.getAsJsonObject().remove("pct"+currentInt);
			data.getAsJsonObject().add("pct"+currentInt, jsonArray);
			
			FileWriter writer = new FileWriter("data.json");
			writer.write(data.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double[] read() throws FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader("data.json"));
		JsonElement data = JsonParser.parseReader(reader);
		double[] arr = new double[data.getAsJsonObject().get("pct"+currentInt).getAsJsonArray().size()];
		List<Double> arr1 = new ArrayList<>();
		for (JsonElement jsonElement : data.getAsJsonObject().get("pct"+currentInt).getAsJsonArray()) {
			arr1.add(jsonElement.getAsDouble());
		}
		arr = Main.convertDoubles(arr1);
		return arr;
	}
	
	public double output(BufferedImage image) {
		double sum=weight[w*h];
		for (int i=0;i<w;i++)
			for (int j=0;j<h;j++)
				if (image.getRGB(i, j)==Color.BLACK.getRGB())
					sum+=weight[i*h+j];
		return 1.0/(1.0+Math.exp(-sum/(1000)));
	}
	
	public void learning(BufferedImage image, int y) {
		y += 1;
		for(int l = 0 ; l < loop ; l++) {
			double o = output(image);
			double d = alpha * o * (1 - o) * (y - o);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
						weight[i*h+j] = weight[i*h+j]+d;
					}
				}
			}
			weight[w*h]=weight[w*h]+d;
		}
		write();
	}

}
