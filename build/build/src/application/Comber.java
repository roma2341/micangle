package application;

import java.util.BitSet;
/**
 * Перебір варіантів розташування бітів
 * @author roma
 *
 */
public class Comber {
	int size;
	BitSet number;
	public Comber(int n){
		size=n;
		number = new BitSet(n);
	}
	public BitSet[] getCoombs(){
		int sizeOfResult = (int) Math.pow(2,size)-1;
		BitSet[] result = new BitSet[sizeOfResult];
		for (int i=0;i<sizeOfResult;i++)
			result[i] = new BitSet(size);
		
		boolean active = true;
		int iCount = 0;
		int coombNumber=0;
		int i=0;
		while (active){
			if		(number.get(i))	{
					number.clear(i++);
					continue;
									}
			else	{number.set(i);i=0;}
			for (int j=0;j<size;j++)
			{
				result[coombNumber].set(j, number.get(j));
				if (number.get(j))iCount++;
				if (iCount>=size)return result;
			}
			iCount=0;
				coombNumber++;
		}
		return null;
		
	}
	
}
