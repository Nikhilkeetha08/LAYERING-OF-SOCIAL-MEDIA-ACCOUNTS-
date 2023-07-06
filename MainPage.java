import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    /*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private JButton retrieveMarksButton;

    public MainPage() {
        // Set frame properties
        setTitle(" LayeringOfSocialNetworkingAccount");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("LayeringOfSocialNetworkingAccount");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create panel for the button
        /*JPanel buttonPanel = new JPanel();
        retrieveMarksButton = new JButton("Retrieve Marks");
        buttonPanel.add(retrieveMarksButton);
*/
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu UsersMenu = new JMenu("Users Details");
        JMenu platformsMenu = new JMenu(" platforms  Details");
        JMenu ThirdMenu = new JMenu("Third Details");
        JMenu User_social_mediaMenu = new JMenu("User_social_media Details");
        JMenu PostsMenu = new JMenu("Posts Details");

        // Create menu item for student menu
        JMenuItem viewUsersDetails = new JMenuItem("View User Details");
        viewUsersDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Users();
            }
        });

        // Create menu item for course menu
        JMenuItem viewplatformsDetails = new JMenuItem("View platforms Details");
        viewplatformsDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new platforms();
            }
        });

        // Create menu item for enrollment menu
        JMenuItem viewThirdDetails = new JMenuItem("View Third Details");
        viewThirdDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Third();
            }
        });

        // Create menu item for semester menu
        JMenuItem viewUser_social_mediaDetails = new JMenuItem("View User_social_media Details");
        viewUser_social_mediaDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new User_social_media();
            }
        });

        // Create menu item for grade menu
        JMenuItem viewpostsDetails = new JMenuItem("View Posts Details");
        viewpostsDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Posts();
            }
        });

        // Add menu items to respective menus
        UsersMenu.add(viewUsersDetails);
        platformsMenu.add(viewplatformsDetails);
        ThirdMenu.add(viewThirdDetails);
        User_social_mediaMenu.add(viewUser_social_mediaDetails);
        PostsMenu.add(viewpostsDetails);

        // Add menus to the menu bar
        menuBar.add(UsersMenu);
        menuBar.add(platformsMenu);
        menuBar.add(ThirdMenu);
        menuBar.add(User_social_mediaMenu);
        menuBar.add(PostsMenu);

        
        setJMenuBar(menuBar);

       
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    System.out.println("Window maximized");
                } else {
                    System.out.println("Window not maximized");
                }
            }
        });

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainPage();
    }
}