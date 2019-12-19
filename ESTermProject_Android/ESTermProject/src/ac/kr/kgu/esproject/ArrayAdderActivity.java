package ac.kr.kgu.esproject;

import java.util.ArrayList;
import java.util.Random;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ArrayAdderActivity extends Activity {
    /** Called when the activity is first created. */
	
	public native int BuzzerControl(int value);
	public native int SegmentControl(byte data0, byte data1);
	public native int[] checkAnswer(int[] data); 
    BackThread s = new BackThread();
    
    void getID(){
    	create = (Button)findViewById(R.id.create);
    	clear = (Button)findViewById(R.id.clear);
    	confirm = (Button)findViewById(R.id.confirm);
    	TV1 = (TextView)findViewById(R.id.TV1);
    	TV2 = (TextView)findViewById(R.id.TV2);
    	TV3 = (TextView)findViewById(R.id.TV3);
    	YorN = (TextView)findViewById(R.id.YorN);
    	enterText = (EditText)findViewById(R.id.enterText);
    	int_list = (Spinner)findViewById(R.id.int_list);
    }
    
    static {
    	System.loadLibrary("arrayadder");
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        s.setDaemon(true);
        s.start();
        for ( int i = 1; i<=10; i++){
        	spin.add(i);
        }
        
        getID();
 
        adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,spin);
        int_list.setAdapter(adapter);
	    create.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){

        		count = Integer.parseInt(int_list.getSelectedItem().toString());
        		box_arr = new int[count+1];
        		clear.setVisibility(View.VISIBLE);
        		confirm.setVisibility(View.VISIBLE);
        		enterText.setVisibility(View.VISIBLE);
        		StringBuffer TV1s = new StringBuffer();
        		StringBuffer TV2s = new StringBuffer();
        		for(int i =0; i<count; i++) {
        			box_arr[i]=num.nextInt(10);
        			
        		}
        		
        		if(count>5) {
	        		for(int i = 0; i<5;i++){
	        			TV1s.append("배열 요소 #"+(i+1)+" : "+ box_arr[i]+"\n");       			
	        		}
	        		for(int i = 5; i<count;i++){
	        			TV2s.append("배열 요소 #"+(i+1)+" : "+ box_arr[i]+"\n");
	        		}
        		}
        		else {
        			for(int i = 0; i<count;i++){
	        			TV1s.append("배열 요소 #"+(i+1)+" : "+ box_arr[i]+"\n");
	        		}
        		}
        			
        		TV1.setText(TV1s.toString());
        		TV2.setText(TV2s.toString());
        		TV3.setText("덧셈 결과:");
        	}
        	
        });
        
        
        confirm.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		TV3.setVisibility(View.VISIBLE);
        		result = Integer.parseInt(enterText.getText().toString());
        		box_arr[count]=result;
        		return_arr = checkAnswer(box_arr);
        		data = seg.getSegmentData(return_arr[1]);
        	
        		if(return_arr[0] == 1){
        			YorN.setText("정확합니다");
        			
        			flag = 1;
        		}
        		else{
        			
        			BuzzerControl(1);
        			YorN.setText("틀렸습니다");
        			wrong_data = seg.getSegmentData(result);
        			
        			flag = 0;
        		}
        		
        	}
    	});
        
        clear.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
    		box_arr = null;
    		reg_index = 0;
    		sum = 0;
    		result = 0;
    		TV1.setText(" ");
    		TV2.setText(" ");
    		TV3.setText(" ");
    		YorN.setText(" ");
    		clear.setVisibility(View.INVISIBLE);
    		confirm.setVisibility(View.INVISIBLE);
    		enterText.setVisibility(View.INVISIBLE);
    		flag = -1; 		//쓰레드 아무것도 안하게 바꿈
    		BuzzerControl(0);
    		}
		});
	}
	
	
	class BackThread extends Thread {
		int buzzer_flag = 0;
		byte[] reg_sel = {0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
		byte[] send_data = new byte[2];
		byte[] default_data = {(byte) 0x80, 0x0C, 0x10, 0x60};
		int default_data_index = 0;
		boolean flow_bit = true;  //아몰랑
		
		
		public void run() {
			while(!stop) {
				switch (flag) {
				default:
					
					send_data[0] = reg_sel[reg_index];
					send_data[1] = default_data[0 + default_data_index];
					
					for(int i = 0; i < 200; i++) {
						SegmentControl(send_data[0], send_data[1]);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					try {
						Thread.sleep(400);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if(reg_index == 5 && default_data_index == 0) { //끝까지 다돈경우 옆면 출력
						send_data[1] = default_data[1];
						for(int i = 0; i < 200; i++) {
							SegmentControl(send_data[0], send_data[1]);
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						reg_index++;
						flow_bit = false;
						default_data_index = (default_data_index + 2) % 4;
					}
					else if(reg_index == 0 && default_data_index == 2) {
						send_data[1] = 0x60;
						for(int i = 0; i < 200; i++) {
							SegmentControl(send_data[0], send_data[1]);
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						reg_index--;
						flow_bit = true;
						default_data_index = (default_data_index + 2) % 4;
					}
					
					if(flow_bit)
						reg_index = (reg_index + 1);
					else
						reg_index = reg_index - 1;
					
					break;
					
				case 0: //틀렸을때
					
					for(int j = 0; j < 200; j++) {
						for(int i = 5; i >= 0; i--) { //틀린거 출력
							if( wrong_data[i] != 0 ) {
								send_data[0] = reg_sel[(i + reg_index + data[6] + 1) % 6];
								send_data[1] = wrong_data[i];
								SegmentControl(send_data[0], send_data[1]);
								
							}
						}
						
						send_data[0] = reg_sel[(reg_index + data[6]) % 6];
						send_data[1] = 0x02;
						SegmentControl(send_data[0], send_data[1]);
						
						for(int k = 5; k >= 0; k--) {
							if( data[k] != 0 ) {
								send_data[0] = reg_sel[(k + reg_index) % 6];
								send_data[1] = data[k];
								SegmentControl(send_data[0], send_data[1]);
							}
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
										
					reg_index = (reg_index + 1) % 6;
					          
					break;
					
				case 1: //맞았을때
					
					if( buzzer_flag == 0 )
						buzzer_flag = 1;
					else
						buzzer_flag = 0;
					
					BuzzerControl(buzzer_flag);
					
					for(int j = 0; j < 200; j++) {
						for(int i = 5; i >= 0; i--) {
							if( data[i] != 0 ) {
								send_data[0] = reg_sel[(i + reg_index) % 6];
								send_data[1] = data[i];
								SegmentControl(send_data[0], send_data[1]);
							}
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					reg_index = (reg_index + 1) % 6;
								
					break;
				}
			}
		}
	}
	
	
	int reg_index = 0;
	byte[] data;
	byte[] wrong_data;
	int[] return_arr;    //return_arr[0] 에 참거짓 return_arr[1]에 결과값
	int flag = - 1;
	boolean stop = false;
	Button create;
    Button clear;
    Button confirm;
    TextView TV1;
    TextView TV2;
    TextView TV3;
    TextView YorN;
    EditText enterText;
    Spinner int_list;
    int count = 0;
    int sum = 0;
    int result = 0;
    int[] box_arr;
    Random num = new Random();
    ArrayAdapter<Integer> adapter;
    ArrayList<Integer> spin = new ArrayList<Integer>();
    Segment seg = new Segment();
}

 