package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddDepartment extends JFrame implements ActionListener {
    // Remove tfManagerEmpId
    JTextField tfDeptName, tfLocation;
    JButton btnAdd, btnBack;

    public AddDepartment() {
        setTitle("Add Department");
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblDeptName = new JLabel("Department Name");
        lblDeptName.setBounds(50, 50, 150, 30);
        add(lblDeptName);

        tfDeptName = new JTextField();
        tfDeptName.setBounds(220, 50, 150, 30);
        add(tfDeptName);

        JLabel lblLocation = new JLabel("Location");
        lblLocation.setBounds(50, 100, 150, 30);
        add(lblLocation);

        tfLocation = new JTextField();
        tfLocation.setBounds(220, 100, 150, 30);
        add(tfLocation);

        // Manager Label and TextField are removed as the column doesn't exist

        // Adjust button positions since manager fields are removed
        btnAdd = new JButton("Add Department");
        btnAdd.setBounds(50, 170, 150, 40); // Adjusted Y position
        btnAdd.addActionListener(this);
        btnAdd.setBackground(Color.BLACK);
        btnAdd.setForeground(Color.WHITE);
        add(btnAdd);

        btnBack = new JButton("Back");
        btnBack.setBounds(220, 170, 150, 40); // Adjusted Y position
        btnBack.addActionListener(this);
        btnBack.setBackground(Color.BLACK);
        btnBack.setForeground(Color.WHITE);
        add(btnBack);

        // Adjust frame size to fit components
        setSize(450, 300); // Reduced height
        setLocation(300, 200);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == btnAdd) {
            String deptName = tfDeptName.getText();
            String location = tfLocation.getText();
            // managerEmpId related logic is removed

            // Basic validation
            if (deptName == null || deptName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Department Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Conn conn = new Conn();
                // Updated query without managerEmpId
                String query = "INSERT INTO department(deptName, location) VALUES('"
                        + deptName.trim() + "', '" + location.trim() + "')";
                conn.s.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Department added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new Home();
            } catch (SQLIntegrityConstraintViolationException e) {

                JOptionPane.showMessageDialog(this, "Department Name '" + deptName.trim() + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding department: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            setVisible(false);
            new Home();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddDepartment());
    }
}