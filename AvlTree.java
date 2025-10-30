public class AvlTree {
    private TreeNode root;
    private final MyComparator<Card> comparator;
    private int size;

    public AvlTree(MyComparator<Card> comparator) {
        this.root = null;
        this.size = 0;
        this.comparator = comparator;
    }

    // --- BURAYA KENDİ AVL AĞACI TEMEL FONKSİYONLARINIZI EKLEYİN ---
    // (insert, delete, rotate, height, getBalanceFactor,
    //  insertHelper, deleteHelper, vb. kodlarınızın tamamı buraya gelecek)
    // ...
    // ... (Lütfen bu fonksiyonları kendi dosyanızdan kopyalayın) ...
    // ...

    private int height(TreeNode node) {
        return (node == null) ? -1 : node.height;
    }

    private void updateHeight(TreeNode node) {
        node.height = 1 + Math.max(height(node.leftChild), height(node.rightChild));
    }

    private int getBalanceFactor(TreeNode node) {
        return height(node.leftChild) - height(node.rightChild);
    }

    private TreeNode leftRotate(TreeNode A) {
        if (A == null || A.rightChild == null) return A;
        TreeNode B = A.rightChild;
        TreeNode T2 = B.leftChild;

        B.leftChild = A;
        A.rightChild = T2;

        updateHeight(A);
        updateHeight(B);
        return B;
    }
    private TreeNode rightRotate(TreeNode A) {
        if (A == null || A.leftChild == null) return A;
        TreeNode B = A.leftChild;
        TreeNode T2 = B.rightChild;

        B.rightChild = A;
        A.leftChild = T2;
        updateHeight(A);
        updateHeight(B);
        return B;
    }

    private TreeNode leftRightRotate(TreeNode C) {
        if (C == null) return null;
        C.leftChild = leftRotate(C.leftChild);
        return rightRotate(C);
    }

    private TreeNode rightLeftRotate(TreeNode A) {
        if (A == null) return null;
        A.rightChild = rightRotate(A.rightChild);
        return leftRotate(A);
    }

    private TreeNode insertHelper(TreeNode currentNode,Card data) {
        if (currentNode==null) {
            size++;
            return new TreeNode(data);
        }

        int comparation  = comparator.compare(data,currentNode.data);

        if (comparation>0) {
            currentNode.rightChild= insertHelper(currentNode.rightChild,data);
        } else if (comparation<0) { // Hata düzeltme: else if olmalı
            currentNode.leftChild= insertHelper(currentNode.leftChild,data);
        }
        else {
            // Eşit kart (aynı insertionTime), ekleme
            return currentNode;
        }

        updateHeight(currentNode);

        int balanceFactor = getBalanceFactor(currentNode);

        if (balanceFactor < -1) {
            if (comparator.compare(data, currentNode.rightChild.data) > 0) {
                return leftRotate(currentNode);
            }
            else {
                return rightLeftRotate(currentNode);
            }
        }

        if (balanceFactor > 1) {
            if (comparator.compare(data, currentNode.leftChild.data) < 0) {
                return rightRotate(currentNode);
            }
            else {
                return leftRightRotate(currentNode);
            }
        }

        return currentNode;
    }

    public void insert(Card data) {
        root = insertHelper(root,data);
    }

    private TreeNode findMinValueNode(TreeNode node) {
        TreeNode current = node;
        while (current.leftChild != null) {
            current = current.leftChild;
        }
        return current;
    }

    private TreeNode deleteHelper(TreeNode currentNode, Card data) {
        if (currentNode == null) {
            return null;
        }

        int comparison = comparator.compare(data, currentNode.data);

        if (comparison < 0) {
            currentNode.leftChild = deleteHelper(currentNode.leftChild, data);
        } else if (comparison > 0) {
            currentNode.rightChild = deleteHelper(currentNode.rightChild, data);
        } else {
            // Düğüm bulundu, silme işlemi
            size--;

            if (currentNode.leftChild == null) {
                return currentNode.rightChild;
            } else if (currentNode.rightChild == null) {
                return currentNode.leftChild;
            }

            TreeNode successor = findMinValueNode(currentNode.rightChild);
            currentNode.data = successor.data;
            // successor'u silmek için size'ı tekrar artırıp deleteHelper'ı çağırıyoruz
            size++;
            currentNode.rightChild = deleteHelper(currentNode.rightChild, successor.data);
        }

        // Düğüm silinmediyse veya alt ağaçtan silindiyse (ve null değilse)
        // yeniden dengeleme yap
        if (currentNode == null) {
            return null;
        }

        updateHeight(currentNode);

        int balanceFactor = getBalanceFactor(currentNode);

        if (balanceFactor > 1) { // Sol-ağır
            if (getBalanceFactor(currentNode.leftChild) >= 0) { // Sol-Sol
                return rightRotate(currentNode);
            } else { // Sol-Sağ
                return leftRightRotate(currentNode);
            }
        }

        if (balanceFactor < -1) { // Sağ-ağır
            if (getBalanceFactor(currentNode.rightChild) <= 0) { // Sağ-Sağ
                return leftRotate(currentNode);
            } else { // Sağ-Sol
                return rightLeftRotate(currentNode);
            }
        }
        return currentNode;
    }

    public void delete(Card data) {
        root = deleteHelper(root, data);
    }

    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void updateCard(Card oldCard, Card newCard) {
        this.delete(oldCard);
        this.insert(newCard);
    }

    // --- ARAMA FONKSİYONLARI ---

    /**
     * P1: (Kill, Survive) - O(log N)
     * AĞAÇ: AscendingAttack
     * YORUM: Bu fonksiyon orijinal dosyanızda
     * zaten O(log N) ve doğruydu. DEĞİŞİKLİK YOK.
     */
    public Card findForPriority1(int strangerAttack, int strangerHealth) {
        return findForPriority1Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority1Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) {
            return null;
        }
        Card currentCard = node.data;
        if (currentCard.getA_cur() < strangerHealth) {
            return findForPriority1Helper(node.rightChild, strangerAttack, strangerHealth);
        }
        Card resultFromLeft = findForPriority1Helper(node.leftChild, strangerAttack, strangerHealth);
        if (resultFromLeft != null) {
            return resultFromLeft;
        }
        if (currentCard.getH_cur() > strangerAttack) {
            return currentCard;
        }
        return findForPriority1Helper(node.rightChild, strangerAttack, strangerHealth);
    }

    /**
     * P2: (NotKill, Survive) - O(log N)
     * AĞAÇ: DescendingAttack
     */
    public Card findForPriority2(int strangerAttack, int strangerHealth) {
        return findForPriority2Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority2Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) {
            return null;
        }

        Card current = node.data;
        int attack = current.getA_cur();
        int health = current.getH_cur();

        // Bu kart Kill yapıyorsa (attack >= strangerHealth)
        if (attack >= strangerHealth) {
            // Çok güçlü, SADECE sağa git
            // Sol tamamiyle gereksiz (sol daha da yüksek ataklı, hepsi Kill yapar)
            return findForPriority2Helper(node.rightChild, strangerAttack, strangerHealth);
        }

        // Buraya geldiyse: attack < strangerHealth (NotKill şartı OK)
        // DescendingAttack: Sol=yüksek, Sağ=düşük

        Card best = null;

        // 1. Mevcut kart geçerli mi kontrol et
        if (health > strangerAttack) {
            best = current;
        }

        // 2. Sola bak (daha yüksek ataklı adaylar)
        // KRITIK: Sol child da strangerHealth'den küçük mü?
        if (node.leftChild != null) {
            Card leftBest = findForPriority2Helper(node.leftChild, strangerAttack, strangerHealth);
            if (leftBest != null) {
                // Sol tarafta geçerli bir kart var
                // Sol her zaman daha yüksek ataklı, o yüzden sol kazanır
                best = leftBest;
            }
        }

        // 3. Eğer hala aday bulamadıysak sağa bak
        if (best == null) {
            best = findForPriority2Helper(node.rightChild, strangerAttack, strangerHealth);
        }

        return best;
    }

    /**
     * P3: (Kill, NotSurvive) - O(log N)
     * AĞAÇ: AscendingAttack
     * YORUM: "Wrong Answer" (WA) hatasını düzeltmek için
     * "sequential logic" optimizasyonundan vazgeçildi.
     * Kod, orijinal dosyanızdaki haline
     * (H_cur <= strangerAttack kontrolünü içeren) geri döndürüldü.
     * Bu mantık zaten O(log N) ve kendi başına doğrudur.
     */
    public Card findForPriority3(int strangerAttack, int strangerHealth) {
        return findForPriority3Helper(root, strangerAttack, strangerHealth);
    }

    private Card findForPriority3Helper(TreeNode node, int strangerAttack, int strangerHealth) {
        if (node == null) return null;

        Card currentCard = node.data;
        if (currentCard.getA_cur() < strangerHealth) {
            // Atak yetersiz. Mecburen sağa git.
            return findForPriority3Helper(node.rightChild, strangerAttack, strangerHealth);
        }

        // Atak yeterli. Solda daha iyi (düşük ataklı) bir aday var mı?
        Card resultFromLeft = findForPriority3Helper(node.leftChild, strangerAttack, strangerHealth);
        if (resultFromLeft != null) {
            // Varsa, o en iyisidir.
            return resultFromLeft;
        }

        // Solda aday yok. Mevcut kart geçerli mi (canı YETMİYOR MU)?
        if (currentCard.getH_cur() <= strangerAttack) {
            // EVET, BU KONTROLÜ GERİ EKLEDİK.
            return currentCard;
        }

        // Mevcut kart geçerli değil (canı yetiyor).
        // Mecburen sağa bakmalıyız (belki orada canı yetmeyen vardır).
        return findForPriority3Helper(node.rightChild, strangerAttack, strangerHealth);
    }

    /**
     * P4: (Any, Any) - O(log N)
     * AĞAÇ: DescendingAttack
     * YORUM: TLE'yi önlemek için O(N) olan kodunuz O(log N)
     * (en soldaki düğümü bulma) ile düzeltildi.
     */
    public Card findForPriority4() {
        return findForPriority4Helper(root);
    }

    private Card findForPriority4Helper(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode current = node;
        // Azalan atak ağacında (Descending), en yüksek ataklı kart en soldaki karttır.
        while (current.leftChild != null) {
            current = current.leftChild;
        }
        return current.data;
    }

    /**
     * Steal: - O(log N)
     * AĞAÇ: AscendingAttack
     * YORUM: Bu fonksiyon orijinal dosyanızda
     * zaten O(log N) ve doğruydu. DEĞİŞİKLİK YOK.
     */
    public Card findForSteal(int attackLimit, int healthLimit) {
        return findForStealHelper(root, attackLimit, healthLimit);
    }

    private Card findForStealHelper(TreeNode node, int attackLimit, int healthLimit) {
        if (node == null) {
            return null;
        }

        Card currentCard = node.data;
        Card resultFromLeft = null;

        if (currentCard.getA_cur() > attackLimit) {
            resultFromLeft = findForStealHelper(node.leftChild, attackLimit, healthLimit);
        }

        if (resultFromLeft != null) {
            return resultFromLeft;
        }

        if (currentCard.getA_cur() > attackLimit && currentCard.getH_cur() > healthLimit) {
            return currentCard;
        }

        return findForStealHelper(node.rightChild, attackLimit, healthLimit);
    }


}