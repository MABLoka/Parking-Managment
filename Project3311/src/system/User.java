package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

// -------------------------- Main User Class --------------------------
public class User extends JFrame implements LoginObserver{
	private CardLayout cardLayout;
	private JPanel contentPanel;
	protected Client client;       // Assume Client is defined with a getParkingRate() method.
	protected Managers manager;    // Assume Managers is defined in your system package.
	public Parkinglot globalLot;   // Global Parkinglot instance.
	
	private List<LoginObserver> observers = new ArrayList<>();

    public void addObserver(LoginObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LoginObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (LoginObserver observer : observers) {
            observer.onLoginSuccess();  // Notify all observers when login is successful
        }
    }

    public void login() {
        
        
            System.out.println("Login Successful!");
            notifyObservers();  // Notify observers on success
      
    }

	public User() throws FileNotFoundException {
		setTitle("Swing Multi-Panel Template");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		
		
		// Create a global Parkinglot with ID 1 and 100 spaces.
		
//		globalLot = new Parkinglot(1, 100);

		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);

		// Add panels (card names must match when switching)
		contentPanel.add(new ClientLoginPanel(this), "ClientLogin");
		contentPanel.add(new ManagerLoginPanel(this), "ManagerLogin");
		contentPanel.add(new RegtisterPanel(this), "ClientRegister");

		add(contentPanel);
	}

	@Override
    public void onLoginSuccess() {
        // Update the UI when login is successful (e.g., switch to the "ClientMain" panel)
		if(this.client != null)
			contentPanel.add(new ClientMainPanel(this), "ClientMain");
		else
			contentPanel.add(new ManagerMainPanel(this), "ManagerMain");
    }
	public void switchTo(String panelName) {
		cardLayout.show(contentPanel, panelName);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new User().setVisible(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}
}

// -------------------------- Client Login Panel --------------------------
class ClientLoginPanel extends JPanel {
	private CSVReader usersFile;
	boolean success = false;

