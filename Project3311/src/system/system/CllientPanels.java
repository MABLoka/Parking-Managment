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

public class CllientPanels {
	
}


//-------------------------- Client Main Panel with Sidebar --------------------------
class ClientMainPanel extends JPanel {
	private CardLayout cardLayout;
	private JPanel panelContainer;
	private PanelSwitcher panelSwitch;
	
	public ClientMainPanel(User frame) {
		setLayout(new BorderLayout());

		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(8, 1));
//		JButton btnPanel1 = new JButton("Client Panel 1");
		JButton btnBooking = new JButton("Booking");
		JButton btnView = new JButton("Parking Lots");
		JButton btnEdit = new JButton("Edit Booking");
		
		JButton btnPayment = new JButton("Payment Panel");
		JButton btnPanel5 = new JButton("Client Panel 5");
		JButton btnPanel6 = new JButton("Client Panel 6");
		
		JButton btnLogout = new JButton("Logout");

		sidebar.add(btnBooking);
		sidebar.add(btnView);
		sidebar.add(btnEdit);
		sidebar.add(btnPayment);
		sidebar.add(btnPanel5);
		sidebar.add(btnPanel6);
//		sidebar.add(btnBooking);
		sidebar.add(btnLogout);

		cardLayout = new CardLayout();
		panelContainer = new JPanel(cardLayout);
		panelContainer.add(new BookingPanel(frame), "BookingPanel");
		panelContainer.add(new ParkingLotsPanel(frame), "ParkingLotsPanel");
		panelContainer.add(new EditBookingPanel(frame), "EditBookingPanel");
		panelContainer.add(new PaymentPanel(frame), "PaymentPanel");
		panelContainer.add(new ClientPanel5(), "ClientPanel5");
		panelContainer.add(new ClientPanel6(), "ClientPanel6");
		
		// Initialize the bridge with the actual switching implementation
        panelSwitch = new ConcretePanelSwitcher(cardLayout, panelContainer, frame);
        
        // Button actions to switch panels using the bridge
        btnBooking.addActionListener(e -> panelSwitch.switchTo("BookingPanel"));
        btnView.addActionListener(e -> panelSwitch.switchTo("ParkingLotsPanel"));
        btnEdit.addActionListener(e -> panelSwitch.switchTo("EditBookingPanel"));
        btnPayment.addActionListener(e -> panelSwitch.switchTo("PaymentPanel"));
        btnPanel5.addActionListener(e -> panelSwitch.switchTo("ClientPanel5"));
        btnPanel6.addActionListener(e -> panelSwitch.switchTo("ClientPanel6"));
        btnLogout.addActionListener(e -> frame.switchTo("ClientLogin"));

		add(sidebar, BorderLayout.WEST);
		add(panelContainer, BorderLayout.CENTER);
	}
	
	public void done() {
		panelContainer.revalidate();
		panelContainer.repaint();
    }
}


interface PanelSwitcher {
    void switchTo(String panelName);
}

class ConcretePanelSwitcher implements PanelSwitcher {
    private CardLayout cardLayout;
    private JPanel panelContainer;
    private User frame;

    public ConcretePanelSwitcher(CardLayout cardLayout, JPanel panelContainer, User frame) {
        this.cardLayout = cardLayout;
        this.panelContainer = panelContainer;
        this.frame = frame;
    }

    @Override
    public void switchTo(String panelName) {
        // Remove all existing panels from the container to reinitialize
        panelContainer.removeAll();
        
        // Add the new panel (reinitializing it)
        switch (panelName) {
            case "BookingPanel":
                panelContainer.add(new BookingPanel(frame), panelName);
                break;
            case "ParkingLotsPanel":
                panelContainer.add(new ParkingLotsPanel(frame), panelName);
                break;
            case "EditBookingPanel":
                panelContainer.add(new EditBookingPanel(frame), panelName);
                break;
            case "PaymentPanel":
                panelContainer.add(new PaymentPanel(frame), panelName);
                break;
            case "ClientPanel5":
                panelContainer.add(new ClientPanel5(), panelName);
                break;
            case "ClientPanel6":
                panelContainer.add(new ClientPanel6(), panelName);
                break;
            default:
                throw new IllegalArgumentException("Invalid panel name");
        }

        // After adding the new panel, trigger a re-layout and re-paint
        panelContainer.revalidate();
        panelContainer.repaint();

        // Switch to the new panel
        cardLayout.show(panelContainer, panelName);
    }

}

