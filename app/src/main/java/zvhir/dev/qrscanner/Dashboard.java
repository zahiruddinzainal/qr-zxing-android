package zvhir.dev.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Dashboard extends AppCompatActivity {

    Button button;
    String TAG = "GenerateQrCode";
    QRGEncoder qrgEncoder;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
    }

    private void scanCode() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("\nScan QR code\n\n\n");
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        final String textDariQR = result.getContents();

        if (textDariQR == null){
            //do nothing
        }
        else {
            Dialog dialog = new Dialog(Dashboard.this);
            dialog.setContentView(R.layout.dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // set adapter background jadi transparent

            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText(textDariQR);

            //Convert text to QR
            String textToQR = textDariQR;
            ImageView qrImage = (ImageView) dialog.findViewById(R.id.imageView);
            WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerdimension = width < height ? width:height;
            smallerdimension = smallerdimension*3/4;
            qrgEncoder = new QRGEncoder(textToQR, null, QRGContents.Type.TEXT, smallerdimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                qrImage.setImageBitmap(bitmap);
            }
            catch (WriterException e){
                Log.v(TAG, e.toString());
            }

            dialog.show();



            Button copy = (Button) dialog.findViewById(R.id.copyButton);

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", textDariQR);
                    clipboard.setPrimaryClip(clip);

                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Text copied", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            Button gotolink = (Button) dialog.findViewById(R.id.gotolink);
            gotolink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(textDariQR); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

        }

    }

}



