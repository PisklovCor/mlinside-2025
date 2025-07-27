package com.multiagent;

import com.multiagent.agent.FundamentalAnalysisAgent;
import com.multiagent.agent.SentimentAnalysisAgent;
import com.multiagent.agent.TechnicalAnalysisAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@SpringBootTest(classes = CryptoMultiAgentApplication.class)
@ActiveProfiles("test")
public abstract class BaseTestConfiguration {

    @MockBean
    protected ChatModel chatModel;

    @MockBean
    protected TechnicalAnalysisAgent technicalAnalysisAgent;

    @MockBean
    protected FundamentalAnalysisAgent fundamentalAnalysisAgent;

    @MockBean
    protected SentimentAnalysisAgent sentimentAnalysisAgent;
}
