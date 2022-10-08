package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

//2 大小寫
/** This class implements the DB server. */
public final class DBServer {
  private static String currentDatabase;
  private static final char END_OF_TRANSMISSION = 4;
  HashMap<String,ArrayList<Table>> databases = new HashMap<>();


  public static void main(String[] args) throws IOException {
    new DBServer(Paths.get(".").toAbsolutePath().toFile()).blockingListenOn(8888);
  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer(File)}) otherwise we won't be able to mark
   * your submission correctly.
   *
   * <p>You MUST use the supplied {@code databaseDirectory} and only create/modify files in that
   * directory; it is an error to access files outside that directory.
   *
   * @param databaseDirectory The directory to use for storing any persistent database files such
   *     that starting a new instance of the server with the same directory will restore all
   *     databases. You may assume *exclusive* ownership of this directory for the lifetime of this
   *     server instance.
   */
  public DBServer(File databaseDirectory) throws IOException {
    // TODO implement your server logic here
    String dir = databaseDirectory.getPath();
    if(dir.endsWith(".")) {
      String location = dir.replace(".","testing");
        File directory = new File(location);
        File[] directoryList = directory.listFiles();
        if(directoryList != null){
          for(File file: directoryList){
            String[] line = String.valueOf(file).split("/");
            databases.put(line[line.length - 1],new ArrayList<>());
            String filesLocation = file.getAbsoluteFile()+ File.separator;
            File files = new File(filesLocation);
            File[] fileList = files.listFiles();
            assert fileList != null;
            for(File f : fileList){
              loopFile(databases,f,line[line.length - 1]);
            }
          }
        }
    }
  }

  /**
   * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
   * able to mark your submission correctly.
   *
   * <p>This method handles all incoming DB commands and carry out the corresponding actions.
   */
  public String handleCommand(String command) throws IOException {
    // TODO implement your server logic here
    CheckParser parser = new CheckParser(command);
    if(!parser.isTrueOrFalse()){
      return "[ERROR] there is something wrong with the parser : " + command;
    }
    if(parser.getCommandType().compareToIgnoreCase("Use") == 0){
    if(databases.containsKey(parser.getDatabaseName())) {
      currentDatabase = parser.getDatabaseName();
      return "[OK] found the database";
    }
    else{
      return "[ERROR] Can't find the database";
    }
  }
      else if(parser.getCommandType().compareToIgnoreCase("Create") == 0){
        CreateCMD create = new CreateCMD(databases,command,currentDatabase);
        return create.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Drop") == 0){
        DropCMD drop = new DropCMD(databases,command,currentDatabase);
        return drop.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Alter") == 0){
        AlterCMD alter = new AlterCMD(databases,command,currentDatabase);
        return alter.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Insert") == 0){
        InsertCMD insert = new InsertCMD(databases,command,currentDatabase);
        return insert.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Select") == 0){
        SelectCMD select = new SelectCMD(databases,command,currentDatabase);
        return select.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Update") == 0){
        UpdateCMD update = new UpdateCMD(databases,command,currentDatabase);
        return update.getOkOrError();
  }
      else if(parser.getCommandType().compareToIgnoreCase("Delete") == 0){
        DeleteCMD delete = new DeleteCMD(databases,command,currentDatabase);
        return delete.getOkOrError();

  }
    return "[ERROR] Thanks for your message: " + command;
  }











  //  === Methods below are there to facilitate server related operations. ===

  /**
   * Starts a *blocking* socket server listening for new connections. This method blocks until the
   * current thread is interrupted.
   *
   * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
   * you want to.
   *
   * @param portNumber The port to listen on.
   * @throws IOException If any IO related operation fails.
   */
  public void blockingListenOn(int portNumber) throws IOException {
    try (ServerSocket s = new ServerSocket(portNumber)) {
      System.out.println("Server listening on port " + portNumber);
      while (!Thread.interrupted()) {
        try {
          blockingHandleConnection(s);
        } catch (IOException e) {
          System.err.println("Server encountered a non-fatal IO error:");
          e.printStackTrace();
          System.err.println("Continuing...");
        }
      }
    }
  }

  /**
   * Handles an incoming connection from the socket server.
   *
   * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
   * * you want to.
   *
   * @param serverSocket The client socket to read/write from.
   * @throws IOException If any IO related operation fails.
   */
  private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
    try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

      System.out.println("Connection established: " + serverSocket.getInetAddress());
      while (!Thread.interrupted()) {
        String incomingCommand = reader.readLine();
        System.out.println("Received message: " + incomingCommand);
        String result = handleCommand(incomingCommand);
        writer.write(result);
        writer.write("\n" + END_OF_TRANSMISSION + "\n");
        writer.flush();
      }
    }
  }



  public void loopFile(HashMap<String,ArrayList<Table>> database,File fileToOpen,String databaseName) throws IOException {
    String eachLine;
    if(fileToOpen != null){
      if(fileToOpen.exists()){
        String changeFilename = fileToOpen.getName();
        String newFileName = changeFilename.replace(".tab","");
        Table fileTable = new Table(newFileName);
        FileReader reader = new FileReader(fileToOpen);
        BufferedReader bufferedReader = new BufferedReader(reader);
        int rows = -1;
        while ((eachLine = bufferedReader.readLine()) != null && !eachLine.isEmpty()){
          fileTable.line(eachLine, rows);
          rows++;
        }
        database.get(databaseName).add(fileTable);
      }
      else{
        System.out.println("file is not exist");
      }
    }
    else{
      System.out.println("NO File is this directory");
    }

  }
}
