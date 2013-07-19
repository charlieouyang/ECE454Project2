package data;

import java.io.Serializable;

public class FileWrapper implements Serializable{
	private String fileName;
	private byte[] content;
	
	public FileWrapper(String fileName, byte[] content){
		this.fileName = fileName;
		this.content = content;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public byte[] getContent(){
		return content;
	}
}
