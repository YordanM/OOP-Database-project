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

public class FlightFrame extends JPanel {

    String message;

    Connection conn=null;
    PreparedStatement state=null;
    ResultSet result=null;
    int id=-1;

    JPanel topPanel = new JPanel();

    JPanel midPanel = new JPanel();
    JPanel midPanelActions = new JPanel();
    JPanel midPanelCleanForm = new JPanel();
    JPanel midPanelSearch = new JPanel();

    JPanel bottomPanel = new JPanel();

    JLabel takeOfAirportLabel = new JLabel("Излита от: ");
    JLabel landingAirportLabel = new JLabel("Каца в: ");
    JLabel flightNumberLabel = new JLabel("Номер на полет: ");

    JTextField takeOfAirportTF = new JTextField();
    JTextField landingAirportTF = new JTextField();
    JTextField flightNumberTF = new JTextField();
    JTextField searchByNumberTField = new JTextField();

    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");
    JButton searchBt = new JButton("Търсене по номер");
    JButton cleanFormBt = new JButton("Изчистване на формата");

    JTable showingTable = new JTable();
    JTable workingTable = new JTable();
    JScrollPane myScroll=new JScrollPane(showingTable);

    public FlightFrame(){
        this.setSize(400, 600);
        this.setLayout(new GridLayout(3,1));

        refreshTable();

        //TOP Panel----------------------------------------------------------------------------------------
        topPanel.setLayout(new GridLayout(4, 2));

        topPanel.add(takeOfAirportLabel);
        topPanel.add(takeOfAirportTF);

        topPanel.add(landingAirportLabel);
        topPanel.add(landingAirportTF);

        topPanel.add(flightNumberLabel);
        topPanel.add(flightNumberTF);
        this.add(topPanel);

        //MID Panel----------------------------------------------------------------------------------------
        midPanelActions.setLayout(new GridLayout(1, 3));

        midPanelActions.add(addBt);
        midPanelActions.add(deleteBt);
        midPanelActions.add(editBt);
        midPanel.add(midPanelActions);

        midPanelCleanForm.setLayout(new GridLayout(1, 1));

        midPanelCleanForm.add(cleanFormBt);
        midPanel.add(midPanelCleanForm);

        midPanelSearch.setLayout(new GridLayout(1, 2));

        midPanelSearch.add(searchBt);
        midPanelSearch.add(searchByNumberTField);
        midPanel.add(midPanelSearch);

        this.add(midPanel);

        //BOTTOM Panel-------------------------------------------------------------------------------------

        myScroll.setPreferredSize(new Dimension(350, 150));
        bottomPanel.add(myScroll);

        showingTable.addMouseListener(new MouseAction());

        this.add(bottomPanel);

        addBt.addActionListener(new AddAction());
        deleteBt.addActionListener(new DeleteAction());
        searchBt.addActionListener(new SearchAction());
        editBt.addActionListener(new UpdateAction());
        cleanFormBt.addActionListener(new CleanAction());
    }

    public void refreshTable() {
        conn=DBConnection.getConnection();

        try {
            state=conn.prepareStatement("select takeofairport, landingairport, number from flight");
            result=state.executeQuery();
            showingTable.setModel(new MyModel(result));

            state=conn.prepareStatement("select id, takeofairport, landingairport, number from flight");
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

    public void clearForm() {
        takeOfAirportTF.setText("");
        landingAirportTF.setText("");
        flightNumberTF.setText("");
        id=-1;
        changeColor(showingTable, -1);
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

            takeOfAirportTF.setText(showingTable.getValueAt(row, 0).toString());
            landingAirportTF.setText(showingTable.getValueAt(row, 1).toString());
            flightNumberTF.setText(showingTable.getValueAt(row, 2).toString());
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

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean isUnique(String flightNumber) {
        conn = DBConnection.getConnection();
        String sql = "select number from flight where number=" + flightNumber;
        String currentPersonSql = "select number from flight where id=" + id;

        try {
            conn = DBConnection.getConnection();
            state = conn.prepareStatement(sql);
            result = state.executeQuery();

            String currentFlightNumber;

            if (!result.next())
            {
                return true;
            }
            else if (id != -1)
            {
                conn = DBConnection.getConnection();
                state = conn.prepareStatement(currentPersonSql);
                ResultSet resultSet = state.executeQuery();
                JTable flightTable = new JTable();
                flightTable.setModel(new MyModel(resultSet));

                currentFlightNumber = flightTable.getValueAt(0,0).toString();

                if (currentFlightNumber.equals(flightNumber))
                {
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class AddAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="insert into flight(takeofairport, landingairport, number) values(?,?,?)";

            try {
                if (takeOfAirportTF.getText().equals("") ||
                    landingAirportTF.getText().equals("") ||
                    flightNumberTF.getText().equals(""))
                {
                    message = "Please enter the needed credentials for flight!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No credentials given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (!isNumeric(flightNumberTF.getText())){
                    message = "Please enter the needed valid credentials for flight!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No valid credentials given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (id != -1)
                    {
                        message = "You are in select mode!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "selective mode",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isUnique(flightNumberTF.getText()))
                    {
                        message = "The flight number must be unique!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "flight number must be unique",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        state = conn.prepareStatement(sql);
                        state.setString(1, takeOfAirportTF.getText());
                        state.setString(2, landingAirportTF.getText());
                        state.setString(3, flightNumberTF.getText());

                        state.execute();
                    }
                }
                refreshTable();
                clearForm();

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            conn=DBConnection.getConnection();
            String sql="delete from flight where id=?";

            try {
                if (id == - 1)
                {
                    message = "Select a flight first!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid flight",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    state = conn.prepareStatement(sql);
                    state.setInt(1, id);
                    state.execute();
                }
                refreshTable();
                clearForm();
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
            String sql="update flight set takeofairport=?, landingairport=?, number=? where id=" + id;

            try {
                if(id == -1) {
                    message = "Select flight!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid flight",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (takeOfAirportTF.getText().equals("") ||
                        landingAirportTF.getText().equals("") ||
                        flightNumberTF.getText().equals("")) {
                        message = "Please enter the needed credentials for flight change!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isNumeric(flightNumberTF.getText())){
                        message = "Please enter the needed valid credentials for flight!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No valid credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isUnique(flightNumberTF.getText()))
                    {
                        message = "The flight number must be unique!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "flight number must be unique",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        state = conn.prepareStatement(sql);
                        state.setString(1, takeOfAirportTF.getText());
                        state.setString(2, landingAirportTF.getText());
                        state.setString(3, flightNumberTF.getText());

                        state.execute();
                    }
                }
                refreshTable();
                clearForm();

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class SearchAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            conn=DBConnection.getConnection();
            String showingSql="select takeofairport, landingairport, number from flight where number=?";
            String workingSql="select id, takeofairport, landingairport, number from flight where number=?";

            try {

                if (searchByNumberTField.getText().equals("")){
                    message = "Please enter searching value!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No search value given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (!isNumeric(searchByNumberTField.getText())){
                    message = "Please enter the needed valid credentials for searching flight!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No valid credentials given",
                            JOptionPane.ERROR_MESSAGE);
                    searchByNumberTField.setText("");
                }
                else {
                    state = conn.prepareStatement(showingSql);
                    state.setString(1, searchByNumberTField.getText());
                    result = state.executeQuery();

                    showingTable.setModel(new MyModel(result));

                    state = conn.prepareStatement(workingSql);
                    state.setString(1, searchByNumberTField.getText());
                    result = state.executeQuery();

                    workingTable.setModel(new MyModel(result));

                    searchByNumberTField.setText("");
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
