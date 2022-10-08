package edu.uob;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.*;


public class DropCMD extends CheckParser{
    private String okOrError;
    public DropCMD(HashMap<String, ArrayList<Table>> databases, String line, String currentDatabase) throws IOException {
        super(line);
        Path thePath = Paths.get("");
        String directoryName = thePath.toAbsolutePath().normalize().toString();
        String Path = directoryName.replace("cw-db","testing");
//        String Path = "/Users/chous/Desktop/07 Briefing on DB assignment/resources/testing";
        String[] lineSplit = line.trim().split("\\s+");
        int choice = 0;
        //check drop database or table
        for (String s : lineSplit) {
            if (s.toLowerCase().contains("database")) {
                choice = 1;
                break;
            }
            if (s.toLowerCase().contains("table")) {
                choice = 2;
                break;
            }
        }
        //drop database
        if(choice == 1){
            currentDatabase = super.getCreateOrDatabase();
            String directory = Path + File.separator + currentDatabase;
            Path path = Paths.get(directory);
            File Directory = new File(directory);
            if(Directory.exists()){
                Files.delete(path);
                databases.remove(currentDatabase);
                if(okOrError == null){
                    okOrError = "[OK] database has removed ";
                }

            }
            else{
                okOrError = "[ERROR] no current database";
            }
            return;
        }
        //drop table
        if(choice == 2){
            if(currentDatabase == null){
                okOrError = "[ERROR] no current database";
                return;
            }
            String DropTable = super.getTableName().get(0);
            String file = Path + File.separator + currentDatabase + File.separator + DropTable + ".tab";
            File FileWantToDrop = new File(file);
            if(FileWantToDrop.exists()){
                FileWantToDrop.delete();
                databases.get(currentDatabase).remove(DropTable);
                if(okOrError == null){
                    okOrError = "[OK] table has removed ";
                }
            }
            else{
                okOrError = "[ERROR] this table does not exist";
            }
        }

    }

    public String getOkOrError() {
        return okOrError;
    }
}
