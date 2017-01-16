package com.example.melky.robotwificontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ResourceBundle;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context ct;
    Vector<Integer> AnimsIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ct = this;
        ControladorDatos.Guardar(ct, Constantes.ip_local, getLocalIpAddress());
        ControladorDatos.Guardar(ct, Constantes.puerto_local, getLocalPort());
        ControladorDatos.Guardar(ct, Constantes.ip_remota, getRemoteIpAddress());
        ControladorDatos.Guardar(ct, Constantes.puerto_remoto, getRemotePort());
        AnimsIds = new Vector<Integer>();
        AnimsIds.add(R.id.Anim1);
        AnimsIds.add(R.id.Anim2);
        AnimsIds.add(R.id.Anim3);
        for(Integer id:AnimsIds)    {
            ((Button)findViewById(id)).setOnClickListener(this);
        }

        ((Button)findViewById(R.id.Parpadeo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Enviar(Constantes.parpadeo);
            }
        });

        final Button Bconfig = ((Button)findViewById(R.id.Config));
        Bconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    LayoutInflater inflador = getLayoutInflater();
                    final View layout_dialogo = inflador.inflate(R.layout.config,null);
                    builder.setView(layout_dialogo);
                    final AlertDialog dialogo = builder.create();
                    dialogo.show();
                    ((EditText)layout_dialogo.findViewById(R.id.IP)).setText(ControladorDatos.Leer(ct,Constantes.ip_remota,"0.0.0.0"));
                    ((EditText)layout_dialogo.findViewById(R.id.tiempo)).setText(ControladorDatos.Leer(ct,Constantes.tiempo,"300"));

                    ((Button)layout_dialogo.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ControladorDatos.Guardar(ct,Constantes.ip_remota,((EditText)layout_dialogo.findViewById(R.id.IP)).getText().toString());
                            ControladorDatos.Guardar(ct,Constantes.tiempo,((EditText)layout_dialogo.findViewById(R.id.tiempo)).getText().toString());
                            dialogo.dismiss();
                        }
                    });
                    ((Button)layout_dialogo.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogo.dismiss();
                        }
                    });
                }
                else
                {
                    Toast.makeText(ct,"Version de android no soportada para este boton",Toast.LENGTH_LONG).show();
                    Bconfig.setVisibility(View.GONE);
                }
            }
        });
    }
    public void MostrarMensaje(String titulo,String mensaje)
    {
        AlertDialog.Builder DialogoError  = new AlertDialog.Builder(ct);
        DialogoError.setTitle(titulo);
        DialogoError.setMessage(mensaje);
        DialogoError.show();
    }
    public void Enviar(String msg)
    {
        String ip_local = ControladorDatos.Leer(ct, Constantes.ip_local, "");
        String ip_rem = ControladorDatos.Leer(ct, Constantes.ip_remota, "");
        String puerto_loc = ControladorDatos.Leer(ct, Constantes.puerto_local, "");
        String puerto_rem = ControladorDatos.Leer(ct, Constantes.puerto_remoto, "");
        Enviar(msg, ip_local, puerto_loc, ip_rem, puerto_rem);
    }
    public void Enviar(String msg,String ip_local,String puerto_local,String ip_remota,String puerto_remoto)
    {
        if(ip_remota==null || ip_local == null || puerto_remoto == null ||
                ip_remota=="" || ip_local == "" || puerto_remoto == "") {
            MostrarMensaje("Error","Revisar datos de envío");
        }
        else {
            Log.d(ip_remota,msg);
            new EnviarUDP(ip_local,puerto_local,ip_remota,puerto_remoto,msg).start();
        }
    }
    public void onClick(View v) {
        int id_R = v.getId();
        int id_Cara = AnimsIds.indexOf(id_R);
        String mensaje = Constantes.animar + "@" + String.valueOf(id_Cara+1);
        Enviar(mensaje);
        Log.d("Enviando",mensaje);
    }

    public String getLocalIpAddress(){
        WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }
    public String getLocalPort(){
        //String ip = getLocalIpAddress();
        //String puerto = "8" + String.format("%3s",ip.substring(ip.lastIndexOf(".")+1)).replace(' ','0');
        String puerto = "8802";
        return puerto;
    }
    public String getRemoteIpAddress(){
        String ip = "192.168.1.7";
        return ip;
    }
    public String getRemotePort(){
        //String ip = getLocalIpAddress();
        //String puerto = "8" + String.format("%3s",ip.substring(ip.lastIndexOf(".")+1)).replace(' ','0');
        String puerto = "8802";
        return puerto;
    }
}
