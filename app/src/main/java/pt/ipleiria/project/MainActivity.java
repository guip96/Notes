package pt.ipleiria.project;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import pt.ipleiria.project.model.Checkboxes;
import pt.ipleiria.project.model.Constants;
import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Notes;
import pt.ipleiria.project.model.Singleton;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CSV_FILE = 3;
    private static final String EXPORT_FILE_FOLDER = "Downloads";
    private static final int REQUEST_ENABLE_BT = 4;
    private PendingIntent myPendingIntent;
    private MyFenceReceiver myFenceReceiver;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";

    public static final String NOTES_DETAILS = "NOTES_DETAILS";
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_DETAILS = 2;
    public static final String PROJECT_NOTE = "NOTE";
    private ListView listView;
    private ListView bluetooth_lv;

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<BluetoothDevice> mArrayAdapter;
    private BluetoothServerSocket mmServerSocket;
    BluetoothDevice device;
    private static final String MY_UUID = "8bed9cd2-e835-4163-af66-23ae08c9d6b1";

    /**
     * Método onCreate(): Carrega o layout da Main Activity e cria a toolbar, carrega do ficheiro
     * .bin todos os dados, da lista de notas e das checkboxes. Carrega a listview e quando é
     * realizado um click num componente da lista é criado um intent que chama a AddActivity e
     * mostra os detalhes da nota que foi clicada. Um long click abre um dialog que pergunta ao
     * utilizador se pretende apagar a nota, se a resposta for sim então a nota é apagada, se for
     * não é mostrada uma Toast
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        try {
            FileInputStream fileInputStream = openFileInput(getString(R.string.notes_bin));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Singleton.getInstance().setNotes((Notes) objectInputStream.readObject());
            Singleton.getInstance().setCheckboxes((Checkboxes) objectInputStream.readObject());


            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.error_internal_storage, Toast.LENGTH_LONG).show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.error_reading_storage, Toast.LENGTH_LONG).show();
        }

        listView = findViewById(R.id.listView_notes);
        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i3 = new Intent(MainActivity.this, AddActivity.class);
                Note n = (Note) parent.getItemAtPosition(position);
                i3.putExtra(NOTES_DETAILS, REQUEST_CODE_DETAILS);
                i3.putExtra(PROJECT_NOTE, n);
                startActivity(i3);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.note_delete);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Singleton.getInstance().getNotes().removeNote(position);
                        updateListView();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    /**
     * Método onResume():Faz refresh da lista de notas sempre que a activity é aberta, tanto no
     * inicio como sempre que a aplicação volta à MainActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    /**
     * Método onOptionsItemSelected(): Faz a deteção do clique nos botões que estão na toolbar,
     * tanto botões como as checkboxes
     * -----------------------NOVO--------------------------------------------------------
     * Bluetooth, abre uma DialogView com um Custom Layout que permite ao utilizador
     * enviar todas as notas e receber notas
     * Botão do contexto abre a Context Activity
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_search):
                Intent i = new Intent(this, SearchActivity.class);
                startActivity(i);
                break;
            case (R.id.action_add):
                Intent i2 = new Intent(this, AddActivity.class);
                startActivityForResult(i2, REQUEST_CODE_ADD);
                break;
            case R.id.contains_description:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Singleton.getInstance().getCheckboxes().setFilterByBody(false);
                    updateListView();
                } else {
                    item.setChecked(true);
                    Singleton.getInstance().getCheckboxes().setFilterByBody(true);
                    updateListView();
                }
                break;
            case R.id.contains_image:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Singleton.getInstance().getCheckboxes().setFilterByImage(false);
                    updateListView();
                } else {
                    item.setChecked(true);
                    Singleton.getInstance().getCheckboxes().setFilterByImage(true);
                    updateListView();
                }
                break;
            case R.id.contains_video:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Singleton.getInstance().getCheckboxes().setFilterByVideo(false);
                    updateListView();
                } else {
                    item.setChecked(true);
                    Singleton.getInstance().getCheckboxes().setFilterByVideo(true);
                    updateListView();
                }
                break;
            case R.id.contains_context:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Singleton.getInstance().getCheckboxes().setFilterByContext(false);
                    updateListView();
                } else {
                    item.setChecked(true);
                    Singleton.getInstance().getCheckboxes().setFilterByContext(true);
                    updateListView();
                }
                break;
            case R.id.update_context:
                Intent i3 = new Intent(this, ContextActivity.class);
                startActivity(i3);
                break;
            case R.id.bluetooth:
                /////////////////////////////////////BLUETOOTH\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.bluetooth_layout, null);
                final Spinner spinner_devices = dialogView.findViewById(R.id.spi_paired_devices);

                final ArrayAdapter<String> deviceNames = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        new ArrayList<String>());

                final ArrayAdapter<BluetoothDevice> mArrayAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        new ArrayList<BluetoothDevice>());

                //TURN ON IF OFF
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "Bluetooth is not available.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(MainActivity.this, "Bluetooth is not enabled.", Toast.LENGTH_SHORT).show();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        Toast.makeText(MainActivity.this, "Bluetooth was already enabled.", Toast.LENGTH_SHORT).show();
                    }
                }
                //LIST DEVICES
                if (mBluetoothAdapter != null) {
                    mArrayAdapter.clear();

                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            mArrayAdapter.add(device);
                            deviceNames.add(device.getName());
                        }
                        mArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }

                //////////////////////////SWITCH\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                Switch switch_accept = dialogView.findViewById(R.id.accept_connections);
                switch_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (mBluetoothAdapter != null) {
                                ServerTask serverTask = new ServerTask();
                                serverTask.execute();
                            } else {
                                Toast.makeText(MainActivity.this, "Bluetooth Adapter not acquired yet. Please press the \"Setup Bluetooth\" button first.", Toast.LENGTH_SHORT).show();
                                buttonView.setChecked(false);
                            }
                        } else {
                            if (mmServerSocket != null) {
                                try {
                                    mmServerSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Error closing BluetoothServerSocket.", Toast.LENGTH_SHORT).show();
                                }

                                mmServerSocket = null;
                                Toast.makeText(MainActivity.this, "Bluetooth Server Socket closed.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Error closing BluetoothServerSocket.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                spinner_devices.setAdapter(deviceNames);
                spinner_devices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        device = mArrayAdapter.getItem(position);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //SEND BUTTON
                Button btn_send = dialogView.findViewById(R.id.btn_send);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(device!=null) {
                            ConnectTask connectTask = new ConnectTask(device);
                            connectTask.execute("asd");
                            //connectTask.execute(Singleton.getInstance().getNotes().toString());
                            Log.d("Bluetooth","Sending");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No Device Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setTitle("Bluetooth");
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                break;
            case R.id.import_notes:
                Intent fileExplorerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                fileExplorerIntent.setDataAndType(FileProvider.getUriForFile(this,MainActivity.this.getApplicationContext().getPackageName() + ".provider",Environment.getExternalStoragePublicDirectory(EXPORT_FILE_FOLDER)), "*/*");
                try {
                    startActivityForResult(fileExplorerIntent, REQUEST_CODE_PICK_CSV_FILE);
                } catch (ActivityNotFoundException e) {
                    Log.e("TAG", "No activity can handle picking a file. Showing alternatives.");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método onCreateOptionsMenu(): "Cria" a toolbar na MainActivity e faz set's nas checkboxes
     * dos filtros
     * ------------------------NOVO-------------------------------------------------
     * Filtro do contexto
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item0 = menu.findItem(R.id.contains_description);
        item0.setChecked(Singleton.getInstance().getCheckboxes().isFilterByBody());
        MenuItem item1 = menu.findItem(R.id.contains_image);
        item1.setChecked(Singleton.getInstance().getCheckboxes().isFilterByImage());
        MenuItem item2 = menu.findItem(R.id.contains_video);
        item2.setChecked(Singleton.getInstance().getCheckboxes().isFilterByVideo());
        MenuItem item3 = menu.findItem(R.id.contains_context);
        item3.setChecked(Singleton.getInstance().getCheckboxes().isFilterByContext());

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Método onActivityResult(): Se for criado um intent e chamada uma actividade à espera de um
     * resultado com o código REQUEST_CODE_ADD então é adicionada uma nota nova à lista de notas
     *
     * ----------------------------------NOVO-----------------------------------------------------
     * CSV -> Recebe o ficheiro CSV e recebe todas as notas no mesmo
     * Bluetooth-> Mostra Toast quando o bluetooth é ligado
     *
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
                updateListView();
            }
        }
        if(resultCode==RESULT_OK && requestCode == REQUEST_CODE_PICK_CSV_FILE && data!=null){
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                new CSVFile().readNotes(inputStream);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Something went wrong while decoding the file. Be sure that the file is the correct!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Bluetooth enabled successfully: " + mBluetoothAdapter.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método onPause(): Grava a lista e as checkboxes sempre que o programa sai da MainActivity
     *
     * --------------------------------NOVO----------------------------------------
     * Coloca todas as checkboxes das fences a true de modo a que estas estejam ligadas
     * quando é aberta a aplicação
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream fileOutputStream = openFileOutput(getString(R.string.notes_bin), Context.MODE_PRIVATE);

            Singleton.getInstance().getCheckboxes().setTimeFence(true);
            Singleton.getInstance().getCheckboxes().setActivityFence(true);
            Singleton.getInstance().getCheckboxes().setLocationFence(true);
            Singleton.getInstance().getCheckboxes().setHeadphoneFence(true);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(Singleton.getInstance().getNotes());
            objectOutputStream.writeObject(Singleton.getInstance().getCheckboxes());

            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método OnStart(): Faz o registo das fences
     * Permite a que as fences sejam mostradas no main activity
     */
    @Override
    protected void onStart() {
        final Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        myFenceReceiver = new MyFenceReceiver();

        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        super.onStart();
    }


    /**
     * Método updateListView(): Método que contém o adapter da lista de notas e faz a sua
     * actualização sempre que é chamado de acordo com as checkboxes dos filtros
     * Se forem seleccionados todos os campos a lista mostra todas as notas, se for seleccionado o
     * filtro por descrição mostra apenas as notas que apresentam body, o mesmo para os outros
     * parâmetros
     */
    protected void updateListView() {
        ArrayList<Note> searched = Singleton.getInstance().getNotes().filterNote(Singleton.getInstance().getCheckboxes());
        ArrayAdapter<Note> searchedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searched);
        listView.setAdapter(searchedAdapter);

        if (!Singleton.getInstance().getCheckboxes().isFilterByBody()
                && !Singleton.getInstance().getCheckboxes().isFilterByImage()
                && !Singleton.getInstance().getCheckboxes().isFilterByVideo()
                && !Singleton.getInstance().getCheckboxes().isFilterByContext()) {
            Toast.makeText(this, R.string.no_filter, Toast.LENGTH_SHORT).show();
        } else {
            if (searched.isEmpty()) {
                Toast.makeText(this, R.string.no_notes_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ConnectTask: Faz a ligação entre dispositivos por bluetooth
     */
    private class ConnectTask extends AsyncTask<String, Void, String> {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        public ConnectTask(BluetoothDevice device) {
            mmDevice = device;
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                Log.i("ConnectTask", "Socket created.");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "ConnectTask error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            try {
                mmSocket.connect();
                Log.i("ConnectTask", "Connected to remote device.");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return "ERROR: " + e.getMessage();
            }
            try {
                sendData(mmSocket, params[0]);
                Log.i("ConnectTask", "Data sent: " + params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }

            return "Data sent to " + mmDevice.getName() + ": " + params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (mmSocket != null && mmSocket.isConnected()) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "ConnectTask error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(s);
            builder.create().show();
        }

        private void sendData(BluetoothSocket socket, String data) throws IOException {
            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(data);
            printStream.close();
        }
    }

    /**
     * Envio de dados através do Bluetooth
     */
    private class ServerTask extends AsyncTask<String, Void, String> {

        public ServerTask() {
            try {
                mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Notes", UUID.fromString(MY_UUID));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "ServerTask error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            BluetoothSocket socket;
            String res = null;

            while (true) { // keep listening until exception occurs or a socket is returned
                try {
                    socket = mmServerSocket.accept();
                    Log.i("ServerTask", "Connection established.");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                if (socket != null) { // if a connection was accepted
                    try {
                        String data = receiveData(socket);
                        Log.i("ServerTask", "Data received: " + res);

                        res = "Data received from "
                                + socket.getRemoteDevice().getName() + ": "
                                + data;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        break; // stop listening
                    }
                }
            }
            return res;
        }
        private String receiveData(BluetoothSocket socket) throws IOException {
            InputStream inputStream = socket.getInputStream();
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}