package gitlet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


/** A class that represents a blob, written contents of a file. 
  * @author Diana Akrami & Juan Cervantes
  */
  public class Blob implements Serializable {

  		Blob(String name, byte[] fileContents) {
  			_name = name;
  			_fileContents = fileContents;
  			_hash = Utils.sha1(_name, fileContents);
            try {
            File next = new File(".gitlet/blobs/" + _hash +".ser");
            FileOutputStream fileOut = new FileOutputStream(next);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            } catch (IOException e) {
                return;
            }    
  	}

  		String getHash() {
  			return _hash;
  		}

  		String getName() {
  			return _name;
  		}

  		byte[] getFileContents() {
  			return _fileContents;
  		}

  		boolean equalsTo(Blob other) {
  			if (other.getHash().compareTo(_hash) == 0) {
  				return true;
  			}
  			return false;
  		}

  		String getFirstTwo() {
        return _hash.substring(0, 2);
    	}

  		byte[] _fileContents;
  		String _hash;

  	String _name;
  	}

