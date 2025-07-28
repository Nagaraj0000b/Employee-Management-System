package employee.management.system;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateEmployee extends JFrame implements ActionListener {

    JTextField tfeducation, tffname, tfaddress, tfphone, tfemail, tfsalary, tfdesignation, tfempId;
    JComboBox<Department> cbDepartment;
    JButton updateBtn, back;
    String oldEmpId; // Original employee id

    public UpdateEmployee(String empId) {
        this.oldEmpId = empId;
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Update Employee Detail");
        heading.setBounds(320, 30, 500, 50);
        heading.setFont(new Font("SAN_SERIF", Font.BOLD, 25));
        add(heading);

        JLabel labelname = new JLabel("Name");
        labelname.setBounds(50, 100, 150, 30);
        labelname.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelname);

        JLabel lblname = new JLabel();
        lblname.setBounds(200, 100, 150, 30);
        add(lblname);

        JLabel labelfname = new JLabel("Father's Name");
        labelfname.setBounds(400, 100, 150, 30);
        labelfname.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelfname);

        tffname = new JTextField();
        tffname.setBounds(600, 100, 150, 30);
        add(tffname);

        JLabel labeldob = new JLabel("Date of Birth");
        labeldob.setBounds(50, 150, 150, 30);
        labeldob.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeldob);

        JLabel lbldob = new JLabel();
        lbldob.setBounds(200, 150, 150, 30);
        add(lbldob);

        JLabel labelsalary = new JLabel("Salary");
        labelsalary.setBounds(400, 150, 150, 30);
        labelsalary.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelsalary);

        tfsalary = new JTextField();
        tfsalary.setBounds(600, 150, 150, 30);
        add(tfsalary);

        JLabel labeladdress = new JLabel("Address");
        labeladdress.setBounds(50, 200, 150, 30);
        labeladdress.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeladdress);

        tfaddress = new JTextField();
        tfaddress.setBounds(200, 200, 150, 30);
        add(tfaddress);

        JLabel labelphone = new JLabel("Phone");
        labelphone.setBounds(400, 200, 150, 30);
        labelphone.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelphone);

        tfphone = new JTextField();
        tfphone.setBounds(600, 200, 150, 30);
        add(tfphone);

        JLabel labelemail = new JLabel("Email");
        labelemail.setBounds(50, 250, 150, 30);
        labelemail.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelemail);

        tfemail = new JTextField();
        tfemail.setBounds(200, 250, 150, 30);
        add(tfemail);

        JLabel labeleducation = new JLabel("Highest Education");
        labeleducation.setBounds(400, 250, 150, 30);
        labeleducation.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeleducation);

        tfeducation = new JTextField();
        tfeducation.setBounds(600, 250, 150, 30);
        add(tfeducation);

        JLabel labeldesignation = new JLabel("Designation");
        labeldesignation.setBounds(50, 300, 150, 30);
        labeldesignation.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeldesignation);

        tfdesignation = new JTextField();
        tfdesignation.setBounds(200, 300, 150, 30);
        add(tfdesignation);

        // Department field
        JLabel labelDept = new JLabel("Department");
        labelDept.setBounds(400, 300, 150, 30);
        labelDept.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelDept);

        cbDepartment = new JComboBox<>();
        cbDepartment.setBackground(Color.WHITE);
        cbDepartment.setBounds(600, 300, 150, 30);
        add(cbDepartment);



        JLabel labelempId = new JLabel("Employee id");
        labelempId.setBounds(400, 350, 150, 30);
        labelempId.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelempId);

        // Editable Employee id field
        tfempId = new JTextField();
        tfempId.setBounds(600, 350, 150, 30);
        tfempId.setFont(new Font("serif", Font.PLAIN, 20));
        add(tfempId);

        // Fetch existing employee details
        try {
            Conn c = new Conn();
            String query = "select * from employee where empId = '" + oldEmpId + "'";
            ResultSet rs = c.s.executeQuery(query);
            if (rs.next()) {
                lblname.setText(rs.getString("name"));
                tffname.setText(rs.getString("fname"));
                lbldob.setText(rs.getString("dob"));
                tfaddress.setText(rs.getString("address"));
                tfsalary.setText(rs.getString("salary"));
                tfphone.setText(rs.getString("phone"));
                tfemail.setText(rs.getString("email"));
                tfeducation.setText(rs.getString("education"));
//                lblaadhar.setText(rs.getString("aadhar"));
                tfdesignation.setText(rs.getString("designation"));
                String empDeptId = rs.getString("deptId");
                tfempId.setText(rs.getString("empId")); // current emp id

                // Populate department combo box with Department objects
                Statement s = c.c.createStatement();
                String deptQuery = "select deptId, deptName from department";
                ResultSet rsDept = s.executeQuery(deptQuery);
                Department selectedDept = null;
                while (rsDept.next()) {
                    Department dept = new Department(rsDept.getInt("deptId"), rsDept.getString("deptName"));
                    cbDepartment.addItem(dept);
                    if (empDeptId != null && empDeptId.equals(String.valueOf(rsDept.getInt("deptId")))) {
                        selectedDept = dept;
                    }
                }
                if (selectedDept != null) {
                    cbDepartment.setSelectedItem(selectedDept);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateBtn = new JButton("Update Details");
        updateBtn.setBounds(250, 450, 150, 40);
        updateBtn.addActionListener(this);
        updateBtn.setBackground(Color.BLACK);
        updateBtn.setForeground(Color.WHITE);
        add(updateBtn);

        back = new JButton("Back");
        back.setBounds(450, 450, 150, 40);
        back.addActionListener(this);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        add(back);

        setSize(900, 550);
        setLocation(300, 50);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateBtn) {
            String fname = tffname.getText();
            String salary = tfsalary.getText();
            String address = tfaddress.getText();
            String phone = tfphone.getText();
            String email = tfemail.getText();
            String education = tfeducation.getText();
            String designation = tfdesignation.getText();
            Department selectedDept = (Department) cbDepartment.getSelectedItem();
            int deptId = selectedDept.getDeptId();
            String newEmpId = tfempId.getText();

            try {
                Conn conn = new Conn();
                String query = "update employee set empId = '" + newEmpId + "', fname = '" + fname + "', salary = '" + salary +
                        "', address = '" + address + "', phone = '" + phone + "', email = '" + email + "', education = '" + education +
                        "', designation = '" + designation + "', deptId = '" + deptId + "' where empId = '" + oldEmpId + "'";
                conn.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Details updated successfully");
                setVisible(false);
                new Home();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setVisible(false);
            new Home();
        }
    }

    public static void main(String[] args) {
        new UpdateEmployee("someEmpId");
    }
}
