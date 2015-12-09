package com.alessandroamico.pManager;

/**
 * 
 * 
 * 
 * @author Alessandro Amico
 * @version %I% %G% %U%
 *
 */
public class XMLRecord implements Record {
	/** */
	String title;
	/** */
	String username;
	/** */
	String password;
	
	/**
	 * Constructor for XMLRecord w/o parameters
	 */
	public XMLRecord() {
		this.title    = null;
		this.username = null;
		this.password = null;
	}
	
	/**
	 * Constructor for XMLRecord w/ only title (commonly service)
	 * 
	 * @param title the record's title
	 */
	public XMLRecord(String title) {
		this.title    = title;
		this.username = null;
		this.password = null;
	}
	
	/**
	 * 
	 * @param title
	 * @param username
	 */
	public XMLRecord(String title, String username) {
		this.title    = title;
		this.username = username;
		this.password = null;
	}
	
	/**
	 * 
	 * @param title
	 * @param username
	 * @param passowrd
	 */
	public XMLRecord(String title, String username, String passowrd) {
		this.title    = title;
		this.username = username;
		this.password = passowrd;
	}

	/**
	 * 
	 * @return 
	 */
	@Override
	public String getTitle() {
		return this.title;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/**
	 * 
	 * @param title
	 * @return
	 */
	@Override
	public boolean setTitle(String title) {
		this.title = title;
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	@Override
	public boolean setUsername(String username) {
		this.username = username;
		return true;
	}

	/**
	 * 
	 * 
	 * @param password
	 * @return
	 */
	@Override
	public boolean setPassowrd(String password) {
		this.password = password;
		return true;
	}
	

	
}