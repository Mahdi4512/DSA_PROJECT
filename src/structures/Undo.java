package structures;

public class Undo {
    public String command;
    public int id;
    public String rideName;
    public String additionalInfo;
    public int previousPosition;
    public Undo(String command, int id, String rideName) {
        this(command, id, rideName, null, -1);
    }

    public Undo(String command, int id, String rideName, String additionalInfo, int previousPosition) {
        this.command = command;
        this.id = id;
        this.rideName = rideName;
        this.additionalInfo = additionalInfo;
        this.previousPosition = previousPosition;
    }
}