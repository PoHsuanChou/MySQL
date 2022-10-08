package edu.uob;
import java.util.*;

public class CheckParser {
    private static int count;
    private final boolean TrueOrFalse;
    ArrayList<String> parserArray= new ArrayList<>();
    private final List<String> ColName = new ArrayList<>();
    private final List<String>TableName = new ArrayList<>();
    private final ArrayList<String>Data = new ArrayList<>();
    private String DatabaseName;
    private String CommandType;
    private String AlterationType;
    private final List<String> ConditionList = new ArrayList<>();
    private String CreateOrDatabase;

    public CheckParser(String line) {
        count = 0;
        line = line.replace(";"," ;");
        line = line.replace(","," , ");
        String[] theLine = line.trim().split("\\s+");
        parserArray.addAll(Arrays.asList(theLine));
        this.TrueOrFalse = Command(parserArray);
    }
    public boolean isTrueOrFalse() {
        return TrueOrFalse;
    }
    public List<String> getColName() {
        return ColName;
    }
    public List<String> getTableName() {
        return TableName;
    }
    public ArrayList<String> getData() {
        return Data;
    }
    public String getDatabaseName() {
        return DatabaseName;
    }
    public String getCommandType() {
        return CommandType;
    }
    public String getAlterationType() {
        return AlterationType;
    }
    public List<String> getConditionList() {
        return ConditionList;
    }
    public String getCreateOrDatabase() {
        return CreateOrDatabase;
    }


