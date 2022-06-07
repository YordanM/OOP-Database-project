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

public class PersonFrame extends JPanel {

    String message;

    Connection conn=null;
    PreparedStatement state=null;
    ResultSet result=null;
    ResultSet resultSet=null;
    int id=-1;

    JPanel topPanel = new JPanel();

    JPanel midPanel = new JPanel();
    JPanel midPanelActions = new JPanel();
    JPanel midPanelCleanForm = new JPanel();
    JPanel midPanelSearch = new JPanel();

    JPanel bottomPanel = new JPanel();

    JLabel firstNameLabel = new JLabel("Име: ");
    JLabel lastNameLabel = new JLabel("Фамилия: ");
    JLabel sexLabel = new JLabel("Пол: ");
    JLabel ageLabel = new JLabel("Години: ");
    JLabel egnLabel = new JLabel("ЕГН: ");

    JTextField firstNameТField = new JTextField();
    JTextField lastNameТField = new JTextField();
    JTextField ageТField = new JTextField();
    JTextField egnTField = new JTextField();
    JTextField searchByNameTField = new JTextField();

    String[] item = {"Мъж", "Жена"};

    JComboBox<String> sexCombo = new JComboBox<String>(item);
    JComboBox<String> personCombo=new JComboBox<String>();

    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");
    JButton searchBt = new JButton("Търсене по име");
    JButton cleanFormBt = new JButton("Изчистване на формата");

    JTable showingTable = new JTable();
    JTable workingTable = new JTable();
    JTable personTable = new JTable();
    JTable otherPersonTable = new JTable();
    JScrollPane myScroll=new JScrollPane(showingTable);

    public PersonFrame(){
        this.setSize(400, 600);
        this.setLayout(new GridLayout(3,1));

        refreshTable();

        //TOP Panel----------------------------------------------------------------------------------------
        topPanel.setLayout(new GridLayout(5, 2));

        topPanel.add(firstNameLabel);
        topPanel.add(firstNameТField);

        topPanel.add(lastNameLabel);
        topPanel.add(lastNameТField);

        topPanel.add(sexLabel);
        topPanel.add(sexCombo);

        topPanel.add(ageLabel);
        topPanel.add(ageТField);

        topPanel.add(egnLabel);
        topPanel.add(egnTField);


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
            state=conn.prepareStatement("select fname, lname, age, sex, egn from person");
            result=state.executeQuery();
            showingTable.setModel(new MyModel(result));

            state=conn.prepareStatement("select id, fname, lname, age, sex, egn from person");
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

    public void refreshComboPerson() {

        String sql="select id, fname, lname from person";
        conn=DBConnection.getConnection();
        String item="";

        try {
            state=conn.prepareStatement(sql);
            result=state.executeQuery();
            personCombo.removeAllItems();
            while(result.next()) {
                item=result.getObject(1).toString()+"."+
                        result.getObject(2).toString()+" "+
                        result.getObject(3).toString();
                personCombo.addItem(item);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void clearForm() {
        firstNameТField.setText("");
        lastNameТField.setText("");
        ageТField.setText("");
        egnTField.setText("");
        id=-1;
        changeColor(showingTable, -1);
    }

    class CleanAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            clearForm();
            refreshTable();
            refreshComboPerson();
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

            firstNameТField.setText(showingTable.getValueAt(row, 0).toString());
            lastNameТField.setText(showingTable.getValueAt(row, 1).toString());
            ageТField.setText(showingTable.getValueAt(row, 2).toString());
            egnTField.setText(showingTable.getValueAt(row, 4).toString());
            if(showingTable.getValueAt(row, 3).toString().equals("Мъж")) {
                sexCombo.setSelectedIndex(0);
            }
            else {
                sexCombo.setSelectedIndex(1);
            }
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

    public boolean isUnique(String egn) {
        conn = DBConnection.getConnection();
        String sql = "select egn from person where egn=" + egn;
        String currentPersonSql = "select egn from person where id=" + id;

        try {
            conn = DBConnection.getConnection();
            state = conn.prepareStatement(sql);
            result = state.executeQuery();

            String currentPersonEGN;

            if (!result.next())
            {
                return true;
            }
            else if (id != -1)
            {
                conn = DBConnection.getConnection();
                state = conn.prepareStatement(currentPersonSql);
                resultSet = state.executeQuery();
                personTable.setModel(new MyModel(resultSet));

                currentPersonEGN = personTable.getValueAt(0,0).toString();

                if (currentPersonEGN.equals(egn))
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
            String sql="insert into person(fname, lname, sex, age, egn) values(?,?,?,?,?)";

            try {

                if (firstNameТField.getText().equals("") ||
                    lastNameТField.getText().equals("") ||
                    ageТField.getText().equals("") ||
                    egnTField.getText().equals(""))
                {
                    message = "Please enter the needed credentials for person!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No credentials given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (!isNumeric(egnTField.getText()) || !isNumeric(ageТField.getText())){
                    message = "Please enter the needed valid credentials for person!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No valid credentials given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (egnTField.getText().length() != 10){
                    message = "The EGN must be 10 digits!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No valid EGN given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (Integer.parseInt(ageТField.getText()) >= 200){
                    message = "ti vampir li si e";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "Too old for this",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (id != -1) {
                        message = "You are in select mode!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "selective mode",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isUnique(egnTField.getText()))
                    {
                        message = "The egn must be unique!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "egn must be unique",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        state = conn.prepareStatement(sql);
                        state.setString(1, firstNameТField.getText());
                        state.setString(2, lastNameТField.getText());
                        state.setString(3, sexCombo.getSelectedItem().toString());
                        state.setInt(4, Integer.parseInt(ageТField.getText()));
                        state.setString(5, egnTField.getText());

                        state.execute();
                    }

                }
                refreshTable();
                refreshComboPerson();
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
            String sql="delete from person where id=?";

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
            String sql="update person set fname=?, lname=?, sex=?, age=?, egn=? where id=" + id;

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
                    if (firstNameТField.getText().equals("") ||
                            lastNameТField.getText().equals("") ||
                            ageТField.getText().equals("") ||
                            egnTField.getText().equals(""))
                    {
                        message = "Please enter the needed credentials for person!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isNumeric(egnTField.getText()) || !isNumeric(ageТField.getText())){
                        message = "Please enter the needed valid credentials for person!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No valid credentials given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (egnTField.getText().length() != 10){
                        message = "The EGN must be 10 digits!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "No valid EGN given",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (Integer.parseInt(ageТField.getText()) >= 200){
                        message = "ti vampir li si e";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "Too old for this",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else if (!isUnique(egnTField.getText()))
                    {
                        message = "The egn must be unique!";
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                message,
                                "egn must be unique",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {

                        state = conn.prepareStatement(sql);
                        state.setString(1, firstNameТField.getText());
                        state.setString(2, lastNameТField.getText());
                        state.setString(3, sexCombo.getSelectedItem().toString());
                        state.setInt(4, Integer.parseInt(ageТField.getText()));
                        state.setString(5, egnTField.getText());

                        state.execute();
                    }
                }

                refreshTable();
                refreshComboPerson();
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
            String showingSql="select fname, lname, age, sex from person where fname=?";
            String workingSql="select id, fname, lname, age, sex from person where fname=?";

            try {
                if (searchByNameTField.getText().equals("")){
                    message = "Please enter searching value!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No search value given",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (!isNumeric(searchByNameTField.getText())){
                    message = "Please enter the needed valid credentials for searching flight!";
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            message,
                            "No valid credentials given",
                            JOptionPane.ERROR_MESSAGE);
                    searchByNameTField.setText("");
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
