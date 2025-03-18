package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class user extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private client Client;
    private managers Manager;
    
    public user() throws FileNotFoundException {
    	
        setTitle("Swing Multi-Panel Template");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Adding different panels
        contentPanel.add(new LoginPanel(this), "Login");
        contentPanel.add(new ManagerLoginPanel(this), "ManagerLogin");
        contentPanel.add(new ClientMainPanel(this), "ClientMain");
        contentPanel.add(new ManagerMainPanel(this), "ManagerMain");
        

        add(contentPanel);
    }

    // Switch between panels
    public void switchTo(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				new user().setVisible(true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
    }
}

// -------- Login Panel --------
class LoginPanel extends JPanel {
	private CSVReader  usersFile;
	boolean success = false;
	
    public LoginPanel(user frame) {
    	Reader reader = null;
    	Path filePath = Paths.get("src", "system", "resources", "users.csv").toAbsolutePath();
		try {
			
			reader = Files.newBufferedReader(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	usersFile = new CSVReader(reader);
    	
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel lblUser = new JLabel("Email:");
        JTextField txtUser = new JTextField(15);
        JLabel lblPass = new JLabel("Password:");
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnManage = new JButton("Manager");
        
        gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnLogin, gbc);
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnManage, gbc);
        
        // Login Button Action
        btnLogin.addActionListener(e -> {
            String id = txtUser.getText();
            String password = new String(txtPass.getPassword());
            
            String[] nextRecord;
            try {
				while ((nextRecord = usersFile.readNext()) != null) {
					if(id.equals(nextRecord[2]) && password.equals(nextRecord[3])) {
						frame.switchTo("ClientMain");
						success = true;
					}
					
				    System.out.println("Column 1: " + nextRecord[2] + ", Column 2: " + nextRecord[3]);
				}
				if(!success) {
					JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
					success = !success;
				}
				
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        //Change to Manager Login
        btnManage.addActionListener(e -> {
        
			frame.switchTo("ManagerLogin");
			
        });
        
    }
}

//-------- Manager Login Panel --------
class ManagerLoginPanel extends JPanel {
	private CSVReader  usersFile;
	boolean success = false;
 public ManagerLoginPanel(user frame) {
 	Reader reader = null;
 	Path filePath = Paths.get("src", "system", "resources", "managers.csv").toAbsolutePath();
		try {
			
			reader = Files.newBufferedReader(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	usersFile = new CSVReader(reader);
 	
     setLayout(new GridBagLayout());
     GridBagConstraints gbc = new GridBagConstraints();
     gbc.insets = new Insets(5, 5, 5, 5);
     
     JLabel lblUser = new JLabel("ID:");
     JTextField txtUser = new JTextField(15);
     JLabel lblPass = new JLabel("Password:");
     JPasswordField txtPass = new JPasswordField(15);
     JButton btnLogin = new JButton("Login");
     JButton btnClient = new JButton("Client");
     
     gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
     gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
     gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
     gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
     gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
     add(btnLogin, gbc);
     gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
     add(btnClient, gbc);
     
     // Login Button Action
     btnLogin.addActionListener(e -> {
         String email = txtUser.getText();
         String password = new String(txtPass.getPassword());
         
         String[] nextRecord;
         try {
				while ((nextRecord = usersFile.readNext()) != null) {
					if(email.equals(nextRecord[1]) && password.equals(nextRecord[3])) {
						frame.switchTo("ManagerMain");
						success = true;
					}
					
				    System.out.println("Column 1: " + nextRecord[1] + ", Column 2: " + nextRecord[3]);
				}
				if(!success) {
					JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
					success = !success;
				}
				
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
     });
     
     //Change to Client Login
     btnClient.addActionListener(e -> {
     
			frame.switchTo("Login");
			
     });
 }
}

// -------- Client Main Panel with Sidebar --------
class ClientMainPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel panelContainer;

    public ClientMainPanel(user frame) {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(3, 1));
        JButton btnPanel1 = new JButton("Panel 1");
        JButton btnPanel2 = new JButton("Panel 2");
        JButton btnLogout = new JButton("Logout");
        sidebar.add(btnPanel1);
        sidebar.add(btnPanel2);
        sidebar.add(btnLogout);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        panelContainer = new JPanel(cardLayout);
        panelContainer.add(new Panel1(), "Panel1");
        panelContainer.add(new Panel2(), "Panel2");

        // Button Actions
        btnPanel1.addActionListener(e -> cardLayout.show(panelContainer, "Panel1"));
        btnPanel2.addActionListener(e -> cardLayout.show(panelContainer, "Panel2"));
        btnLogout.addActionListener(e -> frame.switchTo("Login")); // Switch back to login

        add(sidebar, BorderLayout.WEST);
        add(panelContainer, BorderLayout.CENTER);
    }
}

//-------- Manager Main Panel with Sidebar --------
class ManagerMainPanel extends JPanel {
 private CardLayout cardLayout;
 private JPanel panelContainer;

 public ManagerMainPanel(user frame) {
     setLayout(new BorderLayout());

     // Sidebar
     JPanel sidebar = new JPanel();
     sidebar.setLayout(new GridLayout(3, 1));
     JButton btnPanel1 = new JButton("Panel 1");
     JButton btnPanel2 = new JButton("Panel 2");
     JButton btnLogout = new JButton("Logout");
     sidebar.add(btnPanel1);
     sidebar.add(btnPanel2);
     sidebar.add(btnLogout);

     // Content panel with CardLayout
     cardLayout = new CardLayout();
     panelContainer = new JPanel(cardLayout);
     panelContainer.add(new Panel1(), "Panel1");
     panelContainer.add(new Panel2(), "Panel2");

     // Button Actions
     btnPanel1.addActionListener(e -> cardLayout.show(panelContainer, "Panel1"));
     btnPanel2.addActionListener(e -> cardLayout.show(panelContainer, "Panel2"));
     btnLogout.addActionListener(e -> frame.switchTo("ManagerLogin")); // Switch back to login

     add(sidebar, BorderLayout.WEST);
     add(panelContainer, BorderLayout.CENTER);
 }
}

// Example Panel 1
class Panel1 extends JPanel {
    public Panel1() {
        setBackground(Color.RED);
        add(new JLabel("This is Panel 1"));
    }
}

// Example Panel 2
class Panel2 extends JPanel {
    public Panel2() {
        setBackground(Color.GREEN);
        add(new JLabel("This is Panel 2"));
    }
}
