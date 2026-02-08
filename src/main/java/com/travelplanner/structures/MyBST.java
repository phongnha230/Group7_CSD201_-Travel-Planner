package com.travelplanner.structures;

import com.travelplanner.entities.Customer;
import java.util.ArrayList;
import java.util.List;

public class MyBST {
    private TreeNode root;//Goc cay

    public MyBST() {
        this.root = null;
    }

    //Them khach hang (Insert)
    public void insert(Customer cus) {
        root = insertRec(root,cus);
    }

    private TreeNode insertRec(TreeNode root, Customer cus){
        //Neu nhanh dang trong -> Trong cay vao
        if(root == null){
            return new TreeNode(cus);
        }

        if (cus.compareTo(root.data) < 0){
            root.left = insertRec(root.left, cus);
        }   else if (cus.compareTo(root.data) > 0){
                root.right = insertRec(root.right,cus);
        }
        return root;
    }

    //Tim kiem khach hang
    public Customer search(String id){
        Customer searchKey = new Customer(id, "", "", "");
        return searchRec(root,searchKey);
    }

    private Customer searchRec(TreeNode root, Customer key){
        if (root == null || root.data.getId().equals(key.getId())){
            return (root != null) ? root.data:null;
        }

        if (key.compareTo(root.data) < 0 )
            return searchRec(root.left,key);

        return searchRec(root.right,key);
    }
    
    //Xoa khach hang
    public void delete(String id) {
        Customer key = new Customer(id, "", "", "");
        root = deleteRec(root,key);
    }

  private TreeNode deleteRec(TreeNode root, Customer key) {
        // 1. Nếu cây rỗng hoặc đi quá lá -> Dừng
        if (root == null) return root;

        // 2. Đi tìm node cần xóa
        if (key.compareTo(root.data) < 0) {
            root.left = deleteRec(root.left, key); // Đi trái
        } else if (key.compareTo(root.data) > 0) {
            root.right = deleteRec(root.right, key); // Đi phải
        } 
        // 3. Đã tìm thấy node cần xóa (key == root.data)
        else {
            // TH1 & TH2: Node có 0 hoặc 1 con
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // TH3: Node có 2 con (Phức tạp nhất)
            // Lấy giá trị nhỏ nhất bên phải đập vào chỗ cần xóa
            root.data = minValue(root.right);

            // Xóa node thừa bên phải đi
            root.right = deleteRec(root.right, root.data);
        }
        return root;
    }
    private Customer minValue(TreeNode root){
        Customer minv = root.data;
        while (root.left != null) {
            minv = root.left.data;
            root = root.left;
        }
        return minv;
    }

    //Duyet cay va in danh sach
    public void printAllCustomer(){
        if (root == null) {
            System.out.println("Danh sach trong!");
            return;
        }
        System.out.println("==DANH SACH KHACH HANG(SORTED BY ID)==");
        inOrderRec(root);

        System.out.println("=========================================");
    }

    private void inOrderRec(TreeNode root){
        if (root != null) {
            inOrderRec(root.left);
            System.out.println(root.data);
            inOrderRec(root.right);
        }
    }

    //Tien ich khac
    public int count(){
        return countRec(root);
    }

    private int countRec(TreeNode root) {
        if (root == null )return 0;
        return 1 + countRec(root.left) + countRec(root.right);
    }

    /** Returns all customers in sorted order (in-order traversal) */
    public List<Customer> getAllInOrder() {
        List<Customer> list = new ArrayList<>();
        inOrderToList(root, list);
        return list;
    }

    private void inOrderToList(TreeNode node, List<Customer> list) {
        if (node == null) return;
        inOrderToList(node.left, list);
        list.add(node.data);
        inOrderToList(node.right, list);
    }

    /** Returns tree structure for visualization: [{id, name, left, right}, ...] */
    public List<java.util.Map<String, String>> getTreeStructure() {
        List<java.util.Map<String, String>> result = new ArrayList<>();
        buildTreeStructure(root, result);
        return result;
    }

    private void buildTreeStructure(TreeNode node, List<java.util.Map<String, String>> result) {
        if (node == null) return;
        java.util.Map<String, String> m = new java.util.HashMap<>();
        m.put("id", node.data.getId());
        m.put("name", node.data.getName());
        m.put("left", node.left != null ? node.left.data.getId() : null);
        m.put("right", node.right != null ? node.right.data.getId() : null);
        result.add(m);
        buildTreeStructure(node.left, result);
        buildTreeStructure(node.right, result);
    }
}
