package com.example.assignment1;

public class UnionFind {
    public int[] parent;
    public int[] rank;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }

    }

    public int find(int x) {
        if(parent[x]==-1) return -1;

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    public void displayDSAsText(int width) {
        for (int i = 0; i < parent.length; i++)
            System.out.print(find(i) + ((i + 1) % width == 0 ? "\n" : " "));
    }
}
