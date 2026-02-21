package structures;
public class Event {
    private int time;
    private String type;

    private  String rideName;
    public Event(int time,String type,String rideName){
        this.time=time;
        this.rideName=rideName;
        this.type=type;
    }
    public int compareTo(Event other){
        return Integer.compare(this.time,other.time);
    }
    public int getTime(){
        return time;
    }
    public String getType(){
        return type;
    }
    public String getRideName(){
        return rideName;
    }

}

