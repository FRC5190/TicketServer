package net.viperfish.ticketClient;

import java.awt.Color;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class MainWindow implements Window {
	private DockedWindow dockedWindow;

	private JFrame frmMainWindow;

	private JButton btnConnect;
	private JButton btnDone;
	private JButton btnGetticket;
	private JButton btnDock;
	private JLabel lblMyTicketValue;
	private JLabel lblCurrentTicketValue;
	private JTextField txtIp;
	private JTextField txtName;

	public MainWindow(TicketClient ticketClient) {
		Display display = Display.getInstance();
		ClientProperties properties = ClientProperties.getInstance();
		frmMainWindow = new JFrame();
		frmMainWindow.setType(Type.UTILITY);
		frmMainWindow.setTitle("5190 Ticket Server");
		frmMainWindow.setBackground(Color.YELLOW);
		frmMainWindow.setForeground(Color.RED);
		int x = properties.getInt("window.main.position.x", 100);
		int y = properties.getInt("window.main.position.y", 100);
		int width = properties.getInt("window.main.width", 320);
		int height = properties.getInt("window.main.height", 220);
		frmMainWindow.setBounds(x, y, width, height);
		frmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setOpaque(true);
		frmMainWindow.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][][grow][][]",
				"[][][][][][][][][][]"));

		JLabel lblIp = new JLabel("IP");
		contentPane.add(lblIp, "cell 1 0");

		txtIp = new JTextField(10);
		txtIp.setText(properties.getString("ip", ""));
		contentPane.add(txtIp, "cell 2 0,growx");

		btnConnect = new JButton("Connect");
		btnConnect.setToolTipText("Connect to the server at the ip address");
		btnConnect.setForeground(Color.white);
		btnConnect.setBackground(Color.darkGray);
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = txtIp.getText();
				String name = txtName.getText();
				saveProperties();
				try {
					ticketClient.connect(ip, name);
				} catch (TicketException ex) {
					txtIp.setText(ex.getMessage());
				}
				btnConnect.setEnabled(false);
				btnDock.setEnabled(true);
			}

		});
		contentPane.add(btnConnect, "cell 4 0 2 1");

		JLabel lblName = new JLabel("Name");
		contentPane.add(lblName, "cell 1 1");

		txtName = new JTextField(10);
		txtName.setText(properties.getString("name", ""));
		contentPane.add(txtName, "cell 2 1,growx");

		JLabel lblMyTicket = new JLabel("Ticket");
		contentPane.add(lblMyTicket, "cell 1 2");
		lblMyTicketValue = new JLabel();
		lblMyTicketValue.setText(display.get("ticket", "---"));
		lblMyTicketValue.setBackground(Color.YELLOW);
		contentPane.add(lblMyTicketValue, "cell 2 2,growx");

		JLabel lblCurrentTicket = new JLabel("Current Ticket");
		contentPane.add(lblCurrentTicket, "cell 1 3");
		lblCurrentTicketValue = new JLabel();
		lblCurrentTicketValue.setText(display.get("currentTicket", "---"));
		contentPane.add(lblCurrentTicketValue, "cell 2 3,growx");

		btnGetticket = new JButton("GetTicket");
		btnGetticket.setToolTipText("Get a ticket");
		btnGetticket.setForeground(Color.white);
		btnGetticket.setBackground(Color.darkGray);
		btnGetticket.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ticketClient.getTicket();
					updateDisplay();
				} catch (TicketException ex) {
					JOptionPane.showMessageDialog(null, ex);
				}
				btnGetticket.setEnabled(false);
			}

		});
		contentPane.add(btnGetticket, "cell 4 3");

		btnDone = new JButton("Done");
		btnDone.setToolTipText("Finished with ticket");
		btnDone.setForeground(Color.white);
		btnDone.setBackground(Color.darkGray);
		btnDone.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ticketClient.done();
				} catch (TicketException ex) {
					JOptionPane.showMessageDialog(null, ex);
				}
				btnGetticket.setEnabled(true);
				lblMyTicketValue.setText("---");
				lblCurrentTicketValue.setText("---");
			}

		});
		contentPane.add(btnDone, "cell 4 7");
		btnDone.setEnabled(false);

		btnDock = new JButton("Dock");
		btnDock.setForeground(Color.white);
		btnDock.setBackground(Color.darkGray);
		btnDock.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
				if (dockedWindow == null) {
					dockedWindow = new DockedWindow(ticketClient,
							MainWindow.this);
				}
				ticketClient.setCurrentWindow(dockedWindow);
				dockedWindow.updateDisplay();
				dockedWindow.show();
			}
		});
		btnDock.setEnabled(false);
		contentPane.add(btnDock, "cell 4 8");
	}

	@Override
	public void show() {
		frmMainWindow.setVisible(true);
	}

	@Override
	public void hide() {
		frmMainWindow.setVisible(false);
	}

	@Override
	public void updateDisplay() {
		System.out.println("DISPLAY UPDATED");
		Display display = Display.getInstance();
		String ticket = display.get("ticket");
		if (ticket != null) {
			lblMyTicketValue.setText(ticket);
			btnDone.setEnabled(true);
		}
		lblCurrentTicketValue.setText(display.get("currentTicket", "---"));
		String error = display.get("error");
		if (error != null) {
			JOptionPane.showMessageDialog(null, error);
			display.remove("error");
		}
	}

	private void saveProperties() {
		ClientProperties properties = ClientProperties.getInstance();
		properties.put("ip", txtIp.getText());
		properties.put("name", txtName.getText());
		properties.put("window.main.position.x", frmMainWindow.getX());
		properties.put("window.main.position.y", frmMainWindow.getY());
		properties.put("window.main.width", frmMainWindow.getWidth());
		properties.put("window.main.height", frmMainWindow.getHeight());
	}
}
