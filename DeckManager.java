
public class DeckManager {
    private AvlTree treeByAscendingAttack;
    private AvlTree treeByDescendingAttack;
    private long InsertionTime = 0;
    private int survivorScore=0;
    private int strangerScore=0;


    public DeckManager() {
        MyComparator<Card> ascComp = new AscendingAttackComparator();
        MyComparator<Card> descComp = new DescendingAttackComparator();

        this.treeByAscendingAttack = new AvlTree(ascComp);
        this.treeByDescendingAttack = new AvlTree(descComp);
    }

    public int getSurvivorScore() {
        return this.survivorScore;
    }

    public int getStrangerScore() {
        return this.strangerScore;
    }

    public void delete(Card data) {
        treeByAscendingAttack.delete(data);
        treeByDescendingAttack.delete(data);
    }

    public String drawCard(String name, int attack, int health) {
        Card newCard = new Card(name, attack, health, InsertionTime++);
        treeByAscendingAttack.insert(newCard);
        treeByDescendingAttack.insert(newCard);
        return "Added "+name+" to the deck";
    }



    public int getSize() {
        return treeByAscendingAttack.getSize();
    }


    public BattleResult findForBattle(int strangerAttack, int strangerHealth) {
        Card candidate = null;


        candidate = treeByAscendingAttack.findForPriority1(strangerAttack, strangerHealth);

        if (candidate != null) {
            return new BattleResult(candidate, 1);
        }

        candidate = treeByDescendingAttack.findForPriority2(strangerAttack, strangerHealth);

        if (candidate != null) {
            return new BattleResult(candidate, 2);
        }


        candidate = treeByAscendingAttack.findForPriority3(strangerAttack, strangerHealth);

        if (candidate != null) {
            return new BattleResult(candidate, 3);
        }


        candidate = treeByDescendingAttack.findForPriority4();

        if (candidate != null) {
            return new BattleResult(candidate, 4);
        }

        return null;
    }




    public int calculateNewAttack(Card card) {

        double newAttackDouble = (double) card.getA_base() * card.getH_cur() / card.getH_base();
        double finalAttack = Math.max(1.0, Math.floor(newAttackDouble));
        return (int) finalAttack;
    }
    public void updateCard(Card oldCard, Card damagedCard) {

        int newAttack = calculateNewAttack(damagedCard);

        if (newAttack == oldCard.getA_cur()) {

            oldCard.setH_cur(damagedCard.getH_cur());
            oldCard.setH_base(damagedCard.getH_base());
            return;
        }


        this.delete(oldCard);
        Card trulyNewCard = new Card(
                damagedCard.getName(),
                damagedCard.getA_init(),
                damagedCard.getH_init(),
                this.InsertionTime++
        );
        trulyNewCard.setA_base(damagedCard.getA_base());
        trulyNewCard.setH_base(damagedCard.getH_base());
        trulyNewCard.setA_cur(newAttack);
        trulyNewCard.setH_cur(damagedCard.getH_cur());

        treeByAscendingAttack.insert(trulyNewCard);
        treeByDescendingAttack.insert(trulyNewCard);
    }

    public String battle(int A_stranger,int H_stranger,int heal) {
        BattleResult chosenCard = findForBattle(A_stranger,H_stranger);
        if (chosenCard == null) {
            this.strangerScore += 2;
            return String.format("No card to play, %d cards revived", 0);
        }
        Card playedCard = chosenCard.card;
        Card originalCard = new Card(playedCard);
        int priority = chosenCard.priority;

        int newSurvivorHealth = originalCard.getH_cur() - A_stranger;
        int newStrangerHealth = H_stranger - originalCard.getA_cur();

        if (newSurvivorHealth <= 0) {
            strangerScore += 2;
        } else {
            strangerScore += 1;
        }

        if (newStrangerHealth <= 0) {
            survivorScore += 2;
        } else {
            survivorScore += 1;
        }
        if (newSurvivorHealth <= 0) {
            this.delete(originalCard);
            return String.format("Found with priority %d, Survivor plays %s, the played card is discarded, %d cards revived",
                    priority, originalCard.getName(), 0);
        } else {
            Card damagedCard = new Card(originalCard);
            damagedCard.setH_cur(newSurvivorHealth);

            int newAttack = calculateNewAttack(damagedCard);
            damagedCard.setA_cur(newAttack);

            this.updateCard(originalCard, damagedCard);
            return String.format("Found with priority %d, Survivor plays %s, the played card returned to deck, %d cards revived",
                    priority, originalCard.getName(), 0);
        }
    }
    public String findWinning() {
        if (this.survivorScore >= this.strangerScore) {
            return "The Survivor, Score: " + this.survivorScore ;
        } else {
            return "The Stranger, Score: " + this.strangerScore ;
        }
    }

    public String deckCount() {
        int count = this.getSize();
        return "Number of cards in the deck: " + count ;
    }
    public String stealCard(int attackLimit, int healthLimit) {
        Card stolenCard = treeByAscendingAttack.findForSteal(attackLimit, healthLimit);

        if (stolenCard != null) {
            this.delete(stolenCard);
            return "The Stranger stole the card: " + stolenCard.getName();
        } else {
            return "No card to steal";
        }
    }

}
