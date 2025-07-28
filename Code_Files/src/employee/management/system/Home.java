package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Home extends JFrame implements ActionListener {

    JButton add, view, update, remove, addDept, addProj, viewProj, erDiagram, manageProjEmp;

    public Home() {
        setLayout(null);

        // Example background image – adjust "icons/home.jpg" if needed.
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1120, 630, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 1120, 630);
        add(image);

        JLabel heading = new JLabel("Employee Management System");
        heading.setBounds(620, 20, 400, 40);
        heading.setFont(new Font("Raleway", Font.BOLD, 25));
        heading.setForeground(Color.WHITE);
        image.add(heading);

        // Employee modules (first row)
        add = new JButton("Add Employee");
        add.setBounds(650, 80, 150, 40);
        add.addActionListener(this);
        image.add(add);

        view = new JButton("View Employees");
        view.setBounds(820, 80, 150, 40);
        view.addActionListener(this);
        image.add(view);

        update = new JButton("Update Employee");
        update.setBounds(650, 140, 150, 40);
        update.addActionListener(this);
        image.add(update);

        remove = new JButton("Remove Employee");
        remove.setBounds(820, 140, 150, 40);
        remove.addActionListener(this);
        image.add(remove);

        // Department and Project modules (second row)
        addDept = new JButton("Add Department");
        addDept.setBounds(650, 200, 150, 40);
        addDept.addActionListener(this);
        image.add(addDept);

        addProj = new JButton("Add Project");
        addProj.setBounds(820, 200, 150, 40);
        addProj.addActionListener(this);
        image.add(addProj);

        // Last 3 buttons arranged side by side on the same horizontal line.
        viewProj = new JButton("View Projects");
        viewProj.setBounds(600, 260, 150, 40);
        viewProj.addActionListener(this);
        image.add(viewProj);

        erDiagram = new JButton("ER Diagram");
        erDiagram.setBounds(760, 260, 150, 40); // Side by side with viewProj
        erDiagram.addActionListener(this);
        image.add(erDiagram);

        manageProjEmp = new JButton("Manage Project Employees");
        manageProjEmp.setBounds(920, 260, 150, 40); // Placed next to erDiagram
        manageProjEmp.addActionListener(this);
        image.add(manageProjEmp);

        setSize(1120, 630);
        setLocation(250, 100);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == add) {
            setVisible(false);
            new AddEmployee();
        } else if (ae.getSource() == view) {
            setVisible(false);
            new ViewEmployee();
        } else if (ae.getSource() == update) {
            setVisible(false);
            new ViewEmployee();  // Or a dedicated update module if available.
        } else if (ae.getSource() == remove) {
            setVisible(false);
            new RemoveEmployee();
        } else if (ae.getSource() == addDept) {
            setVisible(false);
            new AddDepartment();
        } else if (ae.getSource() == addProj) {
            setVisible(false);
            new AddProject();
        } else if (ae.getSource() == viewProj) {
            setVisible(false);
            new ViewProject();
        } else if (ae.getSource() == erDiagram) {
            // Open a new frame to display the ER diagram in a scroll pane.
            JFrame erFrame = new JFrame("ER Diagram");
            erFrame.setLayout(new BorderLayout());
            // Replace "icons/ER.jpg" with your actual ER diagram image path.
            ImageIcon erIcon = new ImageIcon(ClassLoader.getSystemResource("icons/ER.jpg"));
            JLabel erLabel = new JLabel(erIcon);

            // Wrap the label in a scroll pane so the full image is viewable.
            JScrollPane scrollPane = new JScrollPane(erLabel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            erFrame.add(scrollPane, BorderLayout.CENTER);
            erFrame.setSize(800, 600);
            erFrame.setLocationRelativeTo(null);
            erFrame.setVisible(true);
        } else if (ae.getSource() == manageProjEmp) {
            setVisible(false);
            new ManageProjectEmployees();
        }
    }

    public static void main(String[] args) {
        new Home();
    }
}
