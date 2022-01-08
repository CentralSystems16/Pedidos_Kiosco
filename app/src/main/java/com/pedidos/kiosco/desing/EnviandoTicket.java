package com.pedidos.kiosco.desing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.TicketDatos;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnviandoTicket extends AppCompatActivity {

    public static final int PERMISSION_BLUETOOTH = 1;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviando_ticket);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
            } else {
                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                if (connection != null) {
                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);

                    final String text =
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                                    this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logokiosko,
                                            DisplayMetrics.DENSITY_LOW, getApplicationContext().getTheme())) + "</img>\n" +
                                    "[L]\n" +
                                    "[L]" + "El " + fechacComplString + " a las " + horaString+ "\n\n" +
                                    "[L]" + "Cliente: " + Login.nombre + "\n" +
                                    "[L]" + "Número: " + Login.usuario + "\n" +
                                    "[C]================================\n" +

                                    "[L]<b>"+ "Productos" +"</b>\n" +

                                    "[C]--------------------------------\n" +
                                    "[L]TOTAL $" + TicketDatos.gTotal + "\n" +
                                    "[C]--------------------------------\n" +
                                    "[C]<barcode type='ean13' height='10'>202105160005</barcode>\n" +
                                    "[C]--------------------------------\n" +
                                    "[C]Gracias por su compra :)\n";

                    printer.printFormattedText(text);
                } else {
                    Toast.makeText(getApplicationContext(), "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("APP", "No se puede imprimir.", e);
        }

        new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), VistaFinal.class));
                finish();
            }, 10000);
        }

    @Override
    public void onBackPressed(){

    }
    }