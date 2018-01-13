package pt.ipleiria.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import pt.ipleiria.project.model.Constants;
import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Notes;
import pt.ipleiria.project.model.Singleton;

import static pt.ipleiria.project.MainActivity.NOTES_DETAILS;
import static pt.ipleiria.project.MainActivity.PROJECT_NOTE;


public class AddActivity extends AppCompatActivity {
    public static final String NOTE_ID = "NOTE_ID";
    public static final String NEW_NOTE = "project.NEW_NOTE";
    public static final int REQUEST_CODE_CHANGE = 3;
    public static final int REQUEST_CODE_ADD = 4;
    private static final int REQUEST_CODE_IMAGE = 5;
    private static final int REQUEST_VIDEO_CAPTURE = 6;
    private static final int REQUEST_CODE_VIDEO = 7;
    private static final int REQUEST_CAMERA = 10;
    public static final int CAMERA_PERMISSIONS_REQUEST = 11;
    public static final int CAMERA_IMAGE_REQUEST = 12;
    public static final String FILE_NAME = ".jpg";
    private static final int REQUEST_LOCATION = 13;
    private static final String TAG = "TAG";

    private AlertDialog alertDialog;
    private Note n;
    private Notes notes;
    private int position;
    private Uri video;
    private Uri image;
    LatLng latlng;
    String fileName;
    File file;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Método OnCreate(): Método que faz a selecção do layout activity_add e carrega a toolbar
     * respectiva a esta activity. Se for receber um intent e i.getIntExtra(NOTES_DETAILS)
     * for igual a 0 significa que não é para mostrar os detalhes mas para adicionar ou
     * editar uma nota. Se o intent não contiver extras significa que é para adicionar uma
     * nova nota, se tiver extras significa que é para editar uma nota.
     * Se i.getIntExtra(NOTES_DETAILS) diferente de 0 significa que é para mostrar os detalhes
     * da nota.
     *
     * ----------------------------NOVO---------------------------------------------
     * Botão AddKeyword -> Permite ao utilizador adicionar keywords de modo a usar filtros
     * da Awareness API, Keywords Predefinidas
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_add);
        Toolbar myToolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(myToolbar);

        Button button_add = findViewById(R.id.button_add);
        Button button_select_image = findViewById(R.id.button_image);
        Button button_select_video = findViewById(R.id.button_video);
        ImageView button_addKeywords = findViewById(R.id.button_keywords);

        /*
        *Faz a distinção entre detalhes e o quando é para adicionar
        */
        if (i.getIntExtra(NOTES_DETAILS, 0) == 0) {

            //METE OS TEXTVIEW NÃO VISIVEIS
            //ID
            TextView textView_id = findViewById(R.id.textView_id);
            textView_id.setVisibility(View.GONE);
            TextView textView_ID = findViewById(R.id.textView_ID);
            textView_ID.setVisibility(View.GONE);
            //CREATION DATE
            TextView textView_creation = findViewById(R.id.textView_creation);
            textView_creation.setVisibility(EditText.GONE);
            TextView textView_Creation = findViewById(R.id.textView_Creation);
            textView_Creation.setVisibility(EditText.GONE);
            //EDIT DATE
            TextView textView_edit = findViewById(R.id.textView_edit);
            textView_edit.setVisibility(EditText.GONE);
            TextView textView_Edit = findViewById(R.id.textView_Edit);
            textView_Edit.setVisibility(EditText.GONE);
            //IMAGE AND VIDEO
            ImageView imageView = findViewById(R.id.imageView_image);
            imageView.setVisibility(View.GONE);
            TextView imagetext = findViewById(R.id.textView_image);
            imagetext.setVisibility(View.GONE);

            VideoView videoView = findViewById(R.id.videoView_video);
            videoView.setVisibility(View.GONE);
            TextView videotext = findViewById(R.id.textView_video);
            videotext.setVisibility(View.GONE);

            //SE NÃO TIVER EXTRAS QUER DIZER QUE É PARA ADICIONAR UMA NOTA NOVA
            if (i.getExtras() == null) {
                button_select_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectVideo();
                        VideoView videoView = findViewById(R.id.videoView_video);
                        videoView.setVisibility(View.VISIBLE);
                        TextView videotext = findViewById(R.id.textView_video);
                        videotext.setVisibility(View.VISIBLE);
                    }
                });

                button_select_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                        ImageView imageView = findViewById(R.id.imageView_image);
                        imageView.setVisibility(View.VISIBLE);
                        TextView imagetext = findViewById(R.id.textView_image);
                        imagetext.setVisibility(View.VISIBLE);
                    }
                });

                button_addKeywords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                        LayoutInflater inflater = AddActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.keywords_layout, null);

                        ArrayAdapter<String> spinnerTypeAdapter = new ArrayAdapter<>(AddActivity.this,
                                android.R.layout.simple_list_item_1, Constants.types);

                        final Spinner spinner_type = dialogView.findViewById(R.id.sp_keywd_type);
                        final Spinner spinner_char = dialogView.findViewById(R.id.sp_keywd_char);
                        spinner_type.setAdapter(spinnerTypeAdapter);
                        final TextView tvParameters = dialogView.findViewById(R.id.tv_parameters);
                        final EditText etParameters = dialogView.findViewById(R.id.editText_parameters);
                        final ImageView button_map = dialogView.findViewById(R.id.button_map);

                        //CHAAAAAAAAANGE
                        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                switch (parent.getItemAtPosition(position).toString()) {
                                    case "Headphones":
                                        ArrayAdapter<String> spinnerTypeAdapter_Headphones = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Head);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Headphones);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Temperature":
                                        ArrayAdapter<String> spinnerTypeAdapter_Temp = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Temp);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Temp);
                                        tvParameters.setText("Temperature:");
                                        tvParameters.setVisibility(View.VISIBLE);
                                        etParameters.setHint("Temperature");
                                        etParameters.setVisibility(View.VISIBLE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Weather":
                                        ArrayAdapter<String> spinnerTypeAdapter_Weather = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Cond);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Weather);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    //CHAAAAAAAAAAAAANGE LOCATION
                                    case "Location":
                                        ArrayAdapter<String> spinnerTypeAdapter_Location = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Loc);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Location);
                                        spinner_char.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                switch (parent.getItemAtPosition(position).toString()) {
                                                    case "Insert Latitude/Longitude":
                                                        tvParameters.setText("Address:");
                                                        etParameters.setHint("Lat,Lng");
                                                        tvParameters.setVisibility(View.VISIBLE);
                                                        etParameters.setVisibility(View.VISIBLE);
                                                        button_map.setVisibility(View.GONE);
                                                        break;
                                                    case "Get Location from Google Maps":
                                                        tvParameters.setVisibility(View.GONE);
                                                        etParameters.setVisibility(View.GONE);
                                                        button_map.setVisibility(View.VISIBLE);
                                                        button_map.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent i4 = new Intent(AddActivity.this, MapsActivity.class);
                                                                startActivityForResult(i4, REQUEST_LOCATION);
                                                            }
                                                        });
                                                        break;
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                            }
                                        });
                                        break;
                                    case "Nearby":
                                        ArrayAdapter<String> spinnerTypeAdapter_NBP = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_NBP);
                                        spinner_char.setAdapter(spinnerTypeAdapter_NBP);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Activity":
                                        ArrayAdapter<String> spinnerTypeAdapter_Activity = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Act);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Activity);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Time":
                                        ArrayAdapter<String> spinnerTypeAdapter_Time = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Time);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Time);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        builder.setView(dialogView)
                                // Add action buttons
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        EditText et_addkeyword = findViewById(R.id.editText_AddKeyword);
                                        String keyword = "";
                                        switch (spinner_type.getSelectedItem().toString()) {
                                            case "Headphones":
                                                keyword = "#" + spinner_type.getSelectedItem().toString() + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Temperature":
                                                keyword = "#" + spinner_type.getSelectedItem().toString()
                                                        + spinner_char.getSelectedItem().toString() + ":"
                                                        + etParameters.getText().toString() + ";";
                                                break;
                                            case "Weather":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Location":
                                                switch (spinner_char.getSelectedItem().toString()) {
                                                    case "Insert Latitude/Longitude":
                                                        keyword = "#" + spinner_type.getSelectedItem().toString() + ":"
                                                                + etParameters.getText().toString() + ";";
                                                        break;
                                                    case "Get Location from Google Maps":
                                                        keyword = "#Location:" + latlng.latitude + "," + latlng.longitude + ";";
                                                        break;
                                                }
                                                break;
                                            case "Nearby":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Activity":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Time":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                        }
                                        et_addkeyword.append(keyword);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                //ADICIONAR NOTA
                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                        builder.setMessage(R.string.add_note);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TITLE
                                EditText editText_addTitle = findViewById(R.id.editText_AddTitle);
                                String title = editText_addTitle.getText().toString();
                                //BODY
                                EditText editText_addBody = findViewById(R.id.editText_AddBody);
                                String body = editText_addBody.getText().toString();
                                //KEYWORDS
                                EditText editText_addKeyword = findViewById(R.id.editText_AddKeyword);
                                String keyword = editText_addKeyword.getText().toString().trim();
                                String[] parts = keyword.split(";");
                                ArrayList<String> keywords = new ArrayList<>();
                                for (String part : parts) {
                                    keywords.add(part);
                                }
                                //IMAGE

                                //NOVA NOTA
                                String videoPath;
                                String imagePath;
                                if (video != null) {
                                    videoPath = video.toString();
                                } else {
                                    videoPath = null;
                                }
                                if (image != null) {
                                    imagePath = image.toString();
                                } else {
                                    imagePath = null;
                                }
                                if (body.isEmpty() && videoPath == null && imagePath == null) {
                                    Toast.makeText(AddActivity.this, R.string.no_body_image_video, Toast.LENGTH_SHORT).show();
                                } else if (title.isEmpty()) {
                                    Toast.makeText(AddActivity.this, R.string.must_have_title, Toast.LENGTH_SHORT).show();
                                } else {
                                    Note newNote = new Note(title, body, keywords, imagePath, videoPath);
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(NEW_NOTE, newNote);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }

                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(AddActivity.this, R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }

            //SE TIVER EXTRAS SIGNIFICA QUE É PARA MODIFICAR A NOTA
            if (i.getExtras() != null) {
                button_add.setText(R.string.edit);
                n = (Note) i.getSerializableExtra(PROJECT_NOTE);
                //GETTERS
                String title = n.getTitle();
                String body = n.getBody();

                ArrayList keywordList = n.getKeyword();
                StringBuilder keywords = new StringBuilder();
                for (int x = 0; x < keywordList.size(); x++) {
                    keywords.append(keywordList.get(x).toString()).append(";");
                }
                //SETTERS
                //TITLE
                EditText editText_title = findViewById(R.id.editText_AddTitle);
                editText_title.setText(title);
                //BODY
                EditText editText_body = findViewById(R.id.editText_AddBody);
                editText_body.setText(body);
                //KEYWORDS
                EditText editText_keyword = findViewById(R.id.editText_AddKeyword);
                editText_keyword.setText(keywords.toString());

                button_select_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectVideo();
                        VideoView videoView = findViewById(R.id.videoView_video);
                        videoView.setVisibility(View.VISIBLE);
                        TextView videotext = findViewById(R.id.textView_video);
                        videotext.setVisibility(View.VISIBLE);
                    }
                });

                button_select_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                        ImageView imageView = findViewById(R.id.imageView_image);
                        imageView.setVisibility(View.VISIBLE);
                        TextView imagetext = findViewById(R.id.textView_image);
                        imagetext.setVisibility(View.VISIBLE);
                    }
                });

                button_addKeywords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                        LayoutInflater inflater = AddActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.keywords_layout, null);

                        ArrayAdapter<String> spinnerTypeAdapter = new ArrayAdapter<>(AddActivity.this,
                                android.R.layout.simple_list_item_1, Constants.types);

                        final Spinner spinner_type = dialogView.findViewById(R.id.sp_keywd_type);
                        final Spinner spinner_char = dialogView.findViewById(R.id.sp_keywd_char);
                        spinner_type.setAdapter(spinnerTypeAdapter);
                        final TextView tvParameters = dialogView.findViewById(R.id.tv_parameters);
                        final EditText etParameters = dialogView.findViewById(R.id.editText_parameters);
                        final ImageView button_map = dialogView.findViewById(R.id.button_map);

                        //CHAAAAAAAAANGE
                        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                switch (parent.getItemAtPosition(position).toString()) {
                                    case "Headphones":
                                        ArrayAdapter<String> spinnerTypeAdapter_Headphones = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Head);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Headphones);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Temperature":
                                        ArrayAdapter<String> spinnerTypeAdapter_Temp = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Temp);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Temp);
                                        tvParameters.setText("Temperature:");
                                        tvParameters.setVisibility(View.VISIBLE);
                                        etParameters.setHint("Temperature");
                                        etParameters.setVisibility(View.VISIBLE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Weather":
                                        ArrayAdapter<String> spinnerTypeAdapter_Weather = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Cond);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Weather);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    //CHAAAAAAAAAAAAANGE LOCATION
                                    case "Location":
                                        ArrayAdapter<String> spinnerTypeAdapter_Location = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Loc);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Location);
                                        spinner_char.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                switch (parent.getItemAtPosition(position).toString()) {
                                                    case "Insert Latitude/Longitude":
                                                        tvParameters.setText("Address:");
                                                        etParameters.setHint("Lat,Lng");
                                                        tvParameters.setVisibility(View.VISIBLE);
                                                        etParameters.setVisibility(View.VISIBLE);
                                                        button_map.setVisibility(View.GONE);
                                                        break;
                                                    case "Get Location from Google Maps":
                                                        tvParameters.setVisibility(View.GONE);
                                                        etParameters.setVisibility(View.GONE);
                                                        button_map.setVisibility(View.VISIBLE);
                                                        button_map.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent i4 = new Intent(AddActivity.this, MapsActivity.class);
                                                                startActivityForResult(i4, REQUEST_LOCATION);
                                                            }
                                                        });
                                                        break;
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                            }
                                        });
                                        break;
                                    case "Nearby":
                                        ArrayAdapter<String> spinnerTypeAdapter_NBP = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_NBP);
                                        spinner_char.setAdapter(spinnerTypeAdapter_NBP);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Activity":
                                        ArrayAdapter<String> spinnerTypeAdapter_Activity = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Act);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Activity);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                    case "Time":
                                        ArrayAdapter<String> spinnerTypeAdapter_Time = new ArrayAdapter<>(AddActivity.this,
                                                android.R.layout.simple_list_item_1, Constants.keywd_Time);
                                        spinner_char.setAdapter(spinnerTypeAdapter_Time);
                                        tvParameters.setVisibility(View.GONE);
                                        etParameters.setVisibility(View.GONE);
                                        button_map.setVisibility(View.GONE);
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        builder.setView(dialogView)
                                // Add action buttons
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        EditText et_addkeyword = findViewById(R.id.editText_AddKeyword);
                                        String keyword = "";
                                        switch (spinner_type.getSelectedItem().toString()) {
                                            case "Headphones":
                                                keyword = "#" + spinner_type.getSelectedItem().toString() + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Temperature":
                                                keyword = "#" + spinner_type.getSelectedItem().toString()
                                                        + spinner_char.getSelectedItem().toString() + ":"
                                                        + etParameters.getText().toString() + ";";
                                                break;
                                            case "Weather":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Location":
                                                switch (spinner_char.getSelectedItem().toString()) {
                                                    case "Insert Latitude/Longitude":
                                                        keyword = "#" + spinner_type.getSelectedItem().toString() + ":"
                                                                + etParameters.getText().toString() + ";";
                                                        break;
                                                    case "Get Location from Google Maps":
                                                        keyword = "#Location:" + latlng.latitude + "," + latlng.longitude + ";";
                                                        break;
                                                }
                                                break;
                                            case "Nearby":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Activity":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                            case "Time":
                                                keyword = "#" + (spinner_type.getSelectedItem().toString()) + ":"
                                                        + spinner_char.getSelectedItem().toString() + ";";
                                                break;
                                        }
                                        et_addkeyword.append(keyword);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });


                //O CÓDIGO DOS SETTERS ACIMA SERVE PARA COLOCAR O TEXTO NOS CAMPOS DO LAYOUT

                //ABAIXO GRAVA OS DADOS QUE ESTÃO NESSES CAMPOS DO LAYOUT
                button_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                        builder.setMessage(R.string.edit_note);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //TITLE
                                EditText editText_addTitle = findViewById(R.id.editText_AddTitle);
                                String title = editText_addTitle.getText().toString();
                                //BODY
                                EditText editText_addBody = findViewById(R.id.editText_AddBody);
                                String body = editText_addBody.getText().toString();
                                //KEYWORDS
                                EditText editText_addKeyword = findViewById(R.id.editText_AddKeyword);
                                String keyword = editText_addKeyword.getText().toString();
                                String[] parts = keyword.split(";");
                                ArrayList<String> keywords = new ArrayList<>();
                                for (String part : parts) {
                                    keywords.add(part);
                                }
                                //NEW
                                String videoPath;
                                String imagePath;
                                if (video != null) {
                                    videoPath = video.toString();
                                } else {
                                    videoPath = null;
                                }
                                if (image != null) {
                                    imagePath = image.toString();
                                } else {
                                    imagePath = null;
                                }

                                if (body.isEmpty() && videoPath == null && imagePath == null) {
                                    Toast.makeText(AddActivity.this, R.string.no_body_image_video, Toast.LENGTH_SHORT).show();
                                } else if (title.isEmpty()) {
                                    Toast.makeText(AddActivity.this, R.string.must_have_title, Toast.LENGTH_SHORT).show();
                                } else {
                                    Note newNote = new Note(title, body, keywords, imagePath, videoPath);
                                    newNote.setId(n.getId());
                                    newNote.setCreation_date(n.getCreation_date());
                                    if (imagePath == null) {
                                        newNote.setImage(n.getImage());
                                    }
                                    if (videoPath == null) {
                                        newNote.setVideo(n.getVideo());
                                    }
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(NEW_NOTE, newNote);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(AddActivity.this, R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        } else {
            Intent i2 = getIntent();
            notes = Singleton.getInstance().getNotes();
            n = (Note) i2.getSerializableExtra(PROJECT_NOTE);
            position = notes.searchNoteByID(n.getId());

            //GETTERS
            String title = n.getTitle();
            String body = n.getBody();
            String id = n.getId();
            ArrayList<String> keywordList = n.getKeyword();
            StringBuilder keywords = new StringBuilder();
            for (int x = 0; x < keywordList.size(); x++) {
                keywords.append(keywordList.get(x)).append(";");
            }
            String note_image = n.getImage();
            String note_video = n.getVideo();

            //SETTERS
            //ID
            TextView textView_ID = findViewById(R.id.textView_ID);
            textView_ID.setText(id);

            //TITLE
            EditText editText_title = findViewById(R.id.editText_AddTitle);
            editText_title.setText(title);
            editText_title.setKeyListener(null);
            //BODY
            EditText editText_body = findViewById(R.id.editText_AddBody);
            editText_body.setText(body);
            editText_body.setKeyListener(null);

            //KEYWORDS
            EditText editText_keyword = findViewById(R.id.editText_AddKeyword);
            editText_keyword.setText(keywords.toString());
            editText_keyword.setKeyListener(null);

            //CREATION DATE
            TextView textView_Creation = findViewById(R.id.textView_Creation);
            String dateString = n.getCreation_dateFormated();
            textView_Creation.setText(dateString);

            //EDIT DATE
            TextView textView_Edit = findViewById(R.id.textView_Edit);
            String dateString2 = n.getEdit_dateFormated();
            textView_Edit.setText(dateString2);

            //ESCONDE OS BOTÕES
            button_add.setVisibility(View.GONE);
            button_select_image.setVisibility(View.GONE);
            button_select_video.setVisibility(View.GONE);
            button_addKeywords.setVisibility(View.GONE);

            //MOSTRA VIDEO E IMAGEM OU ESCONDE
            if (note_video != null) {
                TextView videotext = findViewById(R.id.textView_video);
                videotext.setVisibility(View.VISIBLE);
                video = Uri.parse(note_video);
                VideoView videoView = findViewById(R.id.videoView_video);
                videoView.setVisibility(View.VISIBLE);
                playVideo(videoView, video);
            } else {
                VideoView videoView = findViewById(R.id.videoView_video);
                videoView.setVisibility(View.GONE);
                TextView videotext = findViewById(R.id.textView_video);
                videotext.setVisibility(View.GONE);
            }
            if (note_image != null) {
                TextView imagetext = findViewById(R.id.textView_image);
                imagetext.setVisibility(View.VISIBLE);
                image = Uri.parse(note_image);
                ImageView imageView = findViewById(R.id.imageView_image);
                imageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(note_image).thumbnail(0.1f).into(imageView);
            } else {
                ImageView imageView = findViewById(R.id.imageView_image);
                imageView.setVisibility(View.GONE);
                TextView imagetext = findViewById(R.id.textView_image);
                imagetext.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Método onOptionsItemSelected(): Faz a deteção do clique nos botões que estão na toolbar
     * e realiza ações de acordo com os mesmos
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_add):
                Intent i2 = new Intent(this, AddActivity.class);
                startActivityForResult(i2, REQUEST_CODE_ADD);
                break;
            case (R.id.action_edit):
                n = Singleton.getInstance().getNotes().getNotes().get(position);
                Intent i3 = new Intent(this, AddActivity.class);
                i3.putExtra(PROJECT_NOTE, n);
                i3.putExtra(NOTE_ID, n.getId());
                startActivityForResult(i3, REQUEST_CODE_CHANGE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Método onActivityResult(): Se for criado um intent e chamada uma actividade à espera de um
     * resultado é realizada uma ação diferente para cada código
     *
     * ----------------------------------NOVO--------------------------------------
     * Recebe e tira fotos a funcionar 100%
     * Recebe a localização vinda do Google Maps
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD) {
            if (resultCode == RESULT_OK) {
                Note newNote = (Note) data.getSerializableExtra(AddActivity.NEW_NOTE);
                Singleton.getInstance().getNotes().addNote(newNote);
            }
        }
        if (requestCode == REQUEST_CODE_CHANGE) {
            if (resultCode == RESULT_OK) {
                Note newNote = (Note) data.getSerializableExtra(AddActivity.NEW_NOTE);
                Singleton.getInstance().getNotes().editNote(position, newNote);

                //SETTERS
                //ID
                TextView textView_ID = findViewById(R.id.textView_ID);
                textView_ID.setText(newNote.getId());

                //TITLE
                EditText editText_title = findViewById(R.id.editText_AddTitle);
                editText_title.setText(newNote.getTitle());
                editText_title.setKeyListener(null);

                //BODY
                EditText editText_body = findViewById(R.id.editText_AddBody);
                editText_body.setText(newNote.getBody());
                editText_body.setKeyListener(null);

                //KEYWORDS
                EditText editText_keyword = findViewById(R.id.editText_AddKeyword);
                ArrayList<String> keywordList = newNote.getKeyword();
                StringBuilder keywords = new StringBuilder();
                for (int x = 0; x < keywordList.size(); x++) {
                    keywords.append(keywordList.get(x)).append(";");
                }
                editText_keyword.setText(keywords.toString());
                editText_keyword.setKeyListener(null);

                //CREATION DATE
                TextView textView_Creation = findViewById(R.id.textView_Creation);
                String dateString = newNote.getCreation_dateFormated();
                textView_Creation.setText(dateString);

                //EDIT DATE
                TextView textView_Edit = findViewById(R.id.textView_Edit);
                String dateString2 = newNote.getEdit_dateFormated();
                textView_Edit.setText(dateString2);

                if (newNote.getVideo() != null) {
                    TextView videotext = findViewById(R.id.textView_video);
                    videotext.setVisibility(View.VISIBLE);
                    video = Uri.parse(newNote.getVideo());
                    VideoView videoView = findViewById(R.id.videoView_video);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(videoView, video);
                } else {
                    VideoView videoView = findViewById(R.id.videoView_video);
                    videoView.setVisibility(View.GONE);
                    TextView videotext = findViewById(R.id.textView_video);
                    videotext.setVisibility(View.GONE);
                }
                if (newNote.getImage() != null) {
                    TextView imagetext = findViewById(R.id.textView_image);
                    imagetext.setVisibility(View.VISIBLE);
                    image = Uri.parse(newNote.getImage());
                    ImageView imageView = findViewById(R.id.imageView_image);
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(this).load(newNote.getImage()).into(imageView);
                } else {
                    ImageView imageView = findViewById(R.id.imageView_image);
                    imageView.setVisibility(View.GONE);
                    TextView imagetext = findViewById(R.id.textView_image);
                    imagetext.setVisibility(View.GONE);
                }
            }
        }

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                video = videoUri;
                VideoView videoView = findViewById(R.id.videoView_video);
                playVideo(videoView, videoUri);
            }
        } else if (requestCode == REQUEST_CODE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                image = imageUri;
                ImageView imageView = findViewById(R.id.imageView_image);
                Glide.with(this).load(imageUri).into(imageView);
                TextView tv_upload = findViewById(R.id.tv_upload);
                tv_upload.setVisibility(View.VISIBLE);
                uploadImage(imageUri);
            }
        }
        if (requestCode == CAMERA_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String asd = fileName;
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                image = photoUri;
                ImageView imageView = findViewById(R.id.imageView_image);
                Glide.with(this).load(photoUri).into(imageView);
                uploadImage(photoUri);
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                latlng = data.getParcelableExtra("Location");
            }
        }
    }


    /**
     * Método onCreateOptionsMenu(): Esconde os botões editar/adicionar da toolbar
     * quando se está a adicionar ou a editar uma nova nota
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        Intent i = getIntent();
        if (i.getIntExtra(NOTES_DETAILS, 0) == 0) {
            MenuItem item = menu.findItem(R.id.action_edit);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.action_add);
            item2.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Método selectImage(): Abre um AlertDialog que permite ao utilizador ir buscar uma imagem
     * à galeria ou tirar uma foto
     */
    private void selectImage() {
        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.web), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(getString(R.string.camera))) {
                    startCamera();
                } else if (items[i].equals(getString(R.string.gallery))) {
                    Intent intent = getMyIntent();
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_IMAGE);
                } else if (items[i].equals(getString(R.string.web))) {
                    if(Singleton.getInstance().getAwareness().getImage()!=null) {
                        ImageView imageView = findViewById(R.id.imageView_image);
                        Bitmap bit = Singleton.getInstance().getAwareness().getImage();
                        Uri image2 = getImageUri(AddActivity.this, bit);
                        if(image2==null){
                            imageView.setImageBitmap(bit);
                        }else {
                            Glide.with(AddActivity.this).load(image2).into(imageView);
                            uploadImage(image2);
                        }
                    }else{
                        Toast.makeText(AddActivity.this, "No image available", Toast.LENGTH_SHORT).show();
                    }
                } else if (items[i].equals(getString(R.string.cancel))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * Método selectVideo(): Abre um AlertDialog que permite ao utilizador escolher se pretende
     * escolher um video da galeria ou gravar um video a partir da camera
     */
    private void selectVideo() {

        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle(R.string.add_video);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(getString(R.string.camera))) {
                    try {
                        Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                    } catch (Exception e) {
                        Toast.makeText(AddActivity.this, R.string.not_completed, Toast.LENGTH_SHORT).show();
                    }
                } else if (items[i].equals(getString(R.string.gallery))) {
                    Intent intent = getMyIntent();
                    intent.setType("video/*");
                    startActivityForResult(intent, REQUEST_CODE_VIDEO);
                } else if (items[i].equals(getString(R.string.cancel))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Método playVideo(): Método que faz com que o video comece a dar quando é chamado
     *
     * @param videoView
     * @param videoUri
     */
    private void playVideo(VideoView videoView, Uri videoUri) {
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }

    /**
     * Método onCaptureImageResult(): Método que permite usar a camera para tirar uma fotografia,
     * guarda a mesma e mostra-a
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView imageView = findViewById(R.id.imageView_image);
        Glide.with(this).load(destination).into(imageView);
    }


    /**
     * Método getMyIntent(): Permite fazer a escolha do tipo de intent a usar de acordo
     * com a versão de android utilizada
     *
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    @NonNull
    private Intent getMyIntent() {

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    /**
     * Método getCameraFile(): Recebe a foto da camera e guarda-a com um UUID novo
     * @return file
     */
    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        fileName = UUID.randomUUID().toString() + FILE_NAME;
        file = new File(dir, fileName);
        return file;
    }

    /**
     * Método startCamera(): Cria o intent para abrir a camera e tirar a foto
     */
    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    /**
     * Método uploadImage(): Envia a imagem para a cloudVision Task onde é enviada e processada
     * pela CloudVisionAPI
     * Permite ao utilizador escolher que keywords pretende adicionar à nota a partir das
     * keywords sugeridas pela CloudVision API
     * @param uri
     */
    @SuppressLint("StaticFieldLeak")
    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                TextView tv_upload = findViewById(R.id.tv_upload);
                tv_upload.setVisibility(View.VISIBLE);

                // scale the image to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                        1200);

                new CloudVisionTask(bitmap, this) {
                    @Override
                    protected void onPostExecute(String string) {
                        TextView tv_upload = findViewById(R.id.tv_upload);
                        tv_upload.setVisibility(View.GONE);
                        String[] splited = string.split("\n");


                        for (final String keyword : splited) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                            LayoutInflater inflater = AddActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.cloudvision_keywords, null);
                            builder.setTitle("Add Keyword");

                            TextView tv_keywd = dialogView.findViewById(R.id.tv_cloud_keywd);
                            tv_keywd.setText(keyword);
                            builder.setView(dialogView)
                                    // Add action buttons
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            EditText et_addkeyword = findViewById(R.id.editText_AddKeyword);
                                            et_addkeyword.append(keyword+"; ");
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();

                            alertDialog.show();
                        }
                    }

                }.execute();

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Image Picker Error", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Image Picker Error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método scaleBitmapDown(): Diminui o tamanho da foto
     * @param bitmap
     * @param maxDimension
     * @return
     */
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

    /**
     * Método getImageUri(): Transforma Bitmap em URI
     * @param inContext
     * @param inImage
     * @return
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Image", null);
        if(path!=null) {
            return Uri.parse(path);
        }
        return null;
    }

}
