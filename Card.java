public class Card {
    private String name;
    private long insertionTime;
    private int a_init, h_init;
    private int a_base, h_base;
    private int a_cur, h_cur;
    private int revivalProgress;

    public Card(String name, int attack, int health, long insertionTime) {
        this.name = name;
        this.insertionTime = insertionTime;
        this.a_init = attack;
        this.h_init = health;
        this.a_base = attack;
        this.h_base = health;
        this.a_cur = attack;
        this.h_cur = health;
        this.revivalProgress = 0;
    }
    public Card(Card other) {
        this.name = other.name;
        this.insertionTime = other.insertionTime;
        this.a_init = other.a_init;
        this.h_init = other.h_init;
        this.a_base = other.a_base;
        this.h_base = other.h_base;
        this.a_cur = other.a_cur;
        this.h_cur = other.h_cur;
        this.revivalProgress = other.revivalProgress;
    }

    public String getName() { return name; }
    public long getInsertionTime() { return insertionTime; }
    public int getA_init() { return a_init; }
    public int getH_init() { return h_init; }
    public int getA_base() { return a_base; }
    public int getH_base() { return h_base; }
    public int getA_cur() { return a_cur; }
    public int getH_cur() { return h_cur; }
    public int getRevivalProgress() { return revivalProgress; }

    public void setName(String name) { this.name = name; }
    public void setA_base(int a_base) { this.a_base = a_base; }
    public void setH_base(int h_base) { this.h_base = h_base; }
    public void setA_cur(int a_cur) { this.a_cur = a_cur; }
    public void setH_cur(int h_cur) { this.h_cur = h_cur; }
    public void setRevivalProgress(int revivalProgress) { this.revivalProgress = revivalProgress; }

    public void setInsertionTime(long insertionTime) {
        this.insertionTime = insertionTime;
    }
}