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

import net.miginfocom.swing.MigLayout;

public class DockedWindow implements Window {
	private JFrame frmDockedWindow;

	private JButton btnDone;
	private JButton btnGetticket;
	private JButton btnUndock;
	private JLabel lblMyTicket;
	private JLabel lblCurrentTicket;

	public DockedWindow(TicketClient ticketClient, MainWindow mainWindow) {
		frmDockedWindow = new JFrame();
		frmDockedWindow.setType(Type.UTILITY);
		frmDockedWindow.setTitle("5190 Ticket Server");
		frmDockedWindow.setAlwaysOnTop(true);
		frmDockedWindow.setUndecorated(true);
		frmDockedWindow.setBackground(Color.YELLOW);
		frmDockedWindow.setForeground(Color.RED);
		frmDockedWindow.setBounds(100, 100, 400, 40);
		frmDockedWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		MoveMouseListener moveMouseListener = new MoveMouseListener(contentPane);
		contentPane.addMouseListener(moveMouseListener);
		contentPane.addMouseMotionListener(moveMouseListener);
		frmDockedWindow.setContentPane(contentPane);

		contentPane.setLayout(new MigLayout("", "[][][][][][][][][][]", "[]"));

		lblMyTicket = new JLabel("Ticket");
		lblMyTicket.setBackground(Color.YELLOW);
		contentPane.add(lblMyTicket, "cell 1 0");

		lblCurrentTicket = new JLabel("Current Ticket");
		contentPane.add(lblCurrentTicket, "cell 2 0");

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
		contentPane.add(btnGetticket, "cell 4 0");

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
			}

		});
		contentPane.add(btnDone, "cell 5 0");
		btnDone.setEnabled(false);

		btnUndock = new JButton("Undock");
		btnUndock.setForeground(Color.white);
		btnUndock.setBackground(Color.darkGray);
		btnUndock.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
				mainWindow.show();
				ticketClient.setCurrentWindow(mainWindow);
			}
		});
		contentPane.add(btnUndock, "cell 6 0");
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
