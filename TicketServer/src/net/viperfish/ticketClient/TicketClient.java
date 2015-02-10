package net.viperfish.ticketClient;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;

public class TicketClient {

	private JFrame frame;
	Thread worker;
	ClientWorker w;
	Thread updater;
	JLabel lblMyTicket;
	JLabel lblCurrentTicket;
	JButton btnDone;
	JButton btnGetticket;
	private JTextField textField;
	private JLabel lblIp;
	private JButton btnConnect;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TicketClient window = new TicketClient();
					window.frame.setVisible(true);
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
		w = new ClientWorker();
		worker = new Thread(w);
		updater = new Thread(new Runnable() {

			@Override
			public void run() {
				List<Display> updates;
				while(true) {
					synchronized(w) {
						try {
							w.wait();
						} catch (InterruptedException e) {
							return;
						}
					}
					updates = w.getTask();
					for(Display i : updates) {
						if(i.getLocation().equals("NumberBank")) {
							lblMyTicket.setText(i.getContent());
							btnDone.setEnabled(true);
						}
						if(i.getLocation().equals("UpdateNumberBank")) {
							lblCurrentTicket.setText(i.getContent());
						}
						if(i.getLocation().equals("Pop Up")) {
							JOptionPane.showMessageDialog(null, i.getContent());
						}
					}
					updates.clear();
				}
				
			}
			
		});
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setForeground(Color.RED);
		frame.setBounds(100, 100, 457, 210);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[][][][][grow][][]", "[][][][][][]"));
		
		lblIp = new JLabel("IP");
		frame.getContentPane().add(lblIp, "cell 2 0");
		
		textField = new JTextField();
		frame.getContentPane().add(textField, "cell 3 0 2 1,growx");
		textField.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.setForeground(Color.white);
		btnConnect.setBackground(Color.darkGray);
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip;
				ip = textField.getText();
				try {
					w.connect(ip);
				} catch (IOException e1) {
					textField.setText(e.toString());
				}
				worker.start();
				updater.start();
				btnConnect.setEnabled(false);
			}
			
		});
		frame.getContentPane().add(btnConnect, "cell 6 0");
		
		lblMyTicket = new JLabel("Ticket");
		frame.getContentPane().add(lblMyTicket, "cell 4 1");
		
		lblCurrentTicket = new JLabel("Current Ticket");
		frame.getContentPane().add(lblCurrentTicket, "cell 4 2");
		
		btnDone = new JButton("Done");
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
			}
			
		});
		frame.getContentPane().add(btnDone, "cell 2 5");
		btnDone.setEnabled(false);
		
		btnGetticket = new JButton("GetTicket");
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
		frame.getContentPane().add(btnGetticket, "cell 6 5");
	}

}
