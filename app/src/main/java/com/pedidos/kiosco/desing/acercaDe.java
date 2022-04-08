package com.pedidos.kiosco.desing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pedidos.kiosco.R;

public class acercaDe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

                    goToFacebook();

        });

        ImageView gmail = findViewById(R.id.gmail);
        gmail.setOnClickListener(v -> {

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"centralsystemsmanage2@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto: ");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Quiero comunicarme con Taqueria's Cachinflin porque:");
                    startActivity(emailIntent);

        });
    }

    private void goToPolitica() {
        /*MyDialogTerminos myDialogFragment = new MyDialogTerminos();
        myDialogFragment.show(getSupportFragmentManager(), "MyFragment");
        myDialogFragment.setCancelable(false);*/
        Toast.makeText(getApplicationContext(), "Politicas desabilitadas, intentelo nuevamente mas tarde!", Toast.LENGTH_SHORT).show();
    }

    private void goToFacebook() {
        Uri uri = Uri.parse("https://www.facebook.com/Taquerias-Cachinflin-113556606858092/?ref=page_internal");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}