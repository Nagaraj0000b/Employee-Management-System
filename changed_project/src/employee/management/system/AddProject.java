package employee.management.system;

import java.awt.*;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.awt.event.*;
import java.util.Date;

public class AddProject extends JFrame implements ActionListener {

    JTextField tfProjectName, tfDescription;
    JDateChooser dcStartDate, dcEndDate;
    JComboBox<Department> cbDepartment; // Holds Department objects
    JComboBox<String> cbProjectManager; // Holds "empId - name" strings
    JButton add, back;

    public AddProject() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        setTitle("Add New Project"); // Set a title

        JLabel heading = new JLabel("Add Project Details"); // More descriptive heading
        heading.setBounds(280, 20, 400, 50); // Centered adjustment
        heading.setFont(new Font("SAN_SERIF", Font.BOLD, 25));
        add(heading);

        // --- Project Name ---
        JLabel labelProjectName = new JLabel("Project Name*"); // Indicate required
        labelProjectName.setBounds(50, 100, 150, 30);
        labelProjectName.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelProjectName);

        tfProjectName = new JTextField();
        tfProjectName.setBounds(200, 100, 180, 30); // Slightly wider
        add(tfProjectName);

        // --- Description ---
        JLabel labelDescription = new JLabel("Description");
        labelDescription.setBounds(400, 100, 150, 30);
        labelDescription.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelDescription);

        tfDescription = new JTextField();
        tfDescription.setBounds(550, 100, 180, 30); // Adjusted position & width
        add(tfDescription);

        // --- Start Date ---
        JLabel labelStartDate = new JLabel("Start Date*"); // Indicate required
        labelStartDate.setBounds(50, 150, 150, 30);
        labelStartDate.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelStartDate);

        dcStartDate = new JDateChooser();
        dcStartDate.setBounds(200, 150, 180, 30);
        dcStartDate.setDateFormatString("yyyy-MM-dd"); // Set standard format
        add(dcStartDate);

        // --- End Date ---
        JLabel labelEndDate = new JLabel("End Date");
        labelEndDate.setBounds(400, 150, 150, 30);
        labelEndDate.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelEndDate);

        dcEndDate = new JDateChooser();
        dcEndDate.setBounds(550, 150, 180, 30); // Adjusted position & width
        dcEndDate.setDateFormatString("yyyy-MM-dd"); // Set standard format
        add(dcEndDate);

        // --- Department ---
        JLabel labelDept = new JLabel("Department*"); // Indicate required
        labelDept.setBounds(50, 200, 150, 30);
        labelDept.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelDept);

        cbDepartment = new JComboBox<>(); // Holds Department objects
        cbDepartment.setBackground(Color.WHITE);
        cbDepartment.setBounds(200, 200, 180, 30);
        add(cbDepartment);

        // --- Project Manager ---
        JLabel labelProjectManager = new JLabel("Project Manager");
        labelProjectManager.setBounds(400, 200, 150, 30);
        labelProjectManager.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelProjectManager);

        cbProjectManager = new JComboBox<>(); // Holds "empId - name"
        cbProjectManager.setBackground(Color.WHITE);
        cbProjectManager.setBounds(550, 200, 180, 30); // Adjusted position & width
        add(cbProjectManager);

        // Populate department combo box first
        populateDepartments();

        // Add ActionListener to Department ComboBox AFTER populating it
        cbDepartment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When department changes, update the project manager list
                updateProjectManagers();
            }
        });

        // Initial population of Project Managers based on the initially selected department
        // (Important to call this *after* departments are loaded and listener is added)
        if (cbDepartment.getItemCount() > 0) {
            cbDepartment.setSelectedIndex(0); // Ensure the listener fires for the first item
            // updateProjectManagers(); // The listener added above will trigger this automatically on setSelectedIndex(0)
        } else {
            // Handle case where no departments exist
            cbProjectManager.addItem("No departments available");
            cbProjectManager.setEnabled(false);
        }


        // --- Buttons ---
        add = new JButton("Add Project");
        add.setBounds(250, 280, 150, 40); // Adjusted Y position
        add.addActionListener(this);
        add.setBackground(Color.BLACK);
        add.setForeground(Color.WHITE);
        add(add);

        back = new JButton("Back");
        back.setBounds(450, 280, 150, 40); // Adjusted Y position
        back.addActionListener(this);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        add(back);

        setSize(800, 400); // Adjusted size
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose frame on close
    }

    /**
     * Populates the Department JComboBox from the database.
     */
    private void populateDepartments() {
        try {
            Conn conn = new Conn();
            if (conn.c == null) return; // Handle connection failure
            String query = "SELECT deptId, deptName FROM department ORDER BY deptName";
            ResultSet rs = conn.s.executeQuery(query);
            boolean hasDepartments = false;
            while(rs.next()) {
                hasDepartments = true;
                int id = rs.getInt("deptId");
                String name = rs.getString("deptName");
                cbDepartment.addItem(new Department(id, name)); // Add Department object
            }
            rs.close();
            // Don't close conn here if it's managed globally

            if (!hasDepartments) {
                cbDepartment.addItem(new Department(-1, "No Departments Found")); // Placeholder
                cbDepartment.setEnabled(false);
            }

        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading departments: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the Project Manager JComboBox based on the currently selected Department.
     * Only employees belonging to the selected department will be shown.
     */
    private void updateProjectManagers() {
        // Get the selected Department object
        Object selectedItem = cbDepartment.getSelectedItem();
        cbProjectManager.removeAllItems(); // Clear previous managers

        if (selectedItem instanceof Department) {
            Department selectedDept = (Department) selectedItem;
            int selectedDeptId = selectedDept.getDeptId();

            // Check for placeholder department ID
            if (selectedDeptId == -1) {
                cbProjectManager.addItem("Select a valid department first");
                cbProjectManager.setEnabled(false);
                return;
            }

            cbProjectManager.setEnabled(true); // Enable the combo box

            // Database query to get employees from the selected department
            Connection connection = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                Conn conn = new Conn();
                if (conn.c == null) return; // Handle connection failure
                connection = conn.c;

                String query = "SELECT empId, name FROM employee WHERE deptId = ? ORDER BY name";
                pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, selectedDeptId); // Set the department ID parameter

                rs = pstmt.executeQuery();
                boolean managerFound = false;
                while(rs.next()) {
                    managerFound = true;
                    String item = rs.getInt("empId") + " - " + rs.getString("name");
                    cbProjectManager.addItem(item);
                }

                if (!managerFound) {
                    cbProjectManager.addItem("No employees in this dept");
                    // Optional: Disable if no managers, or keep enabled to show the message
                    // cbProjectManager.setEnabled(false);
                }

            } catch(SQLException sqlEx) {
                sqlEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading project managers: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                cbProjectManager.addItem("Error loading managers");
                cbProjectManager.setEnabled(false);
            } catch(Exception e) {
                e.printStackTrace();
                cbProjectManager.addItem("Error");
                cbProjectManager.setEnabled(false);
            } finally {
                // Close resources in finally block
                try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                // Don't close connection `connection` here if managed globally
            }

        } else {
            // Handle cases where the selected item isn't a Department object (e.g., null)
            cbProjectManager.addItem("Select Department");
            cbProjectManager.setEnabled(false);
        }
    }


    /**
     * Handles Add and Back button clicks.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == add) {
            // --- Get Data from Fields ---
            String projectName = tfProjectName.getText().trim();
            String description = tfDescription.getText().trim();
            Date startDateObj = dcStartDate.getDate();
            Date endDateObj = dcEndDate.getDate(); // Can be null

            // --- Basic Validation ---
            if (projectName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Project Name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (startDateObj == null) {
                JOptionPane.showMessageDialog(this, "Start Date is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object deptItem = cbDepartment.getSelectedItem();
            if (!(deptItem instanceof Department) || ((Department)deptItem).getDeptId() == -1) {
                JOptionPane.showMessageDialog(this, "Please select a valid Department.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- Format Dates ---
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = sdf.format(startDateObj);
            String endDateStr = (endDateObj != null) ? sdf.format(endDateObj) : null; // Handle null end date

            // --- Get Department ID ---
            Department selectedDept = (Department) deptItem;
            int deptId = selectedDept.getDeptId();

            // --- Get Project Manager Employee ID ---
            String managerItem = (String) cbProjectManager.getSelectedItem();
            Integer projectManagerEmpId = null; // Use Integer to allow NULL

            if (managerItem != null && !managerItem.startsWith("No employees") && !managerItem.startsWith("Select") && !managerItem.startsWith("Error")) {
                try {
                    projectManagerEmpId = Integer.parseInt(managerItem.split(" - ")[0]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Could not parse manager Emp ID from: " + managerItem);
                    // Decide if manager is mandatory or optional
                    // JOptionPane.showMessageDialog(this, "Invalid Project Manager selection.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    // return;
                    projectManagerEmpId = null; // Treat as optional if parsing fails or selection is invalid
                }
            }

            // --- Database Insert ---
            Connection connection = null;
            PreparedStatement pstmt = null;
            try {
                Conn conn = new Conn();
                if (conn.c == null) return; // Handle connection failure
                connection = conn.c;

                // Use PreparedStatement to handle potential nulls and prevent SQL injection
                String query = "INSERT INTO project(projectName, description, startDate, endDate, deptId, projectManagerEmpId) " +
                        "VALUES(?, ?, ?, ?, ?, ?)";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, projectName);
                pstmt.setString(2, description);
                pstmt.setString(3, startDateStr);

                // Handle nullable endDate and projectManagerEmpId
                if (endDateStr != null) {
                    pstmt.setString(4, endDateStr);
                } else {
                    pstmt.setNull(4, Types.DATE); // Set SQL NULL for date
                }

                pstmt.setInt(5, deptId);

                if (projectManagerEmpId != null) {
                    pstmt.setInt(6, projectManagerEmpId);
                } else {
                    pstmt.setNull(6, Types.INTEGER); // Set SQL NULL for integer
                }

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Project added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                dispose(); // Release resources of this window
                new Home(); // Go back to home screen

            } catch(SQLException sqlEx) {
                sqlEx.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error adding project: " + sqlEx.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                // Don't close connection if managed globally
            }

        } else if (ae.getSource() == back) {
            setVisible(false);
            dispose(); // Release resources
            new Home();
        }
    }

    public static void main(String[] args) {
        // Run GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new AddProject());
    }
}