package com.myapplication.demo.bluetooth_activar_desactivar;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Declaramos una constante para lanzar los Intent de activacion de Bluetooth
    private static final int 	REQUEST_ENABLE_BT 	= 1;

    Button btnBluetooth;
    //Button btnBluetooth;// = (Button) findViewById(R.id.btn_bluetooth);
    BluetoothAdapter bAdapter; //Adapter para uso del Bluetooth
// https://www.youtube.com/watch?v=ayzmbWJlLo0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.btnBluetooth = (Button)findViewById(R.id.btn_bluetooth);
        this.btnBluetooth.setOnClickListener(this);
        this.bAdapter = BluetoothAdapter.getDefaultAdapter();

        if(this.bAdapter == null){
            this.btnBluetooth.setEnabled(false);
            this.btnBluetooth.setText(R.string.sinBluetooth);
            return;
        }else{
            setEstadoBluetootth();
        }
        // nos suscribimos a sus eventos
        registrarEventosBluetooth();
    }

    public void setEstadoBluetootth(){
        if(this.bAdapter.isEnabled()){
            this.btnBluetooth.setText(R.string.desactivarBluetooth);
            //this.bAdapter.disable();
        }else{
            this.btnBluetooth.setText(R.string.activarBluetooth);
            //this.bAdapter.enable();
        }
    }
    /**
     * Suscribe el BroadcastReceiver a los eventos relacionados con Bluetooth que queremos
     * controlar.
     */
    private void registrarEventosBluetooth()
    {
        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bReceiver,filtro);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_bluetooth:
                    Toast.makeText(getApplicationContext(),"Se va a proceder a "+btnBluetooth.getText(),Toast.LENGTH_SHORT).show();
                    if(this.bAdapter.isEnabled()){
                        this.bAdapter.disable();
                    }else{
                        // Lanzamos el Intent que mostrara la interfaz de activacion del
                        // Bluetooth. La respuesta de este Intent se manejara en el metodo
                        // onActivityResult
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                        //this.bAdapter.enable();

                    }
            }
    }

    // Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
// del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // Filtramos por la accion. Nos interesa detectar BluetoothAdapter.ACTION_STATE_CHANGED
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (estado){
                    // Apagado
                    case BluetoothAdapter.STATE_OFF:
                        ((Button)findViewById(R.id.btn_bluetooth)).setText(R.string.desactivarBluetooth);
                        break;
                    // Encendido
                    case BluetoothAdapter.STATE_ON:
                        ((Button)findViewById(R.id.btn_bluetooth)).setText(R.string.activarBluetooth);
                        break;
                    default:
                        break;

                }
            }
        }
    };
    /**
     * Handler del evento desencadenado al retornar de una actividad. En este caso, se utiliza
     * para comprobar el valor de retorno al lanzar la actividad que activara el Bluetooth.
     * En caso de que el usuario acepte, resultCode sera RESULT_OK
     * En caso de que el usuario no acepte, resultCode valdra RESULT_CANCELED
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:
            {
                if(resultCode == RESULT_OK)
                {
                    // Acciones adicionales a realizar si el usuario activa el Bluetooth

                    Toast.makeText(getApplicationContext(),"Se a Encendido El bluetooth Jonatha ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Acciones adicionales a realizar si el usuario no activa el Bluetooth
                    Toast.makeText(getApplicationContext(),"No Se a Encendido El bluetooth Jonatha ",Toast.LENGTH_SHORT).show();

                }
                break;
            }

            default:
                break;
        }
    }
    // Ademas de realizar la destruccion de la actividad, eliminamos el registro del
// BroadcastReceiver.
    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(bReceiver);
    }

}