//-------------------------- Booking Panel (No "seconds" & Occupied in red) --------------------------
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
		ArrayList<Parkinglot> lots = new ArrayList<Parkinglot>();
		String[] nextRecord;
		try {
			int recordnum = 0;
			lotsFile.readNext(); 
			while ((nextRecord = lotsFile.readNext()) != null) {
		
				lots.add(new Parkinglot(Integer.parseInt(nextRecord[0]), nextRecord[1], nextRecord[2], true, 100));
				for(int i = 4; i < 104; i++) {
					lots.get(recordnum).addSpace(new Parkingspace( i-3 , nextRecord[i].equals("Y") ? true : false));
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
		String[] locations = new String[lots.size()];
		for(int i = 0; i < lots.size(); i++) {
			locations[i] = lots.get(i).getLocation();
		}
		
		JComboBox<String> comboLocation = new JComboBox<>(locations);
		
		// 3) Space ID drop-down (with custom renderer for red if occupied)
		JLabel lblSpaceId = new JLabel("Space ID:");
		JComboBox<Integer> comboSpaceId = new JComboBox<>();
		
		comboLocation.addActionListener(e -> {
			
			int lotid = comboLocation.getSelectedIndex();
			// Populate combo with **all** spaces
			for (Parkingspace ps : lots.get(lotid).getSpaces()) {
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
						for (Parkingspace s : lots.get(lotid).getSpaces()) {
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
			int lotid = comboLocation.getSelectedIndex();
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
			for (Parkingspace s : lots.get(lotid).getSpaces()) {
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
				String date = java.time.LocalDate.now().toString();
				Booking booking = new Booking(
						bookingId,
						lotid + "-" + spaceId,
						frame.client.getId(),
						clientRate,
						license,
						bookingLocation,
						startTime,
						endTime,
						date,
						new Booking.ConcreteClient(frame.client.getName(), frame.client.getId())
				);
				Booking.BookingAdapter adapter = new Booking.BookingAdapterImpl();
				boolean booked = adapter.bookParkingSpace(lots.get(lotid), spaceId, booking);
				if (booked) {
					LocalTime startLocal = LocalTime.ofSecondOfDay(startTime);
					LocalTime endLocal   = LocalTime.ofSecondOfDay(endTime);

					// Write to CSV
					Path bookingPath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
					boolean bookingFileExists = Files.exists(bookingPath);
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookingPath.toFile(), true))) {
						// No "sec" in time UI, so no change needed for actual times
						if (!bookingFileExists) {
							writer.write("BookingId,SpaceId,ClientId,ClientRate,LicencePlate,Location,StartingTime,EndTime,Date,Cost\n");
						}
						
						writer.write(
								booking.getBookingId() + "," +
										"'" + lots.get(lotid).getId() + "-" + targetSpace.getId() + "," +
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
		Path bookingPath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
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

//-------------------------- View lots Panel (No "seconds" & Occupied in red) --------------------------
class ParkingLotsPanel extends JPanel {
	private JTable table;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private ArrayList<Parkinglot> lots ;
    
    
	public ParkingLotsPanel(User frame) {
		
		Reader reader = null;
		Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
		try {
			reader = Files.newBufferedReader(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CSVReader lotsFile = new CSVReader(reader);
		
		setLayout(new GridLayout(1, 2));

        // Create table with default model
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        // Add columns to table
        tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Location", "Status"});

        // Load CSV data
        try {
			lots = loadCSVData();
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for(int i = 0; i < lots.size(); i++) {
        	tableModel.addRow(new String[]{ String.valueOf(lots.get(i).getId()), lots.get(i).getName(), lots.get(i).getLocation() , lots.get(i).isState() ? "Enabled" : "Disabled"});
        }
        
        // Create list with a custom renderer
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new CustomListRenderer());
        JScrollPane listScroll = new JScrollPane(list);

        // Add event listener for table row selection
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    updateList(id-1);
                }
            }
        });
        
        add(tableScroll);
        add(listScroll);
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

	//Load data from CSV file
	private ArrayList<Parkinglot>  loadCSVData() throws CsvValidationException {
		Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
		ArrayList<Parkinglot> lots = new ArrayList<>();
		int recordnum = 0;
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            if (isHeader) {  // Skip header row
	                isHeader = false;
	                continue;
	            }
	            lots.add(new Parkinglot(Integer.parseInt(nextLine[0]), nextLine[1], nextLine[2], true, 100));
				for(int i = 4; i < 104; i++) {
					lots.get(recordnum).addSpace(new Parkingspace( i-3 , nextLine[i].equals("Y") ? true : false));
				}
				recordnum++;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return lots;
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

//-------------------------- Edit Booking Panel (No "seconds" & Occupied in red) --------------------------
class EditBookingPanel extends JPanel {
	private JTable table;
	private int id;
  private DefaultListModel<String> listModel;
  private JList<String> list;
  private ArrayList<Booking> bookings ;	
  //Define input format
  DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
	public EditBookingPanel(User frame) {
		
		setLayout(new GridLayout(1, 2));

      // Create table with default model
      DefaultTableModel tableModel = new DefaultTableModel();
      table = new JTable(tableModel);
      JScrollPane tableScroll = new JScrollPane(table);

      // Add columns to table
      tableModel.setColumnIdentifiers(new String[]{"BookingID", "SpaceId", "ClientID", "Location", "StartTime", "EndTime", "Date", "Cost"});

      // Load CSV data
      try {
    	  bookings = loadCSVData(frame);
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
      for(int i = 0; i < bookings.size(); i++) {
      	tableModel.addRow(new String[]{ String.valueOf(bookings.get(i).getBookingId()), bookings.get(i).getSpaceId(), 
      			String.valueOf(bookings.get(i).getClientId()), bookings.get(i).getLocation(), 
      			LocalTime.ofSecondOfDay(bookings.get(i).getStartTime()).toString(), LocalTime.ofSecondOfDay(bookings.get(i).getEndTime()).toString(),
      			bookings.get(i).getDate(), String.valueOf(bookings.get(i).getCost())});
      }
      
      

      // Add event listener for table row selection
      table.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
              int selectedRow = table.getSelectedRow();
              if (selectedRow != -1) {
                  id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
              }
          }
      });
      
   // Add a Spinner for selecting hours
      JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));  // Default value 1 hour, min 1, max 24
      hourSpinner.setPreferredSize(new Dimension(100, 30));

      // Create the "Extend" button
      JButton extendButton = new JButton("Extend");
      extendButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              int selectedRow = table.getSelectedRow();
              if (selectedRow != -1) {
                  int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                  int hoursToExtend = (int) hourSpinner.getValue();
                  try {
					extendBooking(id, hoursToExtend, frame);
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                  JOptionPane.showMessageDialog(EditBookingPanel.this, "Booking Extended.");
              }
          }
      });

      // Create the "Cancel" button
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // Handle cancel action
        	  int selectedRow = table.getSelectedRow();
        	  if (selectedRow != -1) {
                  int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                  int hoursToExtend = (int) hourSpinner.getValue();
                  try {
					cancelBooking(id, frame);
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
              }
              JOptionPane.showMessageDialog(EditBookingPanel.this, "Booking canceled.");
          }
      });

      // Layout for the spinner and buttons
      JPanel controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());
      controlPanel.add(new JLabel("Hours to Extend:"));
      controlPanel.add(hourSpinner);
      controlPanel.add(extendButton);
      controlPanel.add(cancelButton);

      add(tableScroll, BorderLayout.CENTER);
      add(controlPanel, BorderLayout.SOUTH);

	}
	

	//Load data from CSV file
	private ArrayList<Booking>  loadCSVData(User frame) throws CsvValidationException {
		
		
        
		Path filePath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
		ArrayList<Booking> bookings = new ArrayList<>();
		int recordnum = 0;
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            if (isHeader) {  // Skip header row
	                isHeader = false;
	                continue;
	            }
	            if(frame.client.getId() == Integer.parseInt(nextLine[2]) ) {
	            	

	            	// Parse the time
	                LocalTime timeStart = LocalTime.parse(nextLine[6], inputFormatter);
	                LocalTime timeEnd = LocalTime.parse(nextLine[7], inputFormatter);
	                
	                int timeStartInt = timeStart.getHour() * 3600 + timeStart.getMinute() * 60 + 0;
	                int timeEndInt = timeEnd.getHour() * 3600 + timeEnd.getMinute() * 60;
	                
	            	bookings.add(new Booking( Integer.parseInt(nextLine[0]), nextLine[1], Integer.parseInt(nextLine[2]), Double.parseDouble(nextLine[3]), 
	            			nextLine[4], nextLine[5], timeStartInt, timeEndInt, nextLine[8]));
	            	System.out.print(nextLine[9]);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return bookings;
	}
	
	// Update list based on selected row ID
	private void extendBooking(int id, int hours, User frame) throws CsvValidationException {
		Path filePath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
		ArrayList<String[]> bookings = new ArrayList<>();
		int recordnum = 0;
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            
	            bookings.add(nextLine);

	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    for(int i = 1; i < bookings.size(); i++) {
	    	if(id == Integer.parseInt(bookings.get(i)[0])) {

                LocalTime timeEnd = LocalTime.parse(bookings.get(i)[7], inputFormatter);
                int timeEndHours = timeEnd.getHour();
                int timeEndMinutes = timeEnd.getMinute();
                // Add hours
                timeEndHours += hours;
                // Handle 24-hour format overflow (if hours exceed 24)
                if (timeEndHours >= 24) {
                	timeEndHours -= 24;
                }

                int timeEndInt = timeEndHours * 3600 + timeEndMinutes * 60 + 0;
                LocalTime newEndLocal = LocalTime.ofSecondOfDay(timeEndInt);
	    		bookings.get(i)[7] = newEndLocal.toString();
	    		
	    		int newCost = Integer.parseInt(bookings.get(i)[3])*hours + Integer.parseInt(bookings.get(i)[9]);
	    		bookings.get(i)[9] = String.valueOf(newCost);
	    	}
	    }
	    
	    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
	    	writer.writeAll(bookings);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	// Update list based on selected row ID
	private void cancelBooking(int id, User frame) throws CsvValidationException {
		Path filePath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
		ArrayList<String[]> bookings = new ArrayList<>();
		int recordnum = 0;
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            
	            bookings.add(nextLine);

	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    for(int i = 1; i < bookings.size(); i++) {
	    	if(id == Integer.parseInt(bookings.get(i)[0])) {
	    		bookings.remove(i);
	    	}
	    }
	    
	    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
	    	writer.writeAll(bookings);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}


class PaymentPanel extends JPanel {
	private JTable table;
    private int id;
    private ArrayList<Booking> bookings;
    private DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");

    public PaymentPanel(User frame) {
        setLayout(new GridLayout(1, 2));

        // Create table with default model
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        // Add columns to table
        tableModel.setColumnIdentifiers(new String[]{"BookingID", "SpaceId", "ClientID", "Location", "Date", "Cost"});

        // Load CSV data (assuming loadCSVData() is defined elsewhere)
        try {
            bookings = loadCSVData(frame);
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < bookings.size(); i++) {
            tableModel.addRow(new String[]{
                String.valueOf(bookings.get(i).getBookingId()), bookings.get(i).getSpaceId(),
                String.valueOf(bookings.get(i).getClientId()), bookings.get(i).getLocation(),
                bookings.get(i).getDate(), String.valueOf(bookings.get(i).getCost())
            });
        }

        // Add event listener for table row selection
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                }
            }
        });

        // Create the "Pay" button
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the selected booking ID
                    int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                    // Show the payment options dialog
                    showPaymentOptions(id, frame);
                }
            }
        });

        // Layout for the button
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(payButton);

        // Add components to the panel
        add(tableScroll, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    // Method to show payment options dialog
    private void showPaymentOptions(int id, User frame) {
        // Create a new dialog for payment options
        JDialog paymentDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Payment Options", true);
        paymentDialog.setSize(300, 200); // Set dialog size
        paymentDialog.setLocationRelativeTo(this); // Center the dialog
        paymentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Add the PaymentOptionsPanel to the dialog
        paymentDialog.add(new PaymentOptionsPanel(id, frame));
        paymentDialog.setVisible(true); // Make the dialog visible
    }
	

	//Load data from CSV file
	private ArrayList<Booking>  loadCSVData(User frame) throws CsvValidationException {
		
		
        
		Path filePath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
		ArrayList<Booking> bookings = new ArrayList<>();
		int recordnum = 0;
	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
	        String[] nextLine;
	        boolean isHeader = true;
	        while ((nextLine = reader.readNext()) != null) {
	            if (isHeader) {  // Skip header row
	                isHeader = false;
	                continue;
	            }
	            if(frame.client.getId() == Integer.parseInt(nextLine[2]) ) {
	            	
	            	// Parse the time
	                LocalTime timeStart = LocalTime.parse(nextLine[6], inputFormatter);
	                LocalTime timeEnd = LocalTime.parse(nextLine[7], inputFormatter);
	                
	                int timeStartInt = timeStart.getHour() * 3600 + timeStart.getMinute() * 60 + 0;
	                int timeEndInt = timeEnd.getHour() * 3600 + timeEnd.getMinute() * 60 + 0;
	                
	            	bookings.add(new Booking(Integer.parseInt(nextLine[0]), nextLine[1], Integer.parseInt(nextLine[2]), Double.parseDouble(nextLine[3]), 
	            			nextLine[4], nextLine[5], timeStartInt, timeEndInt, nextLine[8]));
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return bookings;
	}
	
	
}

class PaymentOptionsPanel extends JPanel {
    public PaymentOptionsPanel(int id, User frame) {
        setLayout(new GridLayout(3, 1)); // Layout with 3 options

        // Payment option buttons
        JButton creditCardButton = new JButton("Credit Card");
        JButton paypalButton = new JButton("PayPal");
        JButton cashButton = new JButton("Cash");

     // Action listener for Credit Card payment
        creditCardButton.addActionListener(e -> {
            String cardNumber = JOptionPane.showInputDialog(this, "Enter Credit Card Number:");
            if (cardNumber != null && !cardNumber.isEmpty()) {
            	try {
					cancelBooking(id, frame);
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(this, "Payment successful with Credit Card!");
            } else {
                JOptionPane.showMessageDialog(this, "Credit Card payment canceled.");
            }
        });

        // Action listener for PayPal payment
        paypalButton.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(this, "Enter PayPal Email:");
            if (email != null && !email.isEmpty()) {
            	try {
					cancelBooking(id, frame);
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(this, "Payment successful via PayPal!");
            } else {
                JOptionPane.showMessageDialog(this, "PayPal payment canceled.");
            }
        });

        // Action listener for Cash payment
        cashButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Confirm Cash Payment?", "Cash Payment", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
            	try {
					cancelBooking(id, frame);
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(this, "Cash payment confirmed!");
            } else {
                JOptionPane.showMessageDialog(this, "Cash payment canceled.");
            }
        });

        // Add buttons to panel
        add(creditCardButton);
        add(paypalButton);
        add(cashButton);
    }
    
    // Update list based on selected row ID
 	private void cancelBooking(int id, User frame) throws CsvValidationException {
 		Path filePath = Paths.get("src", "system", "resources", "Bookings.csv").toAbsolutePath();
 		ArrayList<String[]> bookings = new ArrayList<>();
 		int recordnum = 0;
 	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
 	        String[] nextLine;
 	        boolean isHeader = true;
 	        while ((nextLine = reader.readNext()) != null) {
 	            
 	            bookings.add(nextLine);

 	        }
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
 	    
 	    for(int i = 1; i < bookings.size(); i++) {
 	    	if(id == Integer.parseInt(bookings.get(i)[0])) {
 	    		bookings.remove(i);
 	    	}
 	    }
 	    
 	    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
 	    	writer.writeAll(bookings);
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
 	}
}
//-------------------------- Example Client Panels --------------------------
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