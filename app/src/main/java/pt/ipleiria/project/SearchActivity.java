package pt.ipleiria.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ipleiria.project.model.Constants;
import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Singleton;

import static pt.ipleiria.project.MainActivity.NOTES_DETAILS;
import static pt.ipleiria.project.MainActivity.PROJECT_NOTE;
import static pt.ipleiria.project.MainActivity.REQUEST_CODE_DETAILS;


public class SearchActivity extends AppCompatActivity {
    private ListView listView_search;
    final ArrayList seletedItems = new ArrayList();
    String thingToSearch;

    /**
     * Método onCreate(): Carrega o layout activity_search e carrega a listview e quando é
     * realizado um click num componente da lista é criado um intent que chama a AddActivity e
     * mostra os detalhes da nota que foi clicada. Um long click abre um dialog que pergunta ao
     * utilizador se pretende apagar a nota, se a resposta for sim então a nota é apagada, se for
     * não é mostrada uma Toast
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.listView_search = (ListView) findViewById(R.id.listView_search);

        this.listView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i4 = new Intent(SearchActivity.this, AddActivity.class);
                Note n = (Note) parent.getItemAtPosition(position);
                i4.putExtra(PROJECT_NOTE, n);
                i4.putExtra(NOTES_DETAILS, REQUEST_CODE_DETAILS);
                startActivity(i4);
            }
        });

        this.listView_search.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setMessage(R.string.note_delete);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = (Note) parent.getAdapter().getItem(position);
                        int realposition = Singleton.getInstance().getNotes().searchNoteByID(note.getId());
                        Singleton.getInstance().getNotes().removeNote(realposition);
                        updateListView(thingToSearch);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SearchActivity.this, R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    /**
     * Método button_search_onClick(): Quando clica no botão Search/Pesquisar e não tiver nenhuma
     * checkbox checked então mostra uma Toast pois não está a procurar por nenhum parâmetro
     * Se estiverem parâmetros seleccionados então procura na lista de notas, as notas que contêm
     * o que foi escrito na editText. Procura apenas nos parâmetros seleccionados e se algum
     * desses parâmetros contiverem a string que é suposto procurar mostra essa nota na listView
     * @param v
     */
    public void button_search_onClick(View v) {
        EditText editText_search = findViewById(R.id.editText_search);
        thingToSearch = editText_search.getText().toString();
        if (!Singleton.getInstance().getCheckboxes().isSearchByID()
                && !Singleton.getInstance().getCheckboxes().isSearchByTitle()
                && !Singleton.getInstance().getCheckboxes().isSearchByDescription()
                && !Singleton.getInstance().getCheckboxes().isSearchByKeywords()
                && !Singleton.getInstance().getCheckboxes().isSearchByCreationDate()
                && !Singleton.getInstance().getCheckboxes().isSearchByEditDate()){
            Toast.makeText(this, R.string.not_searching_parameter, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Note> searched = Singleton.getInstance().getNotes().searchNoteByEverything(thingToSearch, Singleton.getInstance().getCheckboxes());
            updateListView(thingToSearch);
            if (searched.isEmpty()) {
                Toast.makeText(this, R.string.no_notes_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método button_searchby_onClick: Quando é clicado o botão Search By / Procurar Por é aberto
     * um AlertDialog que permite ao utilizador escolher por que parâmetros pretende procurar
     * o texto que está a introduzir na editText.
     * @param v
     */
    public void button_searchby_onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.search_by)
                .setMultiChoiceItems(Constants.searchby_checkbox_Items,Singleton.getInstance().getCheckboxes().getFilterItens() , new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                            switch (indexSelected) {
                                case 0:
                                    Singleton.getInstance().getCheckboxes().setSearchByID(true);
                                    break;
                                case 1:
                                    Singleton.getInstance().getCheckboxes().setSearchByTitle(true);
                                    break;
                                case 2:
                                    Singleton.getInstance().getCheckboxes().setSearchByDescription(true);
                                    break;
                                case 3:
                                    Singleton.getInstance().getCheckboxes().setSearchByCreationDate(true);
                                    break;
                                case 4:
                                    Singleton.getInstance().getCheckboxes().setSearchByCreationDate(true);
                                    break;
                                case 5:
                                    Singleton.getInstance().getCheckboxes().setSearchByEditDate(true);
                                    break;
                            }

                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                            switch (indexSelected) {
                                case 0:
                                    Singleton.getInstance().getCheckboxes().setSearchByID(false);
                                    break;
                                case 1:
                                    Singleton.getInstance().getCheckboxes().setSearchByTitle(false);
                                    break;
                                case 2:
                                    Singleton.getInstance().getCheckboxes().setSearchByDescription(false);
                                    break;
                                case 3:
                                    Singleton.getInstance().getCheckboxes().setSearchByCreationDate(false);
                                    break;
                                case 4:
                                    Singleton.getInstance().getCheckboxes().setSearchByCreationDate(false);
                                    break;
                                case 5:
                                    Singleton.getInstance().getCheckboxes().setSearchByEditDate(false);
                                    break;
                            }

                        }
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    /**
     * Método que contém o adapter da lista de notas e faz a sua
     * actualização sempre que é chamado de acordo com as checkboxes dos parâmetros a serem
     * procurados. Se forem seleccionados todos os campos a lista mostra todas as notas que
     * apresentam o texto inserido, se for seleccionado apenas o campo "Title", apenas irão ser
     * mostradas as notas que apresentam o texto inserido no titulo.
     * @param thingToSearch
     */
    protected void updateListView(String thingToSearch) {

        ArrayList<Note> searched = Singleton.getInstance().getNotes().searchNoteByEverything(thingToSearch, Singleton.getInstance().getCheckboxes());
        if(thingToSearch.isEmpty()){
            searched.clear();
            Toast.makeText(this, R.string.nothing_searched, Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<Note> searchedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searched);
        listView_search.setAdapter(searchedAdapter);

    }


}
