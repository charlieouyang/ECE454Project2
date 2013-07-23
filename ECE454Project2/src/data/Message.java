package data;

import java.io.Serializable;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum MESSAGE_TYPE {
		PEER_LEAVING, 
		STATUS_UPDATE,
		FILE_REQUEST,
		FILE,
		DELETEFILE,
		DELETEALLVERSIONSOFFILE,
		RETIREDEVICE,
		ADDNEWDEVICE,
		TEST
	}
	
    private String ipAddress;		//Of sender
    private int portNumber;			//Of sender
    private MESSAGE_TYPE msgType;
    private Object data;
    private String senderName;
    
    public Message(String ipAddress, int portNumber, MESSAGE_TYPE type, Object data) {
    	this.ipAddress = ipAddress;
    	this.portNumber = portNumber;
    	this.msgType = type;
    	this.data = data;
    	this.senderName = ipAddress+"."+portNumber;
    }
    
    public String getSenderName(){
    	return this.senderName;
    }
    
    public MESSAGE_TYPE getType() {
        return this.msgType;
    }
    
    public String getIpAddress(){
    	return ipAddress;
    }
    
    public int getPortNumber(){
    	return portNumber;
    }
    
    public Object getData(){
    	return this.data;
    }
}
