package net.viperfish.ticketClient;

import java.awt.Color;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
	private JLabel lblMyTicket;
	private JLabel lblCurrentTicket;
	private JTextField txtIp;
	private JTextField txtName;

	public MainWindow(TicketClient ticketClient) {
		frmMainWindow = new JFrame();
		frmMainWindow.setType(Type.UTILITY);
		frmMainWindow.setTitle("5190 Ticket Server");
		frmMainWindow.setBackground(Color.YELLOW);
		frmMainWindow.setForeground(Color.RED);
		frmMainWindow.setBounds(100, 100, 270, 220);
		frmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setOpaque(true);
		frmMainWindow.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][][grow][][]",
				"[][][][][][][][][][]"));

		JLabel lblIp = new JLabel("IP");
		contentPane.add(lblIp, "cell 1 0");

		txtIp = new JTextField(10);
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
				try {
					ticketClient.connect(ip, name);
				} catch (TicketException ex) {
					txtIp.setText(ex.getMessage());
				}
				btnConnect.setEnabled(false);
			}

		});
		contentPane.add(btnConnect, "cell 4 0 2 1");

		JLabel lblName = new JLabel("Name");
		contentPane.add(lblName, "cell 1 1");

		txtName = new JTextField(10);
		contentPane.add(txtName, "cell 2 1,growx");

		lblMyTicket = new JLabel("Ticket");
		lblMyTicket.setBackground(Color.YELLOW);
		contentPane.add(lblMyTicket, "cell 2 2");

		lblCurrentTicket = new JLabel("Current Ticket");
		contentPane.add(lblCurrentTicket, "cell 2 3");

		btnGetticket = new JButton("GetTicket");
		btnGetticket.setToolTipText("Get a ticket");
		btnGetticket.setForeground(Color.white);
		btnGetticket.setBackground(Color.darkGray);
		btnGetticket.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ticketClient.getTicket();
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
				lblMyTicket.setText("---");
				lblCurrentTicket.setText("---");
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
				dockedWindow.show();
				ticketClient.setCurrentWindow(dockedWindow);
			}
		});
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
	public void updateDisplay(List<Display> displayUpdates) {
		for (Display i : displayUpdates) {
			if (i.getLocation().equals("NumberBank")) {
				lblMyTicket.setText(i.getContent());
				btnDone.setEnabled(true);
			}
			if (i.getLocation().equals("UpdateNumberBank")) {
				lblCurrentTicket.setText(i.getContent());
			}
			if (i.getLocation().equals("Pop Up")) {
				JOptionPane.showMessageDialog(null, i.getContent());
			}
		}
	}
}
