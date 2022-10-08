package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateCMD extends CheckParser {
    private String okOrError;
    public CreateCMD(HashMap<String, ArrayList<Table>> databases, String line,String currentDatabase) throws IOException {
        super(line);
        Path thePath = Paths.get("");
        String directoryName = thePath.toAbsolutePath().normalize().toString();
        String Path = directoryName.replace("cw-db","testing");
        //create database
        if(super.getCreateOrDatabase() != null){
            String name = super.getCreateOrDatabase();
            String FolderName = Path + File.separator + name;
            File emailFolder = new File(FolderName);
            emailFolder.mkdir();
            databases.put(name,new ArrayList<>());
            if(okOrError == null){
                okOrError = "[OK] database has created ";
            }
        }
        //create table
        else{
            if(currentDatabase == null){
                okOrError = "[ERROR] no current database";
                return;
            }
            String name = super.getTableName().get(0);
            String tab = ".tab";
            String fileName = Path +  File.separator + currentDatabase + File.separator + name + tab;
            File fileToOpen = new File(fileName);
            FileWriter writer = new FileWriter(fileToOpen);
            for(int i = 0; i < super.getColName().size(); i ++){
                writer.write(super.getColName().get(i));
                writer.write("      ");
            }
            writer.flush();
            writer.close();
            Table fileTable = new Table(name);
            databases.get(currentDatabase).add(fileTable);
            ArrayList<String> newTitle = new ArrayList<>(super.getColName());
            int index = databases.get(currentDatabase).indexOf(fileTable);
            databases.get(currentDatabase).get(index).setTitle(newTitle);
            if(okOrError == null){
                okOrError = "[OK] table has created ";
            }

        }

    }

    public String getOkOrError() {
        return okOrError;
    }
}
