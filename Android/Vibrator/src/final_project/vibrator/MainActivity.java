package final_project.vibrator;

import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Vibrator vibrator; 
	private ImageButton bVibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bVibrator = (ImageButton)findViewById(R.id.imageButton1);
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		bVibrator.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long [] pattern = {200, 800, 200, 800, 200, 800, 200, 800};
				vibrator.vibrate(pattern, -1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater(); 
		inflater.inflate(R.menu.main, menu);
		//menu.add(1,1,1,"Exit");
		//menu.add(1,2,2,"Setting");
		//menu.add(2,3,3,"About");
		//menu.add(2,4,4,"");
		return true;
	}
	
	public boolean onOptionItemSelected(MenuItem item){
		switch(item.getItemId())
		{
		case 1:
			{
			Intent intent = new Intent(Intent.ACTION_MAIN);  
            intent.addCategory(Intent.CATEGORY_HOME);  
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            startActivity(intent);  
            android.os.Process.killProcess(android.os.Process.myPid());
			}
            break;
		case R.id.settings:
			{
				startActivity(new Intent(this, Settings.class));
			}
			break;
		case 3:
			{
			Toast toast2 = Toast.makeText(getApplicationContext(), "This is a Final Project of Xu Yiting 092732 ", Toast.LENGTH_LONG);
			toast2.setGravity(Gravity.CENTER, 0, 0);
			toast2.show();
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	protected void onStop() {   
		if(null!=vibrator){   
		vibrator.cancel();   
		}   
		super.onStop();   
		}   

}
