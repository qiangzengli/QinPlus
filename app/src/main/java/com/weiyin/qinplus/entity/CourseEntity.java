package com.weiyin.qinplus.entity;

import java.util.List;

/**
 * Created by lenovo on 2017/8/1.
 */

public class CourseEntity extends BaseEntity{
    private String version;
    private List<KnowledgeEntity> knowledge;
    private List<PracticeEntity> practice;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<KnowledgeEntity> getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(List<KnowledgeEntity> knowledge) {
        this.knowledge = knowledge;
    }

    public List<PracticeEntity> getPractice() {
        return practice;
    }

    public void setPractice(List<PracticeEntity> practice) {
        this.practice = practice;
    }
}