    public boolean Command(ArrayList<String> parserArray){
        if(!BoolCommandType(parserArray)){
            return false;
        }
        if(!CommandType(parserArray)){
            return false;
        }
        if(!CheckBoundary(parserArray)){
            return false;
        }
        count++;
        return parserArray.get(count).contains(";");

    }
    //check the command type
    public boolean BoolCommandType(ArrayList<String> parserArray){
        if(parserArray.get(count).compareToIgnoreCase("use")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("create")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Drop")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Alter")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Insert")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Select")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Update")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Delete")== 0){return true;}
        else if(parserArray.get(count).compareToIgnoreCase("Join")== 0){return true;}
        else {
            return false;
        }
    }

    public boolean CommandType(ArrayList<String> parserArray){
        // use command
        if(parserArray.get(count).compareToIgnoreCase("use") == 0){
            this.CommandType = parserArray.get(count);
            if(parserArray.size()!= 3){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            this.DatabaseName = parserArray.get(count);
            return true;
        }
        //create command
        else if(parserArray.get(count).compareToIgnoreCase("create") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            //create table
            if(parserArray.get(count).compareToIgnoreCase("table") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(count + 1 == parserArray.size()-1){
                    if(PlainText(parserArray)){
                        this.TableName.add(parserArray.get(count));
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else if(PlainText(parserArray)){
                    this.TableName.add(parserArray.get(count));
                    if(!CheckBoundary(parserArray)){
                        return false;
                    }
                    count++;
                    if(parserArray.get(count).compareToIgnoreCase("(") == 0){
                        if(!CheckBoundary(parserArray)){
                            return false;
                        }
                        count++;
                        if(AttributeList(parserArray)){
                            if(!CheckBoundary(parserArray)){
                                return false;
                            }
                            count++;
                            return parserArray.get(count).contains(")");
                        }
                    }
                }
                    return false;
            }
            //create database
            else if(parserArray.get(count).compareToIgnoreCase("database") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(PlainText(parserArray)){
                    this.CreateOrDatabase = parserArray.get(count);
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        //drop command
        else if(parserArray.get(count).compareToIgnoreCase("drop") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            //drop table
            if(parserArray.get(count).compareToIgnoreCase("table") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(PlainText(parserArray)){
                    this.TableName.add(parserArray.get(count));
                    return true;
                }
                else{
                    return false;
                }
            }
            //drop database
            else if(parserArray.get(count).compareToIgnoreCase("database") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(PlainText(parserArray)){
                    this.CreateOrDatabase = parserArray.get(count);
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }

        }
        //alter command
        else if(parserArray.get(count).compareToIgnoreCase("alter") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("table") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            TableName.add(parserArray.get(count));//// add table to data
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(AlterationType(parserArray)){
                this.AlterationType = parserArray.get(count); // add alterType
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(PlainText(parserArray)){
                    this.ColName.add(parserArray.get(count)); //add colname
                    return true;
                }
            }
                return false;
        }
        //Insert command
        else if(parserArray.get(count).compareToIgnoreCase("Insert") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("into") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            TableName.add(parserArray.get(count)); //add table name
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("values(") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!ValueList(parserArray)){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            return parserArray.get(count).compareToIgnoreCase(")") == 0;


        }
        //select command
        else if(parserArray.get(count).compareToIgnoreCase("Select") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!WildAttribList(parserArray)){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("FROM") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            this.TableName.add(parserArray.get(count)); //add table name
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("where") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                return Condition(parserArray);
            }
            else{
                if(count == parserArray.size() - 1){
                    count--;
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        //update command
        else if(parserArray.get(count).compareToIgnoreCase("Update") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            this.TableName.add(parserArray.get(count)); // add table name
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("set") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!NameValueList(parserArray)){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("where") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            return Condition(parserArray);

        }
        //delete command
        else if(parserArray.get(count).compareToIgnoreCase("delete") == 0){
            this.CommandType = parserArray.get(count);
            if(!CheckBoundary(parserArray)){
                return false;
            }
             count++;
             if(parserArray.get(count).compareToIgnoreCase("from") != 0){
                 return false;
             }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!PlainText(parserArray)){
                return false;
            }
            this.TableName.add(parserArray.get(count));
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase("where") != 0){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            return Condition(parserArray);
        }
        else {
            return false;
        }
    }

    // check whether it is a plaintext (letter or digit)
    public boolean PlainText(ArrayList<String> parserArray){
        String s = parserArray.get(count);
        if(s == null){
            return false;
        }
        for(int i = 0; i < s.length(); i ++){
            if(!Character.isLetterOrDigit(s.charAt(i))){
                return false;
            }
        }
        return true;
    }
    //check add or drop command
    public boolean AlterationType(ArrayList<String> parserArray){
        return parserArray.get(count).compareToIgnoreCase("add") == 0 || parserArray.get(count).compareToIgnoreCase("drop") == 0;
    }
    //check TRUE or FALSE case-sensitive
    public boolean BooleanLiteral(ArrayList<String> parserArray){
        return parserArray.get(count).contains("TRUE") || parserArray.get(count).contains("FALSE");
    }

    //<StringLiteral>  ::=  "" | <CharLiteral> | <CharLiteral> <StringLiteral>
    public boolean StringLiteral(String s){
        char [] chars = s.toCharArray();
        return CharLiteral(chars); // <CharLiteral>    ::=  <Space> | <Letter> | <Symbol>
    }

    // <Space> | <Letter> | <Symbol>
    public boolean CharLiteral(char[] charArray){
        int check = 0;
        for (char c : charArray) {
            if (IsLetter(c) || IsSymbol(c) || c == ' ') {
                check++;
            }
        }
        return check == charArray.length;
    }
    public boolean IsLetter(char c){
        return Character.isLetter(c);
    }
    public boolean IsSymbol(char c){
        char[] symbol = {'!' , '#' , '$' , '%' , '&' , '(' , ')' , '*' , '+' , ',' , '-' , '.' , '/' , ':' , ';' , '>' , '=' , '<' , '?' , '@' , '[' , '\\' , ']' , '^' , '_' , '`' , '{' ,'}' , '~'};
        for (char value : symbol) {
            if (value == c) {
                return true;
            }
        }
        return false;
    }
    //<FloatLiteral>   ::=  <DigitSequence> "." <DigitSequence> |
    // "-" <DigitSequence> "." <DigitSequence> | "+" <DigitSequence> "." <DigitSequence>
    public boolean FloatLiteral(ArrayList<String> parserArray){
        String s = parserArray.get(count);
        if(s == null){
            return false;
        }
        try {
             Double.parseDouble(s);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    //<IntegerLiteral> ::=  <DigitSequence> | "-" <DigitSequence> | "+" <DigitSequence>
    public boolean IntegerLiteral(ArrayList<String> parserArray){
        String s = parserArray.get(count);
        if (s== null) {
            return false;
        }
        try {
            int d = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    //<Value>          ::=  "'" <StringLiteral> "'" | <BooleanLiteral> | <FloatLiteral> | <IntegerLiteral> | "NULL"
    public boolean Value(ArrayList<String> parserArray){
        char firstChar =parserArray.get(count).charAt(0);
        int last = parserArray.get(count).length() - 1;
        char lastChar = parserArray.get(count).charAt(last);
        // remove ''
        if(firstChar == '\'' && lastChar == '\''){
            String s = parserArray.get(count).replace("'", "");
            return StringLiteral(s);
        }
        else if(BooleanLiteral(parserArray)){
            return true;
        }
        else if(FloatLiteral(parserArray)){
            return true;
        }
        else if(IntegerLiteral(parserArray)){
            return true;
        }
        else if(parserArray.get(count).contains("NULL")){
            return true;
        }
        else{
            return false;
        }
    }
    //<ValueList>      ::=  <Value> | <Value> "," <ValueList>
    public boolean ValueList(ArrayList<String> parserArray){
        if(Value(parserArray)){
            Data.add(parserArray.get(count)); // add data
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(parserArray.get(count).compareToIgnoreCase(",") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                return ValueList(parserArray);
            }
                count--;
                return true;
        }
        return false;
    }

    public boolean Operator(ArrayList<String> parserArray){
        String[] c = {"==",">","<",">=","<=","!=" };
        for (String s : c) {
            if (parserArray.get(count).contains(s)) {
                return true;
            }
        }
        return parserArray.get(count).compareToIgnoreCase("like") == 0;
    }
    //<AttributeList>  ::=  <AttributeName> | <AttributeName> "," <AttributeList>
    public boolean AttributeList(ArrayList<String> parserArray){
        if(PlainText(parserArray)){
            this.ColName.add(parserArray.get(count)); // add col name
            if(!CheckBoundary(parserArray)){
                return false;
            }
                count++;
                if(parserArray.get(count).compareToIgnoreCase(",") == 0){
                    if(!CheckBoundary(parserArray)){
                        return false;
                    }
                    count++;
                    return AttributeList(parserArray);
                }
                else{
                    count--;
                    return true;
                }
        }
        return false;
    }
    //<WildAttribList> ::=  <AttributeList> | "*"
    public boolean WildAttribList(ArrayList<String> parserArray){
        if(AttributeList(parserArray)){
            return true;
        }
        else if(parserArray.get(count).contains("*")){
            this.ColName.add(parserArray.get(count)); // add col name
            return true;
        }
        else{
            return false;
        }
    }
    //<Condition>      ::=  "(" <Condition> ")AND(" <Condition> ")" |
    // "(" <Condition> ")OR(" <Condition> ")" | <AttributeName> <Operator> <Value>
    public boolean Condition(ArrayList<String> parserArray){
        if(parserArray.get(count).contains("(")){
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(!Condition(parserArray)){
                return false;
            }
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(count >= parserArray.size()-1){
                count--;
                return true;
            }
            if(parserArray.get(count).compareToIgnoreCase(")AND(") == 0 || parserArray.get(count).compareToIgnoreCase(")OR(") == 0){
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(!Condition(parserArray)){
                    return false;
                }
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                return parserArray.get(count).contains(")");
            }
            else{
                return false;
            }
        }
        else if(PlainText(parserArray)){
            this.ConditionList.add(parserArray.get(count).replace("'",""));
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            if(Operator(parserArray)){
                this.ConditionList.add(parserArray.get(count).replace("'",""));
                if(!CheckBoundary(parserArray)){
                    return false;
                }
                count++;
                if(Value(parserArray)){
                    this.ConditionList.add(parserArray.get(count).replace("'",""));
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    //<NameValueList>  ::=  <NameValuePair> | <NameValuePair> "," <NameValueList>
    public boolean NameValueList(ArrayList<String> parserArray){
        if(!NameValuePair(parserArray)){
            return false;
        }
        if(!CheckBoundary(parserArray)){
            return false;
        }
        count++;
        if(parserArray.get(count).contains(",")){
            if(!CheckBoundary(parserArray)){
                return false;
            }
            count++;
            return NameValueList(parserArray);
        }
        else{
            if(parserArray.get(count).compareToIgnoreCase("where") == 0){
                count--;
                return true;
            }
            else if(NameValuePair(parserArray)){
                count--;
                return true;
            }
            else{
                return false;
            }
        }

    }
    //<NameValuePair>  ::=  <AttributeName> "=" <Value>
    public boolean NameValuePair(ArrayList<String> parserArray){
        if(!PlainText(parserArray)){
            return false;
        }
        this.ColName.add(parserArray.get(count)); // add col name
        if(!CheckBoundary(parserArray)){
            return false;
        }
        count++;
        if(!parserArray.get(count).contains("=")){
            return false;
        }
        if(!CheckBoundary(parserArray)){
            return false;
        }
        count++;
        if(Value(parserArray)){
            this.Data.add(parserArray.get(count));
            return true;
        }
        else{
            return false;
        }
    }

    //check if the count is not out of the size of the array
    public boolean CheckBoundary(ArrayList<String> parserArray){
        return count < parserArray.size() - 1;
    }
    }




