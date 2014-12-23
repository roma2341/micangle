package application;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
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
	final static short MAX_SHORT = 32767;//32767
	static double V =340;
	final int BUFFER_CAPACITY = 4000;//8000
	ArrayList<SoundEmiter> Sn; 
	ArrayList<Microphone> Mn ;
	int buffer[][];
	int previousBuffer[][];
	double timings[][];
	double errorValueUpperLimit = 0;
	Random rand;
	public SignalsManager(double errorValueUpperLimit){
		Sn = new ArrayList<SoundEmiter>();
		Mn = new ArrayList<Microphone>();
		rand = new Random();
		this.errorValueUpperLimit = errorValueUpperLimit;
	}
	public static int generateSignal(double t,double A,int F){
		int signal = (int) (A * Math.sin(Math.toRadians(2*Math.PI*F*t))*MAX_SHORT);
		return signal;
	}
	
	
	public static int[][] shift(int[][] source,int[] delays)
	{
		if (source==null || delays==null) return source;
		int[][] result = new int[source.length][source[0].length];
		if (source.length==0 || source[0].length==0)return null;
		int delayIterator=0;
		for (int j=0;j<source[0].length;j++)
		{
			
			int delay = 0;
			if (delayIterator<delays.length)delay=delays[delayIterator++];
			for (int i=0;i<source.length;i++)
			{
				if (i-delay<0 || i-delay>source.length-1) result[i][j]=0;
				else
				result[i][j]=source[i-delay][j];
			}
		}
		
	
		return result;
	}
	
	public static int[] shift(int[] source,int delay)
	{
		if (source==null || delay==0) return source;
		int[]result = new int[source.length];
		if (source.length==0)return null;
		for (int i=0;i<source.length;i++)
		{	
				if (i-delay<0 || i-delay>source.length-1) result[i]=0;
				else
				result[i]=source[i-delay];
		}
		
	
		return result;
	}
	
	public int[][] shiftBuffer(int[] delays)
	{
		
		if (buffer==null || delays==null) return null;
		int[][] result = new int[buffer.length][buffer[0].length];
		if (buffer.length==0 || buffer[0].length==0)return null;
		int delayIterator=0;
		for (int j=0;j<buffer[0].length;j++)
		{
			int delay = 0;
			if (delayIterator<delays.length)delay=delays[delayIterator++];
			for (int i=0;i<buffer.length;i++)
			{
				if (i-delay<0) 
				{
					if(previousBuffer!=null)
					result[i][j]=previousBuffer[previousBuffer.length+(i-delay)][j];
					else result[i][j]=0;
				}
				else
					result[i][j]=buffer[i-delay][j];
				if (i-delay>buffer.length-1) result[i][j]=0;
			}
		}
	
		return result;
	}
	
	public double[] getCenterOfMicPane(){
		  double startX=Mn.get(0).x;
			double startY=Mn.get(0).y;
			double endX=Mn.get(0).x;
			double endY=Mn.get(0).y;
			for (Microphone mic : Mn)
			{
				if (mic.x<startX)startX=mic.x;
				if (mic.x>endX)endX=mic.x;
				if (mic.y<startY)startY=mic.y;
				if (mic.y>endY)endY=mic.y;
				
			}
			double[] center = new double[2];
			 center[0] = (endX+startX)/2;
			 center[1]=(endY+startY)/2;
			 return center;
	}
	
	/**
	 * Виконує розрахунок затримок проходження сигналу (t=s/v) від мікрофонів до джерел звуку
	 * @return Двувимірний масив з затримкою від Мікрофони[i] до Звуки[j]
	 */
	
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
		double x=0,y,a;
		int f;
		while (m.find() )
		{
			isCorrect=true;
			x = Double.parseDouble(m.group(1));
			y = Integer.parseInt(m.group(3));
			 a = Double.parseDouble(m.group(5));
			 f = Integer.parseInt(m.group(7));
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
				isCorrect=true;
				x = Double.parseDouble(m.group(1));
				y = Double.parseDouble(m.group(3));
				 Mn.add(new Microphone(x,y));
			}
			if (!isCorrect)System.out.println("Невірні данні");
			//Звук(x:0;y:10;А:10)
			
		}
	 static public long[] toSM(int[][] SMn,boolean isCorelation){
		 if (SMn==null)return null;
		long[] result = new long [SMn.length];
		if (isCorelation)
		{
		for (int i=0;i<result.length;i++)
			result[i]=1;
		for (int i=0;i<SMn.length;i++)
			for (int j=0;j<SMn[i].length;j++)
				if (SMn[i][j]!=0)result[i]=result[i]*SMn[i][j];
		}
		else
			for (int i=0;i<SMn.length;i++)
				for (int j=0;j<SMn[i].length;j++)
					result[i]=result[i]+SMn[i][j];
		
		return result;
				
	}
	 public long[][]  interCorelationFunc(int[][] buffer){
		 final int SHIFT_COUNT = 48;//48
			int[] delays = new int[]{0,0,0,0};
			long[][] result = new long[buffer[0].length-1][SHIFT_COUNT];
			///
			int[][] savedBuffer = new int[buffer.length][buffer[0].length];
			if (buffer!=null)
			{
				savedBuffer = (int[][])buffer.clone();
			for (int i = 0; i < buffer.length; i++) {
				savedBuffer[i] = buffer[i].clone();
			}
			}
			///
			//int[][] savedBuffer=buffer;
			for (int l=1;l<buffer[0].length;l++)
			{
			delays[l-1]=0;	
			int maxIndex = 0;
			long maxValue = 0;
			long summ=0;
			for (int k=0;k<SHIFT_COUNT;k++)	{
				summ=0;
				delays[l]=k;
				buffer=shiftBuffer(delays);
				long[] SM = new long [buffer.length];
				for (int i=0;i<SM.length;i++)
					SM[i]=1;
			for (int i=0;i<buffer.length;i++)	
			{
				for (int j=0;j<buffer[0].length;j++){
					SM[i]*=buffer[i][j];
				}
				summ+=SM[i];
				//if (summ<0)System.out.println("summ:"+summ);
			}
			if (summ>maxValue)	{
								maxValue=summ;
								maxIndex=k;
								}
			//System.out.println("summ:"+summ);
			result[l-1][k]=summ;
									}
			double L =  SignalsManager.getDistance(Mn.get(0).x,Mn.get(0).y,Mn.get(1).x,Mn.get(1).y);
			System.out.println("maxIndex:"+maxIndex +"cos:"+V*maxIndex/(Sn.get(0).F*L)+   " Alpha:"+Math.toDegrees(Math.acos(V*maxIndex/(L*Sn.get(0).F))));
			
			//buffer=savedBuffer;
			
				buffer = (int[][])savedBuffer.clone();
			for (int i = 0; i < savedBuffer.length; i++) {
				buffer[i] = savedBuffer[i].clone();
			}
			}
			return result;
			}
	 
	public static double getDistance(double x1, double y1,double x2,double y2)
	{
		double result = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return result;
	}
	/**
	 * Знаходження енергії сигналу
	 * @param buffer
	 * @return
	 */
	public double soundPressureLevel(final float[] buffer) {
		double power = 0.0D;
		for (float element : buffer) {
		power += element * element;
		}
		double value = Math.pow(power, 0.5)/ buffer.length;;
		return 20.0 * Math.log10(value);
		}
	public void fillBuffer(int[][] source,int from,int capacity)
	{
		if (buffer!=null)
		{
			previousBuffer = (int[][])buffer.clone();
		for (int i = 0; i < buffer.length; i++) {
			previousBuffer[i] = buffer[i].clone();
		}
		}
		buffer = new int[capacity][source[0].length];
		for (int i=0;i<capacity;i++)
		{
			if (i+from<source.length)
			buffer[i]=source[i+from];
		}
	}
}


