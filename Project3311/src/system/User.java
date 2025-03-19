package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class User extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    protected Client client;
    protected Managers manager;
    
    public User() throws FileNotFoundException {
    	
        setTitle("Swing Multi-Panel Template");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Adding different panels
        contentPanel.add(new ClientLoginPanel(this), "ClientLogin");
        contentPanel.add(new ManagerLoginPanel(this), "ManagerLogin");
        contentPanel.add(new ClientMainPanel(this), "ClientMain");
        contentPanel.add(new ManagerMainPanel(this), "ManagerMain");
        contentPanel.add(new RegtisterPanel(this), "ClientRegister");
        

        add(contentPanel);
    }

    // Switch between panels
    public void switchTo(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				new User().setVisible(true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
    }
}

// -------- Login Panel --------
class ClientLoginPanel extends JPanel {
	private CSVReader  usersFile;
	boolean success = false;
	
    public ClientLoginPanel(User frame) {
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
        JTextField txtUser = new JTextField(30);
        JLabel lblPass = new JLabel("Password:");
        JPasswordField txtPass = new JPasswordField(30);
        JButton btnLogin = new JButton("Login");
        JButton btnManage = new JButton("Manager");
        JButton btnRegister = new JButton("Register");
        
        gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnLogin, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnRegister, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnManage, gbc);
        
