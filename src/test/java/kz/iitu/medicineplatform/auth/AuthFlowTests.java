package kz.iitu.medicineplatform.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTests {
    @Autowired private MockMvc mockMvc;

    @Test
    void registrationCreatesOnlyRegularUserAndLoginCreatesSession() throws Exception {
        Csrf csrf = getCsrf();
        String email = "researcher-" + UUID.randomUUID() + "@example.test";
        String password = "Long-Test-Password-2026!";
        String registration = "{\"email\":\"" + email + "\",\"displayName\":\"Test Researcher\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/api/auth/register").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        String login = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void administrativeRoutesRejectAnonymousUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
        Csrf csrf = getCsrf();
        mockMvc.perform(post("/api/assistant/questions").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"What is known?\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/assistant/history")).andExpect(status().isUnauthorized());
    }

    @Test
    void assistantSafetyQuestionsGetFixedWarningAndNoGeneratedMedicalAdvice() throws Exception {
        Csrf csrf = getCsrf();
        String email = "assistant-" + UUID.randomUUID() + "@example.test";
        String password = "Long-Test-Password-2026!";
        String registration = "{\"email\":\"" + email + "\",\"displayName\":\"Assistant User\",\"password\":\"" + password + "\"}";
        mockMvc.perform(post("/api/auth/register").cookie(csrf.cookie()).header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/api/auth/login").cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(post("/api/assistant/questions").session(session).cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"Можно ли при беременности и какая дозировка?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SAFETY_REDIRECT"))
                .andExpect(jsonPath("$.isAiGenerated").value(false))
                .andExpect(jsonPath("$.citations.length()").value(0))
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("врачу или фармацевту")));

        mockMvc.perform(get("/api/assistant/history").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].lastStatus").value("SAFETY_REDIRECT"))
                .andExpect(jsonPath("$.content[0].lastQuestion").value("Можно ли при беременности и какая дозировка?"));
    }

    private Csrf getCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk()).andReturn();
        Matcher tokenMatcher = Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"")
                .matcher(result.getResponse().getContentAsString());
        Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
        assertNotNull(cookie);
        if (!tokenMatcher.find()) throw new AssertionError("CSRF response did not include a token");
        return new Csrf(tokenMatcher.group(1), cookie);
    }

    private record Csrf(String token, Cookie cookie) { }
}
