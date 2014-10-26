package application;

import java.util.ArrayList;
import java.util.BitSet;

public class SignalsManager {
	double S=10;
	double V =340;
	double F = 340;
	double W=2*3.1416 * F;
	double dP=W*S/V;
	ArrayList<SoundEmiter> Sn = new ArrayList<SoundEmiter>();
	ArrayList<Microphone> Mn = new ArrayList<Microphone>();
	double SMn[];
	double SM=0;
	
	
	public SignalsManager(){
		Sn.add(new SoundEmiter(0,10,0.1));
		Sn.add(new SoundEmiter(0,20,0.2));
		Sn.add(new SoundEmiter(10,10,0.3));
		Sn.add(new SoundEmiter(10,20,0.4));
		//Microphones
		Mn.add(new Microphone(0,0,1));
		Mn.add(new Microphone(10,0,0));
		SMn = new double[Mn.size()];
		SMn[0]=0;
		//Microphone firstMP = Mn.get(0);
		int i=0;
		for (Microphone microphone : Mn){
		for (SoundEmiter soundEmiter : Sn){ //Додавання сигналів
			double t = S/V;
			int emiterX=soundEmiter.x;
			int emiterY=soundEmiter.y;
			int microphoneX = microphone.x;
			int microphoneY = microphone.y;
			double dist = getDistance(emiterX,emiterY,microphoneX,microphoneY);
			SMn[i]+=soundEmiter.A*Math.sin(W*t+dist);
		}	
		System.out.println("SM"+i+": "+SMn[i]);
		i++;
		}
		Comber comb = new Comber(SMn.length);
		BitSet[] coombs = comb.getCoombs();
			
		//int iterator=0;
		for (int k=0;k<SMn.length*SMn.length-1;k++)
		{
			for (int l=0;l<SMn.length;l++)
		{
		if(coombs[k].get(l))SM+=SMn[l];
		}
		System.out.println("SM:"+SM);
		SM=0;
		//iterator=0;
		}
	}
	
	//public double getSMn(int k,int t){}
	public double getDistance(int x1, int y1,int x2,int y2){
		double result = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return result;
	}
	
}

class SoundEmiter{
	double A;
	public SoundEmiter(int x,int y,double A){
		this.x=x;
		this.y=y;
		this.A=A;
	}
	int x, y;
}
class Microphone{
	public Microphone(int x,int y,int delay){
		this.x=x;
		this.y=y;
		this.delay=delay;
	}
	int x,y;
	double delay;
}