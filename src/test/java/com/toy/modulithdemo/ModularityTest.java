package com.toy.modulithdemo;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {

    @Test
    void verifyModules() {
        ApplicationModules modules = ApplicationModules.of(ModulithDemoApplication.class);
        modules.verify(); // 모듈 구조 검증
    }

    @Test
    void createDocs() {
        ApplicationModules modules = ApplicationModules.of(ModulithDemoApplication.class);
        new Documenter(modules).writeDocumentation(); // 모듈 다이어그램 등 생성
    }

}
