package com.ting.app;

import android.app.*;
import android.content.res.*;
import android.os.*;
import android.view.*;
import com.ting.app.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.settings:
				//newGame();
				return true;
			case R.id.about:
				showAbout();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void showAbout() {
		// Do something in response to button
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String username = editText.getText().toString();
		int mailCount = 0;

		Resources res = getResources();
		String message = String.format(res.getString(R.string.about_msg),
									   mailCount);				

 		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
		builder
			.setTitle(R.string.app_name)
		    .setMessage(message)
			.setNeutralButton(R.string.ok, null);

		builder.show();

        // 3. Get the AlertDialog from create()
		// AlertDialog dialog = builder.create();
	}	
}
