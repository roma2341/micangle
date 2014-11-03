package application;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignalsManager {
	double V =340;
	double F = 340;
	double W=2*3.1416 * F;
	ArrayList<SoundEmiter> Sn; 
	ArrayList<Microphone> Mn ;
	double timings[][];
	double SM=0;
	
	public SignalsManager(){
		Sn = new ArrayList<SoundEmiter>();
		Mn = new ArrayList<Microphone>();
	}
	public void processTimings()
	{
		timings = new double[Mn.size()][Sn.size()];
		int i=0,j=0;
		for (Microphone microphone : Mn){
			for (SoundEmiter soundEmiter : Sn){ //Додавання сигналів
				double emiterX=soundEmiter.x;
				double emiterY=soundEmiter.y;
				double microphoneX = microphone.x;
				double microphoneY = microphone.y;
				timings[i][j] = getDistance(emiterX,emiterY,microphoneX,microphoneY)/V;
				j++;
			}	
			j=0;
			i++;
	}
	}
	 public void addSoundEmiterFromString(String testString)
	{
		//Звук(X:-1;y:1.0;D:1.0)
		
		//String micPattern = new String("^Звук[(]x:\\d+;y:\\d+;A:\\d+[)]$");
		//String testString = new String("Звук(x:0;y:10;А:10)");
		//  String micPattern = new String("^З[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[a,A]:(-?([0-9]+.)?[0-9]+)[)]$");
		  String micPattern = new String("З[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[a,A]:(-?([0-9]+.)?[0-9]+)[)]");
		Pattern p = Pattern.compile(micPattern);
		Matcher m = p.matcher(testString);
		boolean isCorrect=false;
		double x=0,y=0,a=0;
		while (m.find() )
		{
			isCorrect=true;
			System.out.println(m.group(1));
			x = Double.parseDouble(m.group(1));
			System.out.println(m.group(3));
			y = Double.parseDouble(m.group(3));
			System.out.println(m.group(5));
			 a = Double.parseDouble(m.group(5));
			 Sn.add(new SoundEmiter(x,y,a));
		}
		if (!isCorrect)System.out.println("Невірні данні");
		//Звук(x:0;y:10;А:10)
		
	}
	 public void addMicrophoneFromString(String testString)
		{
			//Звук(X:-1;y:1.0;D:1.0)
			
			//String micPattern = new String("^Звук[(]x:\\d+;y:\\d+;A:\\d+[)]$");
			//String testString = new String("Звук(x:0;y:10;А:10)");
			 // String micPattern = new String("М[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[d,D]:(-?([0-9]+.)?[0-9]+)[)]");
		 String micPattern = new String("М[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+)[)]");
			Pattern p = Pattern.compile(micPattern);
			Matcher m = p.matcher(testString);
			boolean isCorrect=false;
			double x=0,y=0,d=0;
			while (m.find() )
			{
				System.out.println("Мікрофон");
				isCorrect=true;
				System.out.println(m.group(1));
				x = Double.parseDouble(m.group(1));
				System.out.println(m.group(3));
				y = Double.parseDouble(m.group(3));
				 Mn.add(new Microphone(x,y));
			}
			if (!isCorrect)System.out.println("Невірні данні");
			//Звук(x:0;y:10;А:10)
			
		}
	
		public double getSm(double T){
			double[] SMn = new double[Mn.size()];;
			int SMiterator = 0;
			double SM=0;
			int i=0,j=0;
		
		for (Microphone microphone : Mn){
		for (SoundEmiter soundEmiter : Sn){ //Додавання сигналів
			double dP = W*timings[i][j];
			SMn[i]+=soundEmiter.A*Math.sin(W*T+dP);
			j++;
		}	
		j=0;
		i++;
		}
			
		//int iterator=0;
		for (int k=0;k<SMn.length;k++)
		SM+=SMn[k];//*Mn.get(k).t.get(selectedMic);
		return SM;
		}
		
	//public double getSMn(int k,int t){}
	public double getDistance(double x1, double y1,double x2,double y2){
		double result = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return result;
	}
	
}

class SoundEmiter{
	double A;
	public SoundEmiter(double x,double y,double A){
		this.x=x;
		this.y=y;
		this.A=A;
	}
	double x, y;
}
class Microphone{
	public Microphone(double x,double y){
		this.x=x;
		this.y=y;
	}
	double x,y;
}