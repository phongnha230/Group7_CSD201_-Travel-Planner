package com.travelplanner.structures;

import com.travelplanner.entities.Customer;

public class TreeNode {
    public Customer data;
    public TreeNode left;  // Nhánh con chứa ID nhỏ hơn
    public TreeNode right; // Nhánh con chứa ID lớn hơn

    public TreeNode(Customer data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}