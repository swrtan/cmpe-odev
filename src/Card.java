public class Card {
    private String name;
    private long insertionTime; // For deck tie-breaking
    private long discardTime;   // For discard pile tie-breaking
    private int a_init, h_init;
    private int a_base, h_base;
    private int a_cur, h_cur;
    private int revivalProgress;
    private int partialHealCount; // Track number of partial heals received
    private int fullReviveCount;  // Track number of full revivals received

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
        this.discardTime = 0; // Initialized
        this.partialHealCount = 0;
        this.fullReviveCount = 0;
    }

    /**
     * Copy constructor.
     */
    public Card(Card other) {
        this.name = other.name;
        this.insertionTime = other.insertionTime;
        this.discardTime = other.discardTime;
        this.a_init = other.a_init;
        this.h_init = other.h_init;
        this.a_base = other.a_base;
        this.h_base = other.h_base;
        this.a_cur = other.a_cur;
        this.h_cur = other.h_cur;
        this.revivalProgress = other.revivalProgress;
        this.partialHealCount = other.partialHealCount;
        this.fullReviveCount = other.fullReviveCount;
    }

    // --- Getters ---
    public String getName() { return name; }
    public long getInsertionTime() { return insertionTime; }
    public long getDiscardTime() { return discardTime; }
    public int getA_init() { return a_init; }
    public int getH_init() { return h_init; }
    public int getA_base() { return a_base; }
    public int getH_base() { return h_base; }
    public int getA_cur() { return a_cur; }
    public int getH_cur() { return h_cur; }
    public int getRevivalProgress() { return revivalProgress; }
    public int getPartialHealCount() { return partialHealCount; }
    public int getFullReviveCount() { return fullReviveCount; }

    /**
     * Helper to get missing health for revival. [cite: 183]
     */
    public int getH_missing() {
        return this.h_base - this.revivalProgress;
    }

    // --- Setters ---
    public void setA_base(int a_base) { this.a_base = a_base; }
    public void setH_base(int h_base) { this.h_base = h_base; }
    public void setA_cur(int a_cur) { this.a_cur = a_cur; }
    public void setH_cur(int h_cur) { this.h_cur = h_cur; }
    public void setRevivalProgress(int revivalProgress) { this.revivalProgress = revivalProgress; }
    public void setInsertionTime(long insertionTime) { this.insertionTime = insertionTime; }
    public void setDiscardTime(long discardTime) { this.discardTime = discardTime; }
    public void setPartialHealCount(int partialHealCount) { this.partialHealCount = partialHealCount; }
    public void setFullReviveCount(int fullReviveCount) { this.fullReviveCount = fullReviveCount; }
}