package application;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignalsManager {
	double S=10;
	double V =340;
	double F = 340;
	double W=2*3.1416 * F;
	double dP=W*S/V;
	ArrayList<SoundEmiter> Sn; 
	ArrayList<Microphone> Mn ;
	double SMn[];
	double SM=0;
	
	
	public SignalsManager(){
		Sn = new ArrayList<SoundEmiter>();
		Mn = new ArrayList<Microphone>();
		//Sn.add(new SoundEmiter(0,10,0.1));
		//Sn.add(new SoundEmiter(0,20,0.2));
		//Sn.add(new SoundEmiter(10,10,0.3));
		//Sn.add(new SoundEmiter(10,20,0.4));
		
		//Microphones
		//Mn.add(new Microphone(0,0,0));
		//Mn.add(new Microphone(10,0,0));
		SMn = new double[Mn.size()];
		
		if (SMn.length>0)SMn[0]=0; 
		
		//Microphone firstMP = Mn.get(0);
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
		//if (m.matches())System.out.println("true");
		boolean isCorrect=false;
		double x=0,y=0,a=0;
		while (m.find() )
		{
			System.out.println("Звук");
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
		SMn = new double[Mn.size()];
		//Звук(x:0;y:10;А:10)
		
	}
	 public void addMicrophoneFromString(String testString)
		{
			//Звук(X:-1;y:1.0;D:1.0)
			
			//String micPattern = new String("^Звук[(]x:\\d+;y:\\d+;A:\\d+[)]$");
			//String testString = new String("Звук(x:0;y:10;А:10)");
			  String micPattern = new String("М[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[d,D]:(-?([0-9]+.)?[0-9]+)[)]");
			Pattern p = Pattern.compile(micPattern);
			Matcher m = p.matcher(testString);
		//	if (m.matches())System.out.println("true");
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
				System.out.println(m.group(5));
				 d = Double.parseDouble(m.group(5));
				 Mn.add(new Microphone(x,y,(int) d));
			}
			if (!isCorrect)System.out.println("Невірні данні");
			//Звук(x:0;y:10;А:10)
			
		}
	
		public double[] getSmDifferent(double t){
			int SMiterator = 0;
			double[] SM=new double[Mn.size()*Mn.size()];
			System.out.println("time:"+t);
		int i=0;
		for (Microphone microphone : Mn){
		for (SoundEmiter soundEmiter : Sn){ //Додавання сигналів
			double emiterX=soundEmiter.x;
			double emiterY=soundEmiter.y;
			double microphoneX = microphone.x;
			double microphoneY = microphone.y;
			double dP = W*getDistance(emiterX,emiterY,microphoneX,microphoneY)/V;
			SMn[i]+=soundEmiter.A*Math.sin(W*t+dP);
		}	
		//System.out.println("SM"+i+": "+SMn[i]);
		i++;
		}
		Comber comb = new Comber(SMn.length);
		BitSet[] coombs = comb.getCoombs();
			
		//int iterator=0;
		for (int k=0;k<SMn.length*SMn.length-1;k++)
		{
			for (int l=0;l<SMn.length;l++)
		{
		if(coombs[k].get(l))SM[SMiterator]+=SMn[l];
		}
		SMiterator++;
		//iterator=0;
		}
		return SM;
		}
	
		public double getSm(double t){
			int SMiterator = 0;
			double SM=0;
			//System.out.println("time:"+t);
		int i=0;
		for (Microphone microphone : Mn){
		for (SoundEmiter soundEmiter : Sn){ //Додавання сигналів
			double emiterX=soundEmiter.x;
			double emiterY=soundEmiter.y;
			double microphoneX = microphone.x;
			double microphoneY = microphone.y;
			double dP = W*getDistance(emiterX,emiterY,microphoneX,microphoneY)/V;
			SMn[i]+=soundEmiter.A*Math.sin(W*t+dP);
		}	
		//System.out.println("SM"+i+": "+SMn[i]);
		i++;
		}
			
		//int iterator=0;
		for (int k=0;k<SMn.length;k++)
		SM+=SMn[k]*Mn.get(k).delay;
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
	public Microphone(double x,double y,int delay){
		this.x=x;
		this.y=y;
		this.delay=delay;
	}
	double x,y;
	double delay;
}