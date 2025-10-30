public class DeckManager {
    // Deck Trees (Type-1)
    private AvlTree treeByAscendingAttack;
    private AvlTree treeByDescendingAttack;

    // Discard Pile Trees (Type-2)
    private AvlTree treeByLargestMissingHealth;
    private AvlTree treeBySmallestMissingHealth;

    // Global counters for tie-breaking
    private long insertionTime = 0;
    private long discardTime = 0;

    // Game state
    private int survivorScore = 0;
    private int strangerScore = 0;

    public DeckManager() {
        // Init Deck
        this.treeByAscendingAttack = new AvlTree(new AscendingAttackComparator());
        this.treeByDescendingAttack = new AvlTree(new DescendingAttackComparator());

        // Init Discard Piles
        this.treeByLargestMissingHealth = new AvlTree(new LargestMissingHealthComparator());
        this.treeBySmallestMissingHealth = new AvlTree(new SmallestMissingHealthComparator());
    }

    // --- Helper Methods ---

    /**
     * Adds a card to both deck trees with a new insertion time.
     */
    private void addCardToDeck(Card card) {
        card.setInsertionTime(this.insertionTime++);
        this.treeByAscendingAttack.insert(card);
        this.treeByDescendingAttack.insert(card);
    }

    /**
     * Removes a card from both deck trees.
     */
    private void removeCardFromDeck(Card card) {
        this.treeByAscendingAttack.delete(card);
        this.treeByDescendingAttack.delete(card);
    }

    /**
     * Adds a card to both discard pile trees with a new discard time.
     */
    private void addCardToDiscardPiles(Card card) {
        card.setDiscardTime(this.discardTime++);
        this.treeByLargestMissingHealth.insert(card);
        this.treeBySmallestMissingHealth.insert(card);
    }

    /**
     * Removes a card from both discard pile trees.
     */
    private void removeCardFromDiscardPiles(Card card) {
        this.treeByLargestMissingHealth.delete(card);
        this.treeBySmallestMissingHealth.delete(card);
    }

    /**
     * Calculates the new attack for a damaged card. [cite: 114]
     */
    public int calculateNewAttack(int a_base, int h_cur, int h_base) {
        double newAttackDouble = (double) a_base * h_cur / h_base;
        return (int) Math.max(1.0, Math.floor(newAttackDouble));
    }

    /**
     * Applies accumulated revival penalties to a card's base stats.
     * This is called ONLY when the card is fully revived and returns to deck.
     * Penalties from all partial heals and the final full revival are applied together.
     */
    private void applyAccumulatedPenalties(Card card) {
        // Calculate total penalty multiplier
        // Each partial heal: 0.95
        // Final full revival: 0.90
        double totalMultiplier = 1.0;
        
        // Apply all partial heal penalties
        for (int i = 0; i < card.getPartialHealCount(); i++) {
            totalMultiplier *= 0.95;
        }
        
        // Apply full revival penalty (this is always 1 when card goes to deck)
        totalMultiplier *= 0.90;
        
        int new_a_base = (int) Math.floor(card.getA_base() * totalMultiplier);
        int new_h_base = (int) Math.floor(card.getH_base() * totalMultiplier);

        // Base stats are permanently changed
        card.setA_base(new_a_base);
        card.setH_base(new_h_base);
        
        // Current attack is reset to new base
        card.setA_cur(new_a_base);
    }

    // --- Public Commands ---

    public String drawCard(String name, int attack, int health) {
        Card newCard = new Card(name, attack, health, 0); // insertionTime set by helper
        this.addCardToDeck(newCard);
        return "Added " + name + " to the deck";
    }

    public BattleResult findForBattle(int strangerAttack, int strangerHealth) {
        Card candidate;

        candidate = treeByAscendingAttack.findForPriority1(strangerAttack, strangerHealth);
        if (candidate != null) return new BattleResult(candidate, 1);

        candidate = treeByDescendingAttack.findForPriority2(strangerAttack, strangerHealth);
        if (candidate != null) return new BattleResult(candidate, 2);

        candidate = treeByAscendingAttack.findForPriority3(strangerAttack, strangerHealth);
        if (candidate != null) return new BattleResult(candidate, 3);

        candidate = treeByDescendingAttack.findForPriority4();
        if (candidate != null) return new BattleResult(candidate, 4);

        return null; // No card found
    }

    /**
     * Fully refactored battle command with correct scoring and healing.
     */
    public String battle(int a_stranger, int h_stranger, int healPool) {
        BattleResult chosenCardResult = findForBattle(a_stranger, h_stranger);

        // --- Phase 0: No Card Played ---
        if (chosenCardResult == null) {
            this.strangerScore += 2;
            int revivedCount = handleHealingPhase(healPool);
            return String.format("No card to play, %d cards revived", revivedCount);
        }

        // --- Phase 1: Battle ---
        Card playedCard = chosenCardResult.card;
        int priority = chosenCardResult.priority;

        // We must make a deep copy *before* removing it,
        // as removal might alter the tree structure.
        Card originalCard = new Card(playedCard);

        // Remove from deck *before* applying damage
        this.removeCardFromDeck(playedCard);

        int newSurvivorHealth = originalCard.getH_cur() - a_stranger;
        int newStrangerHealth = h_stranger - originalCard.getA_cur();

        // --- Scoring (FIXED) ---
        if (newSurvivorHealth <= 0) {
            strangerScore += 2;
        } else if (newSurvivorHealth < originalCard.getH_cur()) {
            // Damaged but not killed
            strangerScore += 1;
        }

        if (newStrangerHealth <= 0) {
            survivorScore += 2;
        } else if (newStrangerHealth < h_stranger) {
            // Damaged but not killed
            survivorScore += 1;
        }

        String outcomeMessage;

        // --- Battle Resolution ---
        if (newSurvivorHealth <= 0) {
            // Card dies, goes to discard pile
            originalCard.setH_cur(0);
            originalCard.setRevivalProgress(0); // Reset revival progress
            originalCard.setPartialHealCount(0); // Reset heal counts
            originalCard.setFullReviveCount(0);
            this.addCardToDiscardPiles(originalCard);
            outcomeMessage = "the played card is discarded";

        } else {
            // Card survives, returns to deck
            originalCard.setH_cur(newSurvivorHealth);
            int newAttack = calculateNewAttack(
                    originalCard.getA_base(),
                    originalCard.getH_cur(),
                    originalCard.getH_base()
            );
            originalCard.setA_cur(newAttack);

            // Re-add to deck, which gives it a new insertionTime (as per rules) [cite: 102]
            this.addCardToDeck(originalCard);
            outcomeMessage = "the played card returned to deck";
        }

        // --- Phase 2: Healing (Type-2) ---
        int revivedCount = handleHealingPhase(healPool);

        return String.format("Found with priority %d, Survivor plays %s, %s, %d cards revived",
                priority, originalCard.getName(), outcomeMessage, revivedCount);
    }

    /**
     * Implements the Type-2 Healing Phase logic. [cite: 189-197]
     */
    private int handleHealingPhase(int healPool) {
        if (healPool <= 0) {
            return 0; // Type-1 case
        }

        int revivedCount = 0;

        // P1 & P2: Fully revive cards
        while (healPool > 0) {
            Card toRevive = treeByLargestMissingHealth.findCardToFullyRevive(healPool);

            if (toRevive == null) {
                break; // No card can be fully revived
            }

            int costToFullyRevive = toRevive.getH_missing(); // Cost is H_missing (H_base - revivalProgress)
            
            // Edge case: if P3 overheal caused negative H_missing, the cost is 0
            if (costToFullyRevive <= 0) {
                costToFullyRevive = 0;
            }
            
            // Card found, process it
            revivedCount++;
            healPool -= costToFullyRevive;

            // Make a copy, then remove original from discard
            Card revivedCard = new Card(toRevive);
            this.removeCardFromDiscardPiles(toRevive);

            // Apply accumulated penalties (all partial heals + this full revival)
            applyAccumulatedPenalties(revivedCard);

            // Reset stats to new base after penalties
            revivedCard.setH_cur(revivedCard.getH_base());
            revivedCard.setRevivalProgress(0);
            revivedCard.setPartialHealCount(0);
            revivedCard.setFullReviveCount(0);

            // Add back to deck (gets new insertionTime)
            this.addCardToDeck(revivedCard);
        }

        // P3: Apply remaining points to one card (partial healing only)
        if (healPool > 0) {
            Card toPartialRevive = treeBySmallestMissingHealth.findCardForPartialRevive();

            if (toPartialRevive != null) {
                // Make a copy, then remove original
                Card partiallyRevivedCard = new Card(toPartialRevive);
                this.removeCardFromDiscardPiles(toPartialRevive);

                // Apply partial heal (add ALL remaining healPool to revival progress)
                partiallyRevivedCard.setRevivalProgress(
                        partiallyRevivedCard.getRevivalProgress() + healPool
                );

                // Track that this card received a partial heal
                // Penalty will be applied later when card is fully revived
                partiallyRevivedCard.setPartialHealCount(partiallyRevivedCard.getPartialHealCount() + 1);

                // Add back to discard piles (gets new discardTime)
                // Card stays in discard pile with H_base unchanged (tree ordering preserved)
                this.addCardToDiscardPiles(partiallyRevivedCard);
            }
            // If no card in discard, healPool is wasted
        }

        return revivedCount;
    }

    public String findWinning() {
        if (this.survivorScore >= this.strangerScore) {
            return "The Survivor, Score: " + this.survivorScore;
        } else {
            return "The Stranger, Score: " + this.strangerScore;
        }
    }

    public String deckCount() {
        return "Number of cards in the deck: " + this.treeByAscendingAttack.getSize();
    }

    /**
     * New command for Type-2.
     */
    public String discardPileCount() {
        // Both discard trees should have the same size
        return "Number of cards in the discard pile: " + this.treeBySmallestMissingHealth.getSize();
    }

    public String stealCard(int attackLimit, int healthLimit) {
        Card stolenCard = treeByAscendingAttack.findForSteal(attackLimit, healthLimit);

        if (stolenCard != null) {
            this.removeCardFromDeck(stolenCard); // Remove from both trees
            return "The Stranger stole the card: " + stolenCard.getName();
        } else {
            return "No card to steal";
        }
    }
}