package bsu.rfe.java.Lab7.B3.Mashkantsev;


public class Peer {
    private  String sender;
    private  String destinationAddress;

    public void SetSender(String sender){
        this.sender=sender;
    }
    public void SetdestinationAddress(String destinationAddress){
        this.destinationAddress = destinationAddress;
    }
    public String GetdestinationAddress(){
        return destinationAddress;
    }
    public String GetSender(){
        return sender;
    }

}