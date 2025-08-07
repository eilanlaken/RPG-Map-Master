package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;

public class Scene3D implements MemoryResource {

    public ModelMesh[]           allMeshes;
    public ModelMaterial[]       allMaterials;
    public Node[]                allNodes;
    public Node                  root;
    public HashMap<String, Node> namedNodes;

    @Override
    public void delete() {
        // TODO
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("meshes: ").append(allMeshes.length).append('\n');
        sb.append("materials: ").append(allMaterials.length).append('\n');
        sb.append("nodes: ").append(allNodes.length).append('\n');
        sb.append(root);
        return sb.toString();
    }

    public static class Node {

        // TODO: add parent. The node hierarchy should be a tree, not a list.
        public Node parent;
        public Type type;
        public String name;
        public Matrix4x4 localTransform;
        public Model model;
        public Node[] children;

        @Override
        public String toString() {
            return toStringHelper(0);
        }

        private String toStringHelper(int indent) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ".repeat(indent)).append("-").append(name);
            if (children != null && children.length > 0) {
                sb.append(":\n");
                for (Node child : children) {
                    sb.append(child.toStringHelper(indent + 3));
                }
            } else {
                sb.append("\n");
            }
            return sb.toString();
        }

    }

    public enum Type {

        MODEL,
        CAMERA,
        LIGHT,
        EMPTY,

    }

}
