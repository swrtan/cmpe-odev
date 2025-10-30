/**
 * Comparator for Discard Pile (Healing P3).
 * 1. Smallest H_missing
 * 2. Smallest (First-Discarded) DiscardTime
 */
public class SmallestMissingHealthComparator implements MyComparator<Card> {
    @Override
    public int compare(Card c1, Card c2) {
        int missingHealthCompare = Integer.compare(c1.getH_missing(), c2.getH_missing());
        if (missingHealthCompare != 0) {
            return missingHealthCompare;
        }
        return Long.compare(c1.getDiscardTime(), c2.getDiscardTime());
    }
}