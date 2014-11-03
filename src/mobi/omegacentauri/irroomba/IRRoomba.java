package mobi.omegacentauri.irroomba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class IRRoomba extends Activity {
	private IRPlayer irPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("IRRoomba", "OnCreate");

		setContentView(R.layout.main);

		register(R.id.arc_left, "infinite:roomba:139");
		register(R.id.forward, "infinite:roomba:130");
		register(R.id.arc_right, "infinite:roomba:140");
		register(R.id.left, "infinite:roomba:129");
		register(R.id.stop, "time=500000:roomba:137");
		register(R.id.right, "infinite:roomba:131");
		register(R.id.left_1sec, "time=1000000:roomba:129");
		register(R.id.right_1sec, "time=1000000:roomba:131");
		register(R.id.small, "time=500000:roomba:134");
		register(R.id.medium, "time=500000:roomba:135");
		register(R.id.large, "time=500000:roomba:136");
		register(R.id.spot, "time=500000:roomba:132");
		register(R.id.max, "time=500000:roomba:133");
		register(R.id.power, "time=1000000:roomba:138");
		register(R.id.dock, "time=500000:roomba:143");
	}

	private void register(int id, final String cmd) {
		Button b = (Button)findViewById(id);
		final boolean infinite = cmd.startsWith("infinite:");
		b.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN) {
					irPlayer.play(new IRCommand(cmd));
				}
				else if (infinite && event.getAction()==MotionEvent.ACTION_UP) {
					irPlayer.stopPlaying();
				}
				return false;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		irPlayer.stop();
		irPlayer = null;
	}

	@Override
	public void onResume() {
		super.onResume();

		irPlayer = new IRPlayer(this);
	}	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.options:
			Intent i = new Intent(this, Options.class);
			startActivity(i);
			return true;
		}
		return false;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

}
