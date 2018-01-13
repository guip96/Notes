package pt.ipleiria.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Singleton;

/**
 * Recebe notas a partir do ficheiro de CSV
 */

public class CSVFile {

    public void readNotes(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            reader.readLine();
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                ArrayList<String> keywords = new ArrayList<>();
                for(int i=2;i<row.length;i++){
                    keywords.add(row[i]);
                }
                Note n = new Note(row[0], row[1], keywords,null,null);
                Singleton.getInstance().getNotes().addNote(n);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }
}

