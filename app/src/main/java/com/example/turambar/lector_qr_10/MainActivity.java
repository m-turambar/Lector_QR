package com.example.turambar.lector_qr_10;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView miScannerView;
    private Cliente mCliente;
    public TextView textviewServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        new ConnectTask().execute("");
        miScannerView = new ZXingScannerView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        miScannerView.stopCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        miScannerView.stopCamera();
        mCliente.stopClient();
    }

    public void escanearQR(View view) {
        miScannerView.startCamera();
        setContentView(miScannerView);
        miScannerView.setResultHandler(this); //
    }

    @Override
    public void handleResult(Result result) {
        Log.e("handler", result.getText());
        Log.e("handler", result.getBarcodeFormat().toString());

        //mostrar el resultado del escaneo en una dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado del escaneo:");
        builder.setMessage(result.getText());
        AlertDialog alerta = builder.create();
        alerta.show();
        if(mCliente != null) {
            mCliente.sendMessage(result.getText());
        }
        else {
            Log.e("red", "mCliente es NULL, mensaje no enviado");
        }
        //Si quisieras seguir escaneando, llama este método:
        //miScannerView.resumeCameraPreview(this);
        miScannerView.stopCamera();
        setContentView(R.layout.activity_main);
    }

    public class ConnectTask extends AsyncTask<String, String, Cliente> {

        @Override
        protected Cliente doInBackground(String... message) {

            //we create a TCPClient object
            mCliente = new Cliente(new Cliente.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mCliente.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            /*sólo hace esto si la ventana con el View correcto está activa*/

            textviewServidor = (TextView)findViewById(R.id.textviewRed);
            textviewServidor.setText(values[0]);
            //process server response here....

        }
    }
}
