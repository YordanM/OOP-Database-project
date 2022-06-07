package com.company;

import javax.swing.*;

public class MainFrame extends JFrame {
    PersonFrame personFrame = new PersonFrame();
    FlightFrame flightFrame = new FlightFrame();
    PlainFrame plainFrame = new PlainFrame();
    Airport airport = new Airport();
    ReservationFrame reservationFrame = new ReservationFrame();

//    JPanel personFrame = new JPanel();
//    JPanel flightFrame = new JPanel();
//    JPanel plainFrame = new JPanel();

    JTabbedPane tab = new JTabbedPane();

    public MainFrame() {
        this.setSize(400, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        tab.add(personFrame, "Пътник");
        tab.add(flightFrame, "Полет");
        tab.add(plainFrame, "Самолет");
        tab.add(airport, "Летище");
        tab.add(reservationFrame, "Резервация");

        this.add(tab);

        this.setVisible(true);
    }
}
