package structures;

public class Visitor{
    private int id;
    private String name;
    private String type;
    private int arrivalTime;
    private String currentRide;
    public Visitor(int id,String name,int arrivalTime){
        this.type="NORMAL";
        this.arrivalTime=arrivalTime;
        this.name=name;
        this.id=id;
        this.currentRide=null;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getArrivalTime() { return arrivalTime; }
    public String getCurrentRide() { return currentRide; }

    public void setType(String type) { this.type = type; }
    public void setCurrentRide(String ride) { this.currentRide = ride; }

    public boolean isVIP() {
        return type.equals("VIP");
    }
}
