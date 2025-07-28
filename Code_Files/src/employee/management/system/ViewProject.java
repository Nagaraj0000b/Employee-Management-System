package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class ViewProject extends JFrame implements ActionListener {

    JTable table;
    JButton back, print, delete;

    public ViewProject() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Project Details");
        heading.setBounds(20, 20, 400, 30);
        heading.setFont(new Font("Raleway", Font.BOLD, 25));
        add(heading);

        table = new JTable();
        try {
            Conn c = new Conn();
            // Query joining project, department, and project_assignment with employee names.
            String query = "SELECT p.projectId, p.projectName, p.description, p.startDate, p.endDate, " +
                    "d.deptName AS Department, " +
                    "GROUP_CONCAT(e.name SEPARATOR ', ') AS AssignedEmployees " +
                    "FROM project p " +
                    "LEFT JOIN department d ON p.deptId = d.deptId " +
                    "LEFT JOIN project_assignment pa ON p.projectId = pa.projectId " +
                    "LEFT JOIN employee e ON pa.empId = e.empId " +
                    "GROUP BY p.projectId";
            ResultSet rs = c.s.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(20, 70, 850, 500);
        add(jsp);

        print = new JButton("Print");
        print.setBounds(20, 600, 100, 30);
        print.addActionListener(this);
        add(print);

        // New button to delete a project.
        delete = new JButton("Delete Project");
        delete.setBounds(140, 600, 150, 30);
        delete.addActionListener(this);
        delete.setBackground(Color.RED);
        delete.setForeground(Color.WHITE);
        add(delete);

        back = new JButton("Back");
        back.setBounds(320, 600, 100, 30);
        back.addActionListener(this);
        add(back);

        setSize(900, 700);
        setLocation(300, 100);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == print) {
            try {
                table.print();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == delete) {
            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a project to delete.");
                return;
            }
            // Assuming the first column is projectId.
            String projectId = table.getModel().getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete project ID " + projectId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                try {
                    Conn c = new Conn();
                    // First delete the assignments related to the project.
                    String queryAssignments = "DELETE FROM project_assignment WHERE projectId = " + projectId;
                    c.s.executeUpdate(queryAssignments);
                    // Then delete the project.
                    String queryProject = "DELETE FROM project WHERE projectId = " + projectId;
                    c.s.executeUpdate(queryProject);
                    JOptionPane.showMessageDialog(null, "Project deleted successfully");
                    setVisible(false);
                    new Home();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (ae.getSource() == back) {
            setVisible(false);
            new Home();
        }
    }

    public static void main(String[] args) {
        new ViewProject();
    }
}
