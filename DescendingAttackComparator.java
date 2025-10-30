public class DescendingAttackComparator implements MyComparator<Card> {
    @Override
    public int compare(Card c1, Card c2) {
        int attackCompare = Integer.compare(c2.getA_cur(), c1.getA_cur());
        if (attackCompare != 0) {
            return attackCompare;
        }
        int healthCompare = Integer.compare(c1.getH_cur(), c2.getH_cur());
        if (healthCompare != 0) {
            return healthCompare;
        }
        return Long.compare(c1.getInsertionTime(), c2.getInsertionTime());
    }
}
