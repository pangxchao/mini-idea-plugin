package com.mini.plugin.builder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.intellij.psi.JavaPsiFacade.getInstance;

public abstract class PsiAbstractBuilder<T, E extends PsiElement> extends AbstractBuilder<T> {
    protected final PsiElementFactory factory;

    protected PsiAbstractBuilder(@NotNull Project project) {
        factory = getInstance(project).getElementFactory();
    }

    public abstract E build();

    public final T add(PsiElement element) {
        if (element == null) {
            return getThis();
        }
        build().add(element);
        return getThis();
    }

    @SuppressWarnings("UnusedReturnValue")
    public final T addBefore(PsiElement element, Function<E, PsiElement> anchor) {
        PsiElement anchorElement = anchor.apply(build());
        if (element != null && anchorElement != null) {
            build().addBefore(element, anchorElement);
        }
        return getThis();
    }

    public final T addAfter(PsiElement element, Function<E, PsiElement> anchor) {
        PsiElement anchorElement = anchor.apply(build());
        if (element != null && anchorElement != null) {
            build().addAfter(element, anchorElement);
        }
        return getThis();
    }

    public final T addRange(PsiElement first, Function<E, PsiElement> last) {
        PsiElement lastElement = last.apply(build());
        if (first != null && lastElement != null) {
            build().addRange(first, lastElement);
        }
        return getThis();
    }

    public final T addRangeBefore(PsiElement first, Function<E, PsiElement> last, Function<E, PsiElement> anchor) {
        PsiElement lastElement = last.apply(build()), anchorElement = anchor.apply(build());
        if (first != null && lastElement != null && anchorElement != null) {
            build().addRangeBefore(first, lastElement, anchorElement);
        }
        return getThis();
    }

    public final T addRangeAfter(PsiElement first, Function<E, PsiElement> last, Function<E, PsiElement> anchor) {
        PsiElement lastElement = last.apply(build()), anchorElement = anchor.apply(build());
        if (first != null && lastElement != null && anchorElement != null) {
            build().addRangeAfter(first, lastElement, anchorElement);
        }
        return getThis();
    }
}