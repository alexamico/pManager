package com.alessandroamico.pManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Vector;

import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.apple.eawt.Application;

/**
 * 
 * @author Alessandro Amico
 * @version %I% %G% %U%
 */
public class MainWindow extends JFrame implements ActionListener, TableModelListener, TableCellRenderer, Runnable {
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "pManager";
	private static final String[] options = new String[] { "OK", "Cancel" };
	private static final int SERVICE = 0;
	private static final int USERNAME = 1;
	private static final int PASSWORD = 2;
	private JToolBar toolbar;
	private JTable table;
	private JButton newRepo;
	private JButton openRepo;
	private JButton saveRepo;
	private JButton insertRecord;
	private JButton deleteRecord;
	private JButton copyUser;
	private JButton copyPass;
	private JButton search;
	private JButton exit;
	private JPanel panel;
	private JLabel label;
	private JPasswordField pass;

	private JTextField serviceTextField = new JTextField();
	private JTextField usernameTextField = new JTextField();
	private JPasswordField passwordTextField = new JPasswordField();
	private JPasswordField password2TextField = new JPasswordField();
	private final JComponent[] inputs = new JComponent[] { new JLabel("Service"), serviceTextField,
			new JLabel("Username"), usernameTextField, new JLabel("Password"), passwordTextField,
			new JLabel("Repeat Password"), password2TextField };

	private DefaultTableModel model;
	private Repository repo;
	private Vector<String> columsName;
	private final JFileChooser fc = new JFileChooser();
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * Constructs an empty (invisible) JFrame
	 */
	public MainWindow() {
		super(TITLE);
		if (System.getProperty("os.name").contains("indows")) {
			setIconImage(new ImageIcon((getClass().getResource("/icon.png"))).getImage());
		} else if (System.getProperty("os.name").contains("Mac")) {
			Application.getApplication()
					.setDockIconImage(new ImageIcon((getClass().getResource("/icon.png"))).getImage());
		}
	}

	/**
	 * Inits the top toolbar w/ all the necessary buttons
	 */
	private void initToolbar() {
		toolbar = new JToolBar();
		newRepo = new JButton(new ImageIcon(getClass().getResource("/newRepo.png")));
		openRepo = new JButton(new ImageIcon(getClass().getResource("/openRepo.png")));
		saveRepo = new JButton(new ImageIcon(getClass().getResource("/save.png")));
		insertRecord = new JButton(new ImageIcon(getClass().getResource("/insertRecord.png")));
		deleteRecord = new JButton(new ImageIcon(getClass().getResource("/deleteRecord.png")));
		copyUser = new JButton(new ImageIcon(getClass().getResource("/copyUser.png")));
		copyPass = new JButton(new ImageIcon(getClass().getResource("/copyPass.png")));
		search = new JButton(new ImageIcon(getClass().getResource("/search.png")));
		exit = new JButton(new ImageIcon(getClass().getResource("/exit.png")));

		newRepo.setToolTipText("Create new Repository");
		openRepo.setToolTipText("Open existing Repository");
		saveRepo.setToolTipText("Save current Repository");
		insertRecord.setToolTipText("Insert a record");
		deleteRecord.setToolTipText("Delete selected record");
		copyUser.setToolTipText("Copy username of selected record");
		copyPass.setToolTipText("Copy password of selected record");
		search.setToolTipText("Search");
		exit.setToolTipText("Exit");

		newRepo.addActionListener(this);
		openRepo.addActionListener(this);
		saveRepo.addActionListener(this);
		insertRecord.addActionListener(this);
		deleteRecord.addActionListener(this);
		copyUser.addActionListener(this);
		copyPass.addActionListener(this);
		search.addActionListener(this);
		exit.addActionListener(this);

		toolbar.add(newRepo);
		toolbar.add(openRepo);
		toolbar.add(saveRepo);
		toolbar.addSeparator();
		toolbar.add(insertRecord);
		toolbar.add(deleteRecord);
		toolbar.addSeparator();
		toolbar.add(copyUser);
		toolbar.add(copyPass);
		toolbar.addSeparator();
		toolbar.add(search);
		toolbar.add(exit);

		this.add(toolbar, BorderLayout.NORTH);
	}

	/**
	 * init passowrd dialog's pane
	 */
	private void initPanel() {
		label = new JLabel("Enter the password:");
		panel = new JPanel();
		pass = new JPasswordField(20);
		panel.add(label);
		panel.add(pass);
	}

