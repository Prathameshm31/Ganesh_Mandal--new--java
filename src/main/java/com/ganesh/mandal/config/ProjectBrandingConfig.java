package com.ganesh.mandal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBrandingConfig {

    @Value("${project.name:Hindavi Swarajya}")
    private String projectName;

    @Value("${project.logo:/assets/hindavi-swarajya-logo.png}")
    private String projectLogo;

    public String getProjectName() {
        return projectName;
    }

    public String getProjectLogo() {
        return projectLogo;
    }
}
