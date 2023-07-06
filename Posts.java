import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Posts extends JFrame {
    private JTextField txtPostID, txtUserID, txtPlatformID, txtTime, txtPostType;
    private JTable tblPosts;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;
    private Connection connection;

    public Posts() {
        initializeUI();
        connectToDatabase();
        displayPosts();
    }

    private void initializeUI() {
        txtPostID = new JTextField(10);
        txtUserID = new JTextField(10);
        txtPlatformID = new JTextField(10);
        txtTime = new JTextField(10);
        txtPostType = new JTextField(10);

        tblPosts = new JTable();
        tblPosts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPosts.getSelectionModel().addListSelectionListener(e -> selectPost());

        JScrollPane scrollPane = new JScrollPane(tblPosts);

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

        panel.add(new JLabel("Post ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Platform ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Time:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Post Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtPostID, gbc);
        gbc.gridy++;
        panel.add(txtUserID, gbc);
        gbc.gridy++;
        panel.add(txtPlatformID, gbc);
        gbc.gridy++;
        panel.add(txtTime, gbc);
        gbc.gridy++;
        panel.add(txtPostType, gbc);

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

        btnAdd.addActionListener(e -> insertPost());
        btnModify.addActionListener(e -> modifyPost());
        btnDelete.addActionListener(e -> deletePosts());
        btnDisplay.addActionListener(e -> displayPosts());

        setTitle("Social Media Posts App");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
            System.exit(1);
        }
    }

    private void insertPost() {
        String postID = txtPostID.getText();
        String userID = txtUserID.getText();
        String platformID =txtPlatformID.getText();
        String time = txtTime.getText();
        String postType = txtPostType.getText();

        try {
            String query = "INSERT INTO posts (post_id, user_id, platform_id, time, post_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, postID);
            statement.setString(2, userID);
            statement.setString(3, platformID);
            statement.setString(4, time);
            statement.setString(5, postType);
            statement.executeUpdate();

            clearFields();
            displayPosts();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to insert the post.");
        }
    }

    private void modifyPost() {
        int selectedRow = tblPosts.getSelectedRow();
        if (selectedRow >= 0) {
            String postID = txtPostID.getText();
            String userID = txtUserID.getText();
            String platformID = txtPlatformID.getText();
            String time = txtTime.getText();
            String postType = txtPostType.getText();

            try {
                String query = "UPDATE posts SET user_id=?, platform_id=?, time=?, post_type=? WHERE post_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userID);
                statement.setString(2, platformID);
                statement.setString(3, time);
                statement.setString(4, postType);
                statement.setString(5, postID);
                statement.executeUpdate();

                clearFields();
                displayPosts();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to modify the post.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a post to modify.");
        }
    }

    private void deletePosts() {
        int selectedRow = tblPosts.getSelectedRow();
        if (selectedRow >= 0) {
            String postId = tblPosts.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this post?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM posts WHERE post_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, postId);
                    statement.executeUpdate();

                    clearFields();
                    displayPosts();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a post to delete.");
        }
    }

    private void displayPosts() {
        try {
            String query = "SELECT * FROM posts";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<SocialMediaPost> posts = new ArrayList<>();
            while (resultSet.next()) {
                String postID = resultSet.getString("post_id");
                String userID = resultSet.getString("user_id");
                String platformID = resultSet.getString("platform_id");
                String time = resultSet.getString("time");
                String postType = resultSet.getString("post_type");
                posts.add(new SocialMediaPost(postID, userID, platformID, time, postType));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Post ID", "User ID", "Platform ID", "Time", "Post Type"});

            for (SocialMediaPost post : posts) {
                model.addRow(new String[]{post.getPostID(), post.getUserID(), post.getPlatformID(), post.getTime(), post.getPostType()});
            }

            tblPosts.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch posts.");
        }
    }

    private void selectPost() {
        int selectedRow = tblPosts.getSelectedRow();
        if (selectedRow >= 0) {
            String postID = tblPosts.getValueAt(selectedRow, 0).toString();
            String userID = tblPosts.getValueAt(selectedRow, 1).toString();
            String platformID = tblPosts.getValueAt(selectedRow, 2).toString();
            String time = tblPosts.getValueAt(selectedRow, 3).toString();
            String postType = tblPosts.getValueAt(selectedRow, 4).toString();

            txtPostID.setText(postID);
            txtUserID.setText(userID);
            txtPlatformID.setText(platformID);
            txtTime.setText(time);
            txtPostType.setText(postType);
        }
    }

    private void clearFields() {
        txtPostID.setText("");
        txtUserID.setText("");
        txtPlatformID.setText("");
        txtTime.setText("");
        txtPostType.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Posts::new);
    }
}

class SocialMediaPost {
    private String postID;
    private String userID;
    private String platformID;
    private String time;
    private String postType;

    public SocialMediaPost(String postID, String userID, String platformID, String time, String postType) {
        this.postID = postID;
        this.userID = userID;
        this.platformID = platformID;
        this.time = time;
        this.postType = postType;
    }

    public String getPostID() {
        return postID;
    }

    public String getUserID() {
        return userID;
    }

    public String getPlatformID() {
        return platformID;
    }

    public String getTime() {
        return time;
    }

    public String getPostType() {
        return postType;
    }
}
