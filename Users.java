import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Users extends JFrame {
    private JTextField txtUserId, txtName, txtEmail, txtTotalRows;
    private JTable tblUsers;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public Users() {
        initializeUI();
        connectToDatabase();
        displayUsers();
        generateTotalRows();
    }

    private void initializeUI() {
        txtUserId = new JTextField();
        txtName = new JTextField();
        txtEmail = new JTextField();

        tblUsers = new JTable();
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.getSelectionModel().addListSelectionListener(e -> selectUser());

        JScrollPane scrollPane = new JScrollPane(tblUsers);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        txtTotalRows = new JTextField();
        txtTotalRows.setEditable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtUserId, gbc);
        gbc.gridy++;
        panel.add(txtName, gbc);
        gbc.gridy++;
        panel.add(txtEmail, gbc);

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
        add(txtTotalRows, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addUser());
        btnModify.addActionListener(e -> modifyUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnDisplay.addActionListener(e -> displayUsers());

        setTitle("User Table");
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

    private void addUser() {
        String userId = txtUserId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();

        try {
            String query = "INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);

            statement.executeUpdate();

            clearFields();
            displayUsers();
            generateTotalRows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = txtUserId.getText();
            String name = txtName.getText();
            String email = txtEmail.getText();

            try {
                String query = "UPDATE users SET name=?, email=? WHERE user_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, userId);
                statement.executeUpdate();

                clearFields();
                displayUsers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to modify.");
        }
    }

    private void deleteUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = tblUsers.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM users WHERE user_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, userId);
                    statement.executeUpdate();

                    clearFields();
                    displayUsers();
                    generateTotalRows();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23000")) {
                        JOptionPane.showMessageDialog(this, "Cannot delete the user. Associated records exist in other tables.");
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void displayUsers() {
        try {
            String query = "SELECT * FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                String userId = resultSet.getString("user_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");

                users.add(new User(userId, name, email));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"User ID", "Name", "Email"});

            for (User user : users) {
                model.addRow(new String[]{user.getUserId(), user.StringgetName(), user.getEmail()});
            }

            tblUsers.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = tblUsers.getValueAt(selectedRow, 0).toString();
            String name = tblUsers.getValueAt(selectedRow, 1).toString();
            String email = tblUsers.getValueAt(selectedRow, 2).toString();

            txtUserId.setText(userId);
            txtName.setText(name);
            txtEmail.setText(email);
        }
    }

    private void clearFields() {
        txtUserId.setText("");
        txtName.setText("");
        txtEmail.setText("");
    }

    private void generateTotalRows() {
        int totalRows = tblUsers.getRowCount();
        txtTotalRows.setText("Total Rows: " + totalRows);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Users::new);
    }

    private class User {
        private String userId;
        private String name;
        private String email;

        public User(String userId, String name, String email) {
            this.userId = userId;
            this.name = name;
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public String StringgetName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