class SoundEmiter{
	public SoundEmiter(double x,double y,double A,int F)
	{
		this.x=x;
		this.y=y;
		this.A=A;
		this.F=F;
	}
	double x, y,A;
	int F;
	int[] signal;
	public void setSignal(int[] signal){
		this.signal=signal;
	}
	public int[][] getSMnArr(ArrayList<Microphone> Mn)
	{
		int[][] SMnTemp = new int[Mn.size()][signal.length];
		int[][] SMn = new int[signal.length][Mn.size()];
		int k[] = new int[Mn.size()];
		for (int i=0;i<Mn.size();i++)
		{
			k[i]=(int) (SignalsManager.getDistance(x, y, Mn.get(i).x, Mn.get(i).y)*F/SignalsManager.V);
			System.out.println("k:"+k[i]);
		}
		for (int i=0;i<Mn.size();i++)
		SMnTemp[i]=SignalsManager.shift(signal,k[i]);
		for (int j=0;j<signal.length;j++)
		for (int i=0;i<Mn.size();i++)
				SMn[j][i]=SMnTemp[i][j];
		
	return SMn;
	}
	public int[] processEmiterArr(double timeRange,int samplingRate,int F)
	{
		int[] signalValues = new int[(int) (samplingRate*timeRange)];
		double step = 1.0/samplingRate; 
		double t=0;
		for (int l=0;l<signalValues.length;l++)
		{
			signalValues[l]=SignalsManager.generateSignal(t, A,F);
			t+= step;
		}
		return signalValues;
	}
	
}
class Microphone{
	public Microphone(double x,double y)
	{
		this.x=x;
		this.y=y;
	}
	double x,y;
	
}
