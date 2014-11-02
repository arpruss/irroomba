package mobi.omegacentauri.irroomba;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.preference.PreferenceManager;
import android.util.Log;

public class IRPlayer {
	Context context;
	AudioTrack track;
	Thread writeThread = null;
	static final int STREAM = AudioManager.STREAM_MUSIC;
	int count;
	long startTime;
	long endTime;
	int startVolume;
	int stereoMode;
	int pcmMode;
	private AudioManager audioManager;

	public IRPlayer(Context context) {
		this.context = context;
		SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(context);
		stereoMode = Integer.parseInt(options.getString(Options.PREF_STEREO, ""+Options.OPT_STEREO_SAME));
		pcmMode = Integer.parseInt(options.getString(Options.PREF_AUDIO_MODE, ""+Options.OPT_AUDIO_MODE_PCM16));
		track = null;
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		startVolume = audioManager.getStreamVolume(STREAM);
	}

	public void stop() {	
		stopPlaying();
		audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, startVolume, 0);
	}

	public void stopPlaying() {
		if (track != null) {
			try {
				track.flush();
			}
			catch (Exception e) {
				Log.e("IRServer", "flushing track error "+e);
			}
			try {
				track.stop();
			}
			catch (Exception e) {
				Log.e("IRServer", "stopping track error "+e);
			}
			try {
				track.release();
			}
			catch (Exception e) {
				Log.e("IRServer", "releasing track error "+e);
			}
			track = null;
		}
	}

	public void play(final IRCommand command) {
		Log.v("IRServer", "play");
		stopPlaying();
		
		Log.v("IRServer", "commanded to play in mode "+command.playMode);

		if (command.playMode == IRCommand.PLAY_STOP) {
			return;
		}

		Log.v("IRServer", "playing on carrier "+command.carrier);

		final IRToAudio converter = new IRToAudio(command, stereoMode, pcmMode);
		final byte[] samples = converter.getSamples();

		int format = (converter.bits == 16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
		int bufferSize = AudioTrack.getMinBufferSize(IRToAudio.SAMPLE_FREQ, AudioFormat.CHANNEL_CONFIGURATION_STEREO, format);
		if (bufferSize < samples.length)
			bufferSize = samples.length;
		try {
			track = new AudioTrack(STREAM, IRToAudio.SAMPLE_FREQ, 
					(converter.channels == 2) ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO, 
							format, 
							bufferSize, AudioTrack.MODE_STREAM);
		}
		catch (Exception e) {
			Log.e("IRRoomba", "IRPlayer error "+e);
			track = null;
		}
		track.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
		track.play();

		writeThread = new Thread(new Runnable(){
			public void run() {
				try {
					audioManager.setStreamVolume(STREAM, audioManager.getStreamMaxVolume(STREAM), 0);
					long time = 0;
					int count = 0;
					while(!done(count, time)) {
						track.write(samples, 0, samples.length);
						time += converter.getSamplesTimeMicroseconds();
						count++;
					}
				}
				catch(Exception e) {
				}
			}

			private boolean done(int count, long time) {
				if (Thread.interrupted())
					return true;
				switch(command.playMode) {
				case IRCommand.PLAY_INFINITE:
					return false;
				case IRCommand.PLAY_ONCE:
					return count > 0;
				case IRCommand.PLAY_COUNT:
					return count >= command.repeatCount;
				case IRCommand.PLAY_TIME:
					return time >= command.repeatTimeMicroseconds;
				default:
					return true;
				}
			}});

		writeThread.start();		
	}	
}
