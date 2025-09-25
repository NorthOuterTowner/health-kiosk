package com.example.quickexam.bean;

import java.io.Serializable;
import java.util.List;

public class ConfigBean implements Serializable {


    /**
     * setting : {"project":[{"name":"血压","is_open":1},{"name":"血氧","is_open":1},{"name":"体温","is_open":1},{"name":"酒精","is_open":1}]}
     */

    private SettingBean setting;

    public SettingBean getSetting() {
        return setting;
    }

    public void setSetting(SettingBean setting) {
        this.setting = setting;
    }

    public static class SettingBean implements Serializable {
        private List<ProjectBean> project;

        public List<ProjectBean> getProject() {
            return project;
        }

        public void setProject(List<ProjectBean> project) {
            this.project = project;
        }

        public static class ProjectBean implements Serializable {
            /**
             * name : 血压
             * is_open : 1
             */

            private String name;
            private String value;
            private int is_open;
            private int color = 0 ;

            public String getName() {
                return name;
            }
            public String getValue() {
                return value;
            }
            public void setName(String name) {
                this.name = name;
            }
            public int getColor() { return color;}
            public int getIs_open() {
                return is_open;
            }

            public void setIs_open(int is_open) {
                this.is_open = is_open;
            }

            public  void setValue(String val){ this.value = val; }

            public  void setColor(int val){ this.color = val; }
        }
    }
}
