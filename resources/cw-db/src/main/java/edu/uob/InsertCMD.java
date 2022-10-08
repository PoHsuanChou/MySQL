package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertCMD extends CheckParser{
    private String okOrError;
    public InsertCMD(HashMap<String, ArrayList<Table>> databases, String line, String currentDatabase) throws IOException {
        super(line);
        String TableName = super.getTableName().get(0);
        int index= 0; // table index
        if(!databases.get(currentDatabase).get(index).getData().isEmpty()){
            if(super.getData().size() != databases.get(currentDatabase).get(index).getData().get(0).size()){
                okOrError = "[ERROR] the number of data is incorrect";
                return;
            }
        }

        if(!databases.containsKey(currentDatabase)){
            okOrError = "[ERROR] there is no such database";
        }
        boolean checkDuplicate = false;
        for(int i = 0; i < databases.get(currentDatabase).size(); i ++){
            if(databases.get(currentDatabase).get(i).getName().compareToIgnoreCase(TableName)==0){
                index = i;
                checkDuplicate = true;
            }
        }
        if(!checkDuplicate){
            okOrError = "[ERROR] there is no such table";
        }
        int rows = databases.get(currentDatabase).get(index).getData().size() - 1 ;
        ArrayList<ArrayList<String>> newData;
        // if table is empty
        if(rows == -1 ){
            newData = new ArrayList<>();
            newData.add(new ArrayList<>());
            for(int i = 0; i < super.getData().size(); i ++){
                String word = super.getData().get(i);
                String newWord = word.replace("'", "");
                newData.get(0).add(newWord);
            }
        }
        else{
            newData = new ArrayList<>(databases.get(currentDatabase).get(index).getData());
            newData.add(new ArrayList<>());
            for(int i = 0; i < super.getData().size(); i ++){
                String word = super.getData().get(i);
                String newWord = word.replace("'", "");
                newData.get(rows+ 1).add(newWord);
            }
        }
        databases.get(currentDatabase).get(index).setData(newData);
        UpdateNewFile(databases,currentDatabase,index);
        if(okOrError == null){
            okOrError = "[OK] data has been insert ";
        }

    }

    public void UpdateNewFile(HashMap<String, ArrayList<Table>> databases, String currentDatabase,int index ) throws IOException {
        Path thePath = Paths.get("");
        String directoryName = thePath.toAbsolutePath().normalize().toString();
        String Path = directoryName.replace("cw-db","testing");
        String tableName = databases.get(currentDatabase).get(index).getName();
        String file = Path + File.separator + currentDatabase + File.separator + tableName + ".tab";
        File FileWantToDrop = new File(file);
        if(FileWantToDrop.exists()){
            FileWantToDrop.delete();
        }
        else{
            System.out.println("this file does not exist");
            return;
        }
        String fileName = Path +  File.separator + currentDatabase + File.separator + tableName + ".tab";
        File fileToOpen = new File(fileName);
        FileWriter writer = new FileWriter(fileToOpen);
        for(int m = 0; m < databases.get(currentDatabase).get(index).getTitle().size(); m++){
            writer.write(databases.get(currentDatabase).get(index).getTitle().get(m));
            writer.write("      ");
        }
        writer.write("\n");
        for(int n = 0; n < databases.get(currentDatabase).get(index).getData().size();n ++){
            for(int o = 0; o < databases.get(currentDatabase).get(index).getData().get(n).size(); o ++){
                writer.write(databases.get(currentDatabase).get(index).getData().get(n).get(o));
                writer.write("      ");
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }

    public String getOkOrError() {
        return okOrError;
    }
}
