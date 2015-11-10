package final_Project.final_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.*;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView tvTemperature;
	private TextView tvHumidity;
	private TextView tvLight_Digital;
	private TextView tvLight_Analog;
	private TextView tvStatus;
	private TextView tvWindow;
	private TextView tvCurtain;
	private TextView tvFan;
	private TextView tvLightOnOff;
	private TextView tvGas;
	private TextView tvFire;
	private TextView tvTheft1;
	private TextView tvTheft2;

	private ImageButton bSetting;
	private ImageButton bSpeech;
	
	public int iLightTemp;
	public int iFanTemp;
	public int iWindowTemp;
	public int iCurtainTemp;
	
	private double dTemperature;
	
	public int refreshTime = 4;	
	private int sensitivity = 20;
	
	public boolean findLightOn;
	public boolean findLightOff;
	public boolean findFanOn;
	public boolean findFanOff;
	public boolean findWindowOn;
	public boolean findWindowOff;
	public boolean findCurtainOn;
	public boolean findCurtainOff;

	private Handler handler=new Handler();
	
	private SensorManager sensorManager;
	private Sensor sensor;
	
	private long initTime = 0;
	private long lastTime = 0;
	private long curTime = 0;
	private long duration = 0;
	
	private float last_x = 0.0f;
	private float last_y = 0.0f;
	private float last_z = 0.0f;
	
	private float shake = 0.0f;
	private double speed = 0.0f;
	private double speedmax = 0.0f;
	private float totalShake = 0.0f;
	private float x,y,z;
	
	private double current_value_flame[] = {0, 0, 0, 0, 0};
	private double current_value_gas[] = {0, 0, 0, 0, 0};
	
	private Vibrator vibrator; 
	
	private MediaPlayer mpTemperature;
	private MediaPlayer mpHumidity;
	private MediaPlayer mpLight;
	private MediaPlayer mpCelsius;
	private MediaPlayer mpLux;
	private MediaPlayer mpThousand;
	private MediaPlayer mpHundred;
	private MediaPlayer mpTens;
	private MediaPlayer mpPoint;
	private MediaPlayer mpOne;
	private MediaPlayer mpTwo;
	private MediaPlayer mpThree;
	private MediaPlayer mpFour;
	private MediaPlayer mpFive;
	private MediaPlayer mpSix;
	private MediaPlayer mpSeven;
	private MediaPlayer mpEight;
	private MediaPlayer mpNine;
	private MediaPlayer mpZero;
	private MediaPlayer mpFire;
	private MediaPlayer mpGas;
	private MediaPlayer mpTheft1;
	private MediaPlayer mpTheft2;
	
	private RadioButton rbHigh;
	private RadioButton rbLow;
	
	private EditText etRefreshTime;
	
	private String sxDigital_Temperature = "";
	private String sxDigital_Humidity = "";
	private String sxLight_Digital = "";
	private String sxLight_Analog = "";
	private String syLightOnOff = "";
	private String syFanOnOff = "";
	private String syWindowOnOff = "";
	private String syCurtainOnOff = "";

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bSpeech = (ImageButton) this.findViewById(R.id.imageButton1);
		bSetting = (ImageButton) this.findViewById(R.id.imageButton2);
		
		tvTemperature = (TextView) this.findViewById(R.id.textView2);
		tvHumidity = (TextView) this.findViewById(R.id.textView3);
		tvLight_Digital = (TextView) this.findViewById(R.id.textView4);
		tvLight_Analog = (TextView) this.findViewById(R.id.textView38);
		tvStatus = (TextView) this.findViewById(R.id.textView5);
		tvCurtain = (TextView) this.findViewById(R.id.textView34);
		tvFan = (TextView) this.findViewById(R.id.textView6);
		tvLightOnOff = (TextView) this.findViewById(R.id.textView7);
		tvWindow = (TextView) this.findViewById(R.id.textView33);
		tvGas = (TextView) this.findViewById(R.id.textView25);
		tvFire = (TextView) this.findViewById(R.id.textView27);
		tvTheft1 = (TextView) this.findViewById(R.id.textView29);
		tvTheft2 = (TextView) this.findViewById(R.id.textView39);
		
		etRefreshTime = (EditText) this.findViewById(R.id.editText1);
		
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		mpTemperature = MediaPlayer.create(this, R.raw.temperature);
		mpHumidity = MediaPlayer.create(this, R.raw.humidity);
		mpLight = MediaPlayer.create(this, R.raw.light);
		mpCelsius = MediaPlayer.create(this, R.raw.celsius);
		mpLux = MediaPlayer.create(this, R.raw.lux);
		mpThousand = MediaPlayer.create(this, R.raw.thousand);
		mpHundred = MediaPlayer.create(this, R.raw.hundred);
		mpTens = MediaPlayer.create(this, R.raw.tens);
		mpPoint = MediaPlayer.create(this, R.raw.point);
		mpOne = MediaPlayer.create(this, R.raw.one);
		mpTwo = MediaPlayer.create(this, R.raw.two);
		mpThree = MediaPlayer.create(this, R.raw.three);
		mpFour = MediaPlayer.create(this, R.raw.four);
		mpFive = MediaPlayer.create(this, R.raw.five);
		mpSix = MediaPlayer.create(this, R.raw.six);
		mpSeven = MediaPlayer.create(this, R.raw.seven);
		mpEight = MediaPlayer.create(this, R.raw.eight);
		mpNine = MediaPlayer.create(this, R.raw.nine);
		mpZero = MediaPlayer.create(this, R.raw.zero);
		mpFire = MediaPlayer.create(this, R.raw.fire);
		mpGas = MediaPlayer.create(this, R.raw.gas);
		mpTheft1 = MediaPlayer.create(this, R.raw.theft1);
		mpTheft2 = MediaPlayer.create(this, R.raw.theft2);
		
		rbHigh = (RadioButton) this.findViewById(R.id.radioButton1);
		rbLow = (RadioButton) this.findViewById(R.id.radioButton2);

		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);   
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener = new SensorEventListener() {   
        	public void onSensorChanged(SensorEvent e) { 
        			x = e.values[SensorManager.DATA_X];   
        			y = e.values[SensorManager.DATA_Y];   
        			z = e.values[SensorManager.DATA_Z];   
        			float sum = x+y+z;	  
        			//tvJson7.setText("x="+x+" y="+y+" z="+z+" sum="+sum);
        			curTime = System.currentTimeMillis();
        			//100毫秒检测一次
        			if ((curTime - lastTime) > 100) 
        			{
        				duration = (curTime - lastTime);
        				// 看是不是刚开始晃动
        				if (last_x == 0.0f && last_y == 0.0f && last_z == 0.0f) {
        					//last_x、last_y、last_z同时为0时,表示刚刚开始记录
        					initTime = System.currentTimeMillis();
        				} 
        				else {
        					// 单次晃动幅度
        					float deltaX = x - last_x;
        					float deltaY = y - last_y;
        					float deltaZ = z - last_z;
        					shake = (Math.abs(deltaX) + Math.abs(deltaY) + Math.abs(deltaZ)) / duration * 100;
        					speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        					if(speed > speedmax) {
        						speedmax = speed;
        					}
        				}        			
        			//把每次的晃动幅度相加，得到总体晃动幅度
        				totalShake += shake;
        				if (totalShake > 100 && speedmax > sensitivity)
        				{
        					//tvJson8.setText("总体晃动幅度=" + totalShake + "\n最大速度=" + speedmax );
        					tvStatus.setText("Shaked!");
        					totalShake = 0;
        					speedmax = 0;
        					x=0;
        					y=0;
        					z=0;       				
        					mpTemperature.start();
        					int temp = 0;
        					while(mpTemperature.isPlaying())
        					{
        						tvStatus.setText("Shaked!");
        					}
        					temp = VoiceParseNumbers(sxDigital_Temperature);       				
        					mpCelsius.start();
        					while(mpCelsius.isPlaying())
        					{
        						tvStatus.setText("Shaked!");
        					}       				
        					mpHumidity.start();
        					while(mpHumidity.isPlaying())
        					{
        						tvStatus.setText("Shaked!");
        					}
        					temp = VoiceParseNumbers(sxDigital_Humidity);       				
        					mpLight.start();
        					while(mpLight.isPlaying())
        					{
        						tvStatus.setText("Shaked!");
        					}
        					temp = VoiceParseNumbers(sxLight_Digital);        				
        					mpLux.start();
        					while(mpLux.isPlaying())
        					{
        						tvStatus.setText("Shaked!");
        					}				
        				}		
        				//tvJson2.setText("总体晃动幅度="+totalShake+"\n平均晃动幅度="+totalShake / (curTime - initTime) * 1000 );
        			}	
        			last_x = x;
        			last_y = y;
        			last_z = z;
        			lastTime = curTime;	
        	}
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
        };
        	
		sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		bSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int temp;
				temp = Integer.parseInt(etRefreshTime.getText().toString());
				if(temp < 5) {
					tvStatus.setText("Wrong: The refreshing interval must be greater than 5!");
					Toast.makeText(MainActivity.this, "Wrong Input Number! \n Must Be GREATER Than 5!", Toast.LENGTH_SHORT).show();
				}
				else if(temp > 30) {
					tvStatus.setText("Wrong: The refreshing interval must be less than 30!");
					Toast.makeText(MainActivity.this, "Wrong Input Number! \n Must Be LESS Than 30!", Toast.LENGTH_SHORT).show();
				}
				else {
					refreshTime = temp;
					tvStatus.setText("Refresh Time is now " + temp +" seconds.");
				}
			}
		});
		
		Runnable runnable=new Runnable() {
		    @Override
		    public void run() {
				// TODO Auto-generated method stub
		    	handler.postDelayed(this, refreshTime * 1000);
				HttpClient client = new DefaultHttpClient();
				String getURL = "http://api.xively.com/v2/feeds/2037530706.json";
				HttpGet getter = new HttpGet(getURL);
				getter.setHeader("X-ApiKey", "T9XdvcAmX-yjDp5tJgTnJealuamSAKxuaUlLV2UwNWxoaz0g");
				HttpResponse response;
				String strResult = "";
				try {
					response = client.execute(getter);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						strResult = EntityUtils.toString(response.getEntity());
						tvStatus.setText("Online Success.");
						// tvJson.setText(strResult);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tvStatus.setText("protocol error" + e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tvStatus.setText("IO error" + e);
				}
				tvStatus.setText("Refresh Success.");
				parseJsonMulti(strResult);
			}
		};
		
		handler.postDelayed(runnable, refreshTime*1000);
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			bSpeech.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			bSpeech.setEnabled(false);
			bSpeech.setTag("Recognizer not present");
		}
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			findLightOn = matches.contains("开灯");
			findLightOff = matches.contains("关灯");
			findFanOn = matches.contains("开电扇");
			findFanOff = matches.contains("关电扇");
			findWindowOn = matches.contains("开窗");
			findWindowOff = matches.contains("关窗");
			findCurtainOn = matches.contains("开窗帘");
			findCurtainOff = matches.contains("关窗帘");
			
			if (findLightOn && !findLightOff) {
				tvStatus.setText("Light On!");
				iLightTemp = 1;
			} else if (!findLightOn && findLightOff) {
				tvStatus.setText("Light Off!");
				iLightTemp = 0;
			} else if (findFanOn && !findFanOff) {
				tvStatus.setText("Fan On!");
				iFanTemp = 1;
			} else if (!findFanOn && findFanOff) {
				tvStatus.setText("Fan Off!");
				iFanTemp = 0;
			} else if (findWindowOn && !findWindowOff) {
				tvStatus.setText("Window On!");
				iWindowTemp = 1;
			} else if (!findWindowOn && findWindowOff) {
				tvStatus.setText("Window Off!");
				iWindowTemp = 0;
			} else if (findCurtainOn && !findCurtainOff) {
				tvStatus.setText("Curtain On!");
				iCurtainTemp = 1;
			} else if (!findCurtainOn && findCurtainOff) {
				tvStatus.setText("Curtain Off!");
				iCurtainTemp = 0;
			} else if (!findLightOn && !findLightOff && !findFanOn && !findFanOff && !findCurtainOn && !findCurtainOff && !findWindowOn && !findWindowOff) {
				tvStatus.setText("Not found.");
			} else {
				tvStatus.setText("Voice Recogintion Wrong.");
			}
			try {
				HttpEntity strResult = null;
				HttpClient httpclient = new DefaultHttpClient();
				String uri = "http://api.xively.com/v2/feeds/2037530706.json";
				HttpPut httpput = new HttpPut(uri);
				httpput.addHeader("X-ApiKey", "T9XdvcAmX-yjDp5tJgTnJealuamSAKxuaUlLV2UwNWxoaz0g");
				String a = "{\"version\":\"1.0.0\",\"datastreams\":[{\"id\":\"yCurtainOnOff\",\"current_value\":\""
						+ iCurtainTemp + "\"},{\"id\":\"yFanOnOff\",\"current_value\":\""
						+ iFanTemp + "\"},{\"id\":\"yLightOnOff\",\"current_value\":\""
						+ iLightTemp + "\"},{\"id\":\"yWindowOnOff\",\"current_value\":\""
						+ iWindowTemp + "\"}]}";
				httpput.setEntity(new StringEntity(a));
				HttpResponse response;
				response = httpclient.execute(httpput);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					strResult = response.getEntity();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onRadioButtonClicked(View view) {
		//Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		//Check which button was clicked
		switch(view.getId()) {
			case R.id.radioButton1:
				if(checked){
					rbLow.setChecked(false);
					sensitivity = 40;
					tvStatus.setText("Shake Sensitivity is now HIGH.");
				}
				break;
			case R.id.radioButton2:
				if(checked) {
					rbHigh.setChecked(false);
					sensitivity = 20;
					tvStatus.setText("Shake Sensitivity is now LOW.");
				}
				break;
		}
	}
	
	private void parseJsonMulti(String strResult) {
		try {
			JSONArray jsObjs = new JSONObject(strResult).getJSONArray("datastreams");
			tvStatus.setText("Refresh Success.");
			for (int i = 0; i < jsObjs.length(); i++) {
				JSONObject jsObj = ((JSONObject) jsObjs.opt(i));
				String id = jsObj.getString("id");
				if (id.equals("xDigital_Humidity")) {
					double current_value = jsObj.getDouble("current_value");
					sxDigital_Humidity = String.valueOf(current_value);
					dTemperature = current_value;
					tvHumidity.setText(sxDigital_Humidity);
				} else if (id.equals("xDigital_Temperature")) {
					double current_value = jsObj.getDouble("current_value");
					sxDigital_Temperature = String.valueOf(current_value);
					tvTemperature.setText(sxDigital_Temperature);
				} else if (id.equals("xLight_Analog")) {
					double current_value = jsObj.getDouble("current_value");
					sxLight_Analog = String.valueOf(current_value);
					tvLight_Analog.setText(sxLight_Analog);
				} else if (id.equals("xLight_Digital")) {
					double current_value = jsObj.getDouble("current_value");
					sxLight_Digital = String.valueOf(current_value);
					tvLight_Digital.setText(sxLight_Digital);
				} else if (id.equals("yCurtainOnOff")) {
					int current_value = jsObj.getInt("current_value");
					iCurtainTemp = current_value;
					if (current_value == 1) {
						syCurtainOnOff = "On";
					} else if (current_value == 0) {
						syCurtainOnOff = "Off";
					} else {
						syCurtainOnOff = "N/A";
					}
					tvCurtain.setText(syCurtainOnOff);
				} else if (id.equals("yFanOnOff")) {
					int current_value = jsObj.getInt("current_value");
					iFanTemp = current_value;
					if (current_value == 1) {
						syFanOnOff = "On";
					} else if (current_value == 0) {
						syFanOnOff = "Off";
					} else {
						syFanOnOff = "N/A";
					}
					tvFan.setText(syFanOnOff);
				} else if (id.equals("yLightOnOff")) {
					int current_value = jsObj.getInt("current_value");
					iLightTemp = current_value;
					if (current_value == 1) {
						syLightOnOff = "On";
					} else if (current_value == 0) {
						syLightOnOff = "Off";
					} else {
						syLightOnOff = "N/A";
					}
					tvLightOnOff.setText(syLightOnOff);
				} else if (id.equals("yWindowOnOff")) {
					int current_value = jsObj.getInt("current_value");
					iWindowTemp = current_value;
					if (current_value == 1) {
						syWindowOnOff = "On";
					} else if (current_value == 0) {
						syWindowOnOff = "Off";
					} else {
						syWindowOnOff = "N/A";
					}
					tvWindow.setText(syWindowOnOff);
				} else if (id.equals("zBody_Detection")) {
					int current_value = jsObj.getInt("current_value");
					if(current_value == 1) {
						tvTheft1.setText("Alarm!");
						tvStatus.setText("Someone get into your home!!Body Detected.");
						for(int i1 = 0; i1 < 3; i1++) {
							mpTheft1.start();
						}
						long [] pattern = {200, 800, 200, 800, 200, 800, 200, 800};
						vibrator.vibrate(pattern, -1);
						tvTheft1.setText("Alarm!");
						tvStatus.setText("Someone get into your home!!Body Detected.");
					}
					else {
						tvTheft1.setText("OK");
					}
				} else if (id.equals("zFlame")) {
					current_value_flame[4] = jsObj.getDouble("current_value");
					for(int i2 = 0; i2 < 4; i2++) {
						current_value_flame[i2] = current_value_flame[i2+1];
					}
					int temp = 0;
					for(int i2 = 0; i2 < 5; i2++) {
						if(current_value_flame[i2] > 30) {
							temp++;
						}
					}
					if(temp == 5 && dTemperature > 40) {
						tvFire.setText("Alarm!");
						tvStatus.setText("Fire in your home!!");
						String telNum = "119";
						if(PhoneNumberUtils.isGlobalPhoneNumber(telNum)) {
							Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telNum));
							MainActivity.this.startActivity(intent);
						}
						else {
							Toast.makeText(MainActivity.this, "Wrong Number", Toast.LENGTH_SHORT).show();
						}
						for(int i1 = 0; i1 < 3; i1++) {
							mpFire.start();
						}
						long [] pattern = {200, 800, 200, 800, 200, 800, 200, 800};
						vibrator.vibrate(pattern, -1);
						tvFire.setText("Alarm!");
						tvStatus.setText("Fire in your home!!");
					}
					else {
						tvFire.setText("OK");
					}
				} else if (id.equals("zGas_MQ5")) {
					current_value_gas[4] = jsObj.getDouble("current_value");
					for(int i3 = 0; i3 < 4; i3++) {
						current_value_gas[i3] = current_value_gas[i3+1];
					}
					int temp = 0;
					for(int i3 = 0; i3 < 5; i3++) {
						if(current_value_gas[i3] > 30) {
							temp++;
						}
					}
					if(temp == 5) {
						tvGas.setText("Alarm!");
						tvStatus.setText("Gas leaking in your home!!");
						for(int i1 = 0; i1 < 3; i1++) {
							mpGas.start();
						}
						long [] pattern = {200, 800, 200, 800, 200, 800, 200, 800};
						vibrator.vibrate(pattern, -1);
						tvGas.setText("Alarm!");
						tvStatus.setText("Gas leaking in your home!!");
					}
					else {
						tvGas.setText("OK");
					}
				} else if (id.equals("zSound")) {
					double current_value = jsObj.getDouble("current_value");
					if(current_value > 1) {
						tvTheft2.setText("Alarm!");
						tvStatus.setText("Someone get into your home!!Sound Detected.");
						for(int i4 = 0; i4 < 3; i4++) {
							mpTheft2.start();
						}
						long [] pattern = {200, 800, 200, 800, 200, 800, 200, 800};
						vibrator.vibrate(pattern, -1);
						tvTheft2.setText("Alarm!");
						tvStatus.setText("Someone get into your home!!Sound Detected.");
					}
					else {
						tvTheft2.setText("OK");
					}
				} else {
				}
			}
			//tvStatus.setText("Refresh Success.");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tvStatus.setText("JSONException:" + e);
		}
	}
	
	public int VoiceParseNumbers(String s) {
		float temp = Float.parseFloat(s);
		int temp_tho = ((int) (temp / 1000)) % 10;
		int temp_hun = ((int) (temp / 100)) % 10;
		int temp_ten = ((int) (temp / 10)) % 10;
		int temp_sin = ((int) (temp % 10));
		int temp_dec1 = ((int) (temp * 10)) % 10;
		
		if(temp_tho == 0) {
			if(temp_hun == 0) {
				if(temp_ten == 0) {
					if(temp_sin == 0) {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
				else {
					VoiceNumber(temp_ten);
					mpTens.start();
					while(mpTens.isPlaying()) {	
					}
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
			}
			else {
				VoiceNumber(temp_hun);
				mpHundred.start();
				while(mpHundred.isPlaying()) {	
				}
				VoiceNumber(temp_ten);
				if(temp_ten == 0) {
					if(temp_sin == 0) {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
				else {
					mpTens.start();
					while(mpTens.isPlaying()) {
					}
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
			}
		}
		else {
			VoiceNumber(temp_tho);
			mpThousand.start();
			while(mpThousand.isPlaying()) {	
			}
			VoiceNumber(temp_hun);
			if(temp_hun == 0) {
				VoiceNumber(temp_ten);
				if(temp_ten == 0) {
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
				else {
					mpTens.start();
					while(mpTens.isPlaying()) {	
					}
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
			}
			else {
				mpHundred.start();
				while(mpHundred.isPlaying()) {	
				}
				VoiceNumber(temp_ten);
				if(temp_ten == 0) {
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
				else {
					mpTens.start();
					while(mpTens.isPlaying()) {	
					}
					if(temp_sin == 0) {
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
					else {
						VoiceNumber(temp_sin);
						mpPoint.start();
						while(mpPoint.isPlaying()) {	
						}
						VoiceNumber(temp_dec1);
					}
				}
			}
		}
		return 1;
	}
	
	public void VoiceNumber(int a) {
		switch (a) {
		case 1:
			mpOne.start();
			while(mpOne.isPlaying()) {	
			}
			break;
		case 2:
			mpTwo.start();
			while(mpTwo.isPlaying()) {	
			}
			break;
		case 3:
			mpThree.start();
			while(mpThree.isPlaying()) {	
			}
			break;
		case 4:
			mpFour.start();
			while(mpFour.isPlaying()) {	
			}
			break;
		case 5:
			mpFive.start();
			while(mpFive.isPlaying()) {	
			}
			break;
		case 6:
			mpSix.start();
			while(mpSix.isPlaying()) {	
			}
			break;
		case 7:
			mpSeven.start();
			while(mpSeven.isPlaying()) {	
			}
			break;
		case 8:
			mpEight.start();
			while(mpEight.isPlaying()) {	
			}
			break;
		case 9:
			mpNine.start();
			while(mpNine.isPlaying()) {	
			}
			break;
		case 0:
			mpZero.start();
			while(mpZero.isPlaying()) {
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

