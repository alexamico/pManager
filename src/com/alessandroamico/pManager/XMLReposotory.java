package com.alessandroamico.pManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * 
 * @author Alessandro Amico
 * @version %I% %G% %U%
 *
 */
public class XMLReposotory implements Repository {
	/* REAL INSTANCE VARIABLES */
	/** */
	Document doc;
	/** */
	// char[] password;
	/** */
	SecretKey secretKey;
	/** */
	Cipher cipher;
	/** */
	byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };

	/* UTILITY */
	private static final int REP = 100;
	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	PBEKeySpec pbeKeySpec;
	SecretKeyFactory secretKeyFactory;
	PBEParameterSpec pbeParameterSpec;

	/**
	 * 
	 * 
	 * @param <code>password<code>
	 */
	public XMLReposotory(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		pbeKeySpec = new PBEKeySpec(password);
		secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
		secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
		pbeParameterSpec = new PBEParameterSpec(salt, REP);
		cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
	}

	/**
	 * 
	 * 
	 * @param path
	 * @param password
	 * @return <code>true</code> if file was created correctly,
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean create() {
		try {
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("repo");
			doc.appendChild(rootElement);
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens the XML password's file specified by <code>path</code> with the
	 * master passowrd <code>password</code>.
	 * 
	 * @param path
	 * @param password
	 * @return <code>true</code> if file was open correctly, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean open(String path) {
		if (path == null)
			return false;

		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
			StringBuilder temp = new StringBuilder();
			byte[] in = Files.readAllBytes(Paths.get(path));
			for (int i = 0; i < in.length; i++) {
				byte[] in2 = { in[i] };
				byte[] output = cipher.update(in2, 0, 1);
				if (output != null)
					temp.append(new String(output, Charset.defaultCharset()));
			}
			byte[] output = cipher.doFinal();
			if (output != null)
				temp.append(new String(output, Charset.defaultCharset()));

			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(temp.toString()));
			doc = docBuilder.parse(is);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return false;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return false;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Inserts the <code>Record</code> rec in the repo.
	 * 
	 * 
	 * @param
	 * @return
	 */
	@Override
	public boolean insert(Record rec) {
		Element myRec = doc.createElement("record");

		Element elem = doc.createElement("title");
		elem.appendChild(doc.createTextNode(rec.getTitle()));
		myRec.appendChild(elem);

		elem = doc.createElement("username");
		elem.appendChild(doc.createTextNode(rec.getUsername()));
		myRec.appendChild(elem);

		elem = doc.createElement("password");
		elem.appendChild(doc.createTextNode(rec.getPassword()));
		myRec.appendChild(elem);

		elem = doc.getDocumentElement(); // rootElement
		elem.appendChild(myRec);

		return true;
	}

	/**
	 * 
	 * @param rec
	 * @return
	 */
	@Override
	public boolean delete(Record rec) {
		NodeList list = doc.getElementsByTagName("record");
		Element element = null;
		boolean found = false;
		
		for (int i = 0; i < list.getLength() && !found; i++) {
			element = (Element) list.item(i);

			if (element.getElementsByTagName("title").item(0).getTextContent().equals(rec.getTitle()))
				found = true;
		}

		if (found) {		
			Element root = doc.getDocumentElement();
			root.removeChild(element);
		}
		
		return found;
	}

	/**
	 * 
	 * 
	 * 
	 * @param rec 
	 * @return <code>true</code> if file was updated correctly, 
	 * 			<code>false</code> otherwise.
	 */
	@Override
	public boolean update(Record rec) {
		NodeList list = doc.getElementsByTagName("record");
		Element element = null;
		boolean found = false;
		
		for (int i = 0; i < list.getLength() && !found; i++) {
			element = (Element) list.item(i);

			if (element.getElementsByTagName("title").item(0).getTextContent().equals(rec.getTitle()))
				found = true;
		}

		if (found) {
			element.getElementsByTagName("username").item(0).setTextContent(rec.getUsername());
			element.getElementsByTagName("password").item(0).setTextContent(rec.getPassword());
		}
		
		return found;
	}

	/**
	 * 
	 * 
	 * @param search if <code>search</code> is null <code>query</code> 
	 * 					returns all records,
	 * 
	 * @return all the <code>Records</code> that match (also partially) 
	 * 			<code>search</code> 
	 */
	@Override
	public Vector<Record> query(String search) {
		NodeList nList;
		Vector<Record> result = new Vector<Record>();
		
		if (search == null)
			search = "";

		if ((nList = doc.getElementsByTagName("record")) == null)
			return result;
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String title = eElement.getElementsByTagName("title").item(0).getTextContent();

				if (title.contains(search) || title.equalsIgnoreCase(search)) {
					result.add(new XMLRecord(eElement.getElementsByTagName("title").item(0).getTextContent(),
							eElement.getElementsByTagName("username").item(0).getTextContent(),
							eElement.getElementsByTagName("password").item(0).getTextContent()));
				}
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param file
	 * @return <code>true</code> if file was saved correctly, 
	 * 			<code>false</code> otherwise.
	 */
	@Override
	public boolean save(File file) {
		if (file == null)
			return false;

		try {
			FileOutputStream outFile = new FileOutputStream(file);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);

			byte[] in = writer.toString().getBytes();
			for (int i = 0; i < in.length; i++) {
				byte[] in2 = { in[i] };
				byte[] output = cipher.update(in2, 0, 1);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);

			outFile.close();
			return true;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return false;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return false;
		}
	}
}