public class TreeNode {
    Card data;
    int height;
    TreeNode rightChild;
    TreeNode leftChild;
    int maxHealth;

    public TreeNode(Card data) {
        this.data = data;
        this.height = 0;
        this.rightChild = null;
        this.leftChild = null;
        this.maxHealth = data.getH_cur();
    }
}
