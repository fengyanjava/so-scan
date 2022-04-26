package com.github.fengyanjava.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

class ScannerTransform extends Transform {

    @Override
    String getName() {
        return getClass().getSimpleName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        inputs.each {
            // 遍历jar 第三方引入的 class
            it.jarInputs.each { JarInput jarInput ->
                JarFile jarFile = new JarFile(jarInput.file)
//                println("jar file:" + jarInput.file.absolutePath)
                Enumeration<JarEntry> entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement()
                    String entryName = jarEntry.getName().replace("/", ".")
//                    println("jar entry:" + entryName)
                    if (!entryName.endsWith(".class")) {
                        continue
                    }
                    if (entryName.startsWith("android")) {
                        continue
                    }
                    InputStream inputStream = jarFile.getInputStream(jarEntry)
                    ClassReader classReader = new ClassReader(inputStream);
                    classReader.accept(new TargetClassVisitor(Opcodes.ASM7), ClassReader.EXPAND_FRAMES);
                }
                jarFile.close()
            }
        }


    }
}