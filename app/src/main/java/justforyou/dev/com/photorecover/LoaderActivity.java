package justforyou.dev.com.photorecover;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class LoaderActivity extends AppCompatActivity {
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        //Admob

        AdRequest adRequest = new AdRequest.Builder().build();
        // Prepare the Interstitial Ad
        AdView mAdView = findViewById(R.id.ad_view);
        mAdView.loadAd(adRequest);
        interstitial = new InterstitialAd(LoaderActivity.this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });

        Resources res = getResources();

        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        tv = (TextView) findViewById(R.id.tv);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus < 100) {
                    pStatus += 2;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");
                        }
                    });
                    if (pStatus==34 || pStatus==66)
                    {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (pStatus==98)
                    {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(pStatus==100)
                    {
                        Intent i = new Intent(getBaseContext(), RestoredImagesActivity.class);
                        startActivity(i);
                    }
                    try {
                        Thread.sleep(400); //thread will take approx 3 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
