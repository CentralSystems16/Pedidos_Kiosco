package com.pedidos.kiosco.desing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.main.ObtenerManual;

public class acercaDe extends AppCompatActivity {

    TextView versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        versionCode = findViewById(R.id.versionCode);
        versionCode.setText(String.valueOf(getVersionName(getApplicationContext())));

        ImageView politica = findViewById(R.id.politica);
        politica.setOnClickListener(v -> {

                    goToPolitica();

        });

        ImageView error = findViewById(R.id.error);
        error.setOnClickListener(v -> {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"centralsystemsmanage@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto: quiero reportar un error en la app");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "El error consiste en:");
                    startActivity(emailIntent);

        });

        ImageView facebook = findViewById(R.id.facebook);
        facebook.setOnClickListener(v -> {

            Uri uri = Uri.parse("https://www.facebook.com/");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));

        });

        ImageView manual = findViewById(R.id.manual);
        manual.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), ObtenerManual.class));

        });

        ImageView gmail = findViewById(R.id.gmail);
        gmail.setOnClickListener(v -> {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"centralsystemsmanage2@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto: ");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Quiero comunicarme con ustedes porque:");
                    startActivity(emailIntent);

        });
    }

    public String getVersionName(Context ctx){
        try {
            return ctx.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void goToPolitica() {
        /*MyDialogTerminos myDialogFragment = new MyDialogTerminos();
        myDialogFragment.show(getSupportFragmentManager(), "MyFragment");
        myDialogFragment.setCancelable(false);*/
        Toast.makeText(getApplicationContext(), "Politicas desabilitadas, intentelo nuevamente mas tarde!", Toast.LENGTH_SHORT).show();
    }

}