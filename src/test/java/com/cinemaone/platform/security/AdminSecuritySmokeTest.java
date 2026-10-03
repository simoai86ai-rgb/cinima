package com.cinemaone.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {
    "app.bootstrap-admin.enabled=true",
    "app.bootstrap-admin.email=admin@cinemaone.local",
    "app.bootstrap-admin.password=ChangeMe123!",
    "app.bootstrap-admin.display-name=Cinema One Admin",
    "app.bootstrap-admin.reset-existing-password=true",
    "app.demo-content.enabled=false"
})
@AutoConfigureMockMvc
class AdminSecuritySmokeTest {

    @Autowired MockMvc mockMvc;

    @Test
    void defaultDevelopmentAdminCanAuthenticateAndOpenDashboard() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().is3xxRedirection());

        MvcResult login = mockMvc.perform(post("/admin/login")
                .with(csrf())
                .param("email", "admin@cinemaone.local")
                .param("password", "ChangeMe123!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin"))
            .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        mockMvc.perform(get("/admin").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Dashboard")));
    }
}
