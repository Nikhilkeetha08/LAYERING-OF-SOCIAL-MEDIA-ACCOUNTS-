import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class platforms extends JFrame {
private JTextField txtPlatformId, txtName, txtURL;
private JTable tblPlatforms;
private JButton btnAdd, btnModify, btnDelete, btnDisplay;
private Connection connection;

public platforms() {
    initializeUI();
    connectToDatabase();
    displayPlatforms();
}

private void initializeUI() {
    txtPlatformId = new JTextField();
    txtName = new JTextField();
    txtURL = new JTextField();

    tblPlatforms = new JTable();
    tblPlatforms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tblPlatforms.getSelectionModel().addListSelectionListener(e -> selectPlatform());

    JScrollPane scrollPane = new JScrollPane(tblPlatforms);

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

    panel.add(new JLabel("Platform ID:"), gbc);
    gbc.gridy++;
    panel.add(new JLabel("Name:"), gbc);
    gbc.gridy++;
    panel.add(new JLabel("URL:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;

    panel.add(txtPlatformId, gbc);
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

    btnAdd.addActionListener(e -> insertPlatform());

    btnModify.addActionListener(e -> modifyPlatform());

    btnDelete.addActionListener(e -> deletePlatform());

    btnDisplay.addActionListener(e -> displayPlatforms());

    setTitle("Social Media Platform App");
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

private void insertPlatform() {
    String platformId = txtPlatformId.getText();
    String name = txtName.getText();
    String url = txtURL.getText();

    try {
        String query = "INSERT INTO social_media_platform (platform_id, name, url) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, platformId);
        statement.setString(2, name);
        statement.setString(3, url);
        statement.executeUpdate();

        clearFields();
        displayPlatforms();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void modifyPlatform() {
    int selectedRow = tblPlatforms.getSelectedRow();
    if (selectedRow >= 0) {
        String platformId = txtPlatformId.getText();
        String name = txtName.getText();
        String url = txtURL.getText();

        try {
            String query = "UPDATE Social_Media_Platform SET Name=?, URL=? WHERE Platform_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, url);
            statement.setString(3, platformId);
            statement.executeUpdate();

            clearFields();
            displayPlatforms();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a platform to modify.");
    }
}

private void deletePlatform() {
    int selectedRow = tblPlatforms.getSelectedRow();
    if (selectedRow >= 0) {
        String platformId = tblPlatforms.getValueAt(selectedRow, 0).toString();

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this platform?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM social_media_platform WHERE Platform_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, platformId);
                statement.executeUpdate();

                clearFields();
                displayPlatforms();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a platform to delete.");
    }
}

private void displayPlatforms() {
    try {
        String query = "SELECT * FROM social_media_platform";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        List<Platform> platforms = new ArrayList<>();
        while (resultSet.next()) {
            String platformId = resultSet.getString("Platform_ID");
            String name = resultSet.getString("Name");
            String url = resultSet.getString("URL");
            platforms.add(new Platform(platformId, name, url));
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Platform ID", "Name", "URL"});

        for (Platform platform : platforms) {
            model.addRow(new String[]{platform.getPlatformId(), platform.getName(), platform.getUrl()});
        }

        tblPlatforms.setModel(model);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void selectPlatform() {
    int selectedRow = tblPlatforms.getSelectedRow();
    if (selectedRow >= 0) {
        String platformId = tblPlatforms.getValueAt(selectedRow, 0).toString();
        String name = tblPlatforms.getValueAt(selectedRow, 1).toString();
        String url = tblPlatforms.getValueAt(selectedRow, 2).toString();

        txtPlatformId.setText(platformId);
        txtName.setText(name);
        txtURL.setText(url);
    }
}

private void clearFields() {
    txtPlatformId.setText("");
    txtName.setText("");
    txtURL.setText("");
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(platforms::new);
}

private class Platform {
    private String platformId;
    private String name;
    private String url;

    public Platform(String platformId, String name, String url) {
        this.platformId = platformId;
        this.name = name;
        this.url = url;
    }

    public String getPlatformId() {
        return platformId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
}