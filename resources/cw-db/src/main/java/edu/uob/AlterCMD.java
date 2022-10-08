package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
public class AlterCMD extends CheckParser {
    private String okOrError;
    public AlterCMD(HashMap<String, ArrayList<Table>> databases, String line, String currentDatabase) throws IOException {
        super(line);
        String AlterationType = super.getAlterationType();
        String ColName = super.getColName().get(0);
        String TableName = super.getTableName().get(0);
        Path thePath = Paths.get("");
        String directoryName = thePath.toAbsolutePath().normalize().toString();
        String Path = directoryName.replace("cw-db","testing");
        boolean checkTable = false;
        boolean checkDuplicateAttribute = false;
        int index = 0;//table index
        if (!databases.containsKey(currentDatabase)) {
            okOrError = "[ERROR] there is no such database";
        }
        ArrayList<Table> TableList = databases.get(currentDatabase);
        for (int i = 0; i < TableList.size(); i++) {
            if (TableList.get(i).getName().compareToIgnoreCase(TableName) == 0) {
                checkTable = true;
                index = i ;
            }
        }
        if(!checkTable){
            okOrError = "[ERROR] there is no such table";
            return;

        }
        ArrayList<String> newAttribute = databases.get(currentDatabase).get(index).getTitle();
        ArrayList<ArrayList<String>> newData = TableList.get(index).getData();
        if (AlterationType.compareToIgnoreCase("add") == 0) {
            //add attribute
            for(int j = 0; j < super.getColName().size(); j ++){
                newAttribute.add(ColName);
            }
            //add content as null
            for(int j = 0; j <TableList.get(index).getData().size(); j ++){
                newData.get(j).add("NULL");
            }
            databases.get(currentDatabase).get(index).setData(newData);
            databases.get(currentDatabase).get(index).setTitle(newAttribute);
            UpdateNewFile(databases,Path,currentDatabase,index);
            if(okOrError == null){
                okOrError = "[OK] Attribute has created ";
            }

            return;
        }
        if (AlterationType.compareToIgnoreCase("drop") == 0) {
            for (int k = 0; k < TableList.get(index).getTitle().size(); k++) {
                if (TableList.get(index).getTitle().get(k).compareToIgnoreCase(ColName) == 0) {
                    checkDuplicateAttribute = true;
                    break;
                }
            }
            if (!checkDuplicateAttribute) {
                okOrError = "[ERROR] something wrong with the table";
            }
            int colIndex = 0;
            for(int l = 0; l < TableList.get(index).getTitle().size(); l ++){
                if(TableList.get(index).getTitle().get(l).compareToIgnoreCase(ColName) == 0){
                    colIndex = l;
                }
            }
            newAttribute.remove(colIndex);
            for(int k = 0; k < TableList.get(index).getData().size(); k ++){
                newData.get(k).remove(colIndex);
            }
            databases.get(currentDatabase).get(index).setData(newData);
            databases.get(currentDatabase).get(index).setTitle(newAttribute);
            UpdateNewFile(databases,Path,currentDatabase,index);
            if(okOrError == null){
                okOrError = "[OK] Attribute has created ";
            }
        }
    }
    public void UpdateNewFile(HashMap<String, ArrayList<Table>> databases,String Path, String currentDatabase,int index ) throws IOException {
        String tableName = databases.get(currentDatabase).get(index).getName();
        String file = Path + File.separator + currentDatabase + File.separator + tableName + ".tab";
        File FileWantToDrop = new File(file);
        if(FileWantToDrop.exists()){
            FileWantToDrop.delete();
        }
        else{
            okOrError = "[ERROR] this file does not exist";
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
