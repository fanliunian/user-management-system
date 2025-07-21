package com.example.usermanagement;

import com.example.usermanagement.dto.SimpleApiResponseTest;
import com.example.usermanagement.exception.SimpleExceptionHandlerTest;
import com.example.usermanagement.security.SimpleJwtTokenProviderTest;
import com.example.usermanagement.service.SimpleAuthServiceTest;
import com.example.usermanagement.service.SimpleServiceTest;
import com.example.usermanagement.service.SimpleUserServiceTest;
import com.example.usermanagement.controller.SimpleUserControllerTest;
import com.example.usermanagement.integration.SimpleIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 可正常运行的测试套件
 */
@Suite
@SelectClasses({
    SimpleTest.class,
    SimpleApiResponseTest.class,
    SimpleExceptionHandlerTest.class,
    SimpleJwtTokenProviderTest.class,
    SimpleAuthServiceTest.class,
    SimpleServiceTest.class,
    SimpleUserServiceTest.class,
    SimpleUserControllerTest.class,
    SimpleIntegrationTest.class
})
public class WorkingTestSuite {
    // 测试套件，无需额外代码
}