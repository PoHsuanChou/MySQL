package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;
  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) throws IOException {
    Path path = Paths.get("");
    String directoryName = path.toAbsolutePath().normalize().toString();
    String location = directoryName.replace("cw-db","testing");
    File db = new File(location);
    server = new DBServer(db);


    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.

  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCommandIsAnError() throws IOException {
    assertTrue(server.handleCommand("USEtest;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE test").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("Create database ;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("Create table ;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("Createtable paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("Createdatabase paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("create table ( Name Age );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("create table ( Name, Age );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("drop data;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("drop database;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("drop table;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("alter table paul  height;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("alter table paul add ;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into paul values( Name );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into values( 'Name' );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into paul values ( 20 );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into paul values( 'hello' , true );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into paul values( 'hello' , Null );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert ").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into paul ( 20 );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INsert into paul values( 20 , 20;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select from paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select Name from;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select Name from paul where ( Age == 20 )or( Name = 'Bob' );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select Name from paul where ( Age > 30 );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("delete from people Age > 20;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("update people set Age = 30 Name = 'Max' where Name == 'Bob';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("update people  Age = 30 Name = 'Max' where Name == 'Bob';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("drop table paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE paul;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE test;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("Create database paul;").startsWith("[OK]"));
    assertTrue(server.handleCommand("Create table paul;").startsWith("[ERROR]"));
  }
  @Test
  void testValidCommandIsCorrect() throws IOException {
    assertTrue(server.handleCommand("create database paul;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE paul;").startsWith("[OK]"));
    assertTrue(server.handleCommand("create table java;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table test add Gender;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("alter table java add Id;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table java add Name;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table java add Age;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table java add Male;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table java add Country;").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table java drop Country;").startsWith("[OK]"));
    assertTrue(server.handleCommand("insert into java values( 1 , 'josh' , 20 , TRUE );").startsWith("[OK]"));
    assertTrue(server.handleCommand("insert into java values( 2 , 'Tim' , 18 , TRUE );").startsWith("[OK]"));
    assertTrue(server.handleCommand("insert into java values( 3 , 'Lisa' , 40 , FALSE );").startsWith("[OK]"));
    assertTrue(server.handleCommand("insert into java values( 4 , 'Wang' , 9 , false );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("insert into java values( 1 , 'josh' , 20 , TRUE , 20.8 );").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select * from java;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Age > 20;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name == 'josh';").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name like 'L%';").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name like 'T%';").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name like '%s%';").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name like 'test';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name != 'Tim';").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Age >= 20;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Name > 20;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Age == 20;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where Age != 20;").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where ( Age < 30 )AND( Name == 'Tim' );").startsWith("[OK]"));
    assertTrue(server.handleCommand("select Name ,Age from java where ( Male == FALSE )or( Id == 1 );").startsWith("[OK]"));
    assertTrue(server.handleCommand("update java Set Name = 'paul' , Age = 25 where Name == 'Tim' ;").startsWith("[OK]"));
    assertTrue(server.handleCommand("update java Set Male = FALSE where Name == 'josh' ;").startsWith("[OK]"));
    assertTrue(server.handleCommand("update java Set Age = 22 where Age == 25 ;").startsWith("[OK]"));
    assertTrue(server.handleCommand("delete from java where Id > 2;").startsWith("[OK]"));
    assertTrue(server.handleCommand("delete from java where ( Age == 22 )OR( Id == 1 );").startsWith("[OK]"));
    assertTrue(server.handleCommand("drop table java;").startsWith("[OK]"));
    assertTrue(server.handleCommand("drop database paul;").startsWith("[OK]"));


  }
  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific usecase (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)

}
