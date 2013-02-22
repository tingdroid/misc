package com.mycompany.myapp;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.res.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	
	/** Called when the user clicks the Send button */
	public void greetMessage(View view) {
		// Do something in response to button
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String username = editText.getText().toString();
		int mailCount = 0;
        
		Resources res = getResources();
		String message = String.format(res.getString(R.string.greet_msg),
									   username, mailCount);				

 		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
		builder
			.setTitle(R.string.greet_title)
		    .setMessage(message)
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
					Toast.makeText(MainActivity.this, R.string.welcome, Toast.LENGTH_LONG).show();
				}
			});
			
		builder.show();

        // 3. Get the AlertDialog from create()
		// AlertDialog dialog = builder.create();
	}
}