	public ClientLoginPanel(User frame) {
		Reader reader = null;
		Path filePath = Paths.get("src", "system", "resources", "users.csv").toAbsolutePath();
		try {
			reader = Files.newBufferedReader(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		usersFile = new CSVReader(reader);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);

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
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(btnLogin, gbc);
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(btnRegister, gbc);
		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; add(btnManage, gbc);

		btnLogin.addActionListener(e -> {
			// Basic login read
			String email = txtUser.getText().trim();
			String password = new String(txtPass.getPassword());
			String[] nextRecord;
			try {
				while ((nextRecord = usersFile.readNext()) != null) {
					if (email.equals(nextRecord[2].trim()) && password.equals(nextRecord[3].trim())) {
						int id = nextRecord[1].isEmpty() ? 0 : Integer.parseInt(nextRecord[1].trim());
						int uniqueId = nextRecord[4].isEmpty() ? 0 : Integer.parseInt(nextRecord[4].trim());
						frame.client = clientFactory.getClientType(
								nextRecord[5].trim().toUpperCase(),
								nextRecord[0].trim(),
								id,
								nextRecord[2].trim(),
								nextRecord[3].trim(),
								uniqueId
						);
						
						success = true;
						frame.onLoginSuccess();
						frame.switchTo("ClientMain");
						break;
					}
				}
				if (!success) {
					JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (CsvValidationException | IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error reading user data.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnManage.addActionListener(e -> frame.switchTo("ManagerLogin"));
		btnRegister.addActionListener(e -> frame.switchTo("ClientRegister"));
	}
}

// -------------------------- Register Panel --------------------------
class RegtisterPanel extends JPanel {
	private CSVReader usersFile;
	boolean success = false;
	private JRadioButton studentOpt;
	private JRadioButton facultyOpt;
	private JRadioButton staffOpt;
	private JRadioButton noneOpt;

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
		gbc.insets = new Insets(5,5,5,5);

		JButton btnLogin = new JButton("Login");
		JButton btnSignup = new JButton("Signup");
		JButton btnManage = new JButton("Manager");
		studentOpt = new JRadioButton("Student");
		facultyOpt = new JRadioButton("Faculy-member");
		staffOpt = new JRadioButton("Staff");
		noneOpt = new JRadioButton("None");
		ButtonGroup group = new ButtonGroup();
		group.add(studentOpt);
		group.add(facultyOpt);
		group.add(staffOpt);
		group.add(noneOpt);

		lblId.setVisible(false);
		txtId.setVisible(false);

		gbc.insets = new Insets(5,10,5,10);
		gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
		gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
		gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
		gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
		gbc.gridx = 0; gbc.gridy = 2; add(lblEmail, gbc);
		gbc.gridx = 1; gbc.gridy = 2; add(txtEmail, gbc);
		gbc.gridx = 0; gbc.gridy = 3; add(lblId, gbc);
		gbc.gridx = 1; gbc.gridy = 3; add(txtId, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; add(btnSignup, gbc);
		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; add(btnLogin, gbc);
		gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; add(btnManage, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 0; gbc.gridy = 7; add(studentOpt, gbc);
		gbc.gridx = 1; gbc.gridy = 7; add(facultyOpt, gbc);
		gbc.gridx = 0; gbc.gridy = 8; add(staffOpt, gbc);
		gbc.gridx = 1; gbc.gridy = 8; add(noneOpt, gbc);

		ItemListener selectionListener = e -> {
			if (studentOpt.isSelected()) {
				lblId.setText("Student ID:");
				lblId.setVisible(true);
				txtId.setVisible(true);
			} else if (facultyOpt.isSelected()) {
				lblId.setText("Faculty ID:");
				lblId.setVisible(true);
				txtId.setVisible(true);
			} else if (staffOpt.isSelected()) {
				lblId.setText("Staff ID:");
				lblId.setVisible(true);
				txtId.setVisible(true);
			} else if (noneOpt.isSelected()) {
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

		btnSignup.addActionListener(e -> registerUser());
		btnLogin.addActionListener(e -> frame.switchTo("ClientLogin"));
		btnManage.addActionListener(e -> frame.switchTo("ManagerLogin"));
	}
	
	

	private void registerUser() {
		String name = txtUser.getText().trim();
		String password = new String(txtPass.getPassword()).trim();
		String email = txtEmail.getText().trim();
		String uniqueid = txtId.getText().trim();
		String id = "";
		boolean nonVisitor = studentOpt.isSelected() || facultyOpt.isSelected() || staffOpt.isSelected();
		if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!email.contains("@") || !email.contains(".")) {
			JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!validEmail(email)) {
			JOptionPane.showMessageDialog(this, "Email is already Registered/waiting for verification!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!isStrongPassword(password)) {
			JOptionPane.showMessageDialog(this, "Weak!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (txtId.isVisible() && uniqueid.isEmpty()) {
			JOptionPane.showMessageDialog(this, "uniqueid is required!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String userType = "VISITOR";
		if (studentOpt.isSelected()) {
			userType = "STUDENT";
		} else if (facultyOpt.isSelected()) {
			userType = "FACULTY";
		} else if (staffOpt.isSelected()) {
			userType = "STAFF";
		}
		try {
			Path filePath = null;
			
			if (nonVisitor) {
				filePath = Paths.get(CSV_FILE).toAbsolutePath();
			} else {
				filePath = Paths.get(CSV_FILE2).toAbsolutePath();
			}
			boolean fileExists = Files.exists(filePath);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
				if (!fileExists) {
					writer.write("name,id,email,password,uniqueid,type\n");
				}
				id = generateUserId(nonVisitor);
				writer.write(name + "," + id + "," + email + "," + password + "," +
						(uniqueid.isEmpty() ? "" : uniqueid) + "," + userType + "\n");
				JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.PLAIN_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error saving user!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public static boolean isStrongPassword(String password) {
        if (password.length() < 8) return false; // Check length

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if ("!@#$%^&*()-_+=<>?/{}[]".contains(String.valueOf(ch))) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
	
	private boolean validEmail(String email) {
		try {		
			Path filePath = Paths.get(CSV_FILE).toAbsolutePath();
			
			if (Files.exists(filePath)) {
				try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
					String line;
					boolean isHeader = true;
					while ((line = reader.readLine()) != null) {
						String[] columns = line.split(",");
						// Ensure at least 3 columns exist before checking
	                    if (columns.length < 3) {
	                    	return true; // Skip invalid or empty rows
	                    }

	                    if (columns[2].trim().equals(email)) {
	                        return false;
	                    }
					}
				}
			}
			
			filePath = Paths.get(CSV_FILE2).toAbsolutePath();
			if (Files.exists(filePath)) {
				try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
					String line;
					boolean isHeader = true;
					while ((line = reader.readLine()) != null) {
						String[] columns = line.split(",");
						// Ensure at least 3 columns exist before checking
	                    if (columns.length < 3) {
	                    	return true; // Skip invalid or empty rows
	                    }

	                    if (columns[2].trim().equals(email)) {
	                        return false;
	                    }
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private String generateUserId(boolean verifyFile) {
		int lastId = 0;
		try {
			Path filePath = null;
			if (verifyFile) {
				filePath = Paths.get(CSV_FILE).toAbsolutePath();
			} else {
				filePath = Paths.get(CSV_FILE2).toAbsolutePath();
			}
			
			if (Files.exists(filePath)) {
				try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
					String line;
					boolean isHeader = true;
					while ((line = reader.readLine()) != null) {
						
						if (isHeader) {
							isHeader = false;
							continue;
						}
					    
						String[] columns = line.split(",");
						try {
							int parsedId = Integer.parseInt(columns[1]);
							lastId = Math.max(lastId, parsedId);
						} catch (NumberFormatException ex) {
							continue;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.valueOf(lastId + 1);
	}
}




//-------------------------- Manager Login Panel --------------------------
class ManagerLoginPanel extends JPanel {
	private CSVReader usersFile;
	boolean success = false;
	public ManagerLoginPanel(User frame) {
		Reader reader = null;
		Path filePath = Paths.get("src", "system", "resources", "managers.csv").toAbsolutePath();
		try {
			reader = Files.newBufferedReader(filePath);
			usersFile = new CSVReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);

		JLabel lblUser = new JLabel("Name:");
		JTextField txtUser = new JTextField(15);
		JLabel lblPass = new JLabel("Password:");
		JPasswordField txtPass = new JPasswordField(15);
		JButton btnLogin = new JButton("Login");
		JButton btnClient = new JButton("Client");

		gbc.gridx = 0; gbc.gridy = 0; add(lblUser, gbc);
		gbc.gridx = 1; gbc.gridy = 0; add(txtUser, gbc);
		gbc.gridx = 0; gbc.gridy = 1; add(lblPass, gbc);
		gbc.gridx = 1; gbc.gridy = 1; add(txtPass, gbc);
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(btnLogin, gbc);
		gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2; add(btnClient, gbc);

		btnLogin.addActionListener(e -> {
			if (usersFile == null) {
				JOptionPane.showMessageDialog(this,
						"Could not read managers.csv. Check file path and try again.",
						"CSV Error",
						JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			String name = txtUser.getText();
			String password = new String(txtPass.getPassword());
			String[] nextRecord;
			try {
				while ((nextRecord = usersFile.readNext()) != null) {
					if(name.equals(nextRecord[0]) && password.equals(nextRecord[3])) {
						if(nextRecord[4].equals("Y")) {
							frame.manager = SuperManager.getInstance();
						} else {
							frame.manager = Manager.getInstance(
									Integer.parseInt(nextRecord[1]),
									nextRecord[0],
									nextRecord[2]
							);
						}
						
						success = true;
						frame.onLoginSuccess();
						frame.switchTo("ManagerMain");
						break;
					}
				}
				if (!success) {
					JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (CsvValidationException | IOException e1) {
				e1.printStackTrace();
			}
		});

		btnClient.addActionListener(e -> frame.switchTo("ClientLogin"));
	}
	
	
}




