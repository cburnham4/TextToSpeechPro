package letshangllc.tts;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private TextToSpeech textToSpeech;

    private AdsHelper adsHelper;

    private Toolbar toolbar;

    private EditText etText;
    private SeekBar sbPitch, sbSpeed;
    private Button btnPostMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.ERROR){
                    Log.e(TAG, "ERROR");
                }

                textToSpeech.setLanguage(Locale.ENGLISH);
                textToSpeech.setSpeechRate(1.0f);

            }
        });




        this.setToolbar();
        this.findViews();
        this.setupViews();

        adsHelper = new AdsHelper(this.getWindow().getDecorView(), getResources().getString(R.string.admob_id),this);
        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);
    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(getResources().getString(R.string.app_name));

    }

    private void findViews(){
        etText = (EditText) findViewById(R.id.etText);
        btnPostMessage = (Button) findViewById(R.id.btnPostMessage);

        sbPitch = (SeekBar) findViewById(R.id.sbPitch);
        sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
    }

    private void setupViews(){
        sbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textToSpeech.setPitch((progress)/50.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress ==0 ){
                    progress = 5;
                }
                float rate  = progress/50.0f;
                textToSpeech.setSpeechRate(rate);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void playTextOnClick(View view){
        String text = etText.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(this, "Please Enter Text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }

    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
