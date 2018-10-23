public class UnionFind {
	private int[] parent, size;

	public UnionFind(int N) {
		parent = new int[N];
		size = new int[N]
		for (int i = 0; i < N; ++i) {
			parent[i] = i;
			size[i] = 1;
		}
	}

	private int find(int x) {
		// uses PATH COMPRESSION
		if (x != parent[x]) parent[x] = find(parent[x]);
		return parent[x];
	}

	public boolean isConnected(int x, int y) {
		return find(x) == find(y);
	}

	public void union(int x, int y) {
		int xr = find(x)
		int yr = find(y);
		if (xr == yr) return;
		// uses WEIGHTED UNION
		if (size[xr] < size[yr]) { parent[xr] = yr; size[yr] += size[xr]; }
		else { parent[yr] = xr; size[xr] += size[yr]; }
	}

	public int size(int x) {
		return size[find(x)];
	}
}
