package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReservationFrame extends JPanel {

    String message;

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result = null;
    int id = -1;
    int personId = -1;
    int flightId = -1;

    JPanel topPanel = new JPanel();

    JPanel midPanel = new JPanel();
    JPanel midPanelActions = new JPanel();
    JPanel midPanelCleanForm = new JPanel();
    JPanel midPanelSearch = new JPanel();

    JPanel bottomPanel = new JPanel();

    JLabel personEGNLabel = new JLabel("ЕГН на пътник: ");
    JLabel flightNumberLabel = new JLabel("Номер на полет: ");
    JLabel egnLabel = new JLabel("ЕГН: ");
    JLabel flightLabel = new JLabel("Полет: ");

    JComboBox<String> personEGN = new JComboBox<String>();

    JComboBox<String> flight = new JComboBox<String>();

    JTextField personEGNTF = new JTextField();
    JTextField flightNumberTF = new JTextField();
    JTextField searchByFlightTF = new JTextField();
    JTextField searchByPersonEGNTF = new JTextField();

    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");
    JButton cleanFormBt = new JButton("Изчистване на формата");
    JButton searchBt = new JButton("Търсене");

    JTable showingTable = new JTable();
    JTable workingTable = new JTable();
    JTable flightTable = new JTable();
    JTable personTable = new JTable();
    JScrollPane myScroll=new JScrollPane(showingTable);

    public ReservationFrame(){
        this.setSize(400, 600);
        this.setLayout(new GridLayout(3,1));

        refreshTable();
        refreshPeopleEGNs();
        refreshFlightNumbers();

        //TOP Panel----------------------------------------------------------------------------------------
        topPanel.setLayout(new GridLayout(2, 2));

        topPanel.add(personEGNLabel);
        topPanel.add(personEGN);

        topPanel.add(flightNumberLabel);
        topPanel.add(flight);

        this.add(topPanel);

        //MID Panel----------------------------------------------------------------------------------------
        midPanel.setLayout(new GridLayout(4,1));

        midPanelActions.setLayout(new GridLayout(1, 3));

        midPanelActions.add(addBt);
        midPanelActions.add(deleteBt);
        midPanelActions.add(editBt);
        midPanel.add(midPanelActions);

        midPanelCleanForm.setLayout(new GridLayout(1, 1));

        midPanelCleanForm.add(cleanFormBt);
        midPanel.add(midPanelCleanForm);

        midPanelSearch.setLayout(new GridLayout(3, 3));

        midPanelSearch.add(egnLabel);
        midPanelSearch.add(searchByPersonEGNTF);

        midPanelSearch.add(flightLabel);
        midPanelSearch.add(searchByFlightTF);

        midPanel.add(midPanelSearch);

        midPanel.add(searchBt);

        this.add(midPanel);

        //BOTTOM Panel-------------------------------------------------------------------------------------

        myScroll.setPreferredSize(new Dimension(350, 150));
        bottomPanel.add(myScroll);

        showingTable.addMouseListener(new MouseAction());

        this.add(bottomPanel);

        addBt.addActionListener(new AddAction());
        deleteBt.addActionListener(new DeleteAction());
        editBt.addActionListener(new UpdateAction());
        cleanFormBt.addActionListener(new CleanAction());

        searchBt.addActionListener(new SearchByFlightAction());
    }

    public void refreshTable() {
        conn=DBConnection.getConnection();

        try {
            state=conn.prepareStatement("SELECT  p.fname, p.egn, f.number " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid");
            result=state.executeQuery();
            showingTable.setModel(new MyModel(result));

            state=conn.prepareStatement("SELECT r.id, p.id, f.id, p.egn, f.number, p.fname " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid");
            result = state.executeQuery();
            workingTable.setModel(new MyModel(result));

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void refreshPeopleEGNs() {

        String sql = "select fname, egn from person";
        conn=DBConnection.getConnection();
        String item="";

        try {
            state=conn.prepareStatement(sql);
            result=state.executeQuery();
            personEGN.removeAllItems();
            while(result.next()) {
                item = result.getObject(1).toString()+": "+
                        result.getObject(2).toString();
                personEGN.addItem(item);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void refreshFlightNumbers() {

        String sql = "select number from flight";
        conn=DBConnection.getConnection();
        String item="";

        try {
            state=conn.prepareStatement(sql);
            result=state.executeQuery();
            flight.removeAllItems();
            while(result.next()) {
                item = result.getObject(1).toString();
                flight.addItem(item);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void clearForm() {
        personId = -1;
        flightId = -1;
        id=-1;
        changeColor(showingTable, -1);

        refreshPeopleEGNs();
        refreshFlightNumbers();
    }

    class CleanAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            clearForm();
            refreshTable();
        }
    }

    public static void changeColor(JTable table, int coloredRow)
    {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row,
                                                           int column)
            {
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (row == coloredRow)
                {
                    setBackground(Color.lightGray);
                }
                else if (row == -1)
                {
                    setBackground(Color.white);
                }
                else
                {
                    setBackground(null);
                }
                return this;
            }
        });
        table.repaint();

    }

    class MouseAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = showingTable.getSelectedRow();

            id = Integer.parseInt(workingTable.getValueAt(row, 0).toString());

            personEGNTF.setText(showingTable.getValueAt(row, 0).toString());
            flightNumberTF.setText(showingTable.getValueAt(row, 1).toString());

            changeColor(showingTable, row);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

    }

    class AddAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String flightSql ="select id from flight where number=?";
            String personSql ="select id from person where EGN=?";
            String sql="insert into reservation(personId, flightId) values(?,?)";
            String egn = personEGN.toString().split(": |]")[1];
            String number = flight.getSelectedItem().toString();
            try {

                state = conn.prepareStatement(flightSql);
                state.setString(1, number);
                result = state.executeQuery();
                flightTable.setModel(new MyModel(result));

                state = conn.prepareStatement(personSql);
                state.setString(1, egn);
                result = state.executeQuery();
                personTable.setModel(new MyModel(result));

                if (id != -1)
                {
                    message = "You are in select mode!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "selective mode",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {

                    //search for flight id
                    flightId = Integer.parseInt(flightTable.getValueAt(0, 0).toString());

                    //search for person id
                    personId = Integer.parseInt(personTable.getValueAt(0, 0).toString());

                    //add to database
                    state = conn.prepareStatement(sql);
                    state.setInt(1, personId);
                    state.setInt(2, flightId);

                    state.execute();
                }


                refreshTable();
                clearForm();

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="delete from reservation where id=?";

            try {
                if (id == - 1)
                {
                    message = "Select a user first!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid user",
                            JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    state=conn.prepareStatement(sql);
                    state.setInt(1, id);
                    state.execute();
                    refreshTable();
                    clearForm();
                }

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }

    class UpdateAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn = DBConnection.getConnection();

            String personSQL = "select id from person where egn=?";
            String flightSQL = "select id from flight where number=?";
            String sql = "update reservation set personid=?, flightid=? where id=?";

            try {

                if(id == -1) {
                    message = "Select user!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid user",
                            JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    if (flightNumberTF.getText().equals("") || personEGNTF.getText().equals(""))
                    {
                        message = "Please enter the needed credentials for reservation change!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        state = conn.prepareStatement(personSQL);
                        state.setString(1, personEGNTF.getText());
                        result = state.executeQuery();
                        personTable.setModel(new MyModel(result));

                        state = conn.prepareStatement(flightSQL);
                        state.setString(1, flightNumberTF.getText());
                        result = state.executeQuery();
                        flightTable.setModel(new MyModel(result));

                        if (personTable.getRowCount() == 0) {
                            message = "Please enter valid user EGN!";
                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    message,
                                    "Invalid user",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (flightTable.getRowCount() == 0) {
                            message = "Please enter valid flight number!";
                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    message,
                                    "Invalid flight",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            //search for person id
                            personId = Integer.parseInt(personTable.getValueAt(0, 0).toString());

                            //search for flight id
                            flightId = Integer.parseInt(flightTable.getValueAt(0, 0).toString());

                            //update database
                            state = conn.prepareStatement(sql);
                            state.setInt(1, personId);
                            state.setInt(2, flightId);
                            state.setInt(3, id);

                            state.execute();
                        }
                    }
                }
                refreshTable();
                clearForm();

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class SearchByFlightAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            conn=DBConnection.getConnection();

            String showingSql="SELECT p.fname, p.egn, f.number " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid " +
                    "WHERE (f.number=?) and (p.egn=?)";

            String workingSql="SELECT r.id, p.id, f.id, p.egn, f.number " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid " +
                    "WHERE (f.number=?) and (p.egn=?)";

            try {
                if (searchByFlightTF.getText().equals(""))
                {
                    message = "Please enter searching value!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Empty search value",
                            JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    state = conn.prepareStatement(showingSql);
                    state.setString(1, searchByFlightTF.getText());
                    state.setString(2, searchByPersonEGNTF.getText());
                    result = state.executeQuery();

                    showingTable.setModel(new MyModel(result));

                    state = conn.prepareStatement(workingSql);
                    state.setString(1, searchByFlightTF.getText());
                    state.setString(2, searchByPersonEGNTF.getText());
                    result = state.executeQuery();

                    workingTable.setModel(new MyModel(result));

                    searchByFlightTF.setText("");
                    searchByPersonEGNTF.setText("");
                }
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }

    class SearchByEGNAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            conn=DBConnection.getConnection();

            String showingSql="SELECT p.egn, f.number " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid " +
                    "WHERE p.egn=?";

            String workingSql="SELECT r.id, p.id, f.id, p.egn, f.number " +
                    "FROM reservation r " +
                    "INNER JOIN person p ON p.id = r.personid " +
                    "INNER JOIN flight f ON f.id = r.flightid " +
                    "WHERE p.egn=?";

            try {
                if (searchByPersonEGNTF.getText().equals(""))
                {
                    message = "Please enter searching value!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Empty search value",
                            JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    state=conn.prepareStatement(showingSql);
                    state.setString(1, searchByPersonEGNTF.getText());
                    result=state.executeQuery();

                    showingTable.setModel(new MyModel(result));

                    state=conn.prepareStatement(workingSql);
                    state.setString(1, searchByPersonEGNTF.getText());
                    result=state.executeQuery();

                    workingTable.setModel(new MyModel(result));

                    searchByPersonEGNTF.setText("");
                }

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }
}
