import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User_social_media extends JFrame {
    private JTextField txtMappingID, txtUserID, txtPlatformID;
    private JTable tblMappings;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public User_social_media() {
        initializeUI();
        connectToDatabase();
        displayMappings();
    }

    private void initializeUI() {
        txtMappingID = new JTextField();
        txtUserID = new JTextField();
        txtPlatformID = new JTextField();
       

        tblMappings = new JTable();
        tblMappings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMappings.getSelectionModel().addListSelectionListener(e -> selectMapping());

        JScrollPane scrollPane = new JScrollPane(tblMappings);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Mapping ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Platform ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtMappingID, gbc);
        gbc.gridy++;
        panel.add(txtUserID, gbc);
        gbc.gridy++;
        panel.add(txtPlatformID, gbc);
       

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertMapping());

        btnModify.addActionListener(e -> modifyMapping());

        btnDelete.addActionListener(e -> deleteMapping());

        btnDisplay.addActionListener(e -> displayMappings());

        setTitle("User-Social Media Platform Mapping App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "Nikhil";
        String password = "Nikhil";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertMapping() {
        String mappingID = txtMappingID.getText();
        String userID = txtUserID.getText();
        String platformID = txtPlatformID.getText();
        

        try {
            String query = "INSERT INTO user_social_media (mapping_id, user_id, platfrom_id) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, mappingID);
            statement.setString(2, userID);
            statement.setString(3, platformID);
          
            statement.executeUpdate();

            clearFields();
            displayMappings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyMapping() {
        int selectedRow = tblMappings.getSelectedRow();
        if (selectedRow >= 0) {
            String mappingID = txtMappingID.getText();
            String userID = txtUserID.getText();
            String platformID = txtPlatformID.getText();
           

            try {
                String query = "UPDATE user_social_media SET user_id=?, platfrom_id=? WHERE mapping_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userID);
                statement.setString(2, platformID);
               
                statement.setString(3, mappingID);
                statement.executeUpdate();

                clearFields();
                displayMappings();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a mapping to modify.");
        }
    }

    private void deleteMapping() {
        int selectedRow = tblMappings.getSelectedRow();
        if (selectedRow >= 0) {
            String mappingID = tblMappings.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this mapping?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM user_social WHERE mapping_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, mappingID);
                    statement.executeUpdate();

                    clearFields();
                    displayMappings();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a mapping to delete.");
        }
    }

    private void displayMappings() {
        try {
            String query = "SELECT * FROM user_social_media";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<UserSocialMediaPlatformMapping> mappings = new ArrayList<>();
            while (resultSet.next()) {
                String mappingID = resultSet.getString("mapping_id");
                String userID = resultSet.getString("user_id");
                String platformID = resultSet.getString("platfrom_id");
                
                mappings.add(new UserSocialMediaPlatformMapping(mappingID, userID, platformID));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Mapping ID", "User ID", "Platfrom ID"});

            for (UserSocialMediaPlatformMapping mapping : mappings) {
                model.addRow(new String[]{mapping.getMappingID(), mapping.getUserID(), mapping.getPlatformID()});
            }

            tblMappings.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectMapping() {
        int selectedRow = tblMappings.getSelectedRow();
        if (selectedRow >= 0) {
            String mappingID = tblMappings.getValueAt(selectedRow, 0).toString();
            String userID = tblMappings.getValueAt(selectedRow, 1).toString();
            String platformID = tblMappings.getValueAt(selectedRow, 2).toString();
            

            txtMappingID.setText(mappingID);
            txtUserID.setText(userID);
            txtPlatformID.setText(platformID);
           
        }
    }

    private void clearFields() {
        txtMappingID.setText("");
        txtUserID.setText("");
        txtPlatformID.setText("");
       
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(User_social_media::new);
    }

    private class UserSocialMediaPlatformMapping {
        private String mappingID;
        private String userID;
        private String platformID;
       

        public UserSocialMediaPlatformMapping(String mappingID, String userID, String platformID) {
            this.mappingID = mappingID;
            this.userID = userID;
            this.platformID = platformID;
           
        }

        public String getMappingID() {
            return mappingID;
        }

        public String getUserID() {
            return userID;
        }

        public String getPlatformID() {
            return platformID;
        }

      
    }
}
