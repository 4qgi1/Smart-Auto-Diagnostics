package com.smartauto.model;

import java.util.Objects;

/**
 * عَرَض رئيسي يختاره المستخدم في البداية (مثل: تفتفة)،
 * ويملك شجرة قرار خاصة به تبدأ من {@link #getRoot()}.
 */
public final class Symptom {

    private final String id;
    private final String title;
    private final String description;
    private final QuestionNode root;

    public Symptom(String id, String title, String description, QuestionNode root) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.root = Objects.requireNonNull(root, "root");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public QuestionNode getRoot() {
        return root;
    }
}
