package yavor.kolev.flashtobang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import yavor.kolev.thunderdistancemeter.R;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import android.os.Vibrator;

public class MainActivity extends Activity
{
	Vibrator myVibrator;
	int distanceMeter;//distanceMeter = speed * time
	int distanceFeet;//distanceFeet = distanceMeter * 3.280 839 895
	int distanceYard;//distanceInch = distanceMeter * 1.094
	double speed, time; //speed of sound in air 331.3+(0.6*temp), ((milliseconds / 1000.0) + secs + (mins * 60.0))
	int temp, id=0;//air temperature
	private SharedPreferences prefs;
	private String prefName = "spinner_value"; 
	
	private SQLiteAdapter mySQLiteAdapter;
	ListView listContent;
	
	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;
	long millisecondsHistory, secsHistory, minsHistory, timeInMillisecondsHistory, timeSwapBuffHistory, updatedTimeHistory, startTimeHistory;
	private Button flashButton, bangButton;
	private TextView tvMins, tvSecs, tvMills;
	Handler customHandler = new Handler();
	long timeInMilliseconds = 0L, timeSwapBuff = 0L, updatedTime = 0L, startTime = 0L, milliseconds = 0L, secs = 0L, mins = 0L;
	boolean started = false;
	final String DEGREE  = "\u00b0"; //Degree symbol
	Spinner sp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	myVibrator = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final List<String> list=new ArrayList<String>();
        list.add("Air" + DEGREE + "C01" + DEGREE + "F33+1");
        list.add("Air" + DEGREE + "C02" + DEGREE + "F35+1");
        list.add("Air" + DEGREE + "C03" + DEGREE + "F37+1");
        list.add("Air" + DEGREE + "C04" + DEGREE + "F39+1");
        list.add("Air" + DEGREE + "C05" + DEGREE + "F41+1");
        list.add("Air" + DEGREE + "C06" + DEGREE + "F43+1");
        list.add("Air" + DEGREE + "C07" + DEGREE + "F45+1");
        list.add("Air" + DEGREE + "C08" + DEGREE + "F47+1");
        list.add("Air" + DEGREE + "C09" + DEGREE + "F49+1");
        list.add("Air" + DEGREE + "C10" + DEGREE + "F51+1");
        list.add("Air" + DEGREE + "C11" + DEGREE + "F53+1");
        list.add("Air" + DEGREE + "C12" + DEGREE + "F55+1");
        list.add("Air" + DEGREE + "C13" + DEGREE + "F57+1");
        list.add("Air" + DEGREE + "C14" + DEGREE + "F59+1");
        list.add("Air" + DEGREE + "C15" + DEGREE + "F61+1");
        list.add("Air" + DEGREE + "C16" + DEGREE + "F63+1");
        list.add("Air" + DEGREE + "C17" + DEGREE + "F65+1");
        list.add("Air" + DEGREE + "C18" + DEGREE + "F67+1");   
        list.add("Air" + DEGREE + "C19" + DEGREE + "F69+1");
        list.add("Air" + DEGREE + "C20" + DEGREE + "F71+1");
        list.add("Air" + DEGREE + "C21" + DEGREE + "F73+1");
        list.add("Air" + DEGREE + "C22" + DEGREE + "F75+1");
        list.add("Air" + DEGREE + "C23" + DEGREE + "F77+1");
        list.add("Air" + DEGREE + "C24" + DEGREE + "F79+1");
        list.add("Air" + DEGREE + "C25" + DEGREE + "F81+1");
        list.add("Air" + DEGREE + "C26" + DEGREE + "F83+1");
        list.add("Air" + DEGREE + "C27" + DEGREE + "F85+1");
        list.add("Air" + DEGREE + "C28" + DEGREE + "F87+1");
        list.add("Air" + DEGREE + "C29" + DEGREE + "F89+1");
        list.add("Air" + DEGREE + "C30" + DEGREE + "F91+1");
        list.add("Air" + DEGREE + "C31" + DEGREE + "F93+1");
        list.add("Air" + DEGREE + "C32" + DEGREE + "F95+1");
        list.add("Air" + DEGREE + "C33" + DEGREE + "F97+1");
        list.add("Air" + DEGREE + "C34" + DEGREE + "F99+1");
        list.add("Air" + DEGREE + "C35" + DEGREE + "F101+1");
        list.add("Air" + DEGREE + "C36" + DEGREE + "F103+1");    
        list.add("Air" + DEGREE + "C38" + DEGREE + "F105+1");
        list.add("Air" + DEGREE + "C39" + DEGREE + "F107+1");
        list.add("Air" + DEGREE + "C40" + DEGREE + "F109+1");
        list.add("Air" + DEGREE + "C41" + DEGREE + "F111+1");
        list.add("Air" + DEGREE + "C42" + DEGREE + "F113+1");
        list.add("Air" + DEGREE + "C43" + DEGREE + "F115+1");
        list.add("Air" + DEGREE + "C44" + DEGREE + "F117+1");
        list.add("Air" + DEGREE + "C45" + DEGREE + "F119+1");
        list.add("Air" + DEGREE + "C46" + DEGREE + "F121+1");
        
