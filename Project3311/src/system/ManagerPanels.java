package system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import system.ParkingLotsPanel.CustomListRenderer;

public class ManagerPanels {

}

//-------------------------- Manager Main Panel with Sidebar --------------------------
class ManagerMainPanel extends JPanel {
	private CardLayout cardLayout;
	private JPanel panelContainer;

	public ManagerMainPanel(User frame) {
		setLayout(new BorderLayout());

		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(7, 1));
		JButton btnAddLotPanel = new JButton("addLotPanel");
		JButton btnGenerateManagerPanel = new JButton("GenerateManagerPanel");
		JButton btnEditLotSpace = new JButton("EditLotSpacePanel");
		JButton btnVerifyClients = new JButton("verifyPanel");

		JButton btnPanel5 = new JButton("Panel 5");
		JButton btnPanel6 = new JButton("Panel 6");
		JButton btnLogout = new JButton("Logout");

		sidebar.add(btnAddLotPanel);
		sidebar.add(btnGenerateManagerPanel);
		sidebar.add(btnEditLotSpace);
		sidebar.add(btnVerifyClients);
		sidebar.add(btnPanel5);
		sidebar.add(btnPanel6);
		sidebar.add(btnLogout);

		cardLayout = new CardLayout();
		panelContainer = new JPanel(cardLayout);
		panelContainer.add(new addLotPanel(frame), "addLotPanel");
		panelContainer.add(new GenerateManagerPanel(frame), "GenerateManagerPanel");
		panelContainer.add(new EditLotSpacePanel(frame), "EditLotSpacePanel");
		panelContainer.add(new verifyPanel(frame), "verifyPanel");
		panelContainer.add(new Panel5(), "Panel5");
		panelContainer.add(new Panel6(), "Panel6");

		btnAddLotPanel.addActionListener(e -> cardLayout.show(panelContainer, "addLotPanel"));
		btnGenerateManagerPanel.addActionListener(e -> cardLayout.show(panelContainer, "GenerateManagerPanel"));
		btnEditLotSpace.addActionListener(e -> cardLayout.show(panelContainer, "EditLotSpacePanel"));
		btnVerifyClients.addActionListener(e -> cardLayout.show(panelContainer, "verifyPanel"));
		btnPanel5.addActionListener(e -> cardLayout.show(panelContainer, "Panel5"));
		btnPanel6.addActionListener(e -> cardLayout.show(panelContainer, "Panel6"));
		btnLogout.addActionListener(e -> frame.switchTo("ManagerLogin"));

		add(sidebar, BorderLayout.WEST);
		add(panelContainer, BorderLayout.CENTER);
	}
}


//add Lot Panel
class addLotPanel extends JPanel {
	private JTextField nameField;
	private JTextField locationField;
	private JComboBox<String> statusComboBox;
	
