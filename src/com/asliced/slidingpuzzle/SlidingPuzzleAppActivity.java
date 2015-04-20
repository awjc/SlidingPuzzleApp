package com.asliced.slidingpuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SlidingPuzzleAppActivity extends Activity {
	PlayerView playerView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		String img = p.getString(PreferenceActivity.INCLUDED_PICS, null);

		playerView = new PlayerView(this, img);
		setContentView(playerView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gamemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.choose_pic:
			choosePic();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed(){
		new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.quit)
			.setMessage(R.string.reallyquit)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SlidingPuzzleAppActivity.this.finish();    
				}
			})
			.setNegativeButton(R.string.no, null)
			.show();
	}

	public void choosePic(){
		Intent myIntent = new Intent(playerView.getContext(), PreferenceActivity.class);
		startActivity(myIntent);
	}
}