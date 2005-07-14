package com.siyeh.ig.psiutils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class TestUtils{
    private TestUtils(){
        super();
    }

    public static boolean isTest(@NotNull PsiClass aClass){
        final PsiManager manager = aClass.getManager();
        final PsiFile file = aClass.getContainingFile();
        final VirtualFile virtualFile = file.getVirtualFile();
        final Project project = manager.getProject();
        final ProjectRootManager rootManager = ProjectRootManager
                .getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        return fileIndex.isInTestSourceContent(virtualFile);
    }

    public static boolean isJUnitTestMethod(PsiMethod method){
        final String methodName = method.getName();
        if(!methodName.startsWith("test")){
            return false;
        }
        if(method.hasModifierProperty(PsiModifier.ABSTRACT) ||
                !method.hasModifierProperty(PsiModifier.PUBLIC)){
            return false;
        }
        final PsiType returnType = method.getReturnType();
        if(returnType == null){
            return false;
        }
        if(!returnType.equals(PsiType.VOID)){
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        if(parameterList == null){
            return false;
        }
        final PsiParameter[] parameters = parameterList.getParameters();
        if(parameters == null){
            return false;
        }
        if(parameters.length != 0){
            return false;
        }
        final PsiClass targetClass = method.getContainingClass();
        return !isJUnitTestClass(targetClass);
    }

    private static boolean isJUnitTestClass(PsiClass targetClass){
        return targetClass == null ||
                !ClassUtils.isSubclass(targetClass, "junit.framework.TestCase");
    }
}
