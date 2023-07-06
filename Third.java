import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Third extends JFrame {
    private JTextField txtAppId, txtName, txtURL;
    private JTable tblApplications;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public Third() {
        initializeUI();
        connectToDatabase();
        displayApplications();
    }

    private void initializeUI() {
        txtAppId = new JTextField();
        txtName = new JTextField();
        txtURL = new JTextField();

        tblApplications = new JTable();
        tblApplications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblApplications.getSelectionModel().addListSelectionListener(e -> selectApplication());

        JScrollPane scrollPane = new JScrollPane(tblApplications);

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

        panel.add(new JLabel("App ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("URL:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtAppId, gbc);
        gbc.gridy++;
        panel.add(txtName, gbc);
        gbc.gridy++;
        panel.add(txtURL, gbc);

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

        btnAdd.addActionListener(e -> insertApplication());

        btnModify.addActionListener(e -> modifyApplication());

        btnDelete.addActionListener(e -> deleteApplication());

        btnDisplay.addActionListener(e -> displayApplications());

        setTitle("Third-Party Applications");
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

    private void insertApplication() {
        String appId = txtAppId.getText();
        String name = txtName.getText();
        String url = txtURL.getText();

        try {
            String query = "INSERT INTO Third_party(app_id, name, url) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, appId);
            statement.setString(2, name);
            statement.setString(3, url);

            statement.executeUpdate();

            clearFields();
            displayApplications();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyApplication() {
        int selectedRow = tblApplications.getSelectedRow();
        if (selectedRow >= 0) {
            String appId = txtAppId.getText();
            String name = txtName.getText();
            String url = txtURL.getText();

            try {
                String query = "UPDATE Third_party SET name=?, url=? WHERE app_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, url);
                statement.setString(3, appId);
                statement.executeUpdate();

                clearFields();
                displayApplications();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an application to modify.");
        }
    }

    private void deleteApplication() {
        int selectedRow = tblApplications.getSelectedRow();
        if (selectedRow >= 0) {
            String appId = tblApplications.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this application?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Third_party WHERE app_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, appId);
                    statement.executeUpdate();

                    clearFields();
                    displayApplications();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an application to delete.");
        }
    }

    private void displayApplications() {
        try {
            String query = "SELECT * FROM Third_party";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Application> applications = new ArrayList<>();
            while (resultSet.next()) {
                String appId = resultSet.getString("app_id");
                String name = resultSet.getString("name");
                String url = resultSet.getString("url");

                applications.add(new Application(appId, name, url));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"App ID", "Name", "URL"});

            for (Application application : applications) {
                model.addRow(new String[]{application.getAppId(), application.getName(), application.getUrl()});
            }

            tblApplications.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectApplication() {
        int selectedRow = tblApplications.getSelectedRow();
        if (selectedRow >= 0) {
            String appId = tblApplications.getValueAt(selectedRow, 0).toString();
            String name = tblApplications.getValueAt(selectedRow, 1).toString();
            String url = tblApplications.getValueAt(selectedRow, 2).toString();

            txtAppId.setText(appId);
            txtName.setText(name);
            txtURL.setText(url);
        }
    }

    private void clearFields() {
        txtAppId.setText("");
        txtName.setText("");
        txtURL.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Third::new);
    }

    private class Application {
        private String appId;
        private String name;
        private String url;

        public Application(String appId, String name, String url) {
            this.appId = appId;
            this.name = name;
            this.url = url;
        }

        public String getAppId() {
            return appId;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
