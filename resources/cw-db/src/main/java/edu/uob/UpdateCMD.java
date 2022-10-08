package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class UpdateCMD extends CheckParser{
    private String okOrError;
    public UpdateCMD(HashMap<String, ArrayList<Table>> databases, String line, String currentDatabase) throws IOException {
        super(line);
        if(currentDatabase == null){
            okOrError = "[ERROR] no current database ";
            return;
        }
        if(!databases.containsKey(currentDatabase)){
            okOrError = "[ERROR] no current database ";
            return;
        }
        String TableName = super.getTableName().get(0);
        boolean checkDuplicate = false;
        int index = 0;
        for(int i = 0; i < databases.get(currentDatabase).size(); i ++){
            if(databases.get(currentDatabase).get(i).getName().compareToIgnoreCase(TableName)==0){
                index = i;
                checkDuplicate = true;
            }
        }

        if(!checkDuplicate){
            okOrError = "[ERROR] table number does not match ";
            return;
        }
        ArrayList<Integer> SelectedRows = new ArrayList<>();
        String[] lineSplit = line.trim().split("\\s+");
        ArrayList<Integer> SelectedSecondRows = new ArrayList<>();
        int choice = 0;
        for (String s : lineSplit) {
            if (s.toLowerCase().contains(")or(")) {
                choice = 1;
                break;
            }
            if (s.toLowerCase().contains(")and(")) {
                choice = 2;
                break;
            }
            if (s.toLowerCase().contains("where")) {
                choice = 3;
            }
        }
        if(choice == 0){
            StoreData(databases,SelectedRows,currentDatabase,index,choice);
            ChooseData(SelectedRows);
        }
        if(choice == 1){
            StoreData(databases,SelectedRows,currentDatabase,index,choice);
            StoreData(databases,SelectedSecondRows,currentDatabase,index,choice);
            SelectedRows= ChooseData(SelectedRows,SelectedSecondRows,choice);
        }
        else if(choice == 2){
            StoreData(databases,SelectedRows,currentDatabase,index,choice);
            StoreData(databases,SelectedSecondRows,currentDatabase,index,choice);
            SelectedRows= ChooseData(SelectedRows,SelectedSecondRows,choice);
        }
        else if(choice == 3){
            StoreData(databases,SelectedRows,currentDatabase,index,choice);
            SelectedRows= ChooseData(SelectedRows,SelectedSecondRows,choice);
        }

        int count = 0;
        for(int i = 0; i < super.getColName().size(); i ++){
            for(int j = 0; j <databases.get(currentDatabase).get(index).getTitle().size() ; j ++){
                if(databases.get(currentDatabase).get(index).getTitle().get(j).contains(super.getColName().get(i))){
                    count++;
                }
            }
        }
        if(count == super.getColName().size()){
            for(int i = 0; i < super.getColName().size(); i ++){
                UpdateData(databases,SelectedRows,currentDatabase,index,i);
            }
        }
        UpdateNewFile(databases,currentDatabase,index);
        if(okOrError == null){
            okOrError = "[OK] data has been update";
        }



    }
    public void UpdateData(HashMap<String, ArrayList<Table>> databases,ArrayList<Integer> SelectedRows,String currentDatabase, int index, int i )
    {
        ArrayList<ArrayList<String>> newData = new ArrayList<>(databases.get(currentDatabase).get(index).getData());
        int targetAttribute = databases.get(currentDatabase).get(index).getTitle().indexOf(super.getColName().get(i));
        if(targetAttribute == -1){
            okOrError = "[ERROR] no target Attribute" ;
            return;
        }
        for (int row : SelectedRows) {
            newData.get(row).set(targetAttribute, super.getData().get(i).replace("'", ""));
        }
        databases.get(currentDatabase).get(index).setData(newData);

    }

    public void ChooseData(ArrayList<Integer> SelectedRows) {
        ArrayList<Integer> newArray = new ArrayList<>();
        for (int element : SelectedRows) {
            if (!newArray.contains(element)) {
                newArray.add(element);
            }
        }
    }
    public ArrayList<Integer> ChooseData(ArrayList<Integer>SelectedRows,ArrayList<Integer>SelectedSecondRows, int choice){
        if(choice ==1){
            TreeSet<Integer> set = new TreeSet<>();
            set.addAll(SelectedRows);
            set.addAll(SelectedSecondRows);
            return new ArrayList<>(set);
        }
        if(choice == 2){
            ArrayList<Integer> newArray = new ArrayList<>();
            for (Integer selectedRow : SelectedRows) {
                for (Integer selectedSecondRow : SelectedSecondRows) {
                    if (selectedRow.equals(selectedSecondRow)) {
                        newArray.add(selectedRow);
                    }
                }
            }
            return newArray;
        }

        return SelectedRows;
    }




    public void StoreData(HashMap<String, ArrayList<Table>> databases,ArrayList<Integer> SelectedRows,String currentDatabase, int index,int choice)
    {
        if(choice != 1 && choice != 2 && choice != 3){
            for(int i = 0; i < databases.get(currentDatabase).get(index).getData().size(); i ++){
                SelectedRows.add(i);
            }
            return;
        }
        ArrayList<String> ColName = new ArrayList<>();
        ArrayList<String> Operator = new ArrayList<>();
        ArrayList<String> Value = new ArrayList<>();
        ColName.add(super.getConditionList().get(0));
        super.getConditionList().remove(0);
        Operator.add(super.getConditionList().get(0));
        super.getConditionList().remove(0);
        Value.add(super.getConditionList().get(0));
        super.getConditionList().remove(0);
        int targetCol = 0;
        for(int t = 0; t < databases.get(currentDatabase).get(index).getTitle().size(); t ++){
            if(databases.get(currentDatabase).get(index).getTitle().get(t).toLowerCase().contains(ColName.get(0).toLowerCase())){
                targetCol = t;
            }
        }
        boolean CheckDigit = true;
        boolean CheckDigit1 = true;
        for(int x = 0; x < Value.get(0).length(); x ++){
            if(!Character.isDigit(Value.get(0).charAt(x))){
                CheckDigit = false;
            }
        }
        for(int y = 0; y < databases.get(currentDatabase).get(index).getData().size(); y ++){
            String word = databases.get(currentDatabase).get(index).getData().get(y).get(targetCol);
            for(int z = 0; z < word.length();z++){
                if(!Character.isDigit(word.charAt(z))){
                    CheckDigit1 = false;
                }
            }
        }

        if(CheckDigit&& CheckDigit1 ){
            ConditionHandleNum(databases,Operator,Value,SelectedRows,currentDatabase,index,targetCol,0);
        }

        else if(Operator.get(0).contains("==")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(Value.get(0))){
                    SelectedRows.add(b);
                }
            }

        }
        else if(Operator.get(0).contains("!=")){
            if(Isletter(Value.get(0)) && Isletter(ColName.get(0)) || IsNum(ColName.get(0)) && IsNum(Value.get(0))){
                for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                    if(!databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(Value.get(0))){
                        SelectedRows.add(b);
                    }
                }
            }
            else{
                okOrError = "[ERROR] only number can be compared ";
                return;
            }

        }
        else if(Operator.get(0).toLowerCase().contains("like")){
            String RemoveSingleQuote = Value.get(0).replace("'","");
            char[] ToChar = RemoveSingleQuote.toCharArray();
            boolean CheckPercentage = false;
            ArrayList<Integer> percentageIndex = new ArrayList<>();
            for(int c = 0; c < RemoveSingleQuote.length(); c ++){
                if(ToChar[c] == '%'){
                    percentageIndex.add(c);
                }
            }
            ConditionHandleLike(databases,currentDatabase,index,targetCol,percentageIndex,RemoveSingleQuote,SelectedRows);
        }
        else{
            okOrError = "[ERROR] only number can be compared ";
        }
    }

    public void ConditionHandleNum(HashMap<String, ArrayList<Table>> databases,ArrayList<String> Operator,ArrayList<String> Value,ArrayList<Integer> SelectedRows, String currentDatabase, int index, int targetCol,int a){
        if(Operator.get(a).contains(">=")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(Integer.parseInt(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol)) >= Integer.parseInt(Value.get(a))){
                    SelectedRows.add(b);
                }
            }
        }
        else if(Operator.get(a).contains("<=")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(Integer.parseInt(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol)) <= Integer.parseInt(Value.get(a))){
                    SelectedRows.add(b);
                }
            }
        }
        else if(Operator.get(a).contains(">")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(Integer.parseInt(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol)) > Integer.parseInt(Value.get(a))){
                    SelectedRows.add(b);
                }
            }
        }
        else if(Operator.get(a).contains("<")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(Integer.parseInt(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol)) < Integer.parseInt(Value.get(a))){
                    SelectedRows.add(b);
                }
            }
        }
        else if(Operator.get(0).contains("==")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(Value.get(0))){
                    SelectedRows.add(b);
                }
            }
        }
        else if(Operator.get(0).contains("!=")){
            for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                if(!databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(Value.get(0))){
                    SelectedRows.add(b);
                }
            }
        }

    }
    public void ConditionHandleLike (HashMap<String, ArrayList<Table>> databases,String currentDatabase, int index, int targetCol, ArrayList<Integer>  percentageIndex, String RemoveSingleQuote,ArrayList<Integer> SelectedRows ){
        for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
            if(percentageIndex.size() ==2){
                String replace = RemoveSingleQuote.replace("%"," ");
                String[] split = replace.split(" ");
                String target = split[1];
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(target)){
                    SelectedRows.add(b);
                }
            }
            String RemovePercentage =  RemoveSingleQuote.replace("%","");
            if(percentageIndex.get(0) == 0){
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).endsWith(RemovePercentage)){
                    SelectedRows.add(b);
                }

            }
            else if(percentageIndex.get(0) == RemoveSingleQuote.length() - 1){
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).startsWith(RemovePercentage)){
                    SelectedRows.add(b);
                }

            }

        }
    }



    public boolean IsNum(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public boolean Isletter(String str){
        char [] chars = str.toCharArray();
        if(CharLiteral(chars)){
            return true;
        }
        else{
            return false;
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
            okOrError = "[ERROR] this file does not exist";
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