	/**
	 * Fills the central table w/ all the records passed.
	 * 
	 * @param vect
	 *            <code>Records</code> to be inserted
	 */
	private void initTable(Vector<Record> vect) {
		Vector<Vector<String>> res = new Vector<Vector<String>>();
		columsName = new Vector<String>();
		columsName.add("Service");
		columsName.add("Username");
		columsName.add("Password");

		for (int i = 0; i < vect.size(); i++) {
			Vector<String> row = new Vector<String>();
			row.addElement(vect.get(i).getTitle());
			row.addElement(vect.get(i).getUsername());
			row.addElement(vect.get(i).getPassword());

			res.addElement(row);
		}

		table = new JTable(res, columsName) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0 ? false : true;
			}
		};
		table.getModel().addTableModelListener(this);
		table.getColumnModel().getColumn(USERNAME).setCellRenderer(this);
		table.getColumnModel().getColumn(PASSWORD).setCellRenderer(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		model = (DefaultTableModel) table.getModel();
		this.add(scrollPane, BorderLayout.CENTER);
		this.pack();
	}

	/**
	 * 
	 */
	private void createRepo() {
		try {
			if ((JOptionPane.showOptionDialog(this, panel, "pManager", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, options, options[0])) != 0)
				return;

			this.repo = new XMLReposotory(pass.getPassword());
			pass.setText(null); // very important!

			if (!repo.create()) {
				JOptionPane.showMessageDialog(this, "Error!!");
				repo = null;
			}

			initTable(repo.query(null));
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			JOptionPane.showMessageDialog(this, "Failed to save repo: " + e.toString());
			this.repo = null;
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 
	 * 
	 * @param returnVal
	 *            <code>JFileChooser</code> macro for ...
	 */
	private void openRepo(int returnVal) {
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fc.getSelectedFile();
				if ((JOptionPane.showOptionDialog(this, panel, "pManager", JOptionPane.NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0])) != 0)
					return;
				this.repo = new XMLReposotory(pass.getPassword());
				pass.setText(null); // very important!
				if (!repo.open(file.getPath())) {
					JOptionPane.showMessageDialog(this, "Failed to open repo: wrong password?");
					repo = null;
					return;
				}
				initTable(this.repo.query(null));
			} catch (HeadlessException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
					| NoSuchPaddingException | InvalidAlgorithmParameterException e) {
				e.printStackTrace();
				this.repo = null;
				JOptionPane.showMessageDialog(this, "Failed to save repo: " + e.toString());
				return;
			}
		}
	}

	/**
	 * 
	 * @param returnVal
	 *            <code>JFileChooser</code> macro for ...
	 */
	private void saveRepo(int returnVal) {
		if (returnVal == JFileChooser.APPROVE_OPTION && this.repo != null) {
			File file = fc.getSelectedFile();
			if (!repo.save(file))
				JOptionPane.showMessageDialog(this, "Failed to save repo");
		}
	}

	/**
	 * Shows a message dialog to insert a record and inserts the record if and
	 * only if the title/service is not empty
	 */
	private void insertRecord() {
		boolean inserted = false;
		JOptionPane.showMessageDialog(this, inputs, "pManager", JOptionPane.PLAIN_MESSAGE);

		if (!(passwordTextField.getText().equals(password2TextField.getText())))
			JOptionPane.showMessageDialog(this, "Passwords don't match!");
		else {
			if (serviceTextField.getText().isEmpty())
				return;

			Record temp = new XMLRecord(serviceTextField.getText(), usernameTextField.getText(),
					passwordTextField.getText());

			if (this.repo.insert(temp)) {
				Vector<String> row = new Vector<String>();
				row.add(temp.getTitle());
				row.add(temp.getUsername());
				row.add(temp.getPassword());
				model.addRow(row);
				inserted = true;
			}

			if (inserted) {
				serviceTextField.setText("");
				usernameTextField.setText("");
				passwordTextField.setText("");
				password2TextField.setText("");
			}
		}
	}

	/**
	 * 
	 */
	private void deleteRecord() {
		Record temp = new XMLRecord();
		int row = table.getSelectedRow();

		if (row == -1)
			return;

		temp.setTitle((String) table.getValueAt(row, SERVICE));
		temp.setUsername((String) table.getValueAt(row, USERNAME));
		temp.setPassowrd((String) table.getValueAt(row, PASSWORD));

		if (this.repo.delete(temp))
			model.removeRow(table.getSelectedRow());
	}

	/**
	 * Copy username of selected row (service) in the system clipboard
	 * 
	 */
	private void copyUser() {
		int row = table.getSelectedRow();

		if (row == -1)
			return;

		StringSelection selected = new StringSelection((String) (table.getValueAt(row, USERNAME)));
		clipboard.setContents(selected, selected);
	}

	/**
	 * Copy password of selected row (service) in the system clipboard
	 * 
	 */
	private void copyPassword() {
		int row = table.getSelectedRow();

		if (row == -1)
			return;

		StringSelection selected = new StringSelection((String) (table.getValueAt(row, PASSWORD)));
		clipboard.setContents(selected, selected);
	}

	/**
	 * 
	 * 
	 * @param e
	 *            the <code>ActionEvent</code> performed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton but = (JButton) e.getSource();

		if (but == newRepo)
			this.createRepo();
		else if (but == openRepo)
			this.openRepo(fc.showOpenDialog(this));
		else if (but == saveRepo)
			this.saveRepo(fc.showSaveDialog(this));
		else if (but == insertRecord)
			this.insertRecord();
		else if (but == deleteRecord)
			this.deleteRecord();
		else if (but == copyUser)
			this.copyUser();
		else if (but == copyPass)
			this.copyPassword();
		else if (but == search) // TODO
			;
		else if (but == exit)
			System.exit(0);
	}

	/**
	 * 
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int row = e.getFirstRow();
			this.repo.update(new XMLRecord((String) ((TableModel) e.getSource()).getValueAt(row, SERVICE),
					(String) ((TableModel) e.getSource()).getValueAt(row, USERNAME),
					(String) ((TableModel) e.getSource()).getValueAt(row, PASSWORD)));
		}
	}

	/**
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		return (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, "*************", isSelected,
				hasFocus, row, column);

	}

	@Override
	public void run() {
		initPanel();
		initToolbar();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

}
