package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    private static final String TAG = "ScannerActivity";
    public static final String SCAN_FORMAT = "scanFormat";
    public static final String SCAN_CONTENTS = "scanContents";

    public static void startActivityForResult(Fragment fragment, int requestCode) {
        Activity activity = fragment.getActivity();
        Intent intent = new Intent(activity, ScannerActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }


    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);                // Set the scanner view as the content view

        mScannerView = (ZBarScannerView)findViewById(R.id.zbs_scanner);
        mScannerView.setFormats(Arrays.asList(BarcodeFormat.EAN13));
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult != null) {
            Log.v(TAG, rawResult.getContents()); // Prints scan results
            Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

            //send the result to back activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(SCAN_FORMAT, (rawResult.getBarcodeFormat() != null ? rawResult.getBarcodeFormat().getName() : null));
            resultIntent.putExtra(SCAN_CONTENTS, rawResult.getContents());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
