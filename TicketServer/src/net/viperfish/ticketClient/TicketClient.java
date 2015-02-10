package net.viperfish.ticketClient;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class TicketClient {

	private JFrame frmTicketServer;
	protected Thread worker;
	protected ClientWorker w;
	protected Thread updater;
	protected JLabel lblMyTicket;
	protected JLabel lblCurrentTicket;
	protected JButton btnDone;
	protected JButton btnGetticket;
	public static String myName;
	private JTextField textField;
	private JLabel lblIp;
	private JButton btnConnect;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					TicketClient window = new TicketClient();
					window.frmTicketServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TicketClient() {
		myName = new String();
		w = new ClientWorker();
		worker = new Thread(w);
		updater = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					synchronized (w) {
						try {
							w.wait();
						} catch (InterruptedException e) {
							return;
						}
					}
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							List<Display> updates;
							updates = w.getTask();
							for (Display i : updates) {
								if (i.getLocation().equals("NumberBank")) {
									lblMyTicket.setText("Your Ticket:"
											+ i.getContent());
									btnDone.setEnabled(true);
								}
								if (i.getLocation().equals("UpdateNumberBank")) {
									lblCurrentTicket.setText("Current Ticket:"
											+ i.getContent());
								}
								if (i.getLocation().equals("Pop Up")) {
									JOptionPane.showMessageDialog(null,
											i.getContent());
								}
							}
							updates.clear();
						}

					});

				}

			}

		});
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		new NamePrompt().setVisible(true);
		frmTicketServer = new JFrame();
		frmTicketServer.setType(Type.UTILITY);
		frmTicketServer.setTitle("5190 Ticket Server");
		frmTicketServer.setBackground(Color.YELLOW);
		frmTicketServer.setForeground(Color.RED);
		frmTicketServer.setBounds(100, 100, 345, 174);
		frmTicketServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTicketServer.getContentPane().setLayout(
				new MigLayout("", "[][][][][grow][][][]",
						"[][][][][][][][][][]"));

		lblIp = new JLabel("IP");
		frmTicketServer.getContentPane().add(lblIp, "cell 1 0");

		textField = new JTextField();
		frmTicketServer.getContentPane().add(textField, "cell 2 0 3 1,growx");
		textField.setColumns(10);

		btnConnect = new JButton("Connect");
		btnConnect.setToolTipText("Connect to the server at the ip address");
		btnConnect.setForeground(Color.white);
		btnConnect.setBackground(Color.darkGray);
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip;
				ip = textField.getText();
				try {
					w.connect(ip);
					if (myName.length() != 0) {
						w.sendName();
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
				worker.start();
				updater.start();
				btnConnect.setEnabled(false);
			}

		});
		frmTicketServer.getContentPane().add(btnConnect, "cell 6 0");

		lblMyTicket = new JLabel("Ticket");
		lblMyTicket.setBackground(Color.YELLOW);
		frmTicketServer.getContentPane().add(lblMyTicket, "cell 2 1");

		lblCurrentTicket = new JLabel("Current Ticket");
		frmTicketServer.getContentPane().add(lblCurrentTicket, "cell 2 2");

		btnGetticket = new JButton("GetTicket");
		btnGetticket.setToolTipText("Get a ticket");
		btnGetticket.setForeground(Color.white);
		btnGetticket.setBackground(Color.darkGray);
		btnGetticket.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					w.getTicket();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
				btnGetticket.setEnabled(false);
			}

		});
		frmTicketServer.getContentPane().add(btnGetticket, "cell 6 2");

		btnDone = new JButton("Done");
		btnDone.setToolTipText("Finished testing");
		btnDone.setForeground(Color.white);
		btnDone.setBackground(Color.darkGray);
		btnDone.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					w.done();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
				btnGetticket.setEnabled(true);
				lblMyTicket.setText("---");
				lblCurrentTicket.setText("----");
			}

		});
		frmTicketServer.getContentPane().add(btnDone, "cell 6 8");
		btnDone.setEnabled(false);
	}

}
