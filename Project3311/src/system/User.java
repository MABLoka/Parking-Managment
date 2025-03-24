package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

// -------------------------- Main User Class --------------------------
public class User extends JFrame {
	private CardLayout cardLayout;
	private JPanel contentPanel;
	protected Client client;       // Assume Client is defined with a getParkingRate() method.
	protected Managers manager;    // Assume Managers is defined in your system package.
	public Parkinglot globalLot;   // Global Parkinglot instance.

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
		contentPanel.add(new ClientMainPanel(this), "ClientMain");
		contentPanel.add(new BookingPanel(this), "BookingPanel");
		contentPanel.add(new ManagerMainPanel(this), "ManagerMain");
		contentPanel.add(new RegtisterPanel(this), "ClientRegister");

		add(contentPanel);
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
						frame.switchTo("ClientMain");
						success = true;
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
		if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!email.contains("@") || !email.contains(".")) {
			JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (txtId.isVisible() && uniqueid.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Phone number is required!", "Error", JOptionPane.ERROR_MESSAGE);
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
			boolean nonVisitor = studentOpt.isSelected() || facultyOpt.isSelected() || staffOpt.isSelected();
			if (nonVisitor) {
				filePath = Paths.get(CSV_FILE).toAbsolutePath();
			} else {
				id = generateUserId();
				filePath = Paths.get(CSV_FILE2).toAbsolutePath();
			}
			boolean fileExists = Files.exists(filePath);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
				if (!fileExists) {
					writer.write("name,id,email,password,uniqueid,type\n");
				}
				writer.write(name + "," + id + "," + email + "," + password + "," +
						(uniqueid.isEmpty() ? "" : uniqueid) + "," + userType + "\n");
				JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.PLAIN_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error saving user!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private String generateUserId() {
		int lastId = 0;
		try {
			Path filePath = Paths.get(CSV_FILE2).toAbsolutePath();
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

// -------------------------- Manager Login Panel --------------------------
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
						frame.switchTo("ManagerMain");
						success = true;
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

// -------------------------- Client Main Panel with Sidebar --------------------------
class ClientMainPanel extends JPanel {
	private CardLayout cardLayout;
	private JPanel panelContainer;

	public ClientMainPanel(User frame) {
		setLayout(new BorderLayout());

		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(8, 1));
//		JButton btnPanel1 = new JButton("Client Panel 1");
		JButton btnPanel2 = new JButton("Client Panel 2");
		JButton btnPanel3 = new JButton("Client Panel 3");
		JButton btnPanel4 = new JButton("Client Panel 4");
		JButton btnPanel5 = new JButton("Client Panel 5");
		JButton btnPanel6 = new JButton("Client Panel 6");
		JButton btnBooking = new JButton("Booking");
		JButton btnLogout = new JButton("Logout");

		sidebar.add(btnBooking);
		sidebar.add(btnPanel2);
		sidebar.add(btnPanel3);
		sidebar.add(btnPanel4);
		sidebar.add(btnPanel5);
		sidebar.add(btnPanel6);
//		sidebar.add(btnBooking);
		sidebar.add(btnLogout);

		cardLayout = new CardLayout();
		panelContainer = new JPanel(cardLayout);
		panelContainer.add(new BookingPanel(frame), "BookingPanel");
		panelContainer.add(new ClientPanel2(), "ClientPanel2");
		panelContainer.add(new ClientPanel3(), "ClientPanel3");
		panelContainer.add(new ClientPanel4(), "ClientPanel4");
		panelContainer.add(new ClientPanel5(), "ClientPanel5");
		panelContainer.add(new ClientPanel6(), "ClientPanel6");

		btnBooking.addActionListener(e -> cardLayout.show(panelContainer, "BookingPanel"));
		btnPanel2.addActionListener(e -> cardLayout.show(panelContainer, "ClientPanel2"));
		btnPanel3.addActionListener(e -> cardLayout.show(panelContainer, "ClientPanel3"));
		btnPanel4.addActionListener(e -> cardLayout.show(panelContainer, "ClientPanel4"));
		btnPanel5.addActionListener(e -> cardLayout.show(panelContainer, "ClientPanel5"));
		btnPanel6.addActionListener(e -> cardLayout.show(panelContainer, "ClientPanel6"));
//		btnBooking.addActionListener(e -> frame.switchTo("BookingPanel"));
		btnLogout.addActionListener(e -> frame.switchTo("ClientLogin"));

		add(sidebar, BorderLayout.WEST);
		add(panelContainer, BorderLayout.CENTER);
	}
}

// -------------------------- Booking Panel (No "seconds" & Occupied in red) --------------------------
class BookingPanel extends JPanel {
	public BookingPanel(User frame) {
		
		Reader reader = null;
		Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
		try {
			reader = Files.newBufferedReader(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CSVReader lotsFile = new CSVReader(reader);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
		
		// Get the list of lots on data base
		ArrayList<Parkinglot> records = new ArrayList<Parkinglot>();
		String[] nextRecord;
		try {
			int recordnum = 0;
			lotsFile.readNext(); 
			while ((nextRecord = lotsFile.readNext()) != null) {
		
				records.add(new Parkinglot(Integer.parseInt(nextRecord[0]), nextRecord[1], nextRecord[2], true, 100));
				for(int i = 3; i < 104; i++) {
					records.get(recordnum).addSpace(new Parkingspace( i-3 , nextRecord[i].equals("Y") ? true : false));
				}
				recordnum++;
			}
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// 1) License Plate input
		JLabel lblLicense = new JLabel("License Plate:");
		JTextField txtLicense = new JTextField(15);

		// 2) Location drop-down
		
		JLabel lblLocation = new JLabel("Location:");
		String[] locations = new String[records.size()];
		for(int i = 1; i < records.size(); i++) {
			locations[i] = records.get(i).getLocation();
		}
		
		JComboBox<String> comboLocation = new JComboBox<>(locations);
		
		// 3) Space ID drop-down (with custom renderer for red if occupied)
		JLabel lblSpaceId = new JLabel("Space ID:");
		JComboBox<Integer> comboSpaceId = new JComboBox<>();
		
		comboLocation.addActionListener(e -> {
			
			int lotid = comboLocation.getSelectedIndex();
			// Populate combo with **all** spaces
			for (Parkingspace ps : records.get(lotid).getSpaces()) {
				comboSpaceId.addItem(ps.getId());
			}

			// This custom renderer highlights occupied spaces in red
			comboSpaceId.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(
						JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
	
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
					if (value instanceof Integer) {
						int spaceId = (Integer) value;
						Parkingspace ps = null;
						for (Parkingspace s : records.get(lotid).getSpaces()) {
							if (s.getId() == spaceId) {
								ps = s;
								break;
							}
						}
						if (ps != null && !ps.isAvailable()) {
							// Occupied => Red background
							if (isSelected) {
								setBackground(Color.RED.darker());
							} else {
								setBackground(Color.RED);
							}
						} else {
							// Available => normal background
							if (isSelected) {
								setBackground(UIManager.getColor("List.selectionBackground"));
							} else {
								setBackground(UIManager.getColor("List.background"));
							}
						}
					}
					return this;
				}
			});
		});

		
		// 4) Start Time: Only hour & minute spinners (NO seconds).
		LocalDateTime now = LocalDateTime.now();
		int currentHr = now.getHour();
		int currentMin = now.getMinute();

		JLabel lblStartTime = new JLabel("Start Time:");
		JLabel lblHr = new JLabel("Hr:");
		JSpinner spinnerStartHr = new JSpinner(new SpinnerNumberModel(currentHr, 0, 23, 1));
		JLabel lblMin = new JLabel("Min:");
		JSpinner spinnerStartMin = new JSpinner(new SpinnerNumberModel(currentMin, 0, 59, 1));

		// 5) Parking Duration (hours)
		JLabel lblDuration = new JLabel("Parking Duration (hour):");
		JSpinner spinnerDuration = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));

		JButton btnBook = new JButton("Book Space");
		JButton btnBack = new JButton("Back");

		// Layout
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; add(lblSpaceId, gbc);
		gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; add(comboSpaceId, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; add(lblLicense, gbc);
		gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; add(txtLicense, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; add(lblLocation, gbc);
		gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; add(comboLocation, gbc);

		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; add(lblStartTime, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; add(lblHr, gbc);
		gbc.gridx = 1; gbc.gridy = 4; add(spinnerStartHr, gbc);
		gbc.gridx = 2; gbc.gridy = 4; add(lblMin, gbc);
		gbc.gridx = 3; gbc.gridy = 4; add(spinnerStartMin, gbc);

		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; add(lblDuration, gbc);
		gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 3; add(spinnerDuration, gbc);

		gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; add(btnBook, gbc);
		gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; add(btnBack, gbc);

		// 6) Book button logic
		btnBook.addActionListener(e -> {
			// Must be logged in
			if (frame.client == null) {
				JOptionPane.showMessageDialog(this, "Please log in first.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Validate license plate
			String license = txtLicense.getText().trim();
			if (license.isEmpty()) {
				JOptionPane.showMessageDialog(this, "License Plate must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Integer spaceId = (Integer) comboSpaceId.getSelectedItem();
			if (spaceId == null) {
				JOptionPane.showMessageDialog(this, "No parking space selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Find that space in the global lot
			Parkingspace targetSpace = null;
			for (Parkingspace s : frame.globalLot.getSpaces()) {
				if (s.getId() == spaceId) {
					targetSpace = s;
					break;
				}
			}
			if (targetSpace == null) {
				JOptionPane.showMessageDialog(this, "Space not found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!targetSpace.isAvailable()) {
				JOptionPane.showMessageDialog(this, "Space is occupied!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				int hrVal = (Integer) spinnerStartHr.getValue();
				int minVal = (Integer) spinnerStartMin.getValue();
				// no seconds
				int secVal = 0;

				int startTime = hrVal * 3600 + minVal * 60 + secVal;
				int duration = (Integer) spinnerDuration.getValue();
				int endTime = startTime + duration * 3600;

				String bookingLocation = (String) comboLocation.getSelectedItem();
				int bookingId = getNextBookingId();
				double clientRate = frame.client.getParkingRate();

				// Create booking
				Booking booking = new Booking(
						bookingId,
						frame.client.getId(),
						clientRate,
						3.0,    // licence rate if needed
						license,
						bookingLocation,
						startTime,
						endTime,
						new Booking.ConcreteClient(frame.client.getName(), frame.client.getId())
				);
				Booking.BookingAdapter adapter = new Booking.BookingAdapterImpl();
				boolean booked = adapter.bookParkingSpace(frame.globalLot, spaceId, booking);
				if (booked) {
					LocalTime startLocal = LocalTime.ofSecondOfDay(startTime);
					LocalTime endLocal   = LocalTime.ofSecondOfDay(endTime);

					// Write to CSV
					Path bookingPath = Paths.get("src", "system", "resources", "bookings.csv").toAbsolutePath();
					boolean bookingFileExists = Files.exists(bookingPath);
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookingPath.toFile(), true))) {
						// No "sec" in time UI, so no change needed for actual times
						if (!bookingFileExists) {
							writer.write("BookingId,ClientId,ClientRate,LicencePlate,Location,StartingTime,EndTime,Date,Cost\n");
						}
						String date = java.time.LocalDate.now().toString();
						writer.write(
								booking.getBookingId() + "," +
										frame.client.getId() + "," +
										clientRate + "," +
										booking.getLicensePlate() + "," +
										bookingLocation + "," +
										startLocal.toString() + "," +
										endLocal.toString() + "," +
										date + "," +
										booking.getCost() + "\n"
						);
					}
					JOptionPane.showMessageDialog(this, "Booking successful and saved!");
				} else {
					JOptionPane.showMessageDialog(this, "Booking failed!");
				}
			} catch (NumberFormatException | IOException ex1) {
				JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnBack.addActionListener(e -> frame.switchTo("ClientMain"));
	}

	// Auto-generate next booking ID
	private int getNextBookingId() {
		int lastId = 0;
		Path bookingPath = Paths.get("src", "system", "resources", "bookings.csv").toAbsolutePath();
		if (Files.exists(bookingPath)) {
			try (BufferedReader reader = new BufferedReader(new FileReader(bookingPath.toFile()))) {
				String line;
				boolean isHeader = true;
				while ((line = reader.readLine()) != null) {
					if (isHeader) {
						isHeader = false;
						continue;
					}
					String[] parts = line.split(",");
					if (parts.length > 0) {
						try {
							int id = Integer.parseInt(parts[0]);
							if (id > lastId) {
								lastId = id;
							}
						} catch (NumberFormatException ex) {
							// skip invalid
						}
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return lastId + 1;
	}
}

// -------------------------- Example Client Panels --------------------------
class ClientPanel1 extends JPanel {
	public ClientPanel1() {
		setBackground(Color.RED);
		add(new JLabel("This is Client Panel 1"));
	}
}
class ClientPanel2 extends JPanel {
	public ClientPanel2() {
		setBackground(Color.GREEN);
		add(new JLabel("This is Client Panel 2"));
	}
}
class ClientPanel3 extends JPanel {
	public ClientPanel3() {
		setBackground(Color.BLUE);
		add(new JLabel("This is Client Panel 3"));
	}
}
class ClientPanel4 extends JPanel {
	public ClientPanel4() {
		setBackground(Color.YELLOW);
		add(new JLabel("This is Client Panel 4"));
	}
}
class ClientPanel5 extends JPanel {
	public ClientPanel5() {
		setBackground(Color.PINK);
		add(new JLabel("This is Client Panel 5"));
	}
}
class ClientPanel6 extends JPanel {
	public ClientPanel6() {
		setBackground(Color.ORANGE);
		add(new JLabel("This is Client Panel 6"));
	}
}

// -------------------------- Manager Main Panel with Sidebar --------------------------
class ManagerMainPanel extends JPanel {
	private CardLayout cardLayout;
	private JPanel panelContainer;

	public ManagerMainPanel(User frame) {
		setLayout(new BorderLayout());

		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(7, 1));
		JButton btnPanel1 = new JButton("Panel 1");
		JButton btnPanel2 = new JButton("Panel 2");
		JButton btnPanel3 = new JButton("Panel 3");
		JButton btnPanel4 = new JButton("Panel 4");
		JButton btnPanel5 = new JButton("Panel 5");
		JButton btnPanel6 = new JButton("Panel 6");
		JButton btnLogout = new JButton("Logout");

		sidebar.add(btnPanel1);
		sidebar.add(btnPanel2);
		sidebar.add(btnPanel3);
		sidebar.add(btnPanel4);
		sidebar.add(btnPanel5);
		sidebar.add(btnPanel6);
		sidebar.add(btnLogout);

		cardLayout = new CardLayout();
		panelContainer = new JPanel(cardLayout);
		panelContainer.add(new Panel1(), "Panel1");
		panelContainer.add(new Panel2(), "Panel2");
		panelContainer.add(new Panel3(), "Panel3");
		panelContainer.add(new Panel4(), "Panel4");
		panelContainer.add(new Panel5(), "Panel5");
		panelContainer.add(new Panel6(), "Panel6");

		btnPanel1.addActionListener(e -> cardLayout.show(panelContainer, "Panel1"));
		btnPanel2.addActionListener(e -> cardLayout.show(panelContainer, "Panel2"));
		btnPanel3.addActionListener(e -> cardLayout.show(panelContainer, "Panel3"));
		btnPanel4.addActionListener(e -> cardLayout.show(panelContainer, "Panel4"));
		btnPanel5.addActionListener(e -> cardLayout.show(panelContainer, "Panel5"));
		btnPanel6.addActionListener(e -> cardLayout.show(panelContainer, "Panel6"));
		btnLogout.addActionListener(e -> frame.switchTo("ManagerLogin"));

		add(sidebar, BorderLayout.WEST);
		add(panelContainer, BorderLayout.CENTER);
	}
}
class Panel1 extends JPanel {
	public Panel1() {
		setBackground(Color.RED);
		add(new JLabel("This is Panel 1"));
	}
}
class Panel2 extends JPanel {
	public Panel2() {
		setBackground(Color.GREEN);
		add(new JLabel("This is Panel 2"));
	}
}
class Panel3 extends JPanel {
	public Panel3() {
		setBackground(Color.BLUE);
		add(new JLabel("This is Panel 3"));
	}
}
class Panel4 extends JPanel {
	public Panel4() {
		setBackground(Color.YELLOW);
		add(new JLabel("This is Panel 4"));
	}
}
class Panel5 extends JPanel {
	public Panel5() {
		setBackground(Color.PINK);
		add(new JLabel("This is Panel 5"));
	}
}
class Panel6 extends JPanel {
	public Panel6() {
		setBackground(Color.ORANGE);
		add(new JLabel("This is Panel 6"));
	}
}
