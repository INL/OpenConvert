package nl.openconvert.util;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// This should be enhanced to include loading from 
// jar as resource...

public class Serialize<T> 
{
	public void saveObject(Object o, String fileName) throws IOException 
	{
		// TODO Auto-generated method stub
		try
		{
			FileOutputStream fileOut =
					new FileOutputStream(fileName);
			ObjectOutputStream out =
					new ObjectOutputStream(fileOut);
			out.writeObject(o);
			out.close();
			fileOut.close();
		} catch(IOException i)
		{
			i.printStackTrace();
		}
	}

	public  T loadFromFile(String fileName)
	{
		try
		{
			FileInputStream fileIn =
					new FileInputStream(fileName);
			ObjectInputStream in =
					new ObjectInputStream(fileIn);
			T t = (T) in.readObject();
			in.close();
			fileIn.close();
			return t;
		} catch(Exception i)
		{
			i.printStackTrace();
			return null;
		}
	}
}
