package com.multiagent;

import com.multiagent.agent.FundamentalAnalysisAgent;
import com.multiagent.agent.SentimentAnalysisAgent;
import com.multiagent.agent.TechnicalAnalysisAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CryptoMultiAgentApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseTestConfiguration {

    @MockBean
    protected ChatModel chatModel;

    @Autowired
    protected TechnicalAnalysisAgent technicalAnalysisAgent;

    @Autowired
    protected FundamentalAnalysisAgent fundamentalAnalysisAgent;

    @Autowired
    protected SentimentAnalysisAgent sentimentAnalysisAgent;
}
