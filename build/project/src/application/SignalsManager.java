package application;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Клас дозволяє досліджувати накладання сигналів
 * Відстань - метри, час - секунди
 * @author roma
 *
 */
public class SignalsManager {
	double V =340;
	double F=340;
	ArrayList<SoundEmiter> Sn; 
	ArrayList<Microphone> Mn ;
	double timings[][];
	double SM=0;
	double errorValueUpperLimit = 0;
	Random rand;
	public SignalsManager(double errorValueUpperLimit){
		Sn = new ArrayList<SoundEmiter>();
		Mn = new ArrayList<Microphone>();
		rand = new Random();
		this.errorValueUpperLimit = errorValueUpperLimit;
	}
	/**
	 * Виконує розрахунок затримок проходження сигналу (t=s/v) від мікрофонів до джерел звуку
	 * @return Двувимірний масив з затримкою від Мікрофони[i] до Звуки[j]
	 */
	public void processTimings()
	{
		timings = new double[Mn.size()][Sn.size()];
		int i=0,j=0;
		for (Microphone microphone : Mn)
		{
			for (SoundEmiter soundEmiter : Sn)
			{ //Додавання сигналів
				timings[i][j] = getDistance(soundEmiter.x,soundEmiter.y,microphone.x,microphone.y)/V;
				j++;
			}	
			j=0;
			i++;
		}
	}
	 public void addSoundEmiterFromString(String testString)
	{	
		//String micPattern = new String("^Звук[(]x:\\d+;y:\\d+;A:\\d+[)]$");
		//String testString = new String("Звук(x:0;y:10;А:10)");
		 // String micPattern = new String("З[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[a,A]:(-?([0-9]+.)?[0-9]+)[)]");
		  String micPattern = new String(  "З[(][x,X]:(-?([0-9]+.)?[0-9]+);[y,Y]:(-?([0-9]+.)?[0-9]+);[a,A]:(-?([0-9]+.)?[0-9]+);"
		  																							+"[f,F]:(-?([0-9]+.)?[0-9]+)[)]");
		Pattern p = Pattern.compile(micPattern);
		Matcher m = p.matcher(testString);
		boolean isCorrect=false;
		double x=0,y=0,a=0,f=0;
		while (m.find() )
		{
			isCorrect=true;
			System.out.println(m.group(1));
			x = Double.parseDouble(m.group(1));
			System.out.println(m.group(3));
			y = Double.parseDouble(m.group(3));
			System.out.println(m.group(5));
			 a = Double.parseDouble(m.group(5));
			 System.out.println(m.group(7));
			 f = Double.parseDouble(m.group(7));
			 Sn.add(new SoundEmiter(x,y,a,f));
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
	public double[] toSM(double[][] SMn){
		double[] result = new double[SMn.length];
		for (int i=0;i<SMn.length;i++)
			for (int j=0;j<SMn[i].length;j++)
				result[i]+=SMn[i][j];
		return result;
				
	}
	
		public double getSm(double T)
		{
			
		double SM=0;
		double[] SMn = getSMn(T);
		for (int k=0;k<SMn.length;k++)
		SM+=SMn[k];
		return SM;
		}
		public double[] getSMn(double T)
		{
			double[] SMn = new double[Mn.size()];;
			int i=0,j=0;
		
		for (Microphone microphone : Mn)
			{
		for (SoundEmiter soundEmiter : Sn)
				{ //Додавання сигналів
			double W=2*3.1416 * soundEmiter.F;
			double dP = W*timings[i][j];
			int rangeMin=0; //Мінімальне значення похибки
			double randomValue = rangeMin + (errorValueUpperLimit - rangeMin) * rand.nextDouble();
			double errorValue = randomValue; //Значеня введеної похибки
			SMn[i]+=(soundEmiter.A+errorValue)*Math.sin(W*T+dP);
			j++;
				}	
		j=0;
		i++;
			}	

		return SMn;
		}
		
	//public double getSMn(int k,int t){}
	public double getDistance(double x1, double y1,double x2,double y2)
	{
		double result = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return result;
	}
	
}

class SoundEmiter{
	public SoundEmiter(double x,double y,double A,double F)
	{
		this.x=x;
		this.y=y;
		this.A=A;
		this.F=F;
	}
	double x, y,A,F;
}
class Microphone{
	public Microphone(double x,double y)
	{
		this.x=x;
		this.y=y;
	}
	double x,y;
	public static boolean checkOneLinePosition(ArrayList<Microphone> microphones)
	{
		//(x-x1)/(x2-x1)=(y-y1)/(y2-y1)
		boolean result = true;
		double x1 = microphones.get(0).x;
		double y1 = microphones.get(0).y;
		double x2 = microphones.get(1).x;
		double y2 = microphones.get(1).y;
		for (int i=0;i<microphones.size();i++)
		{
			double x=microphones.get(i).x;
			double y=microphones.get(i).y;
		if((x-x1)/(x2-x1)!=(y-y1)/(y2-y1))result=false;	
		}
		return result;
	}
}