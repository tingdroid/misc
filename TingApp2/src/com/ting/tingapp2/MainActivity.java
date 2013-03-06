package com.ting.tingapp2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	boolean toggleChecked(MenuItem item) {
		if (item.isChecked()) {
			item.setChecked(false);
			item.setIcon(R.drawable.check_0);
		} else {
			item.setChecked(true);
			item.setIcon(R.drawable.check_1);					
		}
		return item.isChecked();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.settings:
				showSettings();
				return true;
			case R.id.about:
				showAbout();
				return true;
			case R.id.mute:
			    boolean muted = toggleChecked(item);
				return true;
			case R.id.alert:
			    boolean alerting = toggleChecked(item);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showSettings()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void showAbout()
	{
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