        // Login Button Action
        btnLogin.addActionListener(e -> {
            String email = txtUser.getText();
            String password = new String(txtPass.getPassword());
            
            String[] nextRecord;
            try {
				while ((nextRecord = usersFile.readNext()) != null) {
					if(email.equals(nextRecord[2]) && password.equals(nextRecord[3])) {
						frame.client = clientFactory.getClientType(nextRecord[5], nextRecord[0], Integer.parseInt(nextRecord[1]), 
								nextRecord[2], nextRecord[3], Integer.parseInt(nextRecord[4]));
						
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
        
        //Change to Register
        btnRegister.addActionListener(e -> {
        
			frame.switchTo("ClientRegister");
			
        });
        
    }
}

//-------- Register Panel --------
class RegtisterPanel extends JPanel {
	private CSVReader  usersFile;
	boolean success = false;
	private JRadioButton  studentOpt;
	private JRadioButton  facultyOpt;
	private JRadioButton  staffOpt;
	private JRadioButton  noneOpt;
	
	JLabel lblUser = new JLabel("*Name:");
    JTextField txtUser = new JTextField(30);
    JLabel lblPass = new JLabel("*Password:");
    JPasswordField txtPass = new JPasswordField(30);
    JLabel lblEmail = new JLabel("*Email:");
    JTextField txtEmail = new JTextField(30);
    JLabel lblId = new JLabel("*Id:");
    JTextField txtId = new JTextField(30);
    String CSV_FILE = "src/system/resources/verify.csv"; 
    String CSV_FILE2 = "src/system/resources/users.csv";
    
	public RegtisterPanel(User frame) {
	 	
		 
	 	
	     setLayout(new GridBagLayout());
	     GridBagConstraints gbc = new GridBagConstraints();
	     gbc.insets = new Insets(5, 5, 5, 5);
	     
	     
	     
	     JButton btnLogin = new JButton("Login");
	     JButton btnSignup = new JButton("Signup");
	     JButton btnManage = new JButton("Manager");
	     studentOpt = new JRadioButton("Student");
	     facultyOpt = new JRadioButton("Faculy-member");
	     staffOpt = new JRadioButton("Staff");
	     noneOpt = new JRadioButton("None");
	     // Group the radio buttons to allow only one selection
		 ButtonGroup group = new ButtonGroup();
		 group.add(studentOpt);
		 group.add(facultyOpt);
		 group.add(staffOpt);
		 group.add(noneOpt);
		 
	     lblId.setVisible(false);
	     txtId.setVisible(false);
	     
	     gbc.insets = new Insets(5, 10, 5, 10); // Add horizontal spacing
	     gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
	     gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
	     gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
	     gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
	     gbc.gridx = 0; gbc.gridy = 2; add(lblEmail, gbc);
	     gbc.gridx = 1; gbc.gridy = 2; add(txtEmail, gbc);
	     gbc.gridx = 0; gbc.gridy = 3; add(lblId, gbc);
	     gbc.gridx = 1; gbc.gridy = 3; add(txtId, gbc);
	     
	     
	     gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
	     add(btnSignup, gbc);
	     gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
	     add(btnLogin, gbc);
	     gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
	     add(btnManage, gbc);
	     gbc.gridwidth = 1;
	     gbc.gridx = 0; gbc.gridy = 7;
	     add(studentOpt, gbc); 
	     gbc.gridx = 1; gbc.gridy = 7;
	     add(facultyOpt, gbc); 
	     
	     gbc.gridx = 0; gbc.gridy = 8;
	     add(staffOpt, gbc); 
	     gbc.gridx = 1; gbc.gridy = 8;
	     add(noneOpt, gbc); 
	     
	     
	     ItemListener selectionListener = e -> {
	    	    if (studentOpt.isSelected()) {
	    	        System.out.println("Student option selected");
	    	        lblId.setText("Student ID:");
	    	        lblId.setVisible(true);
	    	        txtId.setVisible(true);
	    	    } else if (facultyOpt.isSelected()) {
	    	        System.out.println("Faculty option selected");
	    	        lblId.setText("Faculty ID:");
	    	        lblId.setVisible(true);
	    	        txtId.setVisible(true);
	    	    } else if (staffOpt.isSelected()) {
	    	        System.out.println("Staff option selected");
	    	        lblId.setText("Staff ID:");
	    	        lblId.setVisible(true);
	    	        txtId.setVisible(true);
	    	    } else if (noneOpt.isSelected()) {
	    	        System.out.println("None option selected");
	    	        lblId.setText("ID:");
	    	        txtId.setText(null);
	    	        lblId.setVisible(false);
	    	        txtId.setVisible(false);
	    	    }
	    	    
	    	};
	     
    	 studentOpt.addItemListener(selectionListener);
    	 facultyOpt.addItemListener(selectionListener);
    	 staffOpt.addItemListener(selectionListener);
    	 noneOpt.addItemListener(selectionListener);
	     
	     // Login Button Action
	     btnSignup.addActionListener(e -> {
	    	 registerUser();
	     });
	     
	     //Change to Login
	     btnLogin.addActionListener(e -> {
	     
				frame.switchTo("ClientLogin");
				
	     });
	     //Change to Manager Login
	     btnManage.addActionListener(e -> {
	     
				frame.switchTo("ManagerLogin");
				
	     });
	 }
	 
	private void registerUser() {
	        String name = txtUser.getText().trim();
	        String password = new String(txtPass.getPassword()).trim();
	        String email = txtEmail.getText().trim();
	        String uniqueid = txtId.getText().trim();  // Get the phone number if visible 
	        String id = "";
	        
	        // Basic validation
	        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
	        	JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	        if (!email.contains("@") || !email.contains(".")) {
	        	JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        

	        // Optional validation for phone number if the field is visible
	        if (txtId.isVisible() && uniqueid.isEmpty()) {
	        	JOptionPane.showMessageDialog(this, "Phone number is required!", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        // Write to CSV file
	        try {
	        	Path filePath = null;
	        	boolean nonVisitor = studentOpt.isSelected() | facultyOpt.isSelected() | staffOpt.isSelected();
	        	if(nonVisitor) {
	        		filePath = Paths.get(CSV_FILE).toAbsolutePath();
	        	}
	        	else {
	        		id = generateUserId();
	        		filePath = Paths.get(CSV_FILE2).toAbsolutePath();
	        	}

	            // Create the file if it does not exist
	            boolean fileExists = Files.exists(filePath);
	            try (BufferedWriter writer = new BufferedWriter(new FileWriter((nonVisitor ? CSV_FILE: CSV_FILE2), true))) {
	                if (!fileExists) {
	                    writer.write("name,id,Email,password,uniqueid\n"); // CSV Header with Phone
	                }
	                // Write user data including id if available
	                writer.write(name + "," + id + "," + email + "," +  password+ "," + (uniqueid.isEmpty() ? "" : uniqueid) + "\n");
	                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.PLAIN_MESSAGE);
	                
	            }
	        } catch (IOException e) {
	        	JOptionPane.showMessageDialog(this, "Error saving user!", "Error", JOptionPane.ERROR_MESSAGE);
	        
	            e.printStackTrace();
	        }
	 }
	 // Method to generate a new unique User ID
	private String generateUserId() {
		    int lastId = 0;
		    
		    try {
		        Path filePath = Paths.get(CSV_FILE2).toAbsolutePath(); // Use users.csv for IDs
		        
		        // Read the existing file
		        if (Files.exists(filePath)) {
		            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE2))) {
		                String line;
		                boolean isHeader = true;  // Flag to skip the header row
		                while ((line = reader.readLine()) != null) {
		                    // Skip the header line
		                    if (isHeader) {
		                        isHeader = false;
		                        continue;
		                    }

		                    String[] columns = line.split(",");
		                    try {
		                        int id = Integer.parseInt(columns[1]); // Now the ID is in the second column
		                        lastId = Math.max(lastId, id); // Get the maximum ID
		                    } catch (NumberFormatException e) {
		                        // Skip any rows where the ID is not a valid integer
		                        continue;
		                    }
		                }
		            }
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		    // Return the next ID as a string
		    return String.valueOf(lastId + 1);
		}

}

//-------- Manager Login Panel --------
class ManagerLoginPanel extends JPanel {
	private CSVReader  usersFile;
	boolean success = false;
	public ManagerLoginPanel(User frame) {
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
	         String id = txtUser.getText();
	         String password = new String(txtPass.getPassword());
	         
	         String[] nextRecord;
	         try {
					while ((nextRecord = usersFile.readNext()) != null) {
						if(id.equals(nextRecord[1]) && password.equals(nextRecord[3])) {
							if(nextRecord[4].equals("Y")) {
								frame.manager = SuperManager.getInstance();
							} else {
								
							}
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
	     
				frame.switchTo("ClientLogin");
				
	     });
	}
}

// -------- Client Main Panel with Sidebar --------
class ClientMainPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel panelContainer;

    public ClientMainPanel(User frame) {
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
        btnLogout.addActionListener(e -> frame.switchTo("ClientLogin")); // Switch back to login

        add(sidebar, BorderLayout.WEST);
        add(panelContainer, BorderLayout.CENTER);
    }
}


//-------- Manager Main Panel with Sidebar --------
class ManagerMainPanel extends JPanel {
 private CardLayout cardLayout;
 private JPanel panelContainer;

 public ManagerMainPanel(User frame) {
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
