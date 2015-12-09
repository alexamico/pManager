package com.alessandroamico.pManager;

/**
 * 
 * 
 * 
 * @author Alessandro Amico
 * @version %I% %G% %U%
 *
 */
public interface Record {

	/**
	 * 
	 * @return the title (commonly the service) of this record
	 */
	public String getTitle();
	
	/**
	 * 
	 * @return
	 */
	public String getUsername();
	
	/**
	 * 
	 * @return
	 */
	public String getPassword();
	
	/**
	 * 
	 * @return
	 */
	public boolean setTitle(String title);
	
	/**
	 * 
	 * @return
	 */
	public boolean setUsername(String username);
	
	/**
	 * 
	 * @return
	 */
	public boolean setPassowrd(String password);
}
