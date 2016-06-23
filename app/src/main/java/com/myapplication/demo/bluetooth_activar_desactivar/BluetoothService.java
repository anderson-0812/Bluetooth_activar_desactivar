package com.myapplication.demo.bluetooth_activar_desactivar;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.logging.Handler;

/**
 * Created by USER on 22/06/2016.
 *
 * Crearemos la clase añadiéndole unas cuantas constantes para definir los posibles
 estados y mensajes a manejar por el handler y un par de atributos privados para
 almacenar el estado actual de la conexión, el socket de la conexión y el handler
 que se encargará de comunicar los datos a la interfaz de usuario.
 */
public class BluetoothService {
    private static final String TAG = "org.danigarcia.examples.bluetooth.BluetoothService";
    public static final int ESTADO_NINGUNO = 0;
    public static final int ESTADO_CONECTADO= 1;
    public static final int ESTADO_REALIZANDO_CONEXION = 2;
    public static final int ESTADO_ATENDIENDO_PETICIOPNES= 3;

    public static final int MSG_LEER= 11;
    public static final int MSG_ESCRIBIR= 12;

    private static final Handler handler = null;
    private BluetoothSocket socket;
    private int estado;

    // Hilo encargado de mantener la conexion y realizar las lecturas y escrituras
    // de los mensajes intercambiados entre dispositivos.
    private class HiloConexion extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;// flujo de entrada (lecturas)
        private final OutputStream outputStream;// flujo de salida (escrituras)

        /*
        El constructor recibirá un BluetoothSocket como parámetro, que será creado bien por el servidor
        como respuesta a la aceptación de una conexión entrante, bien por el cliente como respuesta del
        intento de conexión a un servidor
        */
        private HiloConexion(BluetoothSocket socket) {
            this.socket = socket;
            setName(socket.getRemoteDevice().getName()+ "[" + socket.getRemoteDevice().getAddress() + "]");
            // Se usan variables temporales debido a que los atributos se declaran como final
            // no seria posible asignarles valor posteriormente si fallara esta llamada
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            // Obtenemos los flujos de enyrada y salida del socket
            try{
                tmpInputStream = socket.getInputStream();
                tmpOutputStream = socket.getOutputStream();
            }catch (IOException e){
                Log.e(TAG,"HiloConexion(): Error al obtener flujos de E/S",e);
            }
            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;

        }

        // Metodo principal del hilo, encargado de realizar las lecturas
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            setEstado(ESTADO_CONECTADO);
            // Mientras se mantenga la conexion el hilo se mantiene en espera ocupada
            // leyendo del flujo de entrada
            while(true){
                try{
                    // Leemos del flujo de entrada del socket
                    bytes = inputStream.read(buffer);
                } catch (InterruptedIOException e){
                    e.printStackTrace();
                } catch (IOException e){
                    Log.e(TAG, "HiloConexion.run(): Error al realizar la lectura", e);
                }
            }
        }
    }

}
