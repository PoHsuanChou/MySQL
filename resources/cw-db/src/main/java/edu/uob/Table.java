package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    boolean FirstLine = true;
    private final String name;
//    private int rows = 0;
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    public Table(String name) {
        this.name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
    }

    public void line(String eachLine, int rows){
        String[] word = eachLine.split("\\s+");
        if(FirstLine){
            title.addAll(Arrays.asList(word));
            FirstLine = false;
        }
        else{
            data.add(new ArrayList<String>());
            for (String s : word) {
                data.get(rows).add(s);
            }
        }
    }

}
