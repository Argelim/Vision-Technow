package technow.com.vision;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import sqlite.bd_sqlite;

public class MainActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyC2zS9AvR5at9m_mUjOxQMi41w5jD-5qko";
    public static final String FILE_NAME = "temp.jpg";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 3;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private Uri imagenGaleria;
    public static ArrayList<Imagen> imagens;
    private RecyclerView recyclerView;
    private listaImagenes listaImagenes;
    private RecyclerView.LayoutManager layoutManager;
    private String fecha, descripcion, path;
    private Semaphore semaphore;
    private bd_sqlite bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bd = new bd_sqlite(getApplicationContext(),"vision_technow",1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String [] item = {"Desde la camara", "Desde la galeria"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Imagen im = new Imagen("prueba", "p", "");
                            if(which == 0){
                                startCamera();
//                                listaImagenes.removeItem(0);
                            }
                            if(which == 1){
                                startGalleryChooser();
//                                listaImagenes.addItem(im);
                            }
                        }
                    });
                    builder.create().show();
                }
            });
        }

        imagens = new ArrayList<>();

        //carga la lista de imagenes por si las hay
        imagens=bd.obtenerImagenes(getApplicationContext(),imagens);
        if (!imagens.isEmpty()){
            cargaImagen();
        }
        //iinvertimos la lista se visualize en orden
        Collections.reverse(imagens);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setRemoveDuration(2000);
        //instanciamos el adapter del reciclador
        listaImagenes = new listaImagenes(recyclerView,getApplicationContext());
        //le agregamos el adapter al reciclador
        recyclerView.setAdapter(listaImagenes);
        //creamos un manejador para reciclador
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //instanciamos el semaforo para más control de los hilos
        semaphore = new Semaphore(1);

    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
                startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            }
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenGaleria = data.getData();
            uploadImage(data.getData());
            saveImage(imagenGaleria, "4");
        }
        else if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(getCameraFile());
            uploadImage(uri);
            saveImage(uri, "2");
        }
    }

    /**
     * Método que carga las imagenes
     */
    private void cargaImagen(){
        Iterator<Imagen> iterator = imagens.iterator();
        while (iterator.hasNext()){
            Imagen imagen = iterator.next();
            File file = new File(imagen.getPath());
            RequestCreator requestCreator = Picasso.with(getApplicationContext()).load(file);
            imagen.setRequestCreator(requestCreator);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
        if(PermissionUtils.permissionGranted(requestCode, WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST, grantResults)){
            saveImage(imagenGaleria, "4");
        }
    }

    private void saveImage(final Uri data, final String nombre) {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestPermission(this, WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                prepareToSave(data, nombre);
            }
        }else{
            prepareToSave(data, nombre);
        }
    }

    private void prepareToSave(final Uri data, final String nombre){
        new AsyncTask<Void, Integer, Boolean>() {
            private File file;
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean creado;
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Vision_Technow");
                Log.d("FILE", dir.getAbsolutePath());
                Log.d("Creado", String.valueOf(dir.exists()));
                if (!dir.exists()) {
                    dir.mkdir();
                    Log.d("Creado", "OK");
                }
                File oculto = new File(dir.getPath(), ".media");
                if(!oculto.exists()){
                    oculto.mkdir();
                }
                file = new File(oculto.getAbsolutePath(), nombre + ".jpeg");
                try {
                    file.createNewFile();
                    path = file.getPath();
                    Log.d("FILE", file.getAbsolutePath());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), data);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                    fos.flush();
                    fos.close();
                    creado = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    creado = false;
                }
                return creado;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                path=file.getPath();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                fecha = calendar.get(Calendar.DAY_OF_MONTH)+":"+calendar.get(Calendar.MONTH)+":"+calendar.get(Calendar.YEAR);
                Imagen imagen = new Imagen(descripcion,path,fecha);
                RequestCreator requestCreator = Picasso.with(getApplicationContext()).load(new File(path));
                imagen.setRequestCreator(requestCreator);
                listaImagenes.addItem(imagen);
                insertarImagen(imagen);
                semaphore.release();
            }
        }.execute();
    }

    private void insertarImagen(final Imagen imagen){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                bd.insertarImagen(getApplicationContext(),imagen.getDescripcion(),imagen.getPath(),imagen.getFecha());
                return null;
            }
        }.execute();
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to 800px to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);
                callCloudVision(bitmap);
               // mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    semaphore.acquire();
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
               // mImageDetails.setText(result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                descripcion=result;
                semaphore.release();
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        ArrayList<String> ingles = new ArrayList<>();

        if (labels != null) {
            for (EntityAnnotation label : labels) {
                ingles.add(label.getDescription());
            }
            ArrayList<String> espanyol = traducir(ingles);
            for (String spanish : espanyol){
                message += String.format("%s", spanish);
                message += ", ";
            }
        } else {
            message += "nothing";
        }

        return message;
    }

    private ArrayList<String> traducir(ArrayList<String> ingles) {
        ArrayList<String> espanyol = new ArrayList<>();
        try {
            // See comments on
            //   https://developers.google.com/resources/api-libraries/documentation/translate/v2/java/latest/
            // on options to set
            Translate t = new Translate.Builder(
                    AndroidHttp.newCompatibleTransport()
                    , com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
                    //Need to update this to your App-Name
                    .setApplicationName("Vision-Technow")
                    .build();
            Translate.Translations.List list = t.new Translations().list(
                   ingles,
                    //Target language
                    "ES");
            //Set your API-Key from https://console.developers.google.com/
            list.setKey("AIzaSyC4iz3wb9_4QEKdePfAfHvxXvWl6wT2bjE");
            TranslationsListResponse response = list.execute();
            for(TranslationsResource tr : response.getTranslations()) {
                espanyol.add(tr.getTranslatedText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return espanyol;
    }


}
