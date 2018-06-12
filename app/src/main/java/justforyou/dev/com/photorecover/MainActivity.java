package justforyou.dev.com.photorecover;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    Button btnbegin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //Admob
            AdRequest adRequest = new AdRequest.Builder().build();
            checkForConsent();
            // Prepare the Interstitial Ad
            interstitial = new InterstitialAd(MainActivity.this);
            interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    displayInterstitial();
                }
            });
            Button btndpd = findViewById(R.id.pdp);
            btndpd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConsentInformation.getInstance(getBaseContext())
                            .isRequestLocationInEeaOrUnknown()) {
                        requestConsent();
                    }else{
                        Toast.makeText(getApplicationContext(),"This option is available only for European Users",Toast.LENGTH_LONG).show();}
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
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
    //======================================== GDBR ADS ======================
    private AdView mAdView;
    private ConsentForm form;
    private static final String TAG = MainActivity.class.getSimpleName();


    private void checkForConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {"pub-5227911034604828"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "Showing Personalized ads");
                        showPersonalizedAds();
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "Showing Non-Personalized ads");
                        showNonPersonalizedAds();
                        break;
                    case UNKNOWN:
                        Log.d(TAG, "Requesting Consent");
                        if (ConsentInformation.getInstance(getBaseContext())
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            showPersonalizedAds();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL("your privacy link");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "Requesting Consent: onConsentFormLoaded");
                        showForm();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "Requesting Consent: onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "Requesting Consent: onConsentFormClosed");
                        if (userPrefersAdFree) {
                            // Buy or Subscribe
                            Log.d(TAG, "Requesting Consent: User prefers AdFree");
                        } else {
                            Log.d(TAG, "Requesting Consent: Requesting consent again");
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    showPersonalizedAds();break;
                                case NON_PERSONALIZED:
                                    showNonPersonalizedAds();break;
                                case UNKNOWN:
                                    showNonPersonalizedAds();break;
                            }

                        }
                        // Consent form was closed.
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d(TAG, "Requesting Consent: onConsentFormError. Error - " + errorDescription);
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showPersonalizedAds() {
        //banner
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


    }

    private void showNonPersonalizedAds() {

        //banner
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build();
        mAdView.loadAd(adRequest);

    }

    public Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");

        return extras;
    }

    private void showForm() {
        if (form == null) {
            Log.d(TAG, "Consent form is null");
        }
        if (form != null) {
            Log.d(TAG, "Showing consent form");
            form.show();
        } else {
            Log.d(TAG, "Not Showing consent form");
        }
    }
    //==================END ADS GDPR================

}
