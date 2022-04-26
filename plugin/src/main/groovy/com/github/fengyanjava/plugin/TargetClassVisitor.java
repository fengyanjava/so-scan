package com.github.fengyanjava.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;


/**
 * Created by 星空 on 2022/4/22 18:59
 */

public class TargetClassVisitor extends ClassVisitor {

    private String className;

    public TargetClassVisitor(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String target = className + "#" + name;
        return new _AdviceAdapter(target, Opcodes.ASM7, null, access, name, descriptor);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
//        System.out.println("Lib so scan: " + name + "---------");
        super.visit(version, access, name, signature, superName, interfaces);
    }

    private static class _AdviceAdapter extends AdviceAdapter {

        String target;
        ArrayList<Object> ldcList = new ArrayList<>();

        protected _AdviceAdapter(String target, int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            this.target = target;
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
//            System.out.println("visitMethodInsn:" + owner + " " + name + " " + descriptor);
            if ("java/lang/System".equals(owner) && "loadLibrary".equals(name)) {
                Object libName = null;
                if (!ldcList.isEmpty()) {
                    libName = ldcList.get(ldcList.size() - 1);
                }
                System.out.println("Lib so scan: " + target + "=>" + name + "=>" + libName);
            }
        }

        @Override
        public void visitLdcInsn(Object value) {
            super.visitLdcInsn(value);
//            System.out.println("visitLdcInsn:" + value);
            ldcList.add(value);
        }

    }
}
