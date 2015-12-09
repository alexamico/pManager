package com.alessandroamico.pManager;

import java.io.File;
import java.util.Vector;

/**
 * 
 * 
 * 
 * @author Alessandro Amico
 * @version %I% %G% %U%
 */
public interface Repository {
	/**
	 * 
	 * @return <code>true</code> if file was created correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean create();
	
	/**
	 * 
	 * @param path
	 * @return <code>true</code> if file was opened correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean open(String path);
	
	/**
	 * 
	 * @param rec
	 * @return <code>true</code> if the record was inserted correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean insert(Record rec);
	
	/**
	 * 
	 * @param rec
	 * @return <code>true</code> if the record was deleted correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean delete(Record rec);
	
	/**
	 * 
	 * @param rec 
	 * @return <code>true</code> if the record was updated correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean update(Record rec);
	
	/**
	 * 
	 * @param search
	 * @return
	 */
	public Vector<Record>  query(String search);
	
	/**
	 * 
	 * @param file
	 * @return <code>true</code> if file was saved correctly,
	 *         <code>false</code> otherwise.
	 */
	public boolean save(File file);
}