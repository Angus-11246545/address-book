package com.example.addressbook.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteContactDAO implements IContactDAO{

    private Connection connection;

    @Override
    public void addContact(Contact contact) {
        try {
            PreparedStatement insertAccount = connection.prepareStatement(
                    "INSERT INTO contacts (firstName, lastName, phone, email) VALUES (?, ?, ?, ?)"
            );
            insertAccount.setString(1, contact.getFirstName());
            insertAccount.setString(2, contact.getLastName());
            insertAccount.setString(3, contact.getPhone());
            insertAccount.setString(4, contact.getEmail());
            insertAccount.executeUpdate();
            // Set the id of the new contact
            ResultSet generatedKeys = insertAccount.getGeneratedKeys();
            if (generatedKeys.next()) {
                contact.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void updateContact(Contact contact) {
        try {
            PreparedStatement updateAccount = connection.prepareStatement(
                    "UPDATE contacts SET firstName = ?, lastName = ?, phone = ?, email = ? WHERE id = ?"
            );
            updateAccount.setString(1, contact.getFirstName());
            updateAccount.setString(2, contact.getLastName());
            updateAccount.setString(3, contact.getPhone());
            updateAccount.setString(4, contact.getEmail());
            updateAccount.setInt(5, contact.getId());
            updateAccount.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void deleteContact(Contact contact) {
        try {
            PreparedStatement deleteAccount = connection.prepareStatement("DELETE FROM contacts WHERE id = ?");
            deleteAccount.setInt(1, contact.getId());
            deleteAccount.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public Contact getContact(int id) {
        try {
            PreparedStatement getAccount = connection.prepareStatement("SELECT * FROM contacts WHERE id = ?");
            getAccount.setInt(1, id);
            ResultSet rs = getAccount.executeQuery();
            if (rs.next()) {
                Contact returned_contact = new Contact(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                returned_contact.setId(id);
                return returned_contact;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public List<Contact> getAllContacts() {
        List<Contact> accounts = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM contacts");
            while (rs.next()) {
                Contact returned_contact = new Contact(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                returned_contact.setId(rs.getInt("id"));
                accounts.add(returned_contact);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return accounts;
    }

    public SqliteContactDAO(){
        connection = SqliteConnection.getInstance();
        createTable();
        // Used for testing, to be removed later
//        insertSampleData();
    }

    private void insertSampleData() {
        try {
            // Clear before inserting
            Statement clearStatement = connection.createStatement();
            String clearQuery = "DELETE FROM contacts";
            clearStatement.execute(clearQuery);
            Statement insertStatement = connection.createStatement();
            String insertQuery = "INSERT INTO contacts (firstName, lastName, phone, email) VALUES "
                    + "('John', 'Doe', '0423423423', 'johndoe@example.com'),"
                    + "('Jane', 'Doe', '0423423424', 'janedoe@example.com'),"
                    + "('Jay', 'Doe', '0423423425', 'jaydoe@example.com')";
            insertStatement.execute(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS contacts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "firstName VARCHAR NOT NULL,"
                + "lastName VARCHAR NOT NULL,"
                + "phone VARCHAR NOT NULL,"
                + "email VARCHAR NOT NULL"
                + ")";

        try {
            Statement createTable = connection.createStatement();
            createTable.execute(query);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
}
