package com.example.logonpf.imobiliaria;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String caminhoImagem;
    private ImageView imgFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFoto = (ImageView) findViewById(R.id.imgFoto);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);

        }


    }

    public void tirar(View v) {

        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.i("IMOBILIARIA", storageDir.getAbsolutePath());

        File image = null;

        try {

            image = File.createTempFile("foto", ".jpg", storageDir);
            Log.i("IMOBILIARIA", image.getName());

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            caminhoImagem = image.getAbsolutePath();
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            startActivityForResult(i, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(caminhoImagem, bmOptions);
        imgFoto.setImageBitmap(bitmap);

    }

    public void enviar(View v) {

        JSONObject imovel = new JSONObject();
        try {

            byte[] val = IOUtils.toByteArray(new FileInputStream(caminhoImagem));

            String code = android.util.Base64.encodeToString(val,
                    android.util.Base64.DEFAULT);

            Log.i("IMOBILIARIA", code);

            imovel.put("proprietario", "EDSON");
            imovel.put("imagem", code);

            Log.i("IMOBILIARIA", imovel.toString());

            final String url = "http://10.20.63.61:8080/ImobiliariaWeb/ImobiliariaServlet";

            RequestQueue q = Volley.newRequestQueue(this);
            JsonObjectRequest r = new JsonObjectRequest(url, imovel,new OK(), new Erro());
            q.add(r);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Erro implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {

            Log.e("ERRO", error.getMessage());

        }
    }

    private class OK implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {

            Log.i("OK", response.toString());

        }
    }
}
