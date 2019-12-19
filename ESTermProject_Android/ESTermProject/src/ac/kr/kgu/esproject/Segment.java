package ac.kr.kgu.esproject;

public class Segment {
	byte getSegmentCode(int x){
		byte code;
		
		switch(x){
		case 0x0 : code = (byte) 0xfc; break;
		case 0x1 : code = 0x60; break;
		case 0x2 : code = (byte) 0xda; break;
		case 0x3 : code = (byte) 0xf2; break;
		case 0x4 : code = 0x66; break;
		case 0x5 : code = (byte) 0xb6; break;
		case 0x6 : code = (byte) 0xbe; break;
		case 0x7 : code = (byte) 0xe4; break;
		case 0x8 : code = (byte) 0xfe; break;
		case 0x9 : code = (byte) 0xf6; break;
		default : code = 0; break;
		}
		
		return code;
	}
	
	byte[] getSegmentData(int num) {
		byte[] data = {0, 0, 0, 0, 0, 0, 0};
		
		int count;
		int tmp1, tmp2;
		
		count = num;
		
		
		if (count/100000!=0){
			data[6] = 6;
			data[5] = getSegmentCode(count/100000);
		}
	  	tmp1 = count % 100000;
		if (count/10000!=0){
			if(data[6] == 0)
				data[6] = 5;
			data[4] = getSegmentCode(tmp1 / 10000);
		}
		tmp2 = tmp1 % 10000;
		if (count/1000!=0){
			if(data[6] == 0)
				data[6] = 4;
			data[3] = getSegmentCode(tmp2 / 1000);
		}
		tmp1 = tmp2 % 1000;
		if (count/100!=0){
			if(data[6] == 0)
				data[6] = 3;
			data[2] = getSegmentCode(tmp1 / 100);
		}
		tmp2 = tmp1 % 100;
		if (count/10!=0){
			if(data[6] == 0)
				data[6] = 2;
			data[1] = getSegmentCode(tmp2 / 10);
		}
		data[0] = getSegmentCode(tmp2 % 10);
		if(data[6] == 0)
			data[6] = 1;
	
		
		return data;
	}
}