	public addLotPanel(User frame) {
		
		Reader reader = null;
		Path parkingLotPath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
		try {
			reader = Files.newBufferedReader(parkingLotPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CSVReader lotsFile = new CSVReader(reader);
		
		setLayout(new GridLayout(4, 2, 5, 5));
	
	    // Name Label & Field
	    add(new JLabel("Name:"));
	    nameField = new JTextField(15);
	    add(nameField);
	
	    // Location Label & Field
	    add(new JLabel("Location:"));
	    locationField = new JTextField(15);
	    add(locationField);
	
	    // Status Selection (Enabled/Disabled)
	    add(new JLabel("Status:"));
	    String[] statusOptions = {"Enabled", "Disabled"};
	    statusComboBox = new JComboBox<>(statusOptions);
	    add(statusComboBox);
	
	    // Submit Button
	    JButton submitButton = new JButton("Submit");
	    add(submitButton);
	
	    // Label to display stored values
	    JLabel resultLabel = new JLabel("");
	    add(resultLabel);
	
	    // Button Click Event
	    submitButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	// Write to CSV
	        	Path parkingLotPath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
				boolean parkingLotFileExists = Files.exists(parkingLotPath);
				if(nameField.getText().equals(null) || locationField.getText().equals(null)) {
					JOptionPane.showMessageDialog(addLotPanel.this, "Please fill name/location");
				} else {
					try (CSVWriter writer = new CSVWriter(new FileWriter(parkingLotPath.toFile(), true))) {
						String date = java.time.LocalDate.now().toString();
						String[] row = new String[104]; // Adjust array size based on column count
		                
		                row[0] = generateLotId();         // ID
		                row[1] = nameField.getText();              // Name
		                row[2] = locationField.getText();
		                row[3] = ((String) statusComboBox.getSelectedItem()).equals("Enabled") ? "Y" : "N";
		                for(int i = 4; i < 100+4; i++) {
		                	row[i] = ((String) statusComboBox.getSelectedItem()).equals("Enabled") ? "Y" : "N";
		                }
						writer.writeNext(row);
						JOptionPane.showMessageDialog(addLotPanel.this, "New lot successfully added!");
		
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(addLotPanel.this, "Adding a parking failed!");
						e1.printStackTrace();
					}
				}
				
	        }
	    });
	}
	
	private String generateLotId() {
		int lastId = 0;
		try {
			Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
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
							System.out.println(columns[0]);
							int parsedId = Integer.parseInt(columns[0].replace("\"", ""));
							System.out.print(parsedId);
							lastId = Math.max(lastId, parsedId);
							System.out.print(lastId);
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

//Generate Manager Panel
class GenerateManagerPanel extends JPanel {
	public GenerateManagerPanel(User frame) {
	    // Setting the layout to center the button
	    setLayout(new BorderLayout());
	
	    // Create the "Generate Manager" button
	    JButton generateButton = new JButton("Generate Manager");
	
	    // Add the button to the panel
	    add(generateButton, BorderLayout.CENTER);
	    
	    
	    // Add ActionListener to handle button click
	    generateButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // Show message after button click
	        	if(frame.manager instanceof SuperManager) {
	        		String[] info = ((SuperManager) frame.manager).generateManager();
	        		JOptionPane.showMessageDialog(GenerateManagerPanel.this, "Manager Generated!\n" + "Name: " + info[0] + "\n" + "Password: " + info[1]);
	        	} else {
	        		JOptionPane.showMessageDialog(GenerateManagerPanel.this, "Unauthorized!");
	        	}
	            
	        }
	    });
	}

}

class EditLotSpacePanel extends JPanel {
	private JTable table;
    private DefaultTableModel tableModel;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private ArrayList<Parkinglot> lots;
    private int selectedLotId = -1; // Store selected lot ID
    private int selectedSpaceIndex = -1; // Store selected space index
    
    public EditLotSpacePanel(User frame) {
        setLayout(new BorderLayout());

        // Create table with default model
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Location", "Status"});

        // Load CSV data
        try {
            lots = loadCSVData();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < lots.size(); i++) {
        	tableModel.addRow(new String[]{ String.valueOf(lots.get(i).getId()), lots.get(i).getName(), lots.get(i).getLocation() , lots.get(i).isState() ? "Enabled" : "Disabled"});
        }

        // Create list for parking spaces
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new CustomListRenderer());
        JScrollPane listScroll = new JScrollPane(list);

