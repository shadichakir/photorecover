package justforyou.dev.com.photorecover;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    AlertDialog dialog;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
    private InterstitialAd interstitial;
    Button btnbegin;
    TextView t1,t2;
    private static final String TAG ="MainActivity ----- : " ;
    ConsentForm form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
            ConsentInformation.getInstance(MainActivity.this).addTestDevice("62577C087ADC5497524E2FAF2B0E67AE");
            String[] publisherIds = {"pub-5227911034604828"};
            consentInformation.requestConsentInfoUpdate(publisherIds, new
                    ConsentInfoUpdateListener() {
                        @Override
                        public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                            Log.d(TAG,"onConsentInfoUpdated");
                            switch (consentStatus){
                                case PERSONALIZED:
                                    Log.d(TAG,"PERSONALIZED");
                                    ConsentInformation.getInstance(MainActivity.this)
                                            .setConsentStatus(ConsentStatus.PERSONALIZED);
                                    break;
                                case NON_PERSONALIZED:
                                    Log.d(TAG,"NON_PERSONALIZED");
                                    ConsentInformation.getInstance(MainActivity.this)
                                            .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                    break;
                                case UNKNOWN:
                                    Log.d(TAG,"UNKNOWN");
                                    if(ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown()){
                                        URL privacyUrl = null;
                                        try {
                                            privacyUrl = new URL("https://sites.google.com/view/justforyou-privacypolicy/accueil");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            // Handle error.
                                        }
                                        form = new ConsentForm.Builder(MainActivity.this,
                                                privacyUrl)
                                                .withListener(new ConsentFormListener() {
                                                    @Override
                                                    public void onConsentFormLoaded() {
                                                        // Consent form loaded successfully.
                                                        Log.d(TAG,"onConsentFormLoaded");
                                                        showform();
                                                    }
                                                    @Override
                                                    public void onConsentFormOpened() {
                                                        // Consent form was displayed.
                                                        Log.d(TAG,"onConsentFormOpened");
                                                    }
                                                    @Override
                                                    public void onConsentFormClosed(
                                                            ConsentStatus consentStatus,
                                                            Boolean userPrefersAdFree) {
                                                        // Consent form was closed.
                                                        Log.d(TAG,"onConsentFormClosed");
                                                    }
                                                    @Override
                                                    public void onConsentFormError(String
                                                                                           errorDescription) {
                                                        // Consent form error.
                                                        Log.d(TAG,"onConsentFormError");
                                                        Log.d(TAG,errorDescription);
                                                    }
                                                })
                                                .withPersonalizedAdsOption()
                                                .withNonPersonalizedAdsOption()
                                                .build();
                                        form.load();
                                    } else {
                                        Log.d(TAG,"PERSONALIZED else");
                                        ConsentInformation.getInstance(MainActivity.this)
                                                .setConsentStatus(ConsentStatus.PERSONALIZED);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        @Override
                        public void onFailedToUpdateConsentInfo(String errorDescription) {
                            // User's consent status failed to update.
                            Log.d(TAG,"onFailedToUpdateConsentInfo");
                            Log.d(TAG,errorDescription);
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        //Admob
        AdRequest adRequest = new AdRequest.Builder().build();
        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });

        btnbegin = findViewById(R.id.beginBtn);
        btnbegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable())
                {
                    Intent i = new Intent(getBaseContext(), AppUsedActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"المرجو ربط الاتصال بالانترنت !",Toast.LENGTH_LONG).show();
                }
            }
        });
        t1 =findViewById(R.id.privacy);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyPolicyEvent();

            }
        });
        builder = new AlertDialog.Builder(this);
        builder.setMessage("By using this application, you represent that you have read our Privacy Policy");
        builder.setCancelable(true);

        builder.setNegativeButton(
                "Privacy Policy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/justforyou-privacypolicy/accueil")));
                    }
                });

        builder.setPositiveButton(
                "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();
        t2 = findViewById(R.id.rating);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
    }
    private void showform(){
        if (form!=null){
            Log.d(TAG,"show ok");
            form.show();
        }
    }
    public void privacyPolicyEvent(){
        dialog.show();
    }
}
