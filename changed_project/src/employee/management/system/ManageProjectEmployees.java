package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageProjectEmployees extends JFrame implements ActionListener {
    JComboBox<String> cbProjects;
    JList<String> listAssigned, listAvailable;
    DefaultListModel<String> modelAssigned, modelAvailable;
    JButton btnAssign, btnRemove, btnBack;

    public ManageProjectEmployees() {
        setTitle("Manage Project Employees");
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblProject = new JLabel("Select Project:");
        lblProject.setBounds(30, 20, 150, 30);
        lblProject.setFont(new Font("serif", Font.PLAIN, 18));
        add(lblProject);

        cbProjects = new JComboBox<>();
        cbProjects.setBounds(180, 20, 400, 30);
        cbProjects.setFont(new Font("serif", Font.PLAIN, 14));
        add(cbProjects);

        loadProjects();

        JLabel lblAssigned = new JLabel("Assigned Employees");
        lblAssigned.setBounds(30, 70, 200, 30);
        lblAssigned.setFont(new Font("serif", Font.BOLD, 16));
        add(lblAssigned);

        modelAssigned = new DefaultListModel<>();
        listAssigned = new JList<>(modelAssigned);
        listAssigned.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spAssigned = new JScrollPane(listAssigned);
        spAssigned.setBounds(30, 110, 250, 300);
        add(spAssigned);

        JLabel lblAvailable = new JLabel("Available (Same Dept)");
        lblAvailable.setBounds(330, 70, 250, 30);
        lblAvailable.setFont(new Font("serif", Font.BOLD, 16));
        add(lblAvailable);

        modelAvailable = new DefaultListModel<>();
        listAvailable = new JList<>(modelAvailable);
        listAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spAvailable = new JScrollPane(listAvailable);
        spAvailable.setBounds(330, 110, 250, 300);
        add(spAvailable);

        btnAssign = new JButton("Assign >>");
        btnAssign.setBounds(620, 150, 120, 40);
        btnAssign.addActionListener(this);
        btnAssign.setBackground(Color.BLACK);
        btnAssign.setForeground(Color.WHITE);
        add(btnAssign);

        btnRemove = new JButton("<< Remove");
        btnRemove.setBounds(620, 220, 120, 40);
        btnRemove.addActionListener(this);
        btnRemove.setBackground(Color.BLACK);
        btnRemove.setForeground(Color.WHITE);
        add(btnRemove);

        btnBack = new JButton("Back");
        btnBack.setBounds(620, 290, 120, 40);
        btnBack.addActionListener(this);
        btnBack.setBackground(Color.BLACK);
        btnBack.setForeground(Color.WHITE);
        add(btnBack);

        cbProjects.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                updateLists();
            }
        });

        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if(cbProjects.getItemCount() > 0) {
            cbProjects.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "No projects found in the database.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadProjects() {
        try {
            Conn conn = new Conn();
            if (conn.c == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed. Cannot load projects.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String query = "SELECT p.projectId, p.projectName, p.deptId, d.deptName " +
                    "FROM project p LEFT JOIN department d ON p.deptId = d.deptId " +
                    "ORDER BY p.projectName";
            ResultSet rs = conn.s.executeQuery(query);
            while(rs.next()){
                int projId = rs.getInt("projectId");
                String projName = rs.getString("projectName");
                String deptName = rs.getString("deptName");
                if (deptName == null) {
                    deptName = "N/A";
                }
                cbProjects.addItem(projId + " - " + projName + " (Dept: " + deptName + ")");
            }
            rs.close();
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading projects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLists() {
        modelAssigned.clear();
        modelAvailable.clear();

        String selectedProjectString = (String) cbProjects.getSelectedItem();
        if(selectedProjectString == null || selectedProjectString.isEmpty()) {
            return;
        }

        int projectId = -1;
        int projectDeptId = -1;

        try {
            String[] parts = selectedProjectString.split(" - ");
            if (parts.length > 0) {
                projectId = Integer.parseInt(parts[0]);
            } else {
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing project ID from string: '" + selectedProjectString + "' - " + e.getMessage());
            return;
        }

        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Conn conn = new Conn();
            if (conn.c == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            connection = conn.c;
            stmt = connection.createStatement();

            String projectDeptQuery = "SELECT deptId FROM project WHERE projectId = " + projectId;
            rs = stmt.executeQuery(projectDeptQuery);

            if (rs.next()) {
                projectDeptId = rs.getInt("deptId");
                if (rs.wasNull()) {
                    projectDeptId = -1;
                    JOptionPane.showMessageDialog(this,
                            "Selected project (ID: " + projectId + ") does not have an assigned department.\nCannot list or assign employees based on department.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                System.err.println("Could not find project details for ID: " + projectId);
                JOptionPane.showMessageDialog(this, "Could not find details for selected project.", "Error", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                return;
            }
            rs.close();

            if (projectDeptId == -1) {
                modelAvailable.addElement("Project has no department assigned.");
                stmt.close();
                return;
            }

            Vector<Integer> assignedEmpIds = new Vector<>();
            String queryAssigned = "SELECT e.empId, e.name " +
                    "FROM employee e JOIN project_assignment pa ON e.empId = pa.empId " +
                    "WHERE pa.projectId = " + projectId;
            rs = stmt.executeQuery(queryAssigned);
            while(rs.next()){
                int empId = rs.getInt("empId");
                String name = rs.getString("name");
                modelAssigned.addElement(empId + " - " + name);
                assignedEmpIds.add(empId);
            }
            rs.close();

            String queryAvailable = "SELECT empId, name " +
                    "FROM employee " +
                    "WHERE deptId = " + projectDeptId;
            rs = stmt.executeQuery(queryAvailable);
            while(rs.next()){
                int empId = rs.getInt("empId");
                String name = rs.getString("name");
                if(!assignedEmpIds.contains(empId)){
                    modelAvailable.addElement(empId + " - " + name);
                }
            }

            if (modelAvailable.isEmpty() && projectDeptId != -1) {
                modelAvailable.addElement("No employees in this dept available.");
            }

        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while updating lists: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee lists: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String selectedProjectString = (String) cbProjects.getSelectedItem();

        if (ae.getSource() == btnBack) {
            this.setVisible(false);
            this.dispose();
            SwingUtilities.invokeLater(() -> new Home());
            return;
        }

        if (selectedProjectString == null || selectedProjectString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a project first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int projectId = -1;
        try {
            projectId = Integer.parseInt(selectedProjectString.split(" - ")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing project ID in actionPerformed: " + selectedProjectString);
            JOptionPane.showMessageDialog(this, "Invalid project selection format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(ae.getSource() == btnAssign) {
            String selectedEmployeeString = listAvailable.getSelectedValue();

            if(selectedEmployeeString == null || selectedEmployeeString.startsWith("No employees") || selectedEmployeeString.startsWith("Project has no")) {
                JOptionPane.showMessageDialog(this, "Please select an employee from the 'Available (Same Dept)' list to assign.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int empId = -1;
            try {
                empId = Integer.parseInt(selectedEmployeeString.split(" - ")[0]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Error parsing employee ID from string: " + selectedEmployeeString);
                JOptionPane.showMessageDialog(this, "Invalid employee selection format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection connection = null;
            Statement stmt = null;
            ResultSet rsCheck = null;
            try {
                Conn conn = new Conn();
                if (conn.c == null) { JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                connection = conn.c;
                stmt = connection.createStatement();

                String checkQuery = "SELECT COUNT(*) FROM project_assignment WHERE projectId = " + projectId + " AND empId = " + empId;
                rsCheck = stmt.executeQuery(checkQuery);
                boolean alreadyExists = false;
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    alreadyExists = true;
                }

                if (alreadyExists) {
                    JOptionPane.showMessageDialog(this, "Employee is already assigned to this project.", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String query = "INSERT INTO project_assignment(projectId, empId) VALUES(" + projectId + ", " + empId + ")";
                    int rowsAffected = stmt.executeUpdate(query);
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Employee assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateLists();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to assign employee (no rows affected).", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch(SQLIntegrityConstraintViolationException dive) {
                JOptionPane.showMessageDialog(this, "Assignment failed: Employee might already be assigned.", "Error", JOptionPane.ERROR_MESSAGE);
                dive.printStackTrace();
            } catch(SQLException sqlEx) {
                sqlEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error assigning employee: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error assigning employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (rsCheck != null) rsCheck.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }

        } else if(ae.getSource() == btnRemove) {
            String selectedEmployeeString = listAssigned.getSelectedValue();

            if(selectedEmployeeString == null) {
                JOptionPane.showMessageDialog(this, "Please select an employee from the 'Assigned' list to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int empId = -1;
            try {
                empId = Integer.parseInt(selectedEmployeeString.split(" - ")[0]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Error parsing employee ID from assigned list: " + selectedEmployeeString);
                JOptionPane.showMessageDialog(this, "Invalid assigned employee selection format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection connection = null;
            Statement stmt = null;
            try {
                Conn conn = new Conn();
                if (conn.c == null) { JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                connection = conn.c;
                stmt = connection.createStatement();

                String query = "DELETE FROM project_assignment WHERE projectId = " + projectId + " AND empId = " + empId;
                int rowsAffected = stmt.executeUpdate(query);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee removed from project.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateLists();
                } else {
                    JOptionPane.showMessageDialog(this, "Employee was not found in this project's assignment (no rows affected).", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch(SQLException sqlEx) {
                sqlEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error removing employee: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Failed to set LookAndFeel.");
                }
                new ManageProjectEmployees();
            }
        });
    }
}