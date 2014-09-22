package mobi.omegacentauri.irroomba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Options extends PreferenceActivity {
	static final String PREF_STEREO = "stereo";
	static final int OPT_STEREO_SAME = 0;
	static final int OPT_STEREO_90 = 1;
	static final int OPT_STEREO_180 = 2;
	static final String PREF_AUDIO_MODE = "audioMode";
	static final int OPT_AUDIO_MODE_PCM16 = 0;
	static final int OPT_AUDIO_MODE_PCM8 = 1;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		addPreferencesFromResource(R.xml.options);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
}