        // Event listener for table row selection (selects a lot)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedLotId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    updateList(selectedLotId-1);
                }
            }
        });

        // Event listener for space selection (selects a parking space)
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedSpaceIndex = list.getSelectedIndex();
            }
        });

        // Create buttons
        JButton disableLotButton = new JButton("Disable Lot");
        JButton enableLotButton = new JButton("Enable Lot");
        JButton disableSpaceButton = new JButton("Disable Space");
        JButton enableSpaceButton = new JButton("Enable Space");

        // Add action listeners for buttons
        disableLotButton.addActionListener(e -> {
        	changeLotStatus(false);
        	try {
				updateFile(selectedLotId);
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	});
        enableLotButton.addActionListener(e -> {
        	changeLotStatus(true);
        	try {
				updateFile(selectedLotId);
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	});
        disableSpaceButton.addActionListener(e -> {
        	changeSpaceStatus(false);
        	try {
				updateFile(selectedLotId);
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	});
        enableSpaceButton.addActionListener(e -> {
        	changeSpaceStatus(true);
        	try {
				updateFile(selectedLotId);
			} catch (CsvValidationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	});

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttonPanel.add(enableLotButton);
        buttonPanel.add(disableLotButton);
        buttonPanel.add(enableSpaceButton);
        buttonPanel.add(disableSpaceButton);

        // Add components to the main panel
        add(tableScroll, BorderLayout.WEST);
        add(listScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Load data from CSV
    private ArrayList<Parkinglot> loadCSVData() throws CsvValidationException {
        Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
        ArrayList<Parkinglot> lots = new ArrayList<>();
        int recordNum = 0;
        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextLine;
            boolean isHeader = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Parkinglot lot = new Parkinglot(Integer.parseInt(nextLine[0]), nextLine[1], nextLine[2], nextLine[3].equals("Y") , 100);
                for (int i = 4; i < 104; i++) {
                    lot.addSpace(new Parkingspace(i - 3, nextLine[i].equals("Y")));
                }
                lots.add(lot);
                recordNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lots;
    }
    
    // Update table with lots data
    private void updateFile(int id) throws CsvValidationException {
    	Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
 		ArrayList<String[]> filelots = new ArrayList<>();

 	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
 	        String[] nextLine;

 	        while ((nextLine = reader.readNext()) != null) {
 	            
 	        	filelots.add(nextLine);

 	        }
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
 	    
 	   for (int i = 1; i < filelots.size(); i++) { // Skip header row (i = 1)
 	        int rowLotId = Integer.parseInt(filelots.get(i)[0]); // Get lot ID from CSV
 	        if (rowLotId == id) { // Match the correct row
 	            Parkinglot lot = lots.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
 	            if (lot == null) return; // If no lot found, exit

 	            // Update lot status
 	           filelots.get(i)[3] = lot.isState() ? "Y" : "N";

 	            // Update parking spaces
 	            for (int j = 4; j < 104; j++) {
 	            	filelots.get(i)[j] = lot.getSpaces().get(j - 4).isAvailable() ? "Y" : "N";
 	            }
 	            break; // Exit loop after updating the correct row
 	        }
 	   }
 	    
 	    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
 	    	writer.writeAll(filelots);
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
    }

    // Change lot status
    private void changeLotStatus(boolean enable) {
        if (selectedLotId == -1) {
            JOptionPane.showMessageDialog(this, "Select a lot first.");
            return;
        }
        for (Parkinglot lot : lots) {
            if (lot.getId() == selectedLotId) {
                lot.setState(enable);
                for(Parkingspace space: lot.getSpaces() ) {
                	if(enable)
                		space.enable();
                	else
                		space.disable();
                }
                return;
            }
        }
    }

    // Change space status
    private void changeSpaceStatus(boolean enable) {
        if (selectedLotId == -1 || selectedSpaceIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a space first.");
            return;
        }
        for (Parkinglot lot : lots) {
            if (lot.getId() == selectedLotId) {
            	if(enable)
            		lot.getSpaces().get(selectedSpaceIndex).enable();
            	else
            		lot.getSpaces().get(selectedSpaceIndex).disable();
                return;
            }
        }
    }

    // Custom list renderer for coloring items
    public static class CustomListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (text.endsWith("Available")) {
                label.setBackground(Color.GREEN);
            } else {
                label.setBackground(Color.RED);
            }
            label.setOpaque(true);
            return label;
        }
    }
    
    // Update list based on selected row ID
 	private void updateList(int id) {
 	    listModel.clear();
 	
 	    // Dummy data - Replace with actual logic for fetching related data
 	    ArrayList<Parkingspace> relatedData = lots.get(id).getSpaces();
 	    for (Parkingspace data : relatedData) {
 	        listModel.addElement(data.getId() + " - " + (data.isEnabled() ? "Available" : "Occupied") );  // Name + Status
 	    }
 	}
	
}


class verifyPanel extends JPanel {
	private JTable table;
    private int id;
    private ArrayList<Client> clients;

    public verifyPanel(User frame) {
        setLayout(new GridLayout(1, 2));

        // Create table with default model
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        // Add columns to table
        tableModel.setColumnIdentifiers(new String[]{"Name", "Id", "Email", "Password", "Unique_id", "Type"});

        // Load CSV data (assuming loadCSVData() is defined elsewhere)
        try {
        	clients = loadCSVData(frame);
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < clients.size(); i++) {
            tableModel.addRow(new String[]{clients.get(i).name, 
            		String.valueOf(clients.get(i).id), 
            		clients.get(i).email, 
            		clients.get(i).password, 
            		(clients.get(i).getUniqueId() != 0) ? String.valueOf(clients.get(i).getUniqueId()) : "",  
            		clients.get(i).type
            		});
        }

        // Add event listener for table row selection
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    id = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
                }
            }
        });

        // Create the "Pay" button
        JButton payVerify = new JButton("Verify");
        payVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the selected booking ID
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());

                    // Show the payment options dialog
                    try {
						verifyClient(id, frame);
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            }
        });

        // Layout for the button
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(payVerify);

        // Add components to the panel
        add(tableScroll, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    
	

	//Load data from CSV file
	private ArrayList<Client>  loadCSVData(User frame) throws CsvValidationException {
		
		
        
		Path filePath = Paths.get("src", "system", "resources", "verify.csv").toAbsolutePath();
		ArrayList<Client> clients = new ArrayList<>();
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            if (isHeader) {  // Skip header row
	                isHeader = false;
	                continue;
	            }

	            clients.add(clientFactory.getClientType(nextLine[5], nextLine[0], Integer.parseInt(nextLine[1]), nextLine[2], nextLine[3], Integer.parseInt(nextLine[4])));
	            
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return clients;
	}
	
	// Update list based on selected row ID
		private void verifyClient(int id, User frame) throws CsvValidationException {
			Path filePath = Paths.get("src", "system", "resources", "verify.csv").toAbsolutePath();
			ArrayList<String[]> toVerify = new ArrayList<>();
			ArrayList<String[]> verified = new ArrayList<>();
		    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
		        String[] nextLine;
		        boolean isHeader = true;
		        while ((nextLine = reader.readNext()) != null) {
		            
		        	toVerify.add(nextLine);

		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		    for(int i = 1; i < toVerify.size(); i++) {
		    	if(id == Integer.parseInt(toVerify.get(i)[1])) {
		    		verified.add(toVerify.get(i));
		    		toVerify.remove(i);
		    	} else {

		    	}
		    }
		    
		    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
		    	writer.writeAll(toVerify);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		    filePath = Paths.get("src", "system", "resources", "users.csv").toAbsolutePath();

		    
		    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile(), true))) {
		    	String newId = generateUserId();
		    	verified.get(0)[1] = newId;
		    	writer.writeNext(verified.get(0));
				JOptionPane.showMessageDialog(this, "verification successful!", "Success", JOptionPane.PLAIN_MESSAGE);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		private String generateUserId() {
			int lastId = 0;
			try {
				 Path filePath = Paths.get("src", "system", "resources", "users.csv").toAbsolutePath();
 
				
				if (Files.exists(filePath)) {
					try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
						String line;
						boolean isHeader = true;
						while ((line = reader.readLine()) != null) {
							
							if (isHeader) {
								isHeader = false;
								continue;
							}
							
							line = line.trim();  // Remove leading/trailing spaces

						    if (line.isEmpty()) {
						        break; // Skip completely empty lines
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