        sp = (Spinner) findViewById(R.id.spinnerTemperature);
        ArrayAdapter<String> adp= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adp);
        
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        id=prefs.getInt("last_val",0);
        sp.setSelection(id);
        
        
        sp.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
 
		  public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) 
		  {		
			  prefs = getSharedPreferences(prefName, MODE_PRIVATE);
			  SharedPreferences.Editor editor = prefs.edit();
		
		 	  editor.putInt("last_val", pos);//---save the values in the EditText view to preferences---
		       
		 	  editor.commit();//---saves the values---
			  
			  if(pos ==  0) temp = 1; //C01/F033;034
			  if(pos ==  1) temp = 2; //C02/F035;036
			  if(pos ==  2) temp = 3; //C03/F037;038
			  if(pos ==  3) temp = 4; //C04/F039;040
			  if(pos ==  4) temp = 5; //C05/F041;042
			  if(pos ==  5) temp = 6; //C06/F043;044
			  if(pos ==  6) temp = 7; //C07/F045;046
			  if(pos ==  7) temp = 8; //C08/F047;048
			  if(pos ==  8) temp = 9; //C09/F049;050
			  if(pos ==  9) temp = 10; //C01/F051;052
			  if(pos == 10) temp = 11; //C02/F053;054
			  if(pos == 11) temp = 12; //C03/F055;056
			  if(pos == 12) temp = 13; //C04/F057;058
			  if(pos == 13) temp = 14; //C05/F059;060
			  if(pos == 14) temp = 15; //C06/F061;062
			  if(pos == 15) temp = 16; //C07/F063;064
			  if(pos == 16) temp = 17; //C08/F065;066
			  if(pos == 17) temp = 18; //C09/F067;068
			  if(pos == 18) temp = 19; //C01/F069;070
			  if(pos == 19) temp = 20; //C02/F071;072
			  if(pos == 20) temp = 21; //C03/F073;074
			  if(pos == 21) temp = 22; //C04/F075;076
			  if(pos == 22) temp = 23; //C05/F077;078
			  if(pos == 23) temp = 24; //C06/F079;080
			  if(pos == 24) temp = 25; //C07/F081;082
			  if(pos == 25) temp = 26; //C08/F083;084
			  if(pos == 26) temp = 27; //C09/F085;086
			  if(pos == 27) temp = 28; //C01/F087;088
			  if(pos == 28) temp = 29; //C02/F089;090
			  if(pos == 29) temp = 30; //C03/F091;092
			  if(pos == 30) temp = 31; //C04/F093;094
			  if(pos == 31) temp = 32; //C05/F095;096
			  if(pos == 32) temp = 33; //C06/F097;098
			  if(pos == 33) temp = 34; //C07/F099;100
			  if(pos == 34) temp = 35; //C08/F101;102
			  if(pos == 35) temp = 36; //C09/F103;104
			  if(pos == 38) temp = 37; //C01/F104;105
			  if(pos == 39) temp = 38; //C02/F106;107
			  if(pos == 40) temp = 39; //C03/F108;109
			  if(pos == 41) temp = 40; //C04/F110;111
			  if(pos == 42) temp = 41; //C05/F112;113
			  if(pos == 43) temp = 42; //C06/F114;115
			  if(pos == 44) temp = 43; //C07/F116;117
			  if(pos == 45) temp = 44; //C08/F118;119
			  if(pos == 46) temp = 45; //C09/F120;121
			  
			  speed = (331.3 + (0.6 * temp));//speed of sound in air
		  }
 
		  public void onNothingSelected(AdapterView<?> arg0) 
		  {	
		  }
		  
	    });
        
        listContent = (ListView)findViewById(R.id.listViewResult);
        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();

        cursor = mySQLiteAdapter.queueAll();
        String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
        int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to, 1);
        listContent.setAdapter(cursorAdapter);
        listContent.setOnItemClickListener(listContentOnItemClickListener);
        tvMins = (TextView) findViewById(R.id.tvMins);
		tvSecs = (TextView) findViewById(R.id.tvSecs);
		tvMills = (TextView) findViewById(R.id.tvMills);
		flashButton = (Button) findViewById(R.id.flashButton);
		bangButton = (Button) findViewById(R.id.bangButton);
		flashButton.setVisibility(View.VISIBLE);
		bangButton.setVisibility(View.INVISIBLE);
		
		flashButton.setOnClickListener(new View.OnClickListener() 
		{		
			public void onClick(View view) 
			{
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//keep screen ON
				sp.setEnabled(false);
				myVibrator.vibrate(75);//milliseconds vibration time
				startTime = System.currentTimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
				started = true;
				flashButton.setVisibility(View.INVISIBLE);
				bangButton.setVisibility(View.VISIBLE);
			}
		});	

        bangButton.setOnClickListener(new View.OnClickListener() 
        {
			
			public void onClick(View view) 
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Remove screen ON
				sp.setEnabled(true);
				myVibrator.vibrate(150);//milliseconds vibration time
				time = ((milliseconds / 1000.0) + secs + (mins * 60.0));//in seconds
				distanceMeter = (int) (speed * time);//s=v*t=>meter
				distanceFeet = (int) (distanceMeter * 3.280839895);
				distanceYard = (int) (distanceMeter * 1.094);
				Calendar stopDateTime = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd/HH:mm:ss", Locale.US);
				String formattedDate = df.format(stopDateTime.getTime());
				customHandler.removeCallbacks(updateTimerThread);
				String data1 = formattedDate + "/" +
						       String.format("%01d", mins) +":" +
			                   String.format("%02d", secs) +":" +
					           String.format("%03d", milliseconds);
				String data2 = String.format("%d", distanceMeter)  +"meter" +"/" +
						       String.format("%d", distanceFeet) +"feet" +"/" +
						       String.format("%d", distanceYard) +"yard";
				
				mySQLiteAdapter.insert(data1, data2);
				updateList();
				bangButton.setVisibility(View.INVISIBLE);
				flashButton.setVisibility(View.VISIBLE);
				started = false;
				mins= secs = milliseconds = 0L;
				tvMins.setText(String.format("%01d", mins));
				tvSecs.setText(String.format("%02d", secs));
				tvMills.setText(String.format("%03d", milliseconds));
				updatedTime = timeSwapBuff = startTime = 0L;
			}
		});
    }
	private ListView.OnItemClickListener listContentOnItemClickListener = new ListView.OnItemClickListener()
	{
       @SuppressWarnings("deprecation")
	   @Override
	   public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	   {	
	       Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		   final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
	       String item_content1 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
	       String item_content2 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
	            
	       AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
	            
	       myDialog.setTitle("Delete/Edit?");
	            
	       TextView dialogTxt_id = new TextView(MainActivity.this);
	       LayoutParams dialogTxt_idLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	       dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
	       dialogTxt_id.setText("Flash: " + String.valueOf(item_id));
	            
	       final EditText dialogC1_id = new EditText(MainActivity.this);
	       LayoutParams dialogC1_idLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	       dialogC1_id.setLayoutParams(dialogC1_idLayoutParams);
	       dialogC1_id.setText(item_content1);
	            
	       final EditText dialogC2_id = new EditText(MainActivity.this);
	       LayoutParams dialogC2_idLayoutParams  = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	       dialogC2_id.setLayoutParams(dialogC2_idLayoutParams);
	       dialogC2_id.setText(item_content2);
	            
	       LinearLayout layout = new LinearLayout(MainActivity.this);
	       layout.setOrientation(LinearLayout.VERTICAL);
	       layout.addView(dialogTxt_id);
	       layout.addView(dialogC1_id);
	       layout.addView(dialogC2_id);
	       myDialog.setView(layout);
	            
	       myDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
	       {
	            public void onClick(DialogInterface arg0, int arg1) 
	            {
	                mySQLiteAdapter.delete_byID(item_id);
	        	    updateList();
	            }
	       });
	            
	       myDialog.setNeutralButton("Update", new DialogInterface.OnClickListener() 
	       {
	           public void onClick(DialogInterface arg0, int arg1) 
	           {
	               String value1 = dialogC1_id.getText().toString();
	               String value2 = dialogC2_id.getText().toString();
	               mySQLiteAdapter.update_byID(item_id, value1, value2);
	        	   updateList();
	           }
	       });
	            
	       myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	       {
	           public void onClick(DialogInterface arg0, int arg1) 
	           {
	         
	           }
	       });
	            
	       myDialog.show();          
	   }
	};
		
	@SuppressWarnings("deprecation")
	private void updateList()
	{
		cursor.requery();
	}
	
	private Runnable updateTimerThread = new Runnable() 
	{
		public void run() 
		{		
			timeInMilliseconds = System.currentTimeMillis() - startTime;		
			updatedTime = timeSwapBuff + timeInMilliseconds;
			secs = (updatedTime / 1000);
			mins = secs / 60;
			secs = secs % 60;
			mins = mins % 60;
			milliseconds = (updatedTime % 1000);
			tvMins.setText(String.format("%01d", mins));
			tvSecs.setText(String.format("%02d", secs));
			tvMills.setText(String.format("%03d", milliseconds));
			customHandler.postDelayed(this, 0);
			if(mins == 3)//Timer limit 2min. and 59 seconds if is reached stop and reset all, no save
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Remove screen ON
				myVibrator.vibrate(300);//milliseconds vibration time
				Toast.makeText(getBaseContext(), "Reached END of the timer 2 min. and 59 sec.!!!", Toast.LENGTH_LONG).show();
				mins= secs = milliseconds = 0L;
				tvMins.setText(String.format("%01d", mins));
				tvSecs.setText(String.format("%02d", secs));
				tvMills.setText(String.format("%03d", milliseconds));
				updatedTime = timeSwapBuff = startTime = 0L;
				customHandler.removeCallbacks(updateTimerThread);
				bangButton.setVisibility(View.INVISIBLE);
				flashButton.setVisibility(View.VISIBLE);
				started = false;
				sp.setEnabled(true);
			}
		}
	};
	
	protected void onDestroy() 
	{
		super.onDestroy();
		mySQLiteAdapter.close();
	}
	
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);	
		millisecondsHistory = milliseconds;
        secsHistory = secs;
        minsHistory = mins;
        
        timeInMillisecondsHistory = timeInMilliseconds;
        timeSwapBuffHistory = timeSwapBuff;
        updatedTimeHistory = updatedTime;
        startTimeHistory = startTime;
		
		outState.putSerializable("millisecondsHistory", millisecondsHistory);
		outState.putSerializable("secsHistory", secsHistory);
		outState.putSerializable("minsHistory", mins);
		
		outState.putSerializable("timeInMillisecondsHistory", timeInMillisecondsHistory);
		outState.putSerializable("timeSwapBuffHistory", timeSwapBuffHistory);
		outState.putSerializable("updatedTimeHistory", updatedTimeHistory);
		outState.putSerializable("startTimeHistory", startTimeHistory);
		outState.putSerializable("started", started);
	}
	
	protected void onRestoreInstanceState(Bundle savedState) 
	{	
    	millisecondsHistory = (Long) savedState.getSerializable("millisecondsHistory");
		milliseconds = millisecondsHistory;
		tvMills.setText(String.format("%03d", millisecondsHistory));
		
		secsHistory = (Long) savedState.getSerializable("secsHistory");
		secs = secsHistory;
		tvSecs.setText(String.format("%02d", secsHistory));
		
		minsHistory = (Long) savedState.getSerializable("minsHistory");
		mins = minsHistory;
		tvMins.setText(String.format("%01d", minsHistory));
		
		timeInMillisecondsHistory = (Long) savedState.getSerializable("timeInMillisecondsHistory");
		timeInMilliseconds = timeInMillisecondsHistory; 
		
		timeSwapBuffHistory = (Long) savedState.getSerializable("timeSwapBuffHistory");
		timeSwapBuff = timeSwapBuffHistory;
		
		updatedTimeHistory = (Long) savedState.getSerializable("updatedTimeHistory");
		updatedTime = updatedTimeHistory;
		
		startTimeHistory = (Long) savedState.getSerializable("startTimeHistory");
		startTime = startTimeHistory;
		
		started = (Boolean) savedState.getSerializable("started");
		if(started)
		{
			sp.setEnabled(false);
			flashButton.setVisibility(View.INVISIBLE);
			bangButton.setVisibility(View.VISIBLE);
			customHandler.postDelayed(updateTimerThread, 0);
		}
			
	}
}