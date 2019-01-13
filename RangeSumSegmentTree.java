
public static class RangeSumSegmentTree {
    int[] tree; int n;
    public RangeSumSegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4*n + 1];
        buildTree(arr, 1, 0, n - 1);
    }

    // O(log n)
    public int query(int qs, int qe) {
        return queryTree(1, 0, n-1, qs, qe);
    }

    // O(log n)
    public void update(int i, int val) {
        updateNode(1, 0, n-1, i, val);
    }

    // O(n) - worst case
    public void updateRange(int rs, int re, int incr) {
        updateRangeTree(1, 0, n-1, rs, re, incr);
        }

    // O(n)
    private void buildTree(int[] arr, int index, int s, int e) {
        if (s > e)  return;
        if (s == e) {
            tree[index] = arr[s];
            return;
        }
        int mid = (s + e) / 2;
        buildTree(arr, 2*index, s, mid);
        buildTree(arr, 2*index + 1, mid + 1, e);
        tree[index] = tree[2*index] + tree[2*index + 1];
    }

    private int queryTree(int index, int s, int e, int qs, int qe)  {
        // 1. No overlap
        if (qs > e || qe < s)   return 0;
        // 2. Complete overlap
        if (s >= qs && e <= qe) return tree[index];
        // 3. Partial overlap - call both sides
        int mid = (s + e) / 2;
        int left = queryTree(2*index, s, mid, qs, qe);
        int right = queryTree(2*index + 1, mid + 1, e, qs, qe);
        return left + right;
    }

    private void updateNode(int index, int s, int e, int i, int val) {
        if (i < s || i > e) return;
        if (s == e) { // Leaf node
            tree[index] = val;
            return;
        }
        int mid = (s + e) / 2;
        updateNode(2*index, s, mid, i, val);
        updateNode(2*index + 1, mid + 1, e, i, val);
        tree[index] = tree[2*index] + tree[2*index + 1];
    }

    private void updateRangeTree(int index, int s, int e, int rs, int re, int incr) {
        if (rs > e || re < s)   return;
        if (s == e) { // Leaf node
            tree[index] += incr;
            return;
        }
        // Lying in range - call both sides
        int mid = (s + e) / 2;
        updateRangeTree(2*index, s, mid, rs, re, incr);
        updateRangeTree(2*index + 1, mid + 1, e, rs, re, incr);
        tree[index] = tree[2*index] + tree[2*index + 1];
    }

    public static void main(String[] args) {
        RangeSumSegmentTree rs = new RangeSumSegmentTree(new int[]{1, 4, -2, 3});
        System.out.println(rs.query(0, 2));
        rs.update(3, 5);
        System.out.println(rs.query(0, 2));
        System.out.println(rs.query(0, 3));
        rs.updateRange(1, 2, -2); // array is now [1, 2, -4, 5]
        System.out.println(rs.query(0, 2));
    }
}
