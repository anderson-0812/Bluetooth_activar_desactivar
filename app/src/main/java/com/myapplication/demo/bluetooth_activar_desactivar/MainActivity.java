package com.myapplication.demo.bluetooth_activar_desactivar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Declaramos una constante para lanzar los Intent de activacion de Bluetooth
    private static final int 	REQUEST_ENABLE_BT 	= 1;

    Button btnBluetooth;
    //Button btnBluetooth;// = (Button) findViewById(R.id.btn_bluetooth);
    BluetoothAdapter bAdapter; //Adapter para uso del Bluetooth
// https://www.youtube.com/watch?v=ayzmbWJlLo0

    // Listado de dispositivos
    ArrayList <BluetoothDevice> arrayDevices;

    Button btn_buscarDispositivo;

    ListView lvDispositivos;

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

        this.btn_buscarDispositivo = (Button)findViewById(R.id.btn_buscarDispositivo);
        this.btn_buscarDispositivo.setOnClickListener(this);
        this.lvDispositivos = (ListView)findViewById(R.id.lvDispositivos);

        if(this.bAdapter == null){
            this.btnBluetooth.setEnabled(false);
            this.btnBluetooth.setText(R.string.sinBluetooth);
            this.btn_buscarDispositivo.setEnabled(false);
            this.btn_buscarDispositivo.setText(R.string.buscarDispos);

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
            this.btn_buscarDispositivo.setText(R.string.buscarDispos);
            //this.bAdapter.disable();
        }else{
            this.btnBluetooth.setText(R.string.activarBluetooth);
            this.btn_buscarDispositivo.setEnabled(false);
            //this.bAdapter.enable();
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
                        ((Button)findViewById(R.id.btn_bluetooth)).setText(R.string.activarBluetooth);
                        ((Button)findViewById(R.id.btn_buscarDispositivo)).setEnabled(false);

                        break;
                    // Encendido
                    case BluetoothAdapter.STATE_ON:
                        ((Button)findViewById(R.id.btn_bluetooth)).setText(R.string.desactivarBluetooth);
                        ((Button)findViewById(R.id.btn_buscarDispositivo)).setEnabled(true);
                        break;
                    default:
                        break;

                }
            }


            // BluetoothDevice.ACTION_FOUND
            // Cada vez que se descubra un nuevo dispositivo por Bluetooth, se ejecutara
            // este fragmento de codigo
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // si no ha sido inicializado el array lo inicializamos aqui
                if(arrayDevices == null){
                    arrayDevices = new ArrayList<BluetoothDevice>();
                }
                // Extraemos el dispositivo del intent mediante la clave BluetoothDevice.EXTRA_DEVICE
                BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Añadimos el dispositivo al array
                arrayDevices.add(dispositivo);

                // Le asignamos un nombre del estilo NombreDispositivo [00:11:22:33:44]
                String descripcionDispositivo = dispositivo.getName() + "[" + dispositivo.getAddress() + "]";

                // Mostramos que hemos encontrado el dispositivo por el Toast
                Toast.makeText(getBaseContext(),getString(R.string.DetectadoDispositivo) + ": " + descripcionDispositivo, Toast.LENGTH_LONG).show();
            }
            // este codifo se ejecuta cuando el Bluetooth finaliza labusqueda de dispositivos
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                // Instanciamos un nuevo adapter para el ListView mediante la clase que acabamos de crear
                ArrayAdapter arrayAdapter = new BluetoothDeviceArrayAdapter(getApplicationContext()
                        ,android.R.layout.simple_list_item_2,arrayDevices);
                lvDispositivos.setAdapter(arrayAdapter);
                Toast.makeText(getBaseContext(),"Fin de la busqueda ",Toast.LENGTH_LONG).show();
            }
        }
    };
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

                    Toast.makeText(getApplicationContext(),"Se a Encendido El bluetooth ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Acciones adicionales a realizar si el usuario no activa el Bluetooth
                    Toast.makeText(getApplicationContext(),"No Se a Encendido El bluetooth ",Toast.LENGTH_SHORT).show();

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
