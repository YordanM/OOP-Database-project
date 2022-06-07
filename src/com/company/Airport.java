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

public class Airport extends JPanel {

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

    JLabel cityLabel = new JLabel("Град: ");
    JLabel airportNameLabel = new JLabel("Име на летище: ");

    JTextField cityTF = new JTextField();
    JTextField airportNameTF = new JTextField();
    JTextField searchByNameTField = new JTextField();

    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");
    JButton searchBt = new JButton("Търсене по град");
    JButton cleanFormBt = new JButton("Изчистване на формата");

    JTable showingTable = new JTable();
    JTable workingTable = new JTable();
    JScrollPane myScroll=new JScrollPane(showingTable);

    public Airport(){
        this.setSize(400, 600);
        this.setLayout(new GridLayout(3,1));

        refreshTable();

        //TOP Panel----------------------------------------------------------------------------------------
        topPanel.setLayout(new GridLayout(4, 2));

        topPanel.add(cityLabel);
        topPanel.add(cityTF);

        topPanel.add(airportNameLabel);
        topPanel.add(airportNameTF);
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
        midPanelSearch.add(searchByNameTField);
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
            state=conn.prepareStatement("select city, airportName from airport");
            result=state.executeQuery();
            showingTable.setModel(new MyModel(result));

            state=conn.prepareStatement("select id, city, airportName from airport");
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
        cityTF.setText("");
        airportNameTF.setText("");
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

            cityTF.setText(showingTable.getValueAt(row, 0).toString());
            airportNameTF.setText(showingTable.getValueAt(row, 1).toString());

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
            String sql="insert into airport(city, airportName) values(?,?)";

            try {
                if (cityTF.getText().equals("") || airportNameTF.getText().equals(""))
                {
                    message = "Please enter the needed credentials for airport!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No credentials given",
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
                    else {
                        state = conn.prepareStatement(sql);
                        state.setString(1, cityTF.getText());
                        state.setString(2, airportNameTF.getText());

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
            String sql="delete from airport where id=?";

            try {
                if (id == - 1)
                {
                    message = "Select a airport first!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid airport",
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
            String sql="update airport set city=?, airportName=? where id=" + id;

            try {
                if(id == -1) {
                    message = "Select airport!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Invalid airport",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (cityTF.getText().equals("") || airportNameTF.getText().equals("")) {
                        message = "Please enter the needed credentials for airport change!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        state = conn.prepareStatement(sql);
                        state.setString(1, cityTF.getText());
                        state.setString(2, airportNameTF.getText());
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
            String showingSql="select city, airportName from airport where city=?";
            String workingSql="select id, city, airportName from airport where city=?";

            try {
                if (searchByNameTField.getText().equals("")){
                    message = "Please enter searching value!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No search value given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {

                    state = conn.prepareStatement(showingSql);
                    state.setString(1, searchByNameTField.getText());
                    result = state.executeQuery();

                    showingTable.setModel(new MyModel(result));

                    state = conn.prepareStatement(workingSql);
                    state.setString(1, searchByNameTField.getText());
                    result = state.executeQuery();

                    workingTable.setModel(new MyModel(result));

                    searchByNameTField.setText("");
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
