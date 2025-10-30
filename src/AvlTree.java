/**
 * AVL Tree implementation for self-balancing binary search tree.
 * Maintains O(log n) height for efficient operations.
 * Each tree uses a custom comparator to define ordering.
 */
public class AvlTree {
    private TreeNode root;                          // Root node of the tree
    private final MyComparator<Card> comparator;    // Defines the ordering (ascending/descending by attack, etc.)
    private int size;                                // Number of nodes in the tree

    /**
     * Constructor: Initializes an empty AVL tree with a given comparator.
     * @param comparator The comparison function to use for ordering nodes
     */
    public AvlTree(MyComparator<Card> comparator) {
        this.root = null;
        this.size = 0;
        this.comparator = comparator;
    }

    // ========== HELPER METHODS FOR AVL PROPERTIES ==========

    /**
     * Returns the height of a node. Null nodes have height -1.
     * @param node The node to query
     * @return Height of the node (-1 for null)
     */
    private int height(TreeNode node) {
        return (node == null) ? -1 : node.height;
    }

    /**
     * Updates the height and maxHealth of a node based on its children.
     * This maintains the augmented data structure for O(log n) searches.
     * @param node The node to update
     */
    private void updateHeight(TreeNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.leftChild), height(node.rightChild));
            updateMaxHealth(node);  // Also update the augmented maxHealth field
        }
    }

    /**
     * Updates the maxHealth field for O(log n) search optimization.
     * Each node stores the maximum health value in its subtree.
     * This allows pruning subtrees that can't contain valid cards.
     * @param node The node to update
     */
    private void updateMaxHealth(TreeNode node) {
        if (node == null) return;
        
        // Start with current node's health
        int maxHealth = node.data.getH_cur();
        
        // Check left subtree's maximum
        if (node.leftChild != null) {
            maxHealth = Math.max(maxHealth, node.leftChild.maxHealth);
        }
        // Check right subtree's maximum
        if (node.rightChild != null) {
            maxHealth = Math.max(maxHealth, node.rightChild.maxHealth);
        }
        
        node.maxHealth = maxHealth;
    }

    /**
     * Calculates the balance factor of a node.
     * Balance factor = height(left) - height(right)
     * Used to determine if rotations are needed.
     * @param node The node to check
     * @return Balance factor (positive = left-heavy, negative = right-heavy)
     */
    private int getBalanceFactor(TreeNode node) {
        return (node == null) ? 0 : height(node.leftChild) - height(node.rightChild);
    }

    // ========== AVL ROTATION OPERATIONS ==========

    /**
     * Performs a left rotation to fix right-heavy imbalance.
     * Before:     A              After:      B
     *              \                        / \
     *               B                      A   (R)
     *              / \                      \
     *            T2  (R)                    T2
     * @param A The unbalanced node
     * @return The new root of this subtree (B)
     */
    private TreeNode leftRotate(TreeNode A) {
        TreeNode B = A.rightChild;
        TreeNode T2 = B.leftChild;
        B.leftChild = A;
        A.rightChild = T2;
        updateHeight(A);  // Update A first (now child)
        updateHeight(B);  // Then update B (new root)
        return B;
    }

    /**
     * Performs a right rotation to fix left-heavy imbalance.
     * Before:       A            After:    B
     *              /                      / \
     *             B                     (L)  A
     *            / \                        /
     *          (L) T2                      T2
     * @param A The unbalanced node
     * @return The new root of this subtree (B)
     */
    private TreeNode rightRotate(TreeNode A) {
        TreeNode B = A.leftChild;
        TreeNode T2 = B.rightChild;
        B.rightChild = A;
        A.leftChild = T2;
        updateHeight(A);  // Update A first (now child)
        updateHeight(B);  // Then update B (new root)
        return B;
    }

    /**
     * Performs a right-left double rotation (right then left).
     * Used when right child is left-heavy.
     * @param A The unbalanced node
     * @return The new root of this subtree
     */
    private TreeNode rightLeftRotate(TreeNode A) {
        A.rightChild = rightRotate(A.rightChild);
        return leftRotate(A);
    }

    /**
     * Performs a left-right double rotation (left then right).
     * Used when left child is right-heavy.
     * @param C The unbalanced node
     * @return The new root of this subtree
     */
    private TreeNode leftRightRotate(TreeNode C) {
        C.leftChild = leftRotate(C.leftChild);
        return rightRotate(C);
    }

    // ========== INSERT OPERATION ==========

    /**
     * Recursive helper to insert a new card into the AVL tree.
     * Performs standard BST insertion, then rebalances the tree.
     * Time Complexity: O(log n)
     * @param currentNode The current node in traversal
     * @param data The card to insert
     * @return The (possibly new) root of this subtree after insertion and rebalancing
     */
    private TreeNode insertHelper(TreeNode currentNode, Card data) {
        // Base case: found the insertion point
        if (currentNode == null) {
            size++;
            return new TreeNode(data);
        }

        // Recursive case: compare and go left or right
        int comparation = comparator.compare(data, currentNode.data);

        if (comparation > 0) {
            // New data is "greater", insert in right subtree
            currentNode.rightChild = insertHelper(currentNode.rightChild, data);
        } else if (comparation < 0) {
            // New data is "less", insert in left subtree
            currentNode.leftChild = insertHelper(currentNode.leftChild, data);
        } else {
            // Equal: should not happen with unique insertion/discard times
            return currentNode;
        }

        // Update height and maxHealth of current node
        updateHeight(currentNode);
        
        // Check balance and perform rotations if needed
        int balanceFactor = getBalanceFactor(currentNode);

        // Case 1: Right-heavy (balance factor < -1)
        if (balanceFactor < -1) {
            if (comparator.compare(data, currentNode.rightChild.data) > 0) {
                // Right-Right case: single left rotation
                return leftRotate(currentNode);
            } else {
                // Right-Left case: double rotation (right then left)
                return rightLeftRotate(currentNode);
            }
        }
        
        // Case 2: Left-heavy (balance factor > 1)
        if (balanceFactor > 1) {
            if (comparator.compare(data, currentNode.leftChild.data) < 0) {
                // Left-Left case: single right rotation
                return rightRotate(currentNode);
            } else {
                // Left-Right case: double rotation (left then right)
                return leftRightRotate(currentNode);
            }
        }
        
        // No imbalance, return current node
        return currentNode;
    }

    /**
     * Public method to insert a card into the tree.
     * @param data The card to insert
     */
    public void insert(Card data) {
        root = insertHelper(root, data);
    }

    // ========== DELETE OPERATION ==========

    /**
     * Finds the node with minimum value in a subtree (leftmost node).
     * Used to find inorder successor during deletion.
     * @param node The root of the subtree
     * @return The node with minimum value, or null if tree is empty
     */
    private TreeNode findMinValueNode(TreeNode node) {
        if (node == null) return null;
        
        TreeNode current = node;
        // Keep going left until we can't anymore
        while (current.leftChild != null) {
            current = current.leftChild;
        }
        return current;
    }

    /**
     * Recursive helper to delete a card from the AVL tree.
     * Performs standard BST deletion, then rebalances the tree.
     * Time Complexity: O(log n)
     * @param currentNode The current node in traversal
     * @param data The card to delete
     * @return The (possibly new) root of this subtree after deletion and rebalancing
     */
    private TreeNode deleteHelper(TreeNode currentNode, Card data) {
        // Base case: node not found
        if (currentNode == null) {
            return null;
        }

        // Search for the node to delete
        int comparison = comparator.compare(data, currentNode.data);

        if (comparison < 0) {
            // Target is in left subtree
            currentNode.leftChild = deleteHelper(currentNode.leftChild, data);
        } else if (comparison > 0) {
            // Target is in right subtree
            currentNode.rightChild = deleteHelper(currentNode.rightChild, data);
        } else {
            // Node found! Handle deletion based on number of children
            
            // Case 1: Node has only right child (or no children)
            if (currentNode.leftChild == null) {
                size--;  // Decrement size when actually deleting
                return currentNode.rightChild;
            } 
            // Case 2: Node has only left child
            else if (currentNode.rightChild == null) {
                size--;  // Decrement size when actually deleting
                return currentNode.leftChild;
            }

            // Case 3: Node has two children
            // Replace with inorder successor (smallest node in right subtree)
            TreeNode successor = findMinValueNode(currentNode.rightChild);
            currentNode.data = successor.data;  // Copy successor's data
            // Delete the successor (which has at most one child)
            // This recursive call will trigger Case 1 or 2, decrementing size
            currentNode.rightChild = deleteHelper(currentNode.rightChild, successor.data);
        }

        // Safety check for empty tree
        if (currentNode == null) {
            return null;
        }

        // Update height and maxHealth after deletion
        updateHeight(currentNode);
        
        // Check balance and rebalance if needed
        int balanceFactor = getBalanceFactor(currentNode);

        // Case 1: Left-heavy after deletion
        if (balanceFactor > 1) {
            if (getBalanceFactor(currentNode.leftChild) >= 0) {
                // Left-Left case: single right rotation
                return rightRotate(currentNode);
            } else {
                // Left-Right case: double rotation
                return leftRightRotate(currentNode);
            }
        }
        
        // Case 2: Right-heavy after deletion
        if (balanceFactor < -1) {
            if (getBalanceFactor(currentNode.rightChild) <= 0) {
                // Right-Right case: single left rotation
                return leftRotate(currentNode);
            } else {
                // Right-Left case: double rotation
                return rightLeftRotate(currentNode);
            }
        }
        
        // No imbalance
        return currentNode;
    }

    /**
     * Public method to delete a card from the tree.
     * @param data The card to delete
     */
    public void delete(Card data) {
        root = deleteHelper(root, data);
    }

    /**
     * Returns the number of cards in the tree.
     * @return Current size of the tree
     */
    public int getSize() {
        return this.size;
    }

    // --- BATTLE SEARCH FUNCTIONS (O(log n) optimized) ---

    public Card findForPriority1(int strangerAttack, int strangerHealth) {
        return findForPriority1Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority1Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) return null;
        
        Card currentCard = node.data;
        
        // AscendingAttack tree: smaller attacks on left, larger on right
        if (currentCard.getA_cur() < strangerHealth) {
            // Current attack too weak, go right for stronger attacks
            return findForPriority1Helper(node.rightChild, strangerAttack, strangerHealth);
        }
        
        // Current attack is sufficient (>= strangerHealth)
        // Try to find a better (smaller attack) candidate on the left
        Card resultFromLeft = null;
        if (node.leftChild != null && node.leftChild.maxHealth > strangerAttack) {
            // Left subtree might have valid cards (with health > strangerAttack)
            resultFromLeft = findForPriority1Helper(node.leftChild, strangerAttack, strangerHealth);
        }
        
        if (resultFromLeft != null) {
            return resultFromLeft;
        }
        
        // No better candidate on left, check if current card is valid
        if (currentCard.getH_cur() > strangerAttack) {
            return currentCard;
        }
        
        // Current card doesn't survive, search right
        return findForPriority1Helper(node.rightChild, strangerAttack, strangerHealth);
    }

    public Card findForPriority2(int strangerAttack, int strangerHealth) {
        return findForPriority2Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority2Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) return null;

        Card current = node.data;
        int attack = current.getA_cur();
        int health = current.getH_cur();

        // DescendingAttack tree: larger attacks on left, smaller on right
        if (attack >= strangerHealth) {
            // Attack too strong (would kill), go right for weaker attacks
            return findForPriority2Helper(node.rightChild, strangerAttack, strangerHealth);
        }

        // Current attack is weak enough (< strangerHealth, won't kill)
        // Try left for stronger (but still < strangerHealth) candidates
        Card bestCandidate = null;
        if (node.leftChild != null && node.leftChild.maxHealth > strangerAttack) {
            // Left subtree might have valid cards with sufficient health
            Card leftCandidate = findForPriority2Helper(node.leftChild, strangerAttack, strangerHealth);
            if (leftCandidate != null) {
                bestCandidate = leftCandidate;
            }
        }

        if (bestCandidate == null && health > strangerAttack) {
            // No better candidate on left, current card survives
            bestCandidate = current;
        }

        if (bestCandidate == null) {
            // Current doesn't work, try right
            bestCandidate = findForPriority2Helper(node.rightChild, strangerAttack, strangerHealth);
        }

        return bestCandidate;
    }


    public Card findForPriority3(int strangerAttack, int strangerHealth) {
        return findForPriority3Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority3Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) return null;
        
        Card currentCard = node.data;
        
        // AscendingAttack tree: smaller attacks on left, larger on right
        if (currentCard.getA_cur() < strangerHealth) {
            // Current attack too weak, go right for stronger attacks
            return findForPriority3Helper(node.rightChild, strangerAttack, strangerHealth);
        }
        
        // Current attack is sufficient (>= strangerHealth)
        // Try to find a better (smaller attack) candidate on the left
        Card resultFromLeft = findForPriority3Helper(node.leftChild, strangerAttack, strangerHealth);
        
        if (resultFromLeft != null) {
            return resultFromLeft;
        }
        
        // No better candidate on left, check if current card is valid
        if (currentCard.getH_cur() <= strangerAttack) {
            return currentCard;
        }
        
        // Current card survives (not wanted for P3), search right
        return findForPriority3Helper(node.rightChild, strangerAttack, strangerHealth);
    }

    public Card findForPriority4() {
        // DescendingAttack tree: max attack is the left-most node.
        TreeNode node = findMinValueNode(root);
        return (node == null) ? null : node.data;
    }

    public Card findForSteal(int attackLimit, int healthLimit) {
        return findForStealHelper(root, attackLimit, healthLimit);
    }

    private Card findForStealHelper(TreeNode node, int attackLimit, int healthLimit) {
        if (node == null) return null;
        
        Card currentCard = node.data;
        
        // AscendingAttack tree: smaller attacks on left, larger on right
        if (currentCard.getA_cur() <= attackLimit) {
            // Current attack too weak, need stronger (go right only)
            return findForStealHelper(node.rightChild, attackLimit, healthLimit);
        }
        
        // Current attack is strong enough (> attackLimit)
        // Try to find a better (smaller attack but still > attackLimit) candidate on the left
        Card resultFromLeft = null;
        if (node.leftChild != null && node.leftChild.maxHealth > healthLimit) {
            // Left subtree might have valid cards with sufficient health
            resultFromLeft = findForStealHelper(node.leftChild, attackLimit, healthLimit);
        }
        
        if (resultFromLeft != null) {
            return resultFromLeft;
        }
        
        // No better candidate on left, check if current card is valid
        if (currentCard.getH_cur() > healthLimit) {
            return currentCard;
        }
        
        // Current card doesn't have enough health, search right
        return findForStealHelper(node.rightChild, attackLimit, healthLimit);
    }

    // --- NEW HEALING SEARCH FUNCTIONS (Type-2) ---

    /**
     * Finds the best card for full revival (Healing P1/P2).
     * Searches LargestMissingHealth tree for the max H_missing <= healPool.
     */
    public Card findCardToFullyRevive(int healPool) {
        return findCardToFullyReviveHelper(root, healPool);
    }

    private Card findCardToFullyReviveHelper(TreeNode node, int healPool) {
        if (node == null) return null;

        Card current = node.data;
        int hMissing = current.getH_missing(); // H_base - revivalProgress

        // This tree is LargestMissingHealth (descending).
        // If current.H_missing > healPool, this card and everything
        // to its left is too expensive. We must go right.
        if (hMissing > healPool) {
            return findCardToFullyReviveHelper(node.rightChild, healPool);
        }

        // This card is a candidate (H_missing <= healPool).
        // Check left for a *better* candidate (larger H_missing).
        Card leftCandidate = findCardToFullyReviveHelper(node.leftChild, healPool);

        if (leftCandidate != null) {
            // Found a better one on the left.
            return leftCandidate;
        } else {
            // This is the best candidate in this subtree.
            return current;
        }
    }

    /**
     * Finds the best card for partial revival (Healing P3).
     * Searches SmallestMissingHealth tree for the min H_missing.
     */
    public Card findCardForPartialRevive() {
        // SmallestMissingHealth tree: min H_missing is the left-most node.
        TreeNode node = findMinValueNode(root);
        return (node == null) ? null : node.data;
    }
}