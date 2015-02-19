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

import net.miginfocom.swing.MigLayout;

public class DockedWindow implements Window {
	private JFrame frmDockedWindow;

	private JButton btnDone;
	private JButton btnGetticket;
	private JButton btnUndock;
	private JLabel lblMyTicketValue;
	private JLabel lblCurrentTicketValue;

	public DockedWindow(final TicketClient ticketClient,
			final MainWindow mainWindow) {
		Display display = Display.getInstance();
		ClientProperties properties = ClientProperties.getInstance();
		frmDockedWindow = new JFrame();
		frmDockedWindow.setType(Type.UTILITY);
		frmDockedWindow.setTitle("5190 Ticket Server");
		frmDockedWindow.setAlwaysOnTop(true);
		frmDockedWindow.setUndecorated(true);
		frmDockedWindow.setBackground(Color.YELLOW);
		frmDockedWindow.setForeground(Color.RED);
		int x = properties.getInt("window.docked.position.x", 400);
		int y = properties.getInt("window.docked.position.y", 0);
		frmDockedWindow.setBounds(x, y, 450, 40);
		frmDockedWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		MoveMouseListener moveMouseListener = new MoveMouseListener(contentPane);
		contentPane.addMouseListener(moveMouseListener);
		contentPane.addMouseMotionListener(moveMouseListener);
		frmDockedWindow.setContentPane(contentPane);

		contentPane.setLayout(new MigLayout("", "[][grow][][grow][][][][][][]",
				"[]"));

		JLabel lblMyTicket = new JLabel("T:");
		contentPane.add(lblMyTicket, "cell 0 0");
		lblMyTicketValue = new JLabel();
		lblMyTicketValue.setText(display.get("ticket", "---"));
		lblMyTicketValue.setBackground(Color.YELLOW);
		contentPane.add(lblMyTicketValue, "cell 1 0");

		JLabel lblCurrentTicket = new JLabel("CT:");
		contentPane.add(lblCurrentTicket, "cell 2 0");
		lblCurrentTicketValue = new JLabel();
		lblCurrentTicketValue.setText(display.get("currentTicket", "---"));
		contentPane.add(lblCurrentTicketValue, "cell 3 0");

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
					saveProperties();
				} catch (TicketException ex) {
					JOptionPane.showMessageDialog(null, ex);
				}
				btnGetticket.setEnabled(false);
			}

		});
		contentPane.add(btnGetticket, "cell 5 0");

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
			}

		});
		contentPane.add(btnDone, "cell 6 0");
		btnDone.setEnabled(false);

		btnUndock = new JButton("Undock");
		btnUndock.setForeground(Color.white);
		btnUndock.setBackground(Color.darkGray);
		btnUndock.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
				ticketClient.setCurrentWindow(mainWindow);
				mainWindow.updateDisplay();
				mainWindow.show();
			}
		});
		contentPane.add(btnUndock, "cell 7 0");
	}

	@Override
	public void show() {
		frmDockedWindow.setVisible(true);
	}

	@Override
	public void hide() {
		frmDockedWindow.setVisible(false);
	}

	@Override
	public void updateDisplay() {
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
		properties.put("window.docked.position.x", frmDockedWindow.getX());
		properties.put("window.docked.position.y", frmDockedWindow.getY());
	}
}
