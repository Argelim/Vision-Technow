package technow.com.vision;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.GregorianCalendar;

import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import eventos.AddElementRecyclerViewListener;
import eventos.GestureDetectorEvent;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import login.Registro;
import login.Respuesta;
import login.Usuario;
import sqlite.bd_sqlite;
import vista.DialogoPersonalizadoLogin;

public class MainActivity extends AppCompatActivity implements DialogoPersonalizadoLogin.OnHeadlineSelectedListener {

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
    private LinearLayoutManager layoutManager;
    private String fecha, path, nombreImagen;
    private bd_sqlite bd;
    public static Snackbar snackbar;
    private Usuario usuario;
    private GestureDetectorEvent gestureDetectorEvent;
    private GestureDetector gestureDetector;
    private DialogoPersonalizadoLogin dialogoPersonalizadoLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bd = new bd_sqlite(getApplicationContext(), "vision_technow", 1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] item = {"Desde la camara", "Desde la galeria"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Imagen im = new Imagen("prueba", "p", "");
                            if (which == 0) {
                                if (usuario!=null){
                                    startCamera();
                                }else {
                                    mostrarDialogoDeLogin();
                                }
                            }
                            if (which == 1) {
                                if(usuario!=null){
                                    startGalleryChooser();
                                }else {
                                    mostrarDialogoDeLogin();
                                }
                            }
                        }
                    });
                    builder.create().show();
                }
            });
        }
        imagens = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setRemoveDuration(500);
        recyclerView.getItemAnimator().setAddDuration(500);
        recyclerView.getItemAnimator().setChangeDuration(500);
        recyclerView.setClickable(true);
        recyclerView.setLongClickable(true);
        //instanciamos el adapter del reciclador
        listaImagenes = new listaImagenes(recyclerView, getApplicationContext(),getSupportFragmentManager());
        //le agregamos el adapter al reciclador
        recyclerView.setAdapter(listaImagenes);
        //creamos un manejador para reciclador
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        //añadimos un listener para el borrado con deslizamiento tanto para la derecha como para la izquierda
        ListenerSwipe listenerSwipe = new ListenerSwipe(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,snackbar);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(listenerSwipe);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //carga la lista de imagenes por si las hay
        bd.obtenerImagenes(getApplicationContext(), listaImagenes);
        recyclerView.getLayoutManager().scrollToPosition(ScrollView.FOCUS_UP);
        //Creamos un objeto que será el listener de encarado de gestionar eventos relacionado con la pantala
        gestureDetectorEvent = new GestureDetectorEvent(getSupportFragmentManager(),this,recyclerView);
        //instanciamos un gesture detector y le añadimos el contexto y nuestro gesture personalizado
        gestureDetector = new GestureDetector(this,gestureDetectorEvent );
        //Activamos los eventos que ocurra
        gestureDetector.setOnDoubleTapListener(gestureDetectorEvent);
        //añadimos un evento que será lanzado cuando se añada un nuevo elemento a la lista
        recyclerView.addOnChildAttachStateChangeListener(new AddElementRecyclerViewListener(getSupportFragmentManager(),gestureDetector,recyclerView));
    }


    /**
     * Método que invoca un intent que muestra la galeria de imagenes
     */
    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"),
                GALLERY_IMAGE_REQUEST);
    }

    /**
     * Método que ejecuta la camara del dispositivo
     */
    public void startCamera() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
                startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            }
        } else {
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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyySS");
        Log.d("FECHA", simpleDateFormat.format(calendar.getTime()));
        String aux = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fecha = simpleDateFormat.format(calendar.getTime());
        nombreImagen = "IMG_" + aux + String.valueOf((int) (Math.random() * 1000));
        Log.d("FECHA", fecha);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenGaleria = data.getData();
            Imagen imagen1 = new Imagen();
            new Run(imagen1, imagenGaleria, nombreImagen).execute();

        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            final Uri uri = Uri.fromFile(getCameraFile());
            Imagen imagen1 = new Imagen();
            new Run(imagen1, uri, nombreImagen).execute();
        }
    }

    @Override
    public void onArticleSelected(Usuario usuario) {
        this.usuario = usuario;
        Log.d("USUARIO",this.usuario.getUsername()+", "+this.usuario.getPassword()+", "+this.usuario.getTokenKey().getToken());
    }

    /**
     * Clase privada AsyncTask, realiza la subida de imagen con
     * cloudServer, obtiene la descripción, lo almacenamos en el dispositivo
     * y en la base de datos SQLite para poder recuperarlo.
     */
    private class Run extends AsyncTask<Void, Void, Void> {
        private Imagen imagen;
        private Uri uri;
        private String nombreImagen;
        private int contador =0;

        public Run(Imagen imagen, Uri uri, String nombreImagen) {
            this.imagen = imagen;
            this.uri = uri;
            this.nombreImagen = nombreImagen;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Cargando");
            progressDialog.setIndeterminate(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress();
            saveImage(uri, nombreImagen);
            imagen.setPath(path);
            imagen.setFecha(fecha);
            contador++;
            publishProgress();
            subirImagen(imagen);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if(contador==0){
                progressDialog.show();
                progressDialog.setMessage("Guardando Imagen");
            }else {
                progressDialog.setMessage("Subiendo Imagen");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RequestCreator requestCreator = Picasso.with(getApplicationContext()).load(uri).resize(100,100).transform(new CircleTransform());
            imagen.setRequestCreator(requestCreator);
            bd.insertarImagen(getApplicationContext(), imagen.getDescripcion(), imagen.getPath(), imagen.getFecha());
            recyclerView.getLayoutManager().scrollToPosition(recyclerView.getLayoutManager().getItemCount());
            listaImagenes.addItem(imagen);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
        if (PermissionUtils.permissionGranted(requestCode, WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST, grantResults)) {
            saveImage(imagenGaleria, nombreImagen);
        }
    }

    private void saveImage(final Uri data, final String nombre) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestPermission(this, WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                prepareToSave(data, nombre);
            }
        } else {
            prepareToSave(data, nombre);
        }
    }

    private void prepareToSave(final Uri data, final String nombre) {
        File file;
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Vision_Technow");
        if (!dir.exists()) {
            dir.mkdir();
            Log.d("Creado", "OK");
        }
        File oculto = new File(dir.getPath(), "Pictures");
        if (!oculto.exists()) {
            oculto.mkdir();
        }
        file = new File(oculto.getAbsolutePath(), nombre + ".jpeg");
        try {
            file.createNewFile();
            path = file.getPath();
            Log.d("FILE", file.getAbsolutePath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), data);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,60, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void subirImagen(Imagen imagen){
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            try {
                HttpPost httppost = new HttpPost(estados.HTTP_POST_UP_IMAGE);
                FileBody fileBody = new FileBody(new File(imagen.getPath()));
                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .seContentType(ContentType.MULTIPART_FORM_DATA)
                        .addPart("imagen", fileBody)
                        .build();
                httppost.setHeader("Authorization: ", "JWT " + usuario.getTokenKey().getToken());
                Log.d("TOKEN",usuario.getTokenKey().getToken());
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);

                    switch (200){
                        case estados.HTTP_OK:
                            HttpGet httpGet = new HttpGet(estados.HTTP_POST_GET_DESCRIPTION);
                            httpGet.setHeader("Authorization", "JWT " + usuario.getTokenKey().getToken());
                            response = httpclient.execute(httpGet);
                            Gson gson = new Gson();
                            Type type=new TypeToken<List<Respuesta>>(){}.getType();
                            List<Respuesta> respuestas = gson.fromJson(EntityUtils.toString(response.getEntity(),"UTF-8"),type);
                            imagen.setDescripcion(respuestas.get(0).getDescripcion());
                            Log.d("RESPUESTA",respuestas.get(0).getDescripcion());
                            break;
                        case estados.HTTP_FORBIDDEN:
                            Log.d("ACCESO","NECESITAS LOGEARTE");
                            break;
                        default:
                            Log.d("ACCESO",String.valueOf(response.getStatusLine().getStatusCode()));
                            break;
                    }

            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

//    public void uploadImage(Uri uri, Imagen imagen) {
//        if (uri != null) {
//            try {
//                // scale the image to 800px to save on bandwidth
//                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);
//                callCloudVision(bitmap, imagen);
//            } catch (IOException e) {
//                Log.d(TAG, "Image picking failed because " + e.getMessage());
//                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Log.d(TAG, "Image picker gave us a null image.");
//            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void callCloudVision(final Bitmap bitmap, Imagen imagen) throws IOException {
//
//        try {
//
//            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//
//            Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
//            builder.setVisionRequestInitializer(new
//                    VisionRequestInitializer(CLOUD_VISION_API_KEY));
//            Vision vision = builder.build();
//
//            BatchAnnotateImagesRequest batchAnnotateImagesRequest =
//                    new BatchAnnotateImagesRequest();
//            batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
//                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
//
//                // Add the image
//                Image base64EncodedImage = new Image();
//                // Convert the bitmap to a JPEG
//                // Just in case it's a format that Android understands but Cloud Vision
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
//                byte[] imageBytes = byteArrayOutputStream.toByteArray();
//
//                // Base64 encode the JPEG
//                base64EncodedImage.encodeContent(imageBytes);
//                annotateImageRequest.setImage(base64EncodedImage);
//
//                // add the features we want
//                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
//                    Feature labelDetection = new Feature();
//                    labelDetection.setType("LABEL_DETECTION");
//                    labelDetection.setMaxResults(10);
//                    add(labelDetection);
//
//                    labelDetection = new Feature();
//                    labelDetection.setType("FACE_DETECTION");
//                    labelDetection.setMaxResults(10);
//                    add(labelDetection);
//                }});
//
//                // Add the list of one thing to the request
//                add(annotateImageRequest);
//            }});
//
//            Vision.Images.Annotate annotateRequest =
//                    vision.images().annotate(batchAnnotateImagesRequest);
//            // Due to a bug: requests to Vision API containing large images fail when GZipped.
//            annotateRequest.setDisableGZipContent(true);
//            Log.d(TAG, "created Cloud Vision request object, sending request");
//            BatchAnnotateImagesResponse response = annotateRequest.execute();
//            //añadimos la descripción a la imágen
//            imagen.setDescripcion(convertResponseToString(response));
//        } catch (GoogleJsonResponseException e) {
//            Log.d(TAG, "failed to make API request because " + e.getContent());
//        } catch (IOException e) {
//            Log.d(TAG, "failed to make API request because of other IOException " +
//                    e.getMessage());
//        }
//    }
//
//
//    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
//
//        int originalWidth = bitmap.getWidth();
//        int originalHeight = bitmap.getHeight();
//        int resizedWidth = maxDimension;
//        int resizedHeight = maxDimension;
//
//        if (originalHeight > originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
//        } else if (originalWidth > originalHeight) {
//            resizedWidth = maxDimension;
//            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
//        } else if (originalHeight == originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = maxDimension;
//        }
//        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
//    }
//
//    private String convertResponseToString(BatchAnnotateImagesResponse response) {
//        String message = "";
//
//        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
//        ArrayList<String> ingles = new ArrayList<>();
//
//        if (labels != null) {
//            for (EntityAnnotation label : labels) {
//                Log.d("SCORE", String.valueOf(label.getScore()));
//                if (label.getScore() > 0.50) {
//                    ingles.add(label.getDescription());
//                }
//            }
//            ArrayList<String> espanyol = traducir(ingles);
//            for (int i = 0; i < espanyol.size(); i++) {
//                message += String.format("%s", espanyol.get(i));
//                if (i != espanyol.size() - 1) {
//                    message += ", ";
//                }
//            }
//        } else {
//            message += "No se ha podido describir la imagen";
//        }
//
//        return message;
//    }
//
//    private ArrayList<String> traducir(ArrayList<String> ingles) {
//        ArrayList<String> espanyol = new ArrayList<>();
//        try {
//            // See comments on
//            //   https://developers.google.com/resources/api-libraries/documentation/translate/v2/java/latest/
//            // on options to set
//            Translate t = new Translate.Builder(
//                    AndroidHttp.newCompatibleTransport()
//                    , com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), null)
//                    //Need to update this to your App-Name
//                    .setApplicationName("Vision-Technow")
//                    .build();
//
//            Translate.Translations.List list = t.new Translations().list(
//                    ingles,
//                    //Target language
//                    "ES");
//            //Set your API-Key from https://console.developers.google.com/
//            list.setKey("AIzaSyC4iz3wb9_4QEKdePfAfHvxXvWl6wT2bjE");
//            TranslationsListResponse response = list.execute();
//            for (TranslationsResource tr : response.getTranslations()) {
//                espanyol.add(tr.getTranslatedText());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return espanyol;
//    }

    /**
     * Clase que se encarga de la escucha de los swips que se realiza desde
     * la pantalla
     */
    private class ListenerSwipe extends ItemTouchHelper.SimpleCallback {

        private Snackbar snackbar;

        public ListenerSwipe(int dragDirs, int swipeDirs, Snackbar snackbar) {
            super(dragDirs, swipeDirs);
            this.snackbar=snackbar;
        }



        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int pos = viewHolder.getAdapterPosition();
            final Imagen iAux = MainActivity.imagens.get(pos);
            MainActivity.imagens.remove(pos);
            recyclerView.getAdapter().notifyDataSetChanged();
            //hilo que se encarga de eliminar la imagen de la base de datos y
            //del almacenamiento interno del dispositivo
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //obtenemos el path de donde se almacena la imagen en nuestro disco
                    String path = iAux.getPath();
                    //eliminamos la imagen de nuestra base de datos
                    bd.eliminarImagen(path);
                    //obtenemos el file con el path
                    File f = new File(path);
                    //eliminamos el fichero
                    f.delete();
                }
            }).start();
            snackbar = Snackbar.make(getCurrentFocus(),"Deshacer los cambios?",Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            v.setBackgroundColor(Color.parseColor("#BBDEFB"));
            snackbar.setAction("Deshacer", new OnClickListener() {
                //en caso de que el usuario quiere deshacer los cambios y volver
                @Override
                public void onClick(View v) {
                    MainActivity.imagens.add(pos,iAux);
                    recyclerView.getAdapter().notifyItemInserted(pos);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bd.insertarImagen(getApplication(),iAux.getDescripcion(),iAux.getPath(),iAux.getFecha());
                            try {
                                Bitmap bitmap=null;
                                try {
                                    bitmap = iAux.getRequestCreator().get();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(new File(iAux.getPath()));
                                bitmap.compress(Bitmap.CompressFormat.JPEG,30,fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                Log.d("CREADO","Fichero creado");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals("Iniciar sesion")){
            mostrarDialogoDeLogin();
        }else if(item.getTitle().equals("Crear cuenta en vision")){

        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    private void mostrarDialogoDeLogin(){
        dialogoPersonalizadoLogin = new DialogoPersonalizadoLogin(this,bd);
        dialogoPersonalizadoLogin.show(getSupportFragmentManager(),"login");
    }
}
