package edu.uob;
import java.util.*;

public class SelectCMD extends CheckParser{
    private String okOrError;

    public SelectCMD(HashMap<String, ArrayList<Table>> databases, String line, String currentDatabase) {
        super(line);
        int index = 0;
        String TableName = super.getTableName().get(0);
        if(currentDatabase == null){
            okOrError = "[ERROR] no current database ";
            return;
        }
        if(!databases.containsKey(currentDatabase)){
            okOrError = "[ERROR] no current database ";
            return;
        }
        boolean checkDuplicate = false;
        for(int i = 0; i < databases.get(currentDatabase).size(); i ++){
            if(databases.get(currentDatabase).get(i).getName().compareToIgnoreCase(TableName)==0){
                index = i; // table index
                checkDuplicate = true;
            }
        }

        if(!checkDuplicate){
            okOrError = "[ERROR] no such table ";
            return;
        }
        boolean IsAll = false;
        boolean checkAttributeDuplicate = false;
        String[] lineSplit = line.trim().split("\\s+");
        int choice = 0;
        for (String s : lineSplit) {
            if (s.toLowerCase().contains(")or(")) {
                choice = 1;

            }
            if (s.toLowerCase().contains(")and(")) {
                choice = 2;

            }
            if (s.toLowerCase().contains("where")) {
                choice = 3;
            }
            if (s.toLowerCase().contains("*")) {
                IsAll = true;
                checkAttributeDuplicate = true;
            }
        }
        int count = 0; // count how many attributes
        for(int k = 0; k < super.getColName().size(); k ++){
            for(int j = 0; j < databases.get(currentDatabase).get(index).getTitle().size(); j ++){
                if(databases.get(currentDatabase).get(index).getTitle().get(j).toLowerCase().contains(super.getColName().get(k).toLowerCase())){
                    count++;
                }
            }
        }
        //check the number of the attributes are the same in the database
        if(count == super.getColName().size()) {
            checkAttributeDuplicate = true;

        }
        if(!checkAttributeDuplicate){
            okOrError = "[ERROR] no such AttributeName ";
            return;
        }
        ArrayList<Integer> SelectedRows = new ArrayList<>();
        ArrayList<Integer> SelectedSecondRows = new ArrayList<>();
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
        ArrayList<Integer> col = new ArrayList<>();
              // add every colName index  because of the *
        if(super.getColName().contains("*")){
            for(int i = 0; i < databases.get(currentDatabase).get(index).getTitle().size(); i ++){
                col.add(i);
            }
        }
            //add selected colName index
        else{
            for(int i = 0; i < super.getColName().size(); i ++){
                col.add(databases.get(currentDatabase).get(index).getTitle().indexOf(super.getColName().get(i)));
            }
        }
        //select the data
        ArrayList<String> Correct = new ArrayList<>();
        if(IsAll){
            for(int i = 0; i < databases.get(currentDatabase).get(index).getTitle().size(); i ++){
                Correct.add(databases.get(currentDatabase).get(index).getTitle().get(i)+ "  ");
            }
        }
        else{
            for(int i = 0; i < super.getColName().size(); i ++){
                Correct.add(super.getColName().get(i)+ "  ");
            }
        }
        //store the data abd colName into new table
        for (Integer selectedRow : SelectedRows) {
            for (Integer integer : col) {
                System.out.print(databases.get(currentDatabase).get(index).getData().get(selectedRow).get(integer) + "  ");
                Correct.add(databases.get(currentDatabase).get(index).getData().get(selectedRow).get(integer) + "  ");
            }
        }
        if(okOrError == null){
            okOrError = "[OK]" + Correct;
        }


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
    if(choice == 0){
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
    // if both are digit, then compare
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
            if(IsLetter(Value.get(0)) && IsLetter(ColName.get(0)) || IsNum(ColName.get(0)) && IsNum(Value.get(0))){
                for(int b = 0; b < databases.get(currentDatabase).get(index).getData().size(); b ++){
                    if(!databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(Value.get(0))){
                        SelectedRows.add(b);
                    }
                }
            }
            else{
                okOrError = "[ERROR] only number can be compared ";
            }

    }
    else if(Operator.get(0).toLowerCase().contains("like")){
        String RemoveSingleQuote = Value.get(0).replace("'","");
        char[] ToChar = RemoveSingleQuote.toCharArray();
        ArrayList<Integer> percentageIndex = new ArrayList<>();
        for(int c = 0; c < RemoveSingleQuote.length(); c ++){
            if(ToChar[c] == '%'){
                percentageIndex.add(c);
            }
        }
        if(percentageIndex.isEmpty()){
            okOrError = "[ERROR] missing %";
            return;
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
            if(percentageIndex.size()  == 2){
                String replace = RemoveSingleQuote.replace("%"," ");
                String[] split = replace.split(" ");
                String target = split[1];
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).contains(target)){
                    SelectedRows.add(b);
                }
            }
            String RemovePercentage =  RemoveSingleQuote.replace("%","");
            //if the % is at the beginning
            if(percentageIndex.get(0) == 0){
                if(databases.get(currentDatabase).get(index).getData().get(b).get(targetCol).endsWith(RemovePercentage)){
                    SelectedRows.add(b);
                }

            }
            // if the % is at the end
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

    public boolean IsLetter(String str){
        char [] chars = str.toCharArray();
        return CharLiteral(chars);
    }

    public String getOkOrError() {
        return okOrError;
    }
}


